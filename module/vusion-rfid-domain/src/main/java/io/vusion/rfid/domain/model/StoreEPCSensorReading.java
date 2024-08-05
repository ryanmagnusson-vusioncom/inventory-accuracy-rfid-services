package io.vusion.rfid.domain.model;

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
@EqualsAndHashCode(callSuper = true)
public class StoreEPCSensorReading extends EPCSensorReading {
    private String storeId;

}