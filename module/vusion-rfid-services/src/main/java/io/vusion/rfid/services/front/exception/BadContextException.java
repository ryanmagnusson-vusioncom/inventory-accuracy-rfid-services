package io.vusion.rfid.services.front.exception;

import io.vusion.vtransmit.v2.commons.exceptions.EnumStatusCode;

public class BadContextException extends RuntimeException {

    private final EnumStatusCode code;

    public BadContextException(final EnumStatusCode code) {
        super();
        this.code = code;
    }

    public BadContextException(final EnumStatusCode code, final String message) {

        super(message);
        this.code = code;
    }

    public EnumStatusCode getStatusCode() {
        return code;
    }
}