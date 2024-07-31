package io.vusion.rfid.services.front.exception;

import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.secure.logs.VusionLogger;
import io.vusion.vtransmit.v2.commons.exceptions.ErrorMapper;
//import io.vusion.vtransmit.v2.commons.exceptions.FunctionalException;
import io.vusion.vtransmit.v2.commons.exceptions.StoreNotAvailable;
import io.vusion.vtransmit.v2.commons.exceptions.StoreNotFound;

import io.vusion.vtransmit.v2.commons.exceptions.ValidationException;
import io.vusion.rfid.services.front.model.MessageResponse;
import io.vusion.rfid.services.front.utils.FrontExecutionContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class HttpExceptionHandler {

    private static final VusionLogger LOGGER = VusionLogger.getLogger(HttpExceptionHandler.class);
    private final MessageSource errorMessages;
    private final ErrorMapper errorMapper;

    @ExceptionHandler(value = HttpResultException.class)
    public ResponseEntity<Object> exception(HttpResultException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.status);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<Object> exception(HttpMessageNotReadableException exception) {
        return new ResponseEntity<>(new MessageResponse("Invalid or empty body "), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ StoreNotAvailable.class })
    public ResponseEntity<Object> handleStoreNotAvailable(Exception ex, HttpServletResponse response) {
        return new ResponseEntity<>(new MessageResponse(ex.getMessage()), null, 552);
    }

    @ExceptionHandler({ StoreNotFound.class })
    public ResponseEntity<Object> handleStoreNotFound(Exception ex, HttpServletResponse response) {
        return new ResponseEntity<>(new MessageResponse(ex.getMessage()), null, 552);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Object> exception(RuntimeException exception) {
        LOGGER.warn("Internal server Error", exception);

        return new ResponseEntity<>(new MessageResponse("Internal server Error. Please, contact your support"),
                HttpStatus.INTERNAL_SERVER_ERROR);

    }

    public Map<String, String> mapContext(FrontExecutionContext.Context context) {
        if (context == null) {
            return emptyMap();
        }
        // no externalId, due it is not documented in our public API
        final Map<String, String> map = new LinkedHashMap<>();
        if (isNotBlank(context.getCorrelationId())) {
            map.put("correlationId", FrontExecutionContext.getCorrelationId());
        }
        if (isNotBlank(context.getExtClientId())) {
            map.put("extClientId", FrontExecutionContext.getExtClientId());
        }
        if (isNotBlank(context.getStoreId())) {
            map.put("storeId", FrontExecutionContext.getStoreId());
        }
        if (context.getEventType() != null) {
            map.put("eventType", FrontExecutionContext.getEventType().name());
        }

        return map;
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException error) {

        final ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();

        final HttpStatus status = Optional.ofNullable(error.getStatus()).orElse(HttpStatus.BAD_REQUEST);
        final HttpServletRequest request = Optional.ofNullable(servletRequestAttributes)
                .map(ServletRequestAttributes::getRequest)
                .orElse(null);

        final String httpMethod = Optional.ofNullable(request)
                .map(HttpServletRequest::getMethod)
                .map(StringUtils::upperCase)
                .orElse(null);

        final String relativePath = Optional.ofNullable(request)
                .map(HttpServletRequest::getRequestURI)
                .orElse(null);

        final String explanation = isBlank(error.getMessage())
                ? "ValidationException thrown calling HTTP %s service: %s".formatted(httpMethod,
                        relativePath)
                : ExceptionUtils.getMessage(error);

        final List<Object> mappedErrorDetails = error.getErrors()
                .stream()
                .map(errorMapper::toErrorDetail)
                .map(detail -> isBlank(detail.getMessage())
                        ? detail.getId()
                        : Map.of(detail.getId(), detail.getMessage()))
                .toList();

        final Map<String, Object> entity = new LinkedHashMap<>();
        // see ProblemResponse
        entity.put("type", "about:blank");

        entity.put("title", "Validation Errors");
        entity.put("status", status.value());
        entity.put("statusName", status.name());
        entity.put("message", explanation);

        if (isNotBlank(httpMethod)) {
            entity.put("httpMethod", httpMethod);
        }

        if (isNotEmpty(mappedErrorDetails)) {
            entity.put("errors", mappedErrorDetails);
        }

        final Map<String, String> context = mapContext(FrontExecutionContext.getContext());
        if (MapUtils.isNotEmpty(context)) {
            entity.put("context", context);
        }

        LOGGER.error(GsonHelper.toJson(entity));

        return new ResponseEntity<>(entity, status);
    }
}
