package io.vusion.rfid.domain.model;

import io.vusion.vtransmit.v2.commons.model.request.WithSensorId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static org.apache.commons.lang3.StringUtils.stripToNull;

@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class EPCSensorReading extends EPCReading {
    private String sensorId;
}
