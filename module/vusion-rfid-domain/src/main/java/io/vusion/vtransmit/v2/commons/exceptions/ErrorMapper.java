package io.vusion.vtransmit.v2.commons.exceptions;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.getIfBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.left;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import io.vusion.secure.logs.VusionLogger;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ErrorMapper {
    private static final VusionLogger LOGGER        = VusionLogger.getLogger(ErrorMapper.class);
    private static final NumberFormat intFormat     = new DecimalFormat("0");
    private static final NumberFormat decimalFormat = new DecimalFormat("0.000000");

    public static ErrorMapper INSTANCE;

    @Autowired
    private MessageSource errorMessageSource;


    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        INSTANCE = this;
    }

    @AfterMapping
    protected void resolveErrorCodeId(ErrorDetail detail) {
        if (detail != null && isNotBlank(detail.getId())) {
            if (endsWithIgnoreCase(detail.getId(), ".ACT")) {
                detail.setId(left(detail.getId(), detail.getId().length() - 4));
            }
        }
    }

    @Mapping(target="withMessage", expression = "java(toFormattedMessage(code))")
    @Mapping(target="withId", source="id")
    public abstract ErrorDetail toErrorDetail(ErrorCode code);

    public static String formatUsingMessageSource(MessageSource messageSource,
                                                  String messageProperty,
                                                  List<String> parameters,
                                                  boolean rethrowOnError) {
        Objects.requireNonNull(messageSource,
                               () -> String.format("""
                                                   The message source is required to properly lookup the property: %s \
                                                   and parameters: %s""",
                                                   messageProperty,
                                                   parameters == null
                                                               ? "null"
                                                               : String.format("[%s]",
                                                                               join(parameters, ", "))));

        final Object[] paramArray = isEmpty(parameters)
                                  ? null
                                  : parameters.toArray();
        try {
            return messageSource.getMessage(messageProperty, paramArray, Locale.ENGLISH);
        } catch (NoSuchMessageException e) {
            LOGGER.debug(() -> """
                               NoSuchMessageExection caught trying to resolve key: %s \
                               with messageSource using parameters: [%s]""".formatted(
                               messageProperty, messageSource, join(parameters, ", ")), e);
            if (rethrowOnError) {
                throw e;
            }
            /* returning empty string to allow further processing to happen if the code is in another usable format */
            return "";
        }
    }

    public String formatUsingMessageSource(String messageProperty,
                                            List<String> parameters) {
        return formatUsingMessageSource(getErrorMessageSource(), messageProperty, parameters, false);
    }

    public String formatPlainText(String messageProperty, List<String> parameters) {
        if (isBlank(messageProperty)) {
            return isEmpty(parameters) ? "" : String.format("[%s]", join(parameters, ", "));
        }

        if (isEmpty(parameters)) {
            return messageProperty;
        }

        return String.format("%s: [%s]", messageProperty, join(parameters, ", "));
    }

    public String formatUsingStringFormat(String messageProperty, List<String> parameters) {
        if (isBlank(messageProperty)) {
            return "";
        }

        if (isEmpty(parameters)) {
            return messageProperty;
        }

        if (!contains(messageProperty, "%")) {
            LOGGER.debug("Returning an empty string, no %% placeholders found in: %s".formatted(messageProperty));
            return "";
        }

        try {
            return String.format(messageProperty, parameters.toArray());
        } catch (IllegalFormatException e) {
            LOGGER.debug(() -> """
                               IllegalFormatException caught while trying to format messageProperty using String.format
                               \t[messageProperty=%s]
                               \t[parameters=(%s)]
                               \t[error=%s]""".formatted(messageProperty,
                                                         join(parameters, ", "),
                                                         ExceptionUtils.getMessage(e)),
                         e);

            return "";
        }
    }

    public String formatUsingMessageFormat(String messageProperty, List<String> parameters) {
        if (isBlank(messageProperty)) {
            return "";
        }

        if (isEmpty(parameters)) {
            return messageProperty;
        }

        if (!contains(messageProperty, "{") && !contains(messageProperty, "}")) {
            LOGGER.debug("Returning an empty string, no {{ }} placeholders found in: %s".formatted(messageProperty));
            return "";
        }

        try {
            return MessageFormat.format(messageProperty, parameters.toArray());
        } catch (IllegalArgumentException | NullPointerException e) {
            LOGGER.debug(() -> """
                               Exception caught while trying to format messageProperty using MessageFormat.format
                               \t[messageProperty=%s]
                               \t[parameters=(%s)]
                               \t[error=%s]""".formatted(messageProperty,
                                                         join(parameters, ", "),
                                                         ExceptionUtils.getMessage(e)),
                         e);
            return "";
        }
    }


    public String formatErrorCode(String messageProperty, List<String> parameters) {
        if (isBlank(messageProperty)) {
            return isEmpty(parameters) ? "" : String.format("[%s]", join(parameters, ", "));
        }

        return getIfBlank(formatUsingMessageSource(messageProperty, parameters),
                          () -> getIfBlank(formatUsingMessageFormat(messageProperty, parameters),
                                           () -> getIfBlank(formatUsingStringFormat(messageProperty, parameters),
                                                            () -> formatPlainText(messageProperty, parameters))));
    }

    public String toFormattedMessage(ErrorCode code) {
        if (code == null) {
            return "";
        }

        final String key = getIfBlank(code.getMessageProperty(), code::getId);
        if (isBlank(key)) {
            return formatPlainText(key, code.getParameters());
        }

        return formatErrorCode(key, code.getParameters());
    }

    //// GSON mapping methods
    public static JsonElement toGson(ErrorDetail detail) {
        if (detail == null) {
            return JsonNull.INSTANCE;
        }

        JsonObject json = new JsonObject();
        if (isNotBlank(detail.getId())) {
            json.addProperty("id", detail.getId());
        }

        if (isNotBlank(detail.getMessage())) {
            json.addProperty("message", detail.getMessage());
        }
        return json;
    }

    public static JsonElement toGson(ErrorCode code) {
        if (code == null) {
            return JsonNull.INSTANCE;
        }

        JsonObject json = new JsonObject();
        if (isNotBlank(code.getId())) {
            json.addProperty("id", code.getId());
        }

        if (isNotBlank(code.getMessageProperty()) && !StringUtils.equals(code.getId(), code.getMessageProperty())) {
            json.addProperty("messageProperty", code.getMessageProperty());
        }

        final JsonArray jsArray = new JsonArray();
        if (isNotEmpty(code.getParameters())) {
            /// Using this verbose index access to make sure any null refs are not ignored
            /// Streams ignores them, not sure about iterators and "for x:y" loops.
            for (int index = 0; index < code.getParameters().size(); index++) {
                final JsonElement element = Optional.ofNullable(code.getParameters().get(index))
                                                    .map(JsonPrimitive::new)
                                                    .map(JsonElement.class::cast)
                                                    .orElse(JsonNull.INSTANCE);
                jsArray.add(element);
            }
        }

        return json;
    }

}
