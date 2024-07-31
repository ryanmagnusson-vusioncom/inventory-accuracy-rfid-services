package io.vusion.rfid.services.front.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends HttpResultException {
	
	private static final long serialVersionUID = 1L;

	public NotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}
	
}
