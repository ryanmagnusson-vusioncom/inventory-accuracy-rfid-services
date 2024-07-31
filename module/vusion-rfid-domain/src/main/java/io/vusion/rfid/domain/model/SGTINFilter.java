package io.vusion.rfid.domain.model;

import lombok.Getter;

import java.math.BigInteger;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.right;

@Getter
public enum SGTINFilter implements EPCFilter {

    ALL_OTHERS(0),
    POINT_OF_SALE_TRADE_ITEM(1),
    FULL_CASE_TRANSPORT(2),
    RESERVED_3(3),
    INNER_PACK_TRADE_ITEM_GROUPING(4),
    RESERVED_5(5),
    UNIT_LOAD(6),
    UNIT_INSIDE_TRADE_ITEM_NOT_FOR_INDIVIDUAL_SALE(7)
    ;

    private final int value;
    private final String binary;

    SGTINFilter(int value) {
        this.value = value;
        this.binary = right(leftPad(BigInteger.valueOf(value).toString(2), 4, '0'), 3);
    }

    public static Stream<SGTINFilter> stream() {
        return Stream.of(SGTINFilter.values());
    }

    public static SGTINFilter fromInt(int value) {
        return SGTINFilter.stream()
                          .filter(p -> p.value == value)
                          .findFirst()
                          .orElseThrow(() -> new IllegalArgumentException("Invalid SGTIN Filter value: " + value));
    }
}
