package io.vusion.vtransmit.v2.commons.servicebus;

import org.springframework.stereotype.Service;

import io.vusion.secure.logs.ILogger;
import io.vusion.secure.logs.VusionLogger;
import io.vusion.servicebus.AbstractServiceBusManager;

@Service
public class ServiceBusManager extends AbstractServiceBusManager {
	private final VusionLogger logger = VusionLogger.getLogger(ServiceBusManager.class);
	
	@Override
	protected ILogger getLogger() {
		return logger.getSecureLogger();
	}
	
}
