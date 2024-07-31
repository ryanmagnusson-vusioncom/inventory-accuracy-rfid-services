package io.vusion.vtransmit.v2.commons.utils;

import static org.apache.commons.lang3.StringUtils.stripToNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.ClassUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface GsonAdapter {

    static boolean isNull(JsonElement json) { return json == null || json.isJsonNull(); }

    default String getStringProperty(JsonObject json, String property) {
        if (isNull(json)) {
            return null;
        }

        final JsonElement value = json.get(property);
        if (value == null || value.isJsonNull()) {
            return null;
        }
        return value.getAsString();
    }

    default int getIntProperty(JsonObject json, String property) {
        if (isNull(json)) {
            return 0;
        }

        final JsonElement value = json.get(property);
        if (value.isJsonNull()) {
            return 0;
        }
        return value.getAsInt();
    }

    default <T> T getProperty(JsonObject json,
                              String property,
                              Function<String,T> adapter) {

        if (isNull(json)) {
            return null;
        }

        final JsonElement value = json.get(property);
        if (value.isJsonNull()) {
            return null;
        }
        final String asText = value.getAsString();

        return adapter.apply(asText);
    }

    default <T> String toJsonString(T property) {
        return toJsonString(property, Objects::toString);
    }

    default <T> String toJsonString(T property, Function<T, String> adapter) {
        if (property == null) {
            return null;
        }

        Objects.requireNonNull(adapter,
                              () -> {
                                // this goofy bit of logic is needed because the typename is always the classname
                                final String typeName = Optional.ofNullable(stripToNull(ClassUtils.getSimpleName(property)))
                                                                .orElse("object");
                                return String.format("An adapter function is required to convert %s into a String",
                                                     typeName);
                              });

        return adapter.apply(property);
    }
}
