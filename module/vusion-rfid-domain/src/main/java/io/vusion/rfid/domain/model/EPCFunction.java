package io.vusion.rfid.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.strip;

public enum EPCFunction {

    SGTIN;

    public static Stream<EPCFunction> stream() {
        return Stream.of(EPCFunction.values());
    }

    @JsonCreator
    public static EPCFunction fromString(String text) {
        if (isBlank(text)) {
            return null;
        }
        final String cleanedUp = strip(text).replaceAll("_\\d+$", "");
        return EPCFunction.stream()
                          .filter(fx -> equalsIgnoreCase(cleanedUp, fx.name()))
                          .findAny()
                          .orElseThrow(() -> new IllegalArgumentException("Invalid EPC Function: " + cleanedUp));
    }
}
