package io.vusion.vtransmit.v2.commons.utils;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import io.vusion.gson.utils.GsonHelper;

public class RequestUtils {
	
	public static final String EXTERNAL_ID_HEADER_NAME = "swexternalid";
	public static final String UUID_REGEX_MATCH = "^[a-zA-Z0-9_-]{16,128}$";
	public static final String CORRELATION_ID_HEADER_NAME = "correlationId";
	
	public static String sanitize(String text) {
		return sanitize(text, false);
	}
	
	public static String sanitize(String text, boolean preserveWhitespace) {
		if (text == null) {
			return null;
		}
		
		final String needsSanitizing = preserveWhitespace ? text : stripToEmpty(text);
		if (needsSanitizing.isEmpty()) {
			return "";
		}
		
		return Jsoup.clean(StringEscapeUtils.escapeHtml4(needsSanitizing), Safelist.basic());
	}
	
	public static <T> T sanitize(T original) {
		if (original == null) {
			return null;
		}
		
		final String json = Jsoup.clean(
				StringEscapeUtils.escapeJson(
						GsonHelper.toJson(original)), Safelist.basic());
		//noinspection unchecked
		return (T) GsonHelper.fromJson(json, original.getClass());
	}
	
	public static List<String> findAllHeaders(String key, MultiValuedMap<String, String> map) {
		if (map == null || map.isEmpty()) {
			return Collections.emptyList();
		}
		
		final String stripped = stripToEmpty(key);
		
		return map.entries()
				.stream()
				.filter(entry -> isBlank(stripped) ? isBlank(entry.getKey())
						: equalsIgnoreCase(stripped, entry.getKey()))
				.map(Map.Entry::getValue)
				.toList();
	}
	
	
	public static Optional<Object> findHeader(String key, Map<String, Object> map) {
		final String stripped = stripToEmpty(key);
		return map.entrySet()
				.stream()
				.filter(entry -> equalsIgnoreCase(stripped, entry.getKey()))
				.map(Map.Entry::getValue)
				.findFirst();
	}
	
	// public static Optional<String> getCorrelationId(Map<String,String> headers) {
	// return getCorrelationId(headers, "no-correlation-id");
	// }
	
	// public static Optional<String> getCorrelationId(Map<String,String> headers, String defaultValue) {
	// if (MapUtils.isEmpty(headers)) {
	// return Optional.empty();
	// }
	//
	// Optional<String> value = findHeader(VtransmitV2Utils.CORRELATION_ID_KEY, headers);
	// if (value.isEmpty() && ExecutionContext.isInitialized()) {
	// value = Optional.ofNullable(ExecutionContext.getCorrelationId());
	// }
	//
	// return value.or(() -> Optional.ofNullable(defaultValue));
	// }
	
	// public static Optional<String> getExternalId(Map<String, String> headers) {
	// return getExternalId(headers, null);
	// }
	//
	// public static Optional<String> getExternalId(Map<String, String> headers, String defaultValue) {
	// if (MapUtils.isEmpty(headers)) {
	// return Optional.ofNullable(defaultValue);
	// }
	//
	// Optional<String> value = findHeader(VtransmitV2Utils.EXTERNAL_ID_KEY, headers);
	// // if no header was found, or the found value was blank
	// // try to retrieve it from the ExecutionContext
	// if ((value.isEmpty() || isBlank(value.get())) && ExecutionContext.isInitialized()) {
	// value = Optional.ofNullable(ExecutionContext.getExternalId());
	// }
	//
	// // if we have a value, it is not blank, and it matches the regex pattern --- use it
	// if (value.isPresent() && isNotBlank(value.get()) && value.get().matches(UUID_REGEX_MATCH)) {
	// return value;
	// }
	//
	// return Optional.ofNullable(defaultValue);
	// }
	//
	// public static Optional<String> getExtClientId(Map<String, String> headers) {
	// return getExtClientId(headers, null);
	// }
	//
	// public static Optional<String> getExtClientId(Map<String, String> headers, String defaultValue) {
	// if (MapUtils.isEmpty(headers)) {
	// return Optional.ofNullable(defaultValue);
	// }
	//
	// Optional<String> value = findHeader(VtransmitV2Utils.EXT_CLIENT_ID_KEY, headers);
	// // if no header was found, or the found value was blank
	// // try to retrieve it from the ExecutionContext
	// if ((value.isEmpty() || isBlank(value.get())) && ExecutionContext.isInitialized()) {
	// value = Optional.ofNullable(ExecutionContext.getExtClientId());
	// }
	//
	// // if we have a value, return it.
	// if (value.isPresent() && isNotBlank(value.get())) {
	// return value;
	// }
	//
	// return Optional.ofNullable(defaultValue);
	// }
}
