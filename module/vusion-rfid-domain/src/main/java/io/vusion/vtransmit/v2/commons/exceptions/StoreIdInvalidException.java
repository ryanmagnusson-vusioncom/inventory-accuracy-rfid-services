package io.vusion.vtransmit.v2.commons.exceptions;

import java.util.List;

import lombok.Getter;

@Getter
public class StoreIdInvalidException extends ValidationException {
    private final String storeId;

    protected StoreIdInvalidException() {
        super();
        this.storeId = null;
    }


    public StoreIdInvalidException(String message) {
        this(null, message);
    }

    public StoreIdInvalidException(String storeId, String message) {
        super(message);
        this.storeId = storeId;
    }

    public StoreIdInvalidException addError(String code) {
       return (StoreIdInvalidException) super.addError(code);
    }

    public StoreIdInvalidException addError(String code, List<String> parameters) {
        return (StoreIdInvalidException) super.addError(code, parameters);
    }

    @Override
    public StoreIdInvalidException addContextValue(String label, Object value) {
        return (StoreIdInvalidException) super.addContextValue(label, value);
    }

    @Override
    public StoreIdInvalidException setContextValue(String label, Object value) {
        return (StoreIdInvalidException) super.setContextValue(label, value);
    }
}
