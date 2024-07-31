package io.vusion.rfid.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.right;

@Getter
public enum SGTINPartition implements EPCFilter {

    ZERO(0, new Segment(40, 12), new Segment(4, 1)),
    ONE(1, new Segment(37, 11), new Segment(7, 2)),
    TWO(2, new Segment(34, 10), new Segment(10, 3)),
    THREE(3, new Segment(30, 9), new Segment(14, 4)),
    FOUR(4, new Segment(27, 8), new Segment(17, 5)),
    FIVE(5, new Segment(24, 7), new Segment(20, 6)),
    SIX(6, new Segment(20, 6), new Segment(24, 7)),
    ;


    @Getter @AllArgsConstructor
    public static class Segment {
        private final int bits;
        private final int digits;
    }

    private final int value;
    private final Segment company;
    private final Segment itemReference;
    private final String binary;

    SGTINPartition(int value, Segment company, Segment itemReference) {
        this.value = value;
        this.company = company;
        this.itemReference = itemReference;
        this.binary = right(leftPad(Integer.toString(value, 2), 3, '0'), 3);
    }

    public static Stream<SGTINPartition> stream() {
        return Stream.of(SGTINPartition.values());
    }

    public static SGTINPartition fromInt(int value) {
        return SGTINPartition.stream().filter(p -> p.value == value).findFirst()
                             .orElseThrow(() -> new IllegalArgumentException("Invalid SGTIN partition value: " + value));
    }
}
