package io.vusion.vtransmit.v2.commons.exceptions;

public class StoreNotFound extends StoreIsIncorrect {
	public StoreNotFound(String storeId) {
		this(storeId, null);
	}

	public StoreNotFound(String storeId, String message) {
		super(EnumStatusCode.NOT_FOUND, String.format("Store not found '%s'", storeId) + (message != null ? String.format(". (%s)", message) : ""));
	}
}
