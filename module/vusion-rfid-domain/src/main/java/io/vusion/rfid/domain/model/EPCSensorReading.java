package io.vusion.rfid.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter @Setter
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class EPCSensorReading extends EPCReading {
    private String macAddress;
}
