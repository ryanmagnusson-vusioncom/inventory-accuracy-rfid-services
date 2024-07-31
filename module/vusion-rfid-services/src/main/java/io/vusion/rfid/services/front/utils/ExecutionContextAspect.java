package io.vusion.rfid.services.front.utils;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.vusion.secure.logs.VusionLogger;
import io.vusion.vtransmit.v2.commons.annotation.VTransmitContext;
import io.vusion.vtransmit.v2.commons.exceptions.EnumStatusCode;
import io.vusion.rfid.services.front.exception.BadContextException;
import io.vusion.vtransmit.v2.commons.model.EnumEventType;
import io.vusion.vtransmit.v2.commons.model.EnumPriority;
import io.vusion.vtransmit.v2.commons.utils.BaseApplicationConfig;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class ExecutionContextAspect {
	@Autowired
	private BaseApplicationConfig applicationConfig;
	
	private final VusionLogger logger = VusionLogger.getLogger(ExecutionContextAspect.class);
	
	@Before("@annotation(vtransmitContext)")
	public void initExecutionContext(JoinPoint joinPoint,
			VTransmitContext vtransmitContext) {
		final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		final HttpHeaders headers = getHeaders(attributes);
		final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		final String priorityParam = request.getParameter("priority");
		EnumPriority priority;
		try {
			priority = priorityParam != null && !priorityParam.isEmpty() ? EnumPriority.valueOf(priorityParam)
					: vtransmitContext.priority();
		} catch (final IllegalArgumentException ex) {
			throw new BadContextException(EnumStatusCode.BAD_REQUEST, "Invalid priority value: " + priorityParam);
		}
		
		
		final EnumEventType eventType = vtransmitContext.eventType();
		final String storeId = getStoreId(joinPoint);
		if (storeId == null || storeId.isEmpty()) {
			FrontExecutionContext.initContextNoStore(priority, eventType, headers);
		} else {
			FrontExecutionContext.initContext(storeId, priority, eventType, headers);
		}
		
		if (applicationConfig.isVerboseIncomingRequest()) {
			final Map<String, String> headersToLog = new HashMap<>(headers.toSingleValueMap());
			headersToLog.remove("Ocp-Apim-Subscription-Key");
			headersToLog.remove("ocp-apim-subscription-key");
			
			logger.info("Receving: " + request.getMethod() + " " + request.getRequestURL().toString() + ". Headers: " + headersToLog);
		}
	}
	
	private HttpHeaders getHeaders(ServletRequestAttributes attributes) {
		final HttpHeaders headers = new HttpHeaders();
		if (attributes != null) {
			attributes.getRequest().getHeaderNames().asIterator().forEachRemaining(
					headerName -> headers.add(headerName, attributes.getRequest().getHeader(headerName)));
		}
		return headers;
	}
	
	private String getStoreId(JoinPoint joinPoint) {
		final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		final String[] parameterNames = signature.getParameterNames();
		final Object[] parameterValues = joinPoint.getArgs();
		for (int i = 0; i < parameterNames.length; i++) {
			if ("storeId".equals(parameterNames[i])) {
				return (String) parameterValues[i];
			}
		}
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
				.getParameter("storeId");
	}
}
