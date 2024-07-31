package io.vusion.rfid.domain.model;

import io.vusion.gson.utils.GsonHelper;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.strip;

public enum EPCHeader {

    SGTIN_96(EPCFunction.SGTIN, 0x30, 96),
    SGTIN_198(EPCFunction.SGTIN, 0x36, 198)
    ;

    @Getter
    private final EPCFunction function;
    @Getter
    private final int length;

    @Getter
    private final String hex;

    @Getter final String binary;

    @Getter
    private final int value;

    EPCHeader(EPCFunction function, int hex, int length) {
        this.function = function;
        this.length = length;
        this.value = hex;
        this.hex = String.format("%02X", hex);
        this.binary = leftPad(BigInteger.valueOf(hex).toString(2), 8, '0');
    }

    public static Stream<EPCHeader> stream() {
        return Stream.of(EPCHeader.values());
    }

    public static Collection<EPCHeader> fromEPCFunction(EPCFunction func) {
        if (func == null) {
            return Collections.emptyList();
        }

        return stream().filter(epc -> epc.function == func).toList();
    }

    public static Collection<EPCHeader> fromEPCFunction(String text) {
        return fromEPCFunction(EPCFunction.fromString(text));
    }

    public static EPCHeader fromString(String text) {
        if (isBlank(text)) {
            return null;
        }

        final String cleanedUp = strip(text).replaceAll("[:\\-\\s\\t]+", "_");
        EPCHeader header =  EPCHeader.stream()
                                     .filter(fx -> equalsIgnoreCase(cleanedUp, fx.name()))
                                     .findAny()
                                     .orElse(null);

        if (header == null) {
            final String withoutSpaces = cleanedUp.replaceAll("_", "");
            if (withoutSpaces.length() > 7 && withoutSpaces.matches("[10]{8}[01]*")) {
                header = EPCHeader.stream()
                                  .filter(h -> StringUtils.equals(h.getBinary(), withoutSpaces))
                                  .findFirst()
                                  .orElseThrow(() -> new IllegalArgumentException("""
                                                                                  Text looks like a binary string \
                                                                                  but a match could be found to: '%s' \
                                                                                  from: %s""".formatted(
                                                                                  left(withoutSpaces, 8),
                                                                                  withoutSpaces)));
            }
            if (header == null && withoutSpaces.length() >= 2 &&
                left(withoutSpaces, 2).matches("[0-9A-F]{2}")) {
                header = EPCHeader.stream()
                                  .filter(h -> equalsIgnoreCase(left(withoutSpaces, 2), h.hex))
                                  .findFirst().orElse(null);
            }
        }
        if (header == null) {
            throw new IllegalArgumentException("Unable to find a supported header for string: '%s'".formatted(text));
        }
        return header;
    }

    @Override
    public String toString() { return GsonHelper.toJson(this); }

    /**
     * Fixes an issue with Java serialization that maintains an enum's singleton state in the JVM.
     *
     * @return singleton enum
     * @throws ObjectStreamException if something goes wrong while deserializing the enum
     */
    @SuppressWarnings({"java:s2221","java:S1162","java:S1108"})
    private Object readResolve() throws ObjectStreamException {
        try {
            return fromString(name());
        } catch (final IllegalArgumentException e) {
            throw new NotSerializableException(ExceptionUtils.getMessage(e));
        }
    }
}
