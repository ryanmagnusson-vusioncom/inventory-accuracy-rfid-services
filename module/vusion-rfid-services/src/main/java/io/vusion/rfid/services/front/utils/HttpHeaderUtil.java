package io.vusion.rfid.services.front.utils;

import org.springframework.http.HttpHeaders;

public class HttpHeaderUtil {
	
	private HttpHeaderUtil() {
	}
	
	public static String getHeader(final HttpHeaders headers, final String key, final String defaultValue) {
		if (headers == null || !headers.containsKey(key)) {
			return defaultValue;
		}
		return headers.getFirst(key);
	}
	
	public static String getHeader(final HttpHeaders headers, final String key) {
		if (headers == null || !headers.containsKey(key)) {
			return null;
		}
		return headers.getFirst(key);
	}
	
	public static String getValidHeader(final HttpHeaders headers, final String key,
			final String pattern, final String defaultValue) {
		if (headers == null || !headers.containsKey(key)) {
			return defaultValue;
		}
		final String headerValue = headers.getFirst(key);
		if (headerValue.matches(pattern)) {
			return headerValue;
		}
		return defaultValue;
	}

	public static Boolean getBooleanHeader(final HttpHeaders headers, final String key) {
		if (headers == null || !headers.containsKey(key)) {
			return null;
		}
		return Boolean.parseBoolean(headers.getFirst(key));
	}

}
