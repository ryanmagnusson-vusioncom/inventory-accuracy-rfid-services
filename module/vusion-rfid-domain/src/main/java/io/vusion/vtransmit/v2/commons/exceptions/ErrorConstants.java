package io.vusion.vtransmit.v2.commons.exceptions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public final class ErrorConstants {
    // common field names for marshalling
    public static final String STORE_ID = "storeId";
    public static final String PING_REQUEST = "pingRequest";
    public static final String PING_REQUESTS = "pingRequests";

    public static final String STORE_ID_REQUIRED = "STOR001";
    public static final String STORE_ID_REQUIRED_TO_ACT = "STOR001.ACT";
    public static final String STORE_NOT_FOUND = "STOR002";
    public static final String STORE_NOT_AVAILABLE = "STOR003";

    public static final String INVALID_BODY_NO_XMIT_CONFIG_FOUND = "XCFG001";
    public static final String XMIT_SYSTEM_CONFIG_IS_REQUIRED    = "XCFG002";
    public static final String XMIT_SYSTEM_CONFIG_INVALID_VENDOR = "XCFG003";
    public static final String XMIT_SYSTEM_CONFIG_INVALID_TECHNOLOGY = "XCFG004";
    public static final String XMIT_SYSTEM_CONFIG_URL_REQUIRED = "XCFG005";
    public static final String XMIT_SYSTEM_CONFIG_USERNAME_REQUIRED = "XCFG006";
    public static final String XMIT_SYSTEM_CONFIG_PASSWORD_REQUIRED = "XCFG007";

    public static final String DEVICE_ID_REQUIRED = "DVCID001";
    public static final String MISSING_DEVICE_IDS = "DVCID002";
    public static final String MISSING_DEVICE_IDS_TO_ACT = "DVCID002.ACT";
    public static final String INVALID_DEVICE_ID = "DVCID003";
    public static final String INVALID_DEVICE_ID_AT_INDEX = "DVCID003.INDEX";
    public static final String LIST_OF_DEVICE_IDS_ONLY_BLANKS = "DVCID004";
    public static final String LIST_OF_DEVICE_IDS_ONLY_BLANKS_TO_ACT = "DVCID004.ACT";
    public static final String TOO_MANY_DEVICE_IDS_ERROR = "DVCID005";
    public static final String TOO_MANY_DEVICE_IDS_ERROR_TO_ACT = "DVCID005.ACT";

    public static final String TOO_MANY_FLASH_REQUESTS = "FLASH001";
    public static final String FLASH_COLOR_IS_REQUIRED_ERROR = "FLASH002";
    public static final String FLASH_DURATION_IS_REQUIRED = "FLASH003";
    public static final String FLASH_DURATION_EXCEEDS_LIMIT = "FLASH004";
    public static final String FLASH_PATTERN_IS_REQUIRED = "FLASH005";

    public static final String XMITTER_ID_REQUIRED = "XMIT001";
    public static final String XMITTER_ID_NOT_FOUND = "XMIT002";
    public static final String XMITTER_DESCRIPTION_REQUIRED = "XMIT003";
    public static final String XMITTER_DESCRIPTION_HAS_NO_CHANGES = "XMIT004";
    public static final String XMITTER_ID_AND_DESCRIPTION_TRANSMITTER_ID_ARE_DIFFERENT = "XMIT005";

    public static final String REQUEST_IS_EMPTY = "REQ001";
    public static final String REQUEST_LIST_IS_EMPTY = "REQ002";
    public static final String REQUEST_AT_LEAST_ONE_ELEMENT_IS_NULL = "REQ003";
    public static final String PRIORITY_IS_INVALID = "REQ004";
    public static final String MAXIMUM_BODY_SIZE_EXCEEDED = "REQ005";
    public static final String REQUEST_MISSING_CORRELATION_ID = "REQ006";

}
