package io.vusion.rfid.services.front.utils;

import org.apache.commons.lang3.StringUtils;

public class OpenEslFrontUtils {
	
	private static <T extends Enum<T>> T getValue(final String value, final Class<T> type, final T defaultValue) {
		if (StringUtils.isBlank(value)) {
			return defaultValue;
		}
		try {
			return Enum.valueOf(type, value.trim());
		} catch (final Exception e) {
			return defaultValue;
		}
	}
}
