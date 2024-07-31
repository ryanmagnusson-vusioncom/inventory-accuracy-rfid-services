package io.vusion.vtransmit.v2.commons.exceptions;

public class StoreNotAvailable extends StoreIsIncorrect {
	public StoreNotAvailable(String storeId, String message) {
		super(EnumStatusCode.NOT_AVAILABLE, String.format("Store not available: %s. %s", storeId, message));
	}
}
