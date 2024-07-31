package io.vusion.vtransmit.v2.commons.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This class is used to calculate the difference between all fields of two JsonObject.
 */
public class JsonUtils {

    private JsonUtils() {
    }

    public static Map<String, Pair<String, String>> getJsonDifferences(
            final JsonObject firstJson, final JsonObject secondJson) {
        Map<String, Pair<String, String>> modifiedFields = new HashMap<>();
        getNextDiff(firstJson, secondJson, modifiedFields, "");
        return modifiedFields;
    }

    private static void getNextDiff(final JsonObject firstJson, final JsonObject secondJsonEntry,
                                    final Map<String, Pair<String, String>> modifications, final String prefix) {

        Set<Map.Entry<String, JsonElement>> firstJsonEntries = firstJson.entrySet();
        Set<Map.Entry<String, JsonElement>> secondJsonEntries = secondJsonEntry.entrySet();

        for (Map.Entry<String, JsonElement> firstJsonEntry : firstJsonEntries) {
            String key = firstJsonEntry.getKey();
            JsonElement firstJsonValue = firstJsonEntry.getValue();
            JsonElement secondJsonValue = secondJsonEntry.get(key);

            String fullPath = prefix.isEmpty() ? key : prefix + "." + key;

            if (secondJsonValue == null) {
                if (firstJsonValue.isJsonObject()) {
                    getNextDiff(firstJsonValue.getAsJsonObject(), new JsonObject(), modifications, fullPath);
                } else {
                    modifications.put(fullPath, Pair.of(firstJsonValue.getAsString(), null));
                }
            } else if (firstJsonValue.isJsonObject() && secondJsonValue.isJsonObject()) {
                getNextDiff(firstJsonValue.getAsJsonObject(), secondJsonValue.getAsJsonObject(), modifications, fullPath);
            } else if (!firstJsonValue.equals(secondJsonValue)) {
                modifications.put(fullPath, Pair.of(firstJsonValue.getAsString(), secondJsonValue.getAsString()));
            }
        }

        for (Map.Entry<String, JsonElement> secondEntry : secondJsonEntries) {
            String key = secondEntry.getKey();
            if (!firstJson.has(key)) {
                String fullPath = prefix.isEmpty() ? key : prefix + "." + key;
                modifications.put(fullPath, Pair.of(null, secondEntry.getValue().getAsString()));
            }
        }
    }
}
