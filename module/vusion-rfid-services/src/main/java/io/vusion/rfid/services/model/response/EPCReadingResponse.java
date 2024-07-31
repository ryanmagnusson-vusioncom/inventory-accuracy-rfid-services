package io.vusion.rfid.services.model.response;

import io.vusion.rfid.domain.model.EPCReading;
import io.vusion.rfid.domain.model.StoreEPCSensorReading;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;

@NoArgsConstructor
@Getter @Setter
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class EPCReadingResponse extends StoreEPCSensorReading {

    private String correlationId;
    private String gtin;
    private String upc;
    private BigInteger serial;
}
