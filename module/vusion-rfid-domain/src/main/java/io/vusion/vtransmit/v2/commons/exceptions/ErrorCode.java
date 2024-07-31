package io.vusion.vtransmit.v2.commons.exceptions;

import static org.apache.commons.lang3.StringUtils.stripToNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.vusion.gson.utils.GsonHelper;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
@Getter
public class ErrorCode implements Serializable {

    @Setter(AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String id;

    @Setter
    private String messageProperty;

    @Setter(AccessLevel.PROTECTED)
    private List<String> parameters = new ArrayList<>();

    public ErrorCode() {
        this(null, new ArrayList<>());
    }

    public ErrorCode(String code) {
        this(code, new ArrayList<>());
    }

    public ErrorCode(String code, List<String> parameters) {
        this.id = stripToNull(code);
        this.messageProperty = this.id;
        this.parameters = Objects.requireNonNullElseGet(parameters, ArrayList::new);
    }

    public ErrorCode(String code, String messageProperty, List<String> parameters) {
        this.id = stripToNull(code);
        this.messageProperty = stripToNull(messageProperty);
        this.parameters = Objects.requireNonNullElseGet(parameters, ArrayList::new);
    }

    public static ErrorCode of(String id) {
        return new ErrorCode(id);
    }

    @Override
    public String toString() { return GsonHelper.toJson(ErrorMapper.toGson(this)); }

}