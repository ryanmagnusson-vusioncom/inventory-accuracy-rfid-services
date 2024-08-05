package io.vusion.rfid.services.mapping;

import io.vusion.rfid.data.model.EPCReadingEntity;
import io.vusion.rfid.domain.model.EPCReading;
import io.vusion.rfid.domain.model.SGTIN96;
import io.vusion.rfid.domain.model.StoreEPCSensorReading;
import io.vusion.rfid.domain.utils.EPCUtility;
import io.vusion.rfid.services.front.utils.FrontExecutionContext;
import io.vusion.rfid.services.model.response.EPCReadingResponse;
import io.vusion.rfid.services.service.EPCReadingService;
import lombok.Builder;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNullElseGet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring",
        imports = { FrontExecutionContext.class },
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = IGNORE)
public interface EPCReadingMapper {

//    @Mapping(target = "withStoreId", source = "storeId")
//    @Mapping(target = "withSensorId", source = "sensorId")
//    @Mapping(target = "withData",  source = "data")
//    @Mapping(target = "withReadingTimestamp", source = "readingTimestamp")
//    EPCReadingEntity.UniqueKey toEPCReadingEntityUniqueKey(EPCReadingEntity entity);
//
//    @Mapping(target = "withStoreId", source = "storeId")
//    @Mapping(target = "withSensorId", source = "sensorId")
//    @Mapping(target = "withData",  source = "data")
//    @Mapping(target = "withReadingTimestamp", source = "timestamp")
//    EPCReadingEntity.UniqueKey toEPCReadingEntityUniqueKey(StoreEPCSensorReading entity);


    @Mapping(target = "withStoreId", source = "storeId")
    @Mapping(target = "withSensorId", source = "macAddress")
    @Mapping(target = "withData",  source = "reading.data")
    @Mapping(target = "withTimestamp", source = "reading.timestamp")
    @Mapping(target = "withRssi", source = "reading.rssi")
    StoreEPCSensorReading toStoreEPCReading(String storeId, String macAddress, EPCReading reading);

    @Mapping(target = "withStoreId", source = "reading.storeId")
    @Mapping(target = "withSensorId", source = "reading.sensorId")
    @Mapping(target = "withData",  source = "reading.data")
    @Mapping(target = "withReadingTimestamp", source = "reading.timestamp")
    @Mapping(target = "withRssi", source = "reading.rssi")
    @Mapping(target = "withCorrelationId", expression = "java(FrontExecutionContext.getCorrelationId())")
    EPCReadingEntity mapEPCReadingFieldsToEntity(StoreEPCSensorReading reading);

    @Mapping(target = "withStoreId", source = "entity.storeId")
    @Mapping(target = "withSensorId", source = "entity.sensorId")
    @Mapping(target = "withData",  source = "entity.data")
    @Mapping(target = "withTimestamp", source = "entity.readingTimestamp")
    @Mapping(target = "withRssi", source = "entity.rssi")
    StoreEPCSensorReading toStoreEPCReading(EPCReadingEntity entity);

    @Mapping(target = "serial", source="sgtin.serialNumber")
    @Mapping(target = "upc", source = "sgtin.upc")
    @Mapping(target = "gtin", expression = "java(formatGTIN(sgtin))")
    void updateSGTINInfo(SGTIN96 sgtin, @MappingTarget EPCReadingEntity entity);

    default EPCReadingEntity toEPCReadingEntity(StoreEPCSensorReading reading, SGTIN96 sgtin) {
        final EPCReadingEntity readingEntity = mapEPCReadingFieldsToEntity(reading);
        updateSGTINInfo(sgtin, readingEntity);
        return readingEntity;
    }

    @Mapping(target = "withStoreId", source = "entity.storeId")
    @Mapping(target = "withSensorId", source = "entity.sensorId")
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
    @Mapping(target = "sensorId", ignore = true)
    @Mapping(target = "readingTimestamp",  ignore = true)
    void updateEPCReadingEntity(EPCReadingEntity source, @MappingTarget EPCReadingEntity target);

    @Mapping(target = "storeId", ignore = true)
    @Mapping(target = "sensorId", ignore = true)
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
                                                                               .withSensorId(reading.getSensorId())
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
        readingResponse.setSensorId(reading.getSensorId());

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


    default EPCReadingEntity mapChangedFields(EPCReadingEntity changes, EPCReadingEntity target) {
        return mapChangedFields(changes, target, true);
    }

    default EPCReadingEntity mapChangedFields(EPCReadingEntity changes, EPCReadingEntity target, boolean onlyNonIndexedFields) {
        if (target == null) {
            return changes.toBuilder().build();
        }

        if ((isBlank(target.getGtin()) && isNotBlank(changes.getGtin())) ||
            (isNotBlank(target.getGtin()) && isBlank(changes.getGtin())) ||
            !target.getGtin().equalsIgnoreCase(changes.getGtin())) {
            target.setGtin(changes.getGtin());
        }

        if ((isBlank(target.getUpc()) && isNotBlank(changes.getUpc())) ||
            (isNotBlank(target.getUpc()) && isBlank(changes.getUpc())) ||
            !target.getUpc().equalsIgnoreCase(changes.getUpc())) {
            target.setUpc(changes.getUpc());
        }

        if ((isBlank(target.getCorrelationId()) && isNotBlank(changes.getCorrelationId())) ||
            (isNotBlank(target.getCorrelationId()) && isBlank(changes.getCorrelationId())) ||
            !target.getCorrelationId().equalsIgnoreCase(changes.getCorrelationId())) {
            target.setCorrelationId(changes.getCorrelationId());
        }

        if (target.getSerial() == null) {
            if (changes.getSerial() != null) {
                target.setSerial(changes.getSerial());
            }
        } else if (changes.getSerial() == null) {
            target.setSerial(null);
        } else if (!target.getSerial().equals(changes.getSerial())) {
           target.setSerial(changes.getSerial());
        }

        if (target.getRssi() == null) {
            if (changes.getRssi() != null) {
                target.setRssi(changes.getRssi());
            }
        } else if (changes.getRssi() == null) {
            target.setRssi(null);
        } else if (!target.getRssi().equals(changes.getRssi())) {
            target.setRssi(changes.getRssi());
        }

        if (target.getModificationDate() == null) {
            if (changes.getModificationDate() != null) {
                target.setModificationDate(changes.getModificationDate());
            }
        } else if (changes.getModificationDate() == null) {
            target.setModificationDate(null);
        } else if (!target.getModificationDate().truncatedTo(ChronoUnit.SECONDS).equals(changes.getModificationDate().truncatedTo(ChronoUnit.SECONDS))) {
            target.setModificationDate(changes.getModificationDate());
        }

        // only if it has not already been set
        if (target.getId() == null && changes.getId() != null) {
            target.setId(changes.getId());
        }

        if (!onlyNonIndexedFields) {
            if (( isBlank(target.getStoreId()) && isNotBlank(changes.getStoreId()) ) ||
                ( isNotBlank(target.getStoreId()) && isBlank(changes.getStoreId()) ) ||
                !target.getStoreId().equals(changes.getStoreId())) {
                target.setStoreId(changes.getStoreId());
            }

            if (( isBlank(target.getData()) && isNotBlank(changes.getData()) ) ||
                ( isNotBlank(target.getData()) && isBlank(changes.getData()) ) ||
                !target.getData().equalsIgnoreCase(changes.getData())) {
                target.setData(changes.getData());
            }

            if (( isBlank(target.getSensorId()) && isNotBlank(changes.getSensorId()) ) ||
                ( isNotBlank(target.getSensorId()) && isBlank(changes.getSensorId()) ) ||
                !target.getSensorId().equalsIgnoreCase(changes.getSensorId())) {
                target.setSensorId(changes.getSensorId());
            }

            if (target.getReadingTimestamp() == null) {
                if (changes.getReadingTimestamp() != null) {
                    target.setReadingTimestamp(changes.getReadingTimestamp());
                }
            } else if (changes.getReadingTimestamp() == null) {
                target.setReadingTimestamp(null);
            } else if (!target.getReadingTimestamp().truncatedTo(ChronoUnit.SECONDS).equals(changes.getReadingTimestamp().truncatedTo(ChronoUnit.SECONDS))) {
                target.setReadingTimestamp(changes.getReadingTimestamp());
            }
        }
        return target;
    }

}
