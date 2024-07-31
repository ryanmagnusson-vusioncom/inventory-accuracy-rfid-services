package io.vusion.rfid.services.controller;

import com.google.gson.JsonObject;
import io.vusion.dao.model.AbstractEntity;
import io.vusion.gson.utils.GsonHelper;
import io.vusion.rfid.services.front.model.BaseResponse;
import io.vusion.secure.logs.VusionLogger;
import io.vusion.vtransmit.v2.commons.model.Store;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@RestController
public abstract class BaseController {
	private static final VusionLogger LOGGER = VusionLogger.getLogger(BaseController.class);

	protected BaseController() {}
	//protected BaseController(StoreService storeService) {
//		this.storeService = storeService;
//	}

	protected <T extends Enum<T>> T getValue(final HttpHeaders headers, final String key, final Class<T> type,
			final T defaultValue) {
		final String value = headers.getFirst(key);
		return getValue(value, type, defaultValue);
	}
	
	protected <T extends Enum<T>> T getValue(final Map<String, String> requestParam, final String key,
			final Class<T> type, final T defaultValue) {
		final String value = requestParam.get(key);
		return getValue(value, type, defaultValue);
	}
	
	protected <T extends Enum<T>> T getValue(final String value, final Class<T> type, final T defaultValue) {
		if (StringUtils.isNotBlank(value)) {
			return defaultValue;
		}
		try {
			return Enum.valueOf(type, value.trim());
		} catch (final Exception e) {
			return defaultValue;
		}
	}
	
	protected ResponseEntity<Object> success(String message) {
		return new ResponseEntity<>(new BaseResponse(message), HttpStatus.OK);
	}
	
	protected ResponseEntity<Object> success(List<String> extCorrelationIds) {
		return new ResponseEntity<>(new BaseResponse(extCorrelationIds), HttpStatus.OK);
	}
	
	protected ResponseEntity<Object> success(Stream<String> extCorrelationIds) {
		return new ResponseEntity<>(new BaseResponse(extCorrelationIds.toList()), HttpStatus.OK);
	}
	
	protected ResponseEntity<?> success(Object response) {
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	protected ResponseEntity<?> success(AbstractEntity response) {
		return new ResponseEntity<>(sanitizedEntity(response), HttpStatus.OK);
	}
	
	private static JsonObject sanitizedEntity(AbstractEntity entity) {
		
		final JsonObject copy = GsonHelper.toJsonObject(entity);
		copy.remove("id");
		copy.remove("login");
		copy.remove("password");
		copy.remove("token");
		return copy;
	}
	
	protected ResponseEntity<?> success(Collection<? extends AbstractEntity> response) {
		return success(response.stream().map(BaseController::sanitizedEntity).toList());
	}
	
	protected ResponseEntity<?> notFound(String message) {
		return new ResponseEntity<>(new BaseResponse(message), HttpStatus.NOT_FOUND);
	}
	
	protected ResponseEntity<?> badRequest(String message) {
		return new ResponseEntity<>(new BaseResponse(message), HttpStatus.BAD_REQUEST);
	}
	
	public ResponseEntity<Object> execute(final String storeId,
			final String runName,
			final Function<Store, ResponseEntity<Object>> run) {
		final Store store = Store.fromId(storeId);
		try {
			return run.apply(store);
		} catch (final Exception e) {
			final String message = String.format("Unexpected error caught while sending to store %s request %s.",
					storeId, runName);
			LOGGER.error(message, e);
			return new ResponseEntity<>(new BaseResponse(message), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
}
