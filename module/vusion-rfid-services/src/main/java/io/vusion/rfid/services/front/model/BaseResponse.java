package io.vusion.rfid.services.front.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import io.vusion.rfid.services.front.utils.FrontExecutionContext;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BaseResponse extends MessageResponse {
	
	private final String correlationId = FrontExecutionContext.getCorrelationId();
	private final String extClientId = FrontExecutionContext.getExtClientId();
	private Collection<String> extCorrelationIds;
	
	public BaseResponse() { }
	public BaseResponse(Collection<String> extCorrelationIds) {
		
		final List<String> nonNullExtCorrelationIds = extCorrelationIds.stream().filter(Objects::nonNull).toList();
		if (nonNullExtCorrelationIds.isEmpty()) {
			return;
		}
		this.extCorrelationIds = nonNullExtCorrelationIds.stream().filter(Objects::nonNull).toList();
	}
	
	public BaseResponse(String message) {
		super(message);
	}
	
	@Override
	public void setMessage(String message) {
		super.setMessage(message);
	}
}
