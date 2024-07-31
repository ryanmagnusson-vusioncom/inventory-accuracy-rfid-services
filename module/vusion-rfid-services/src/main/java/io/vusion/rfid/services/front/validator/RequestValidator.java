package io.vusion.rfid.services.front.validator;

import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import io.vusion.vtransmit.v2.commons.model.Store;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sesimagotag.ecs.commons.preparedtask.TaskPriority;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.vtransmit.v2.commons.exceptions.ErrorCode;
import io.vusion.vtransmit.v2.commons.exceptions.ErrorConstants;
import io.vusion.vtransmit.v2.commons.exceptions.StoreNotAvailable;
import io.vusion.vtransmit.v2.commons.exceptions.StoreNotFound;
import io.vusion.vtransmit.v2.commons.exceptions.ValidationException;
//import io.vusion.vtransmit.v2.commons.model.EnumTransmissionTechnology;
//import io.vusion.vtransmit.v2.commons.model.LabelType;
//import io.vusion.vtransmit.v2.commons.model.dao.Store;
import io.vusion.vtransmit.v2.commons.model.request.RequestWithLabelId;
//import io.vusion.vtransmit.v2.commons.service.LabelTypeService;
//import io.vusion.vtransmit.v2.commons.service.StoreService;
//import io.vusion.rfid.services.front.exception.FailedDependency;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class RequestValidator<T> {
	public static final int MAX_ELEMENTS_ALLOWED_PER_REQUEST = 100_000;
	protected Class<T> typeOfT;

//	@Getter(AccessLevel.PROTECTED)
//	@Setter(onMethod_ = {@Autowired})
//	protected StoreService storeService;
//
//	@Getter(AccessLevel.PROTECTED)
//	@Setter(onMethod_ = {@Autowired})
//	protected LabelTypeService labelTypeService;

	@SuppressWarnings("unchecked")
	protected RequestValidator() {
		this.typeOfT = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

    protected RequestValidator(Class<T> supportedType) {
        this.typeOfT = supportedType;
    }

	protected String cleanMessage(final Exception e) {
		final String message = e.getMessage();
		if (message == null) {
			return null;
		}
		if (message.startsWith("com.")) {
			return message.substring(message.indexOf("Exception: "));
		}
		return message;
	}

	protected String validateTaskPriority(final String priority, final String defaultPriority) {
		if (StringUtils.isBlank(priority)) {
			return defaultPriority;
		} else {
			try {
				TaskPriority.fromString(priority);
			} catch (final IllegalArgumentException e) {
				throw new ValidationException("Invalid priority " + priority + " for ping request",
						ErrorConstants.PRIORITY_IS_INVALID);
			}
		}
		return priority;
	}

	protected Store validateAndGetStore(final String storeId) {
		if (isBlank(storeId)) {
//        try {
//			return storeService.getStoreOrFail(storeId);
//		} catch (final StoreNotFound e1) {
//			throw new ValidationException(String.format("unable to find store: %s", storeId));
//		} catch (final StoreNotAvailable e2) {
			throw new ValidationException(String.format("store is not available: %s", storeId));
		}
        return Store.fromId(storeId);
	}

	public List<T> validate(String storeId, final Optional<String> body) {
		final String bodyGet = notTooBigNotEmpty(body);
		final List<JsonElement> elements = getNotEmptyListOfElements(bodyGet);
        final Store store = validateAndGetStore(storeId);
		final List<T> requests = getRequestsFromJson(store, elements);
        checkNotTooManyDevices(store, requests);
        return validateRequests(store, requests);
	}

    protected List<T> validateRequests(Store store, List<T> requests) {
        final List<T> validatedRequests = new ArrayList<>();
        for (final T request : requests) {
            validatedRequests.add(validate(store, request));
        }
        return validatedRequests;
    };

	protected List<JsonElement> getNotEmptyListOfElements(final String bodyGet) {
		JsonElement rootElement;
        final List<JsonElement> elements;

		try {
			rootElement = GsonHelper.fromJson(bodyGet, JsonElement.class);
		} catch (final Exception e) {
			throw new ValidationException("Invalid payload due to "
			+ cleanMessage(e)
			+ " original body is " + bodyGet.substring(0, Math.min(bodyGet.length(), 1000)));
		}

        if (isJsonNull(rootElement)) {
            throw new ValidationException("Empty body");
        }

        elements = rootElement.isJsonArray()
                        ? rootElement.getAsJsonArray().asList()
                        : List.of(rootElement);

		if (elements.isEmpty()) {
			throw new ValidationException("Empty list");
		}
		return elements;
	}

	private String notTooBigNotEmpty(final Optional<String> body) {
		if (body.isEmpty() || body.get() == null || body.get().isEmpty()) {
			throw new ValidationException("Empty body");
		}
		final String bodyGet = body.get();
		if (bodyGet.length() > 1024 * 1024 * 100) {
			throw new ValidationException("Maximum body size is 100MB");
		}
		return bodyGet;
	}

	protected List<T> getRequestsFromJson(final Store store, final List<JsonElement> elements) {
		final List<T> requests = new ArrayList<>();
		for (final JsonElement e : elements) {
			if (e == null || e.isJsonNull()) {
				throw new ValidationException("One element is null",
						new ErrorCode(ErrorConstants.REQUEST_AT_LEAST_ONE_ELEMENT_IS_NULL));
			}
			final T request;
			if(e.isJsonPrimitive()) {
				request = convertPrimitiveToRequest(e);
			} else {
				request = convertJsonElementToRequest(e);
			}

//			if (request instanceof final RequestWithLabelId withLabelId) {
//				validateLabelId(store, withLabelId.getLabelId());
//			}

			requests.add(request);
		}
		return requests;
	}

	protected T convertJsonElementToRequest(final JsonElement json) {
		return GsonHelper.fromJson(json, typeOfT);
	}

	private boolean isLabelIdRequest() {
		return RequestWithLabelId.class.isAssignableFrom(typeOfT);
	}

	protected T convertPrimitiveToRequest(JsonElement labelId) {
		if (labelId.getAsJsonPrimitive().isString() && isLabelIdRequest()) {
			final JsonObject jsonObject = new JsonObject();
			jsonObject.add("labelId", labelId);
			return GsonHelper.fromJson(jsonObject, typeOfT);
		}

		throw new ValidationException("One element is not a valid request and is a primitive element, please change it");
	}

	protected int getMaxRequestElements() {
		return MAX_ELEMENTS_ALLOWED_PER_REQUEST;
	}

	protected Collection<T> checkNotTooManyDevices(Store store, Collection<T> requests) {
		final int requestCount = size(requests);
		if (requestCount > getMaxRequestElements()) {
			final List<String> parameters = List.of(String.format("%d", requestCount),
					String.format("%d", getMaxRequestElements()));
			throw new ValidationException("Too many devices",
					new ErrorCode(ErrorConstants.TOO_MANY_DEVICE_IDS_ERROR, parameters))
			.addContextValue("storeId", store.getStoreId())
			.addContextValue("requests", requests);
		}
		return requests;
	}

	protected boolean isJsonNull(JsonElement json) {
		return json == null || json.isJsonNull();
	}

    protected <T> T tryParse(String name, Supplier<T> parser) {
        try {
            return parser.get();
        } catch (final Exception e) {
            throw new ValidationException(String.format("%s: %s", name, e.getMessage()));
        }
    }

    protected Instant parseInstantElse(String text, Instant defaultValue) {
        return parseInstantElseGet(text, () -> defaultValue);
    }

    protected Instant parseInstantElseGet(String text, Supplier<Instant> supplier) {
        if (isBlank(text)) {
            return supplier == null ? null : supplier.get();
        }

        try {
            return GsonHelper.toDate(text).toInstant();
        } catch (Exception e) {
            throw new ValidationException(String.format("Unable to parse as a date: '%s'", text));
        }
    }

	protected abstract T validate(Store store, T request);

//	public void validateLabelId(Store store, final String labelId) throws ValidationException {
//		if (StringUtils.isBlank(labelId)) {
//			throw new ValidationException("labelId is mandatory");
//		}
//
//		// As we are on the critical path, we don't access to the storeDao
//		final LabelType labelType = labelTypeService.getLabelType(labelId);
//		if (labelType == null) {
//			throw new ValidationException("label type of label '" + labelId + "'' unknown");
//		}
//
//		final EnumTransmissionTechnology transmissionTechnology = labelType.getTransmissionTechnology();
//		if (storeService.getTransmissionSystem(store, transmissionTechnology) == null) {
//			throw new FailedDependency("No transmission system parametrized for " + transmissionTechnology.name());
//		}
//	}
}
