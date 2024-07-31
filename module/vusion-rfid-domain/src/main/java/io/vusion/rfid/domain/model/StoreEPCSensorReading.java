package io.vusion.rfid.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class StoreEPCSensorReading extends EPCSensorReading {
    private String storeId;
}