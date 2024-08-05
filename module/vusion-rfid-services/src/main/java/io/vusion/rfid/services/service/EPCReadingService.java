package io.vusion.rfid.services.service;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.rfid.data.model.EPCReadingEntity;
import io.vusion.rfid.domain.model.StoreEPCSensorReading;
import io.vusion.rfid.services.front.utils.FrontExecutionContext;
import io.vusion.rfid.services.mapping.EPCReadingMapper;
import io.vusion.rfid.services.mapping.EPCSensorMapper;
import io.vusion.secure.logs.VusionLogger;
import io.vusion.vtransmit.v2.commons.dao.EPCReadingDao;
import io.vusion.vtransmit.v2.commons.dao.SensorDao;
import io.vusion.vtransmit.v2.commons.repository.EPCRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static io.vusion.vtransmit.v2.commons.utils.BlankStringComparator.blanksLast;
import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.stripToNull;

@Service
@RequiredArgsConstructor
public class EPCReadingService implements EPCRepository {
    private static final VusionLogger LOGGER = VusionLogger.getLogger(EPCReadingService.class);
    private final EPCReadingMapper epcReadingMapper;
    private final EPCSensorMapper  epcSensorMapper;
    private final SensorDao        sensorDao;
    private final EPCReadingDao    epcReadingDao;

    @Override
    public void save(StoreEPCSensorReading reading) {


        final EPCReadingEntity savedEntity = epcReadingDao.findByStoreIdSensorIdDataTimestamp(reading.getStoreId(),
                                                                                              reading.getSensorId(),
                                                                                              reading.getData(),
                                                                                              reading.getTimestamp());
        final EPCReadingEntity entityToSave;
        if (savedEntity != null) {
            entityToSave = epcReadingMapper.updateEPCReadingEntity(reading, savedEntity);
        } else {
            entityToSave = epcReadingMapper.toEPCReadingEntity(reading);
        }
        epcReadingDao.saveOrUpdate(entityToSave);
    }

    Collection<EPCReadingEntity> findAllMatchingEntity(StoreEPCSensorReading reading) {
        if (reading == null) {
            return null;
        }

        return epcReadingDao.findByStoreIdSensorIdTimestamp(reading.getStoreId(),
                                                            reading.getSensorId(),
                                                            reading.getTimestamp())
                            .stream()
                            .toList();
    }

    Map<EPCReadingEntity.UniqueKey, EPCReadingEntity> toUniqueEntities(Collection<StoreEPCSensorReading> readings) {
        if (isEmpty(readings)) {
            return emptyMap();
        }

        final String correlationId = FrontExecutionContext.getCorrelationId();
        final List<Pair<StoreEPCSensorReading, EPCReadingEntity>> parsedEntities = new ArrayList<>();
        readings.forEach(rd -> {
            EPCReadingEntity entity;
            try {
                entity = epcReadingMapper.toEPCReadingEntity(correlationId, rd);
            } catch (Exception e) {
                LOGGER.error("Failed to parse EPC reading into an EPCReadingEntity: '%s'. %s".formatted(rd, ExceptionUtils.getMessage(e)), e);
                entity = null;
            }
            parsedEntities.add(Pair.of(rd, entity));
        });

        final List<StoreEPCSensorReading> failedParsings = parsedEntities.stream()
                                                                         .filter(p -> p.getRight() == null)
                                                                         .map(Pair::getLeft)
                                                                         .toList();
        if (isNotEmpty(failedParsings)) {
            if (size(failedParsings) == size(readings)) {
                throw new IllegalArgumentException("Bad request. No readings from %d could be parsed into a supported EPC to save".formatted(size(readings)));
            }
            LOGGER.error("%d of %d readings uploaded could not be parsed into a supported EPC to save".formatted(size(failedParsings), size(readings)));
        }

        final List<EPCReadingEntity> entities = parsedEntities.stream()
                                                              .filter(p -> p.getRight() != null)
                                                              .map(Pair::getRight)
                                                              .toList();
        final List<EPCReadingEntity.UniqueKey> keys = entities.stream().map(EPCReadingEntity.UniqueKey::wrap).toList();

        final Map<EPCReadingEntity.UniqueKey, List<EPCReadingEntity>> entitiesByUniqueIndex = new LinkedHashMap<>();
        keys.forEach(r -> {
            final List<EPCReadingEntity> entitiesForKey = entitiesByUniqueIndex.computeIfAbsent(r, k -> new ArrayList<>());
            entitiesForKey.add(r.unwrap());
        });

        // Any with too many
        entitiesByUniqueIndex.forEach((key, grouped) -> {
            if (size(grouped) > 1) {
                LOGGER.info(String.format("""
                                          Multiple (%d) entries found for storeId: '%s'\
                                          , sensor: '%s', at timestamp: '%s'\
                                          , and data: '%s': %s""",
                                          size(grouped),
                                          key.getStoreId(),
                                          key.getSensorId(),
                                          key.getReadingTimestamp(),
                                          key.getData(),
                                          GsonHelper.toJson(grouped)));
                grouped.clear();
                grouped.add(key.unwrap());
            }
        });

        return entitiesByUniqueIndex.entrySet()
                                    .stream()
                                    .map(entry -> Pair.of(entry.getKey(), entry.getValue().get(0)))
                                    .collect(toMap(Pair::getKey, Pair::getValue, (left, right) -> left, LinkedHashMap::new));
    }

    @Override
    public void saveAll(Collection<StoreEPCSensorReading> readings) {
        if (isEmpty(readings)) {
            return;
        }
        final Collection<EPCReadingEntity> entities = toUniqueEntities(readings).values();

        final Collection<EPCReadingEntity> unchangedEntities = new ArrayList<>();
        final Collection<Pair<EPCReadingEntity,EPCReadingEntity>> entitiesToUpdate = new ArrayList<>();
        final Collection<EPCReadingEntity> entitiesToSave = new ArrayList<>();

        entities.forEach(entity -> {
            if (isBlank(entity.getStoreId()) || isBlank(entity.getSensorId())) {
                final String message =  entity.getReadingTimestamp() == null
                                     ? "Unable to find an EPC scan without a store, sensor, and timestamp: %s"
                                     : "Unable to find an EPC scan without a store or sensor: %s";
                LOGGER.info(String.format(message, entity));
            } else if (entity.getReadingTimestamp() == null) {
                entity.setReadingTimestamp(Instant.now());
                entitiesToSave.add(entity);
            } else {
                final EPCReadingEntity entityFound = epcReadingDao.findByStoreIdSensorIdDataTimestamp(entity.getStoreId(),
                                                                                                      entity.getSensorId(),
                                                                                                      entity.getData(),
                                                                                                      entity.getReadingTimestamp().truncatedTo(ChronoUnit.SECONDS));
                if (entityFound != null) {
                    if (entityFound.equals(entity)) {
                        LOGGER.info(String.format("No differences found between DB EPCReading and new EPCReading:%n\t%s%n\t%s",
                                                  entityFound, entity));
                        unchangedEntities.add(entity);
                    } else {
                        entitiesToUpdate.add(Pair.of(entity, entityFound));
                    }
                } else {
                    entitiesToSave.add(entity);
                }
            }
        });
        epcReadingDao.saveOrUpdate(entitiesToSave);
        LOGGER.debug(() -> String.format("Saved %d new EPCReadings: [%s]", size(entitiesToSave), GsonHelper.getGsonNull().toJson(entitiesToSave)));

        final Collection<Pair<EPCReadingEntity, EPCReadingEntity>> beforeAndAfter =
                entitiesToUpdate.stream()
                                .map(p -> Pair.of(p.getRight().toBuilder().build(),
                                                  epcReadingMapper.mapChangedFields(p.getLeft(), p.getRight())))
                                .toList();

        LOGGER.info("Found %d entities already save to the database, %d will be updated".formatted(
                size(entitiesToUpdate) + size(unchangedEntities), size(entitiesToUpdate)));

        beforeAndAfter.forEach(p -> {
            LOGGER.debug(() -> String.format("""
                                             Updating EPCReading
                                             \tfrom: %s
                                             \tto  : %s""", p.getLeft(), p.getRight()));
            epcReadingDao.saveOrUpdate(p.getRight());
        });
    }

    @Override
    public Collection<StoreEPCSensorReading> getAll() {
        return epcReadingDao.getAll()
                            .stream()
                            .map(epcReadingMapper::toStoreEPCReading)
                            .toList();
    }

    @Override
    public Collection<StoreEPCSensorReading> findAll(String storeId) {
        return epcReadingDao.findAllByStoreId(storeId)
                            .stream()
                            .map(epcReadingMapper::toStoreEPCReading)
                            .toList();
    }


    @Override
    public Collection<StoreEPCSensorReading> findAllByDate(String storeId, LocalDate date) {
        return epcReadingDao.findAllByStoreId(storeId)
                            .stream()
                            .map(entity -> Pair.of(entity, Optional.ofNullable(entity.getReadingTimestamp())
                                                                                     .map(ts -> ts.atOffset(ZoneOffset.UTC))
                                                                                     .map(OffsetDateTime::toLocalDate)
                                                                                     .orElse(null)))
                            .filter(p -> p.getRight() != null)
                            .filter(p -> p.getRight().equals(date))
                            .map(Pair::getLeft)
                            .map(epcReadingMapper::toStoreEPCReading)
                            .toList();
    }

    @Override
    public Collection<StoreEPCSensorReading> findAllByDateTimeRange(String storeId, Instant start, Instant end) {
        return findAllBySensorUPCDateTimeRange(storeId, null, null, start, end);
    }


    @Override
    public Collection<StoreEPCSensorReading> findAllByUPCDateTimeRange(String store, String upc, Instant start, Instant end) {
        return findAllBySensorUPCDateTimeRange(store, null, upc, start, end);
    }

    @Override
    public Collection<StoreEPCSensorReading> findAllBySensorUPCDateTimeRange(String store, String sensor, String upc, Instant start, Instant end) {
        return epcReadingDao.findAllByStoreId(store)
                            .stream()
                            .filter(entity -> isBlank(sensor) || equalsIgnoreCase(entity.getSensorId(), sensor))
                            .filter(entity -> isBlank(upc) || equalsIgnoreCase(entity.getUpc(), upc))
                            .filter(entity -> start == null ||
                                              entity.getReadingTimestamp().equals(start) ||
                                              entity.getReadingTimestamp().isAfter(start))
                            .filter(entity -> end == null ||
                                              entity.getReadingTimestamp().equals(end) ||
                                              entity.getReadingTimestamp().isBefore(end))
                            .map(epcReadingMapper::toStoreEPCReading)
                            .toList();
    }

    @Override
    public Collection<StoreEPCSensorReading> findAllBySensorDateTimeRange(String storeId, String sensorId, Instant start, Instant end) {
        return findAllBySensorUPCDateTimeRange(storeId, sensorId, null, start, end);
    }
}
