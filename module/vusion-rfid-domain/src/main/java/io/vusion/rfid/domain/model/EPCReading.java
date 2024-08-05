package io.vusion.rfid.domain.model;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.vusion.gson.utils.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static org.apache.commons.lang3.StringUtils.stripToNull;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@EqualsAndHashCode
public class EPCReading implements Serializable {

    @JsonAlias("epc")
    private String data;

    @JsonAlias({"time", "when"})
    private Instant timestamp;

    private Integer rssi;

    @Override
    public String toString() {
        return GsonHelper.getGsonNull().toJson(this);
    }

}