package io.vusion.vtransmit.v2.commons.utils;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import io.vusion.secure.logs.VusionLogger;

public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {
	
	private final Logger log;
	
	
	public RequestResponseLoggingInterceptor(final VusionLogger logger) {
		// secure log don't work like slf4j, waiting for improvement
		//this.log = logger;
		log = LoggerFactory.getLogger(this.getClass());
		
	}
	
	@Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException
	{
		logRequest(request, body);
		final ClientHttpResponse response = execution.execute(request, body);
		logResponse(response);
		
		//Add optional additional headers
		//        response.getHeaders().add("headerName", "VALUE");
		
		return response;
	}
	
	private void logRequest(final HttpRequest request, final byte[] body) throws IOException
	{
		if (ApplicationContextProvider.getApplicationContext()
				.getBean("baseApplicationConfig", BaseApplicationConfig.class)
				.isVerboseLabelServerRequest())
		{
			log.info("===================================== request  begin =====================================");
			log.info("URI         : {}", request.getURI());
			log.info("Method      : {}", request.getMethod());
			log.info("Headers     : {}", request.getHeaders().toString().replaceAll("\"Basic [^\"]*\"", "\"Basic ...\""));
			log.info("Request body: {}", new String(body, "UTF-8")
					.replaceAll("<Image>([^<]{0,5})[^<]*</Image>", "<Image>$1...</Image>")
					.replaceAll("correlationId=\"([^\"]{0,5})[^\"]*\"", "correlationId=\"$1...\""));
			log.info("===================================== request  end   =====================================");
		}
	}
	
	private void logResponse(final ClientHttpResponse response) throws IOException
	{
		if (ApplicationContextProvider.getApplicationContext()
				.getBean("baseApplicationConfig", BaseApplicationConfig.class)
				.isVerboseLabelServerRequest())
		{
			log.info("===================================== response begin =====================================");
			log.info("Status code  : {}", response.getStatusCode());
			log.info("Status text  : {}", response.getStatusText());
			log.info("Headers      : {}", response.getHeaders());
			log.info("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
			log.info("===================================== response end  =====================================");
		}
	}
}
