package io.vusion.rfid.services.mapping;

import io.vusion.rfid.data.model.EPCReadingEntity;
import io.vusion.rfid.domain.model.EPCReading;
import io.vusion.rfid.domain.model.SGTIN96;
import io.vusion.rfid.domain.model.StoreEPCSensorReading;
import io.vusion.rfid.domain.utils.EPCUtility;
import io.vusion.rfid.services.front.utils.FrontExecutionContext;
import io.vusion.rfid.services.model.response.EPCReadingResponse;
import io.vusion.rfid.services.service.EPCReadingService;
import jakarta.persistence.Column;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;

import static java.util.Objects.requireNonNullElseGet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring",
        imports = { FrontExecutionContext.class },
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = IGNORE)
public interface EPCReadingMapper {

    @Mapping(target = "withStoreId", source = "storeId")
    @Mapping(target = "withSensorMacAddress", source = "sensorMacAddress")
    @Mapping(target = "withData",  source = "data")
    @Mapping(target = "withTimestamp", source = "readingTimestamp")
    EPCReadingService.UniqueKey toEPCReadingEntityUniqueKey(EPCReadingEntity entity);

    @Mapping(target = "withStoreId", source = "storeId")
    @Mapping(target = "withSensorMacAddress", source = "macAddress")
    @Mapping(target = "withData",  source = "data")
    @Mapping(target = "withTimestamp", source = "timestamp")
    EPCReadingService.UniqueKey toEPCReadingEntityUniqueKey(StoreEPCSensorReading entity);


    @Mapping(target = "withStoreId", source = "storeId")
    @Mapping(target = "withMacAddress", source = "macAddress")
    @Mapping(target = "withData",  source = "reading.data")
    @Mapping(target = "withTimestamp", source = "reading.timestamp")
    @Mapping(target = "withRssi", source = "reading.rssi")
    StoreEPCSensorReading toStoreEPCReading(String storeId, String macAddress, EPCReading reading);

    @Mapping(target = "withStoreId", source = "reading.storeId")
    @Mapping(target = "withSensorMacAddress", source = "reading.macAddress")
    @Mapping(target = "withData",  source = "reading.data")
    @Mapping(target = "withReadingTimestamp", source = "reading.timestamp")
    @Mapping(target = "withRssi", source = "reading.rssi")
    @Mapping(target = "withCorrelationId", expression = "java(FrontExecutionContext.getCorrelationId())")
    EPCReadingEntity mapEPCReadingFieldsToEntity(StoreEPCSensorReading reading);

    @Mapping(target = "withStoreId", source = "entity.storeId")
    @Mapping(target = "withMacAddress", source = "entity.sensorMacAddress")
    @Mapping(target = "withData",  source = "entity.data")
    @Mapping(target = "withTimestamp", source = "entity.readingTimestamp")
    @Mapping(target = "withRssi", source = "entity.rssi")
    StoreEPCSensorReading toStoreEPCReading(EPCReadingEntity entity);

    @Mapping(target = "withStoreId", source = "entity.storeId")
    @Mapping(target = "withMacAddress", source = "entity.sensorMacAddress")
    @Mapping(target = "withData",  source = "entity.data")
    @Mapping(target = "withTimestamp", source = "entity.readingTimestamp")
    @Mapping(target = "withRssi", source = "entity.rssi")
    @Mapping(target = "withUpc", source = "entity.upc")
    @Mapping(target = "withGtin", source = "entity.gtin")
    @Mapping(target = "withSerial", source = "entity.serial")
    @Mapping(target = "withCorrelationId", source = "entity.correlationId")
    EPCReadingResponse toEPCReadingResponse(EPCReadingEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storeId", ignore = true)
    @Mapping(target = "sensorMacAddress", ignore = true)
    @Mapping(target = "readingTimestamp",  ignore = true)
    void updateEPCReadingEntity(EPCReadingEntity source, @MappingTarget EPCReadingEntity target);

    @Mapping(target = "storeId", ignore = true)
    @Mapping(target = "sensorMacAddress", ignore = true)
    @Mapping(target = "readingTimestamp",  ignore = true)
    @Mapping(target = "correlationId", expression = "java(FrontExecutionContext.getCorrelationId())")
    @Mapping(target = "data", source="reading.data")
    @Mapping(target = "rssi", source="reading.rssi")
    @Mapping(target = "gtin", expression="java(formatGTIN(sgtin))")
    @Mapping(target = "upc", source="sgtin.upc")
    @Mapping(target = "serial", source="sgtin.serialNumber")
    void updateEPCReadingEntity(StoreEPCSensorReading reading, SGTIN96 sgtin, @MappingTarget EPCReadingEntity target);

    default EPCReadingEntity updateEPCReadingEntity(StoreEPCSensorReading reading, EPCReadingEntity target) {
        final EPCReadingEntity targetEntity;
        if (reading == null) {
            targetEntity = target;
        } else {
            targetEntity = requireNonNullElseGet(target, () -> EPCReadingEntity.builder()
                                                                               .withStoreId(reading.getStoreId())
                                                                               .withSensorMacAddress(reading.getMacAddress())
                                                                               .withData(reading.getData())
                                                                               .withReadingTimestamp(requireNonNullElseGet(reading.getTimestamp(), Instant::now))
                                                                               .build());
            updateEPCReadingEntity(reading, toSGTIN96(reading), targetEntity);
        }
        return targetEntity;
    }

    default String formatGTIN(SGTIN96 sgtin) {
        if (sgtin == null || sgtin.getPartition() == null) {
            return null;
        }
        final String companyPrefixFormat = "%0" + sgtin.getPartition().getCompany().getDigits() + "d";
        final String companyPrefix = String.format(companyPrefixFormat, sgtin.getCompanyPrefix());

        // Pad the item reference to the specified amount
        final String itemRefFormat = "%0" + sgtin.getPartition().getItemReference().getDigits() + "d";
        final String formattedItemRef = String.format(itemRefFormat, sgtin.getItemReference());

        return "%s|%s".formatted(companyPrefix, formattedItemRef);
    }


    default SGTIN96 toSGTIN96(EPCReading reading) {
        if (reading == null || isBlank(reading.getData())) {
            return null;
        }

        return EPCUtility.parseSgtin96(reading.getData());
    }

    default EPCReadingEntity toEPCReadingEntity(StoreEPCSensorReading reading) {
        final SGTIN96 sgtin96 = toSGTIN96(reading);
        return toEPCReadingEntity(reading, sgtin96);
    }

    default EPCReadingEntity toEPCReadingEntity(StoreEPCSensorReading reading, SGTIN96 sgtin) {
        final EPCReadingEntity readingEntity = mapEPCReadingFieldsToEntity(reading);
        if (readingEntity != null && sgtin != null) {
            readingEntity.setUpc(sgtin.toUpc());
            readingEntity.setSerial(sgtin.getSerialNumber());
            readingEntity.setGtin(formatGTIN(sgtin));
            readingEntity.setCorrelationId(FrontExecutionContext.getCorrelationId());
        }
        return readingEntity;
    }

    default EPCReadingResponse toEPCReadingResponse(StoreEPCSensorReading reading) {
        if (reading == null) {
            return null;
        }

        if (reading instanceof EPCReadingResponse response) {
            return response;
        }

        final EPCReadingResponse readingResponse = new EPCReadingResponse();
        readingResponse.setData(reading.getData());
        readingResponse.setTimestamp(reading.getTimestamp());
        readingResponse.setRssi(reading.getRssi());
        readingResponse.setStoreId(reading.getStoreId());
        readingResponse.setMacAddress(reading.getMacAddress());

        final SGTIN96 sgtin96 = toSGTIN96(reading);
        if (sgtin96 != null) {
            readingResponse.setSerial(sgtin96.getSerialNumber());
            readingResponse.setUpc(sgtin96.getUpc());
            readingResponse.setGtin(formatGTIN(sgtin96));
        }
        return readingResponse;
    }

    default EPCReadingEntity toEPCReadingEntity(String correlationId, StoreEPCSensorReading reading) {
        final EPCReadingEntity readingEntity = toEPCReadingEntity(reading);
        if (readingEntity != null) {
            readingEntity.setCorrelationId(correlationId);
        }
        return readingEntity;
    }

}
