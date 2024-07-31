package io.vusion.vtransmit.v2.commons.exceptions;

import lombok.Getter;

public class StoreIsIncorrect extends RuntimeException {

    @Getter
    private EnumStatusCode code;

    public StoreIsIncorrect(final EnumStatusCode code, final String message) {
        super(message);
        this.code = code;
	}
}
