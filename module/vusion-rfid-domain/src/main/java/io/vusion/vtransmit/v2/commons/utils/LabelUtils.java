package io.vusion.vtransmit.v2.commons.utils;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class LabelUtils {
	
	private static final String BLE_VALID_PATTERN = "^("
			// BLE Gen 2
			+ "([2-5][GHJMPQRVWXghjmpqrvwx]-[2-9CFGHJMPQRVWXcfghjmpqrvwx]{6})|(([GHJMPQRVWXghjmpqrv][2-9CFGHJMPQRVWXcfghjmpqrvwx]|[6-9CFcf][GHJMPQRVWXghjmpqrvwx])[2-9CFGHJMPQRVWXcfghjmpqrvwx]-[2-9CFGHJMPQRVWXcfghjmpqrvwx]{5})|([wx][2-9CFGHJMPQRVWXcfghjmpqrvwx]{3}-[2-9CFGHJMPQRVWXcfghjmpqrvwx]{4})"
			// BLE Gen 3
			+ "|(([0-3][GHJKMNPQRSTVWXYZ]-[0-9ABCDEFGHJKMNPQRSTVWXYZ]{6})|(([4-9ABCDEF][GHJKMNPQRSTVWXYZ]|[GHJKMNPQRSTVWX][0-9ABCDEFGHJKMNPQRSTVWXYZ])[0-9ABCDEFGHJKMNPQRSTVWXYZ]-[0-9ABCDEFGHJKMNPQRSTVWXYZ]{5})|([YZ][0-9ABCDEFGHJKMNPQRSTVWXYZ]{3}-[0-9ABCDEFGHJKMNPQRSTVWXYZ]{4}))"
			+ ")$";
	private static final String BLE_LABEL_START_WITH_80_REGEX = "^80[0-9A-F]{6}$";
	
	public static String computeShardFromLabelId(final String labelId) {
		return labelId.substring(labelId.length() - 1).toLowerCase();
	}
	
	public static boolean isValidRail(final String labelId) {
		return labelId != null && (labelId.startsWith("80") || labelId.startsWith("x222-") || labelId.startsWith("Z002-") || labelId.startsWith("Z003-"));
	}
	
	public static boolean isValidBle(final String labelId) {
		return isNotBlank(labelId) &&
				(labelId.matches(BLE_LABEL_START_WITH_80_REGEX) ||
						labelId.matches(BLE_VALID_PATTERN));
	}
	
	public static boolean isNotValidBle(final String labelId) {
		return !isValidBle(labelId);
	}
	
	public static boolean isValidGen2AndGen3(final String labelId) {
		return isNotBlank(labelId) && labelId.matches(BLE_VALID_PATTERN);
	}
	
	public static boolean isNotValidGen2AndGen3(final String labelId) { return !isValidGen2AndGen3(labelId); }
	
	public static boolean isValidMacAddress(String mac) {
		if (mac.length() == 12) {
			final String[] results = mac.split("(?<=\\G.{" + 2 + "})");
			mac = String.join(":", results);
		}
		return mac.matches("^([0-9A-Fa-f]{2}[:]){5}([0-9A-Fa-f]{2})$");
	}
	
	public static boolean isNotValidMacAddress(String mac) {
		return isBlank(mac) || !isValidMacAddress(mac);
	}
	
	public static String calculBasicLEDValue(String color, String pattern, int duration, String patternType) {
		final int repeatCount = Math.max(1, duration / getFlashCycleDuration(pattern));
		String hfPattern;
		String blePattern = null;
		switch (pattern) {
			case "EACH_1_SECOND" -> {
				hfPattern = "PATTERN_2";
				blePattern = "PATTERN_3";
			}
			case "EACH_2_SECONDS" -> {
				hfPattern = "PATTERN_1";
				blePattern = "PATTERN_2";
			}
			case "EACH_4_SECONDS" -> {
				hfPattern = "PATTERN_0";
				blePattern = "PATTERN_1";
			}
			case "FLASH_4_TIMES" -> {
				hfPattern = "PATTERN_4";
				blePattern = "PATTERN_0";
			}
			default -> hfPattern = "PATTERN_DEFAULT";
		}
		if (repeatCount <= 30) {
			return patternType.equals("BLE") ? String.format("<BasicLED color=\"%s\" pattern=\"%s\" repeatCount=\"%d\" />", color, blePattern, Math.ceil(repeatCount)) : String.format("<BasicLED color=\"%s\" pattern=\"%s\" repeatCount=\"%d\" />", color, hfPattern, Math.ceil(repeatCount));
		} else {
			int durationInMinutes = (int) Math.floor(duration / 60);
			if (duration % 60 != 0) {
				durationInMinutes = durationInMinutes + 1;
			}
			return patternType.equals("BLE") ? String.format("<BasicLED color=\"%s\" pattern=\"%s\" durationInMinutes=\"%d\" />", color, blePattern, durationInMinutes) : String.format("<BasicLED color=\"%s\" pattern=\"%s\" durationInMinutes=\"%d\" />", color, hfPattern, durationInMinutes);
		}
	}
	
	private static int getFlashCycleDuration(String pattern) {
		return switch (pattern) {
			case "FLASH_4_TIMES" -> 1;
			case "EACH_1_SECOND" -> 1;
			case "EACH_2_SECONDS" -> 2;
			case "EACH_4_SECONDS" -> 4;
			default -> 1;
		};
	}
	
	public static boolean isValidHf(final String labelId) {
		return labelId.matches("^[8-F][0-9A-F]{7}$") && !labelId.matches("^[8-F][0-9A-F]{5}FF$");
		
	}
	
	/**
	 * @param labelId
	 * @return true if label or rail
	 */
	public static boolean isValidDeviceId(final String labelId) {
		return isValidBle(labelId) || isValidHf(labelId);
	}
	
	/**
	 * @param labelId
	 * @return true if label but a rail
	 */
	public static boolean isValidLabelId(final String labelId) {
		return (isValidBle(labelId) && !isValidRail(labelId)) || isValidHf(labelId);
	}
	
}
