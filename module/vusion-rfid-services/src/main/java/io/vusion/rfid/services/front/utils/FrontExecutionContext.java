package io.vusion.rfid.services.front.utils;

import static io.vusion.vtransmit.v2.commons.utils.VtransmitV2Utils.CORRELATION_ID_KEY;
import static io.vusion.vtransmit.v2.commons.utils.VtransmitV2Utils.ENQUEUED_DATE_KEY;
import static io.vusion.vtransmit.v2.commons.utils.VtransmitV2Utils.EXTERNAL_ID_KEY;
import static io.vusion.vtransmit.v2.commons.utils.VtransmitV2Utils.EXT_CLIENT_ID_KEY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.HttpHeaders;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.vtransmit.v2.commons.model.EnumEventType;
import io.vusion.vtransmit.v2.commons.model.EnumPriority;
import io.vusion.vtransmit.v2.commons.model.EnumRetryStrategy;
import io.vusion.vtransmit.v2.commons.utils.MDCUtils;
import io.vusion.vtransmit.v2.commons.utils.RequestUtils;
import io.vusion.vtransmit.v2.commons.utils.VtransmitV2Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class FrontExecutionContext {
	
	private FrontExecutionContext() {
	}
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	public static class Context implements Serializable {
		
		private Instant contextCreationDate = Instant.now();
		private String storeId;
		private EnumPriority priority;
		private EnumEventType eventType;
		private String correlationId;
		private boolean correlationCreated;
		private String externalId;
		private EnumRetryStrategy retryStrategy = EnumRetryStrategy.NO_RETRY_ON_FAILURE;
		private String extClientId;
		private Instant enqueuedDate;
		private long deliveryCount;
		private AtomicInteger externalIdHfIndex = new AtomicInteger(0);

		private Instant now;
		
		@Override
		public String toString() {
			return GsonHelper.toJson(this);
		}

		public Context(String storeId) {
			this.storeId = storeId;
		}
	}
	
	
	private static final String ALL_OR_NO_STORE = "*";
	
	private static final Map<String, FrontExecutionContext.Context> EXECUTION_CONTEXTS = new ConcurrentHashMap<>();
	
	private static void initContext(final FrontExecutionContext.Context ctx) {
		
		EXECUTION_CONTEXTS.put(Thread.currentThread().getName(), ctx);
		MDCUtils.init(getStoreId(), getCorrelationId(), getExternalId(), getEventType());
	}
	
	public static void close() {
		EXECUTION_CONTEXTS.remove(Thread.currentThread().getName());
		MDCUtils.clear();
	}
	
	public static void initContextNoStore(final EnumPriority priority, EnumEventType eventType, final HttpHeaders headers) {
		initContext(ALL_OR_NO_STORE, priority, eventType, headers);
	}
	
	
	public static void initContext(final String storeId,
			final EnumPriority priority,
			final EnumEventType eventType,
			final HttpHeaders headers) {
		initContext(storeId, priority, eventType, new HashMap<>(headers.toSingleValueMap()));
		
		if (eventType == null || eventType == EnumEventType.UNDEFINED) {
			throw new IllegalArgumentException(
					"Event type cannot be UNDEFINED. " +
							"Please implement the @ExecutionContext annotation on the endpoint method. " +
					"You can use EventTypes.NONE if you don't want to specify an event type.");
		}
	}
	
	private static void initContext(final String storeId,
			final EnumPriority priority,
			final EnumEventType eventType,
			final Map<String, Object> headers) {
		
		
		final String correlationId = Objects.toString(RequestUtils.findHeader(CORRELATION_ID_KEY, headers).orElse(UUID.randomUUID()));
		final Object enqueued = RequestUtils.findHeader(ENQUEUED_DATE_KEY, headers).orElse(null);
		final Instant enqueuedTimestamp = enqueued == null ? Instant.now() : Instant.parse(enqueued.toString());
		final Context ctx = new Context(storeId);
		
		ctx.setStoreId(storeId);
		ctx.setCorrelationId(correlationId);
		ctx.setCorrelationCreated(toBoolean(RequestUtils.findHeader(VtransmitV2Utils.CORRELATION_CREATED_KEY, headers).orElse(false)));
		ctx.setExternalId(toString(RequestUtils.findHeader(EXTERNAL_ID_KEY, headers).orElse(correlationId)));
		ctx.setExtClientId(toString(RequestUtils.findHeader(EXT_CLIENT_ID_KEY, headers).orElse(null)));
		ctx.setEnqueuedDate(enqueuedTimestamp);
		ctx.setPriority(priority);
		ctx.setEventType(eventType);
		initContext(ctx);
	}
	
	private static String toString(Object object) {
		if (object == null) {
			return null;
		}
		return object.toString();
	}
	
	private static boolean toBoolean(final Object value) {
		return value != null && BooleanUtils.toBooleanObject(Objects.toString(value));
	}
	
	public static boolean hasCorrelationId() {
		return isInitialized() && isNotBlank(getCorrelationId());
	}
	
	public static void setEventType(final EnumEventType eventType) {
		getContext().setEventType(eventType);
	}
	
	public static boolean isInitialized() {
		return getContext() != null;
	}
	
	public static FrontExecutionContext.Context getContext() {
		return EXECUTION_CONTEXTS.get(Thread.currentThread().getName());
	}
	
	public static String getStoreId() {
		return Optional.ofNullable(getContext()).map(Context::getStoreId).orElse(null);
	}
	
	public static String getCorrelationId() {
		return Optional.ofNullable(getContext()).map(Context::getCorrelationId).orElse(null);
	}
	
	public static boolean isCorrelationCreated() {
		return Optional.ofNullable(getContext()).map(Context::isCorrelationCreated).orElse(false);
	}
	
	
	public static String getExternalId() {
		return Optional.ofNullable(getContext()).map(Context::getExternalId).orElse(null);
	}
	
	public static String getExtClientId() {
        return Optional.ofNullable(getContext()).map(Context::getExtClientId).orElse(null);
	}
	
	public static Instant getEnqueuedDate() {
		return Optional.ofNullable(getContext()).map(Context::getEnqueuedDate).orElse(null);
	}
	
	public static EnumPriority getPriority() {
		return Optional.ofNullable(getContext()).map(Context::getPriority).orElse(null);
	}
	
	public static EnumEventType getEventType() {
		return Optional.ofNullable(getContext()).map(Context::getEventType).orElse(null);
	}
	
	public static Instant getContextCreationDate() {
		return Optional.ofNullable(getContext()).map(Context::getContextCreationDate).orElse(null);
	}
	
	public static long getDeliveryCount() {
		return Optional.ofNullable(getContext()).map(Context::getDeliveryCount).orElse(0L);
	}
	
	public static void setDeliveryCount(final long l) {
		getContext().deliveryCount = l;
	}
	
	public static EnumRetryStrategy getRetryStrategy() {
		return Optional.ofNullable(getContext()).map(Context::getRetryStrategy).orElse(null);
	}
	
	// TODO RMA 20240327 we might be able to get this from the Spring context or configuration
	public static String getCurrentApplicationAsSource() {
		return "vtransmit-v2";
	}
	
	public static boolean shouldCorrelationBeCreated() {
		return getContext() == null || !isCorrelationCreated();
	}
	
	public static Instant getNow() {
		if (getContext().now == null) {
			getContext().now = Instant.now();
		}
		return getContext().now;
	}
	
	public static long generateExternalIdHf() {

        return getEnqueuedDate().toEpochMilli() * 100 + getContext().externalIdHfIndex.getAndIncrement() % 100;
	}
	
}
