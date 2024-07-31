package io.vusion.vtransmit.v2.commons.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.vusion.gson.utils.GsonHelper;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder(toBuilder = true, setterPrefix = "with")
public class ErrorDetail {
    @Setter(AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @EqualsAndHashCode.Include
    private String id;

    @Setter(AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @EqualsAndHashCode.Include
    private String message;

    @Override
    public String toString() { return GsonHelper.toJson(ErrorMapper.toGson(this)); }

}
