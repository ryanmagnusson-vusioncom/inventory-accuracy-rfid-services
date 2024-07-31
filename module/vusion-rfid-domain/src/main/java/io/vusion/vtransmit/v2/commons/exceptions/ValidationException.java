package io.vusion.vtransmit.v2.commons.exceptions;

import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ValidationException extends ContextedRuntimeException {

    private final HttpStatus status;
    private final List<ErrorCode> errors;

    protected ValidationException() {
        this(HttpStatus.BAD_REQUEST, "", new ArrayList<>());
    }

    public ValidationException(String message) {
        this(HttpStatus.BAD_REQUEST, message, new ArrayList<>());
    }

    public ValidationException(String message, ErrorCode errorCode) {
        this(HttpStatus.BAD_REQUEST, message, Arrays.asList(errorCode));
    }

    public ValidationException(String message, String errorCode) {
        this(HttpStatus.BAD_REQUEST, message, Arrays.asList(ErrorCode.of(errorCode)));
    }

    public ValidationException(String message, List<ErrorCode> errorCodes) {
        this(HttpStatus.BAD_REQUEST, message, errorCodes);
    }

    public ValidationException(HttpStatus status, String message, List<ErrorCode> errorCodes) {
        super(message);
        this.status = status;
        this.errors = Optional.ofNullable(errorCodes).orElseGet(ArrayList::new);
    }

    public ValidationException addError(String error) {
        if (isNotBlank(error)) {
            errors.add(ErrorCode.of(error));
        }
        return this;
    }

    public ValidationException addError(String code, List<String> parameters) {
        if (isNotBlank(code)) {
            final ErrorCode msg = ErrorCode.of(code);
            for (int index = 0; index < size(parameters); index++) {
                msg.getParameters().add(parameters.get(index));
            }

            errors.add(msg);
        }
        return this;
    }

    @Override
    public ValidationException addContextValue(String label, Object value) {
        return (ValidationException) super.addContextValue(stripToEmpty(label), value);
    }

    @Override
    public ValidationException setContextValue(String label, Object value) {
        return (ValidationException) super.setContextValue(stripToEmpty(label), value);
    }
}
