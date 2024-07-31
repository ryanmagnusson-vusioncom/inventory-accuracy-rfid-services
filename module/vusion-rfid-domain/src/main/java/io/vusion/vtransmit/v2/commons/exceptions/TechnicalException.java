package io.vusion.vtransmit.v2.commons.exceptions;

import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {

	private final EnumStatusCode code;

	public TechnicalException(final EnumStatusCode code, final String message) {
        super(message);
		this.code = code;
    }
}
