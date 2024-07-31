package io.vusion.vtransmit.v2.commons.utils;

import java.time.Instant;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class JsonNullPatterUtils {

    public static final String IMPSSOBLE_STRING = "_$_NULL_$_";
    public static final Instant IMPOSSIBLE_INSTANCE = Instant.parse("1111-11-11T11:11:11Z");

    private JsonNullPatterUtils() {
    }

    public static JsonObject replaceImpossibleValuesWithNullFromJsonObject(JsonObject request) {
        JsonObject response = new JsonObject();

        for (Map.Entry<String, JsonElement> entry : request.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (value.isJsonNull()) {
                response.add(key, JsonNull.INSTANCE);
                continue;
            }

            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                String stringValue = value.getAsString();
                if (isImpossibleInstant(stringValue) || isImpossibleString(stringValue)) {
                    response.add(key, JsonNull.INSTANCE);
                } else {
                    response.add(key, value);
                }
            } else if (value.isJsonObject()) {
                response.add(key, replaceImpossibleValuesWithNullFromJsonObject(value.getAsJsonObject()));
            } else if (value.isJsonArray()) {
                response.add(key, replaceImpossibleValuesWithNullFromJsonArray(value.getAsJsonArray()));
            } else {
                response.add(key, value);
            }
        }

        return response;
    }

    private static JsonArray replaceImpossibleValuesWithNullFromJsonArray(JsonArray request) {
        JsonArray response = new JsonArray();

        for (JsonElement element : request) {
            if (element.isJsonNull()) {
                response.add(JsonNull.INSTANCE);
                continue;
            }

            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                String stringValue = element.getAsString();
                if (isImpossibleInstant(stringValue) || isImpossibleString(stringValue)) {
                    response.add(JsonNull.INSTANCE);
                } else {
                    response.add(element);
                }
            } else if (element.isJsonObject()) {
                response.add(replaceImpossibleValuesWithNullFromJsonObject(element.getAsJsonObject()));
            } else if (element.isJsonArray()) {
                response.add(replaceImpossibleValuesWithNullFromJsonArray(element.getAsJsonArray()));
            } else {
                response.add(element);
            }
        }

        return response;
    }

    private static boolean isImpossibleString(final String stringValue) {
        return IMPSSOBLE_STRING.equals(stringValue);
    }

    private static boolean isImpossibleInstant(final String stringValue) {
        try {
            final Instant instant = Instant.parse(stringValue);
            return IMPOSSIBLE_INSTANCE.equals(instant);
        } catch (Exception e) {
            return false;
        }
    }
}
