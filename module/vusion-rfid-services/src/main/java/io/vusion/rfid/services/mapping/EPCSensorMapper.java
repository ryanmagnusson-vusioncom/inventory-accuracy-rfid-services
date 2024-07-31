package io.vusion.rfid.services.mapping;

import io.vusion.rfid.data.model.SensorEntity;
import io.vusion.rfid.domain.model.EPCSensor;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = IGNORE)
public interface EPCSensorMapper {

    @Mapping(source="id", target="withId")
    @Mapping(source="macAddress", target="withMacAddress")
    @Mapping(source="location", target="withLocation")
    EPCSensor toEPCSensor(SensorEntity entity);

    @Mapping(source="id", target="id")
    @Mapping(source="macAddress", target="macAddress")
    @Mapping(source="location", target="location")
    @Mapping(target="storeId", ignore = true)
    @Mapping(target="creationDate", ignore = true)
    @Mapping(target="modificationDate", ignore = true)
    SensorEntity toSensorEntity(EPCSensor sensor);

    default SensorEntity toSensorEntity(String storeId, EPCSensor sensor) {
        return toSensorEntity(storeId, sensor, null);
    }

    default SensorEntity toSensorEntity(String storeId, EPCSensor sensor, Instant modificationDate) {
        final SensorEntity entity = toSensorEntity(sensor);
        if (entity != null) {
            entity.setStoreId(storeId);
            entity.setModificationDate(modificationDate);
        }
        return entity;
    }



}
