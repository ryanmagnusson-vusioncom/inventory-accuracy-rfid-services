package io.vusion.vtransmit.v2.commons.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.vusion.gson.utils.GsonHelper;

public class VtransmitV2Utils {
	
	public static final String CORRELATION_ID_KEY = "swCorrelationId";
	public static final String CORRELATION_CREATED_KEY = "swCorrelationAlreadyCreated";
	public static final String EVENT_TYPE = "swEventType";
	public static final String EXTERNAL_ID_KEY = "swExternalId";
	public static final String EXT_CLIENT_ID_KEY = "extClientId";
	public static final String STORE_ID_KEY = "swStoreId";
	public static final String CLIENT_IP_KEY = "swClientIp";
	public static final String SOURCE_KEY = "swSource";
	public static final String ENQUEUED_DATE_KEY = "swEnqueuedDate";
	public static final String PRIORITY_KEY = "swPriority";
	public static final String TYPE_KEY = "swType";
	public static final String VTRANSMIT_TASK_ID = "vtransmitTaskId";
	public static final String SESSION_ID_KEY = "swHFCoreSessionId";
	public static final String RETAILCHAIN_ID_KEY = "swRetailChainId";
	
	
	private VtransmitV2Utils() {
		// utility class
	}
	
	public static String getRetailChain(final String storeId) {
		if (!storeId.contains(".")) {
			throw new RuntimeException("Store id: " + storeId + " is not a store id");
		}
		
		return storeId.split("[.]")[0];
	}
	
	public static <T extends JsonElement> T getConfiguration(final String jsonString, final String storeId) {
		
		final JsonObject globalConfiguration = GsonHelper.getGsonNull().fromJson(jsonString.replace("'", "\""),
				JsonObject.class);
		
		return getConfiguration(globalConfiguration, storeId);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends JsonElement> T getConfiguration(final JsonObject globalConfiguration, final String storeId) {
		
		if(!"*".equals(storeId)){
			if (globalConfiguration.has(storeId)) {
				return (T) globalConfiguration.get(storeId);
			}
			
			final String retailChain = getRetailChain(storeId);
			if (globalConfiguration.has(retailChain)) {
				return (T) globalConfiguration.get(retailChain);
			}
		}
		
		if (globalConfiguration.has("defaultValue")) {
			return (T) globalConfiguration.get("defaultValue");
		}
		
		if (globalConfiguration.has("default")) {
			return (T) globalConfiguration.get("default");
		}
		
		throw new RuntimeException("Invalid configuration: " + globalConfiguration);
	}
	
	public static int calculateObjectNumberBySlot(final int objectSize, final int durationBetweenSlotsInMinute,
			final int totalDurationInHour) {
		final int slots = (totalDurationInHour * 60) / durationBetweenSlotsInMinute;
		return objectSize % slots == 0 ? (objectSize / slots) : ((objectSize / slots) + 1);
	}
	
	public static <KEY, VALUE> Map<KEY, List<VALUE>> fromJsonToMapOfList(final Gson gson, final JsonObject jsonObject, final Class<KEY> keyClazz,
			final Class<VALUE> valueClazz) {
		
		if (jsonObject.isJsonNull()) {
			return null;
		}
		
		final Map<KEY, List<VALUE>> result = new HashMap<>();
		for (final Entry<String, JsonElement> lineAsEntry : jsonObject.entrySet()) {
			final KEY key = gson.fromJson(lineAsEntry.getKey(), keyClazz);
			final List<VALUE> value = GsonHelper.fromJsonToList(gson, lineAsEntry.getValue().getAsJsonArray(), valueClazz);
			result.put(key, value);
		}
		
		return result;
	}
	
}
