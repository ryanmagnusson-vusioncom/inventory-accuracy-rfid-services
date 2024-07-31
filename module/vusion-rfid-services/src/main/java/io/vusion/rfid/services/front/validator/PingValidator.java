package io.vusion.rfid.services.front.validator;

import org.springframework.stereotype.Service;

import io.vusion.vtransmit.v2.commons.model.Store;
import io.vusion.vtransmit.v2.commons.model.request.PingRequest;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PingValidator extends RequestValidator<PingRequest> {
	public static final int MAXIMUM_DEVICES_PING_BY_ONE_REQUEST = 1000;
	
	@Override
	protected int getMaxRequestElements() {
		return MAXIMUM_DEVICES_PING_BY_ONE_REQUEST;
	}
	
	@Override
	protected PingRequest validate(Store store, PingRequest request) {
		// Nothing special to validate
		return request;
	}
	
}
