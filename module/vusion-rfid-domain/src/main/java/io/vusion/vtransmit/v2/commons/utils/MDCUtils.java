package io.vusion.vtransmit.v2.commons.utils;

import org.slf4j.MDC;

import io.vusion.vtransmit.v2.commons.model.EnumEventType;
import lombok.Value;


@Value
public class MDCUtils {
	
	private MDCUtils() {
		// utility class
	}
	
	public static void init(String storeId, String correlationId, String externalId, EnumEventType eventType) {
		MDC.clear();
		putIfNotNull("storeId", storeId);
		putIfNotNull("correlationId", correlationId);
		putIfNotNull("externalId", externalId);
		putIfNotNull("eventType", eventType);
	}
	
	public static void clear() {
		MDC.clear();
	}
	
	private static void putIfNotNull(String key, String value) {
		if (value != null) {
			MDC.put(key, value);
		}
	}
	
	private static void putIfNotNull(String key, Enum<?> value) {
		if (value != null) {
			MDC.put(key, value.name());
		}
	}
	
}
