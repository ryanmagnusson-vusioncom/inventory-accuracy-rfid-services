package io.vusion.rfid.domain.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.Instant;

@Getter @Setter
public class SGTINReading {

    private String storeId;
    private String sensorMacAddress;

    @JsonAlias("epc")
    private String  data;
    @JsonAlias({"time", "when"})
    private Instant timestamp;
    private Integer rssi;

    private String     gtin;
    private BigInteger serial;

}
