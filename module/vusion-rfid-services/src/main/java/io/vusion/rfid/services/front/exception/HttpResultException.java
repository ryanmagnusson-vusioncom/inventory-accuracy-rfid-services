package io.vusion.rfid.services.front.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class HttpResultException extends RuntimeException {
	protected final HttpStatus status;
	
	public HttpResultException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}
}
