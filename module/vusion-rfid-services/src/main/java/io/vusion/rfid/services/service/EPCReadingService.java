package io.vusion.rfid.services.service;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.rfid.data.model.EPCReadingEntity;
import io.vusion.rfid.domain.model.EPCReading;
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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static io.vusion.vtransmit.v2.commons.utils.BlankStringComparator.blanksLast;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;
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

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    @Builder(toBuilder = true, setterPrefix = "with")
    @EqualsAndHashCode
    public static class UniqueKey implements Serializable, Comparable<UniqueKey> {
        private String storeId;
        private String sensorMacAddress;
        private Instant timestamp;
        private String data;

        public void setData(String data) {
            this.data = stripToNull(data);
        }

        public void setSensorMacAddress(String sensorMacAddress) {
            this.sensorMacAddress = stripToNull(sensorMacAddress);
        }

        public void setStoreId(String storeId) {
            this.storeId = stripToNull(storeId);
        }

        @Override
        public int compareTo(@Nullable UniqueKey other) {
            if (other == null) {
                return -1;
            }

            return comparing(UniqueKey::getStoreId, blanksLast(naturalOrder()))
                    .thenComparing(UniqueKey::getSensorMacAddress, blanksLast(naturalOrder()))
                    .thenComparing(UniqueKey::getData, blanksLast(naturalOrder()))
                    .thenComparing(UniqueKey::getTimestamp, nullsLast(naturalOrder()))
                    .compare(this, other);
        }

        @Override
        public String toString() { return GsonHelper.getGsonNull().toJson(this); }
    }

    @Override
    public void save(StoreEPCSensorReading reading) {


        final EPCReadingEntity savedEntity = epcReadingDao.findByStoreIdSensorIdDataTimestamp(reading.getStoreId(),
                                                                                              reading.getMacAddress(),
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

        return epcReadingDao.findByStoreIdMacAddressTimestamp(reading.getStoreId(),
                                                              reading.getMacAddress(),
                                                              reading.getTimestamp())
                            .stream()
                            .toList();
    }

    @Override
    public void saveAll(Collection<StoreEPCSensorReading> readings) {
        if (isEmpty(readings)) {
            return;
        }

        final Collection<EPCReadingEntity> entities = readings.stream()
                                                              .filter(Objects::nonNull)
                                                              .map(r -> epcReadingMapper.toEPCReadingEntity(FrontExecutionContext.getCorrelationId(), r))
                                                              .toList();

        final Collection<EPCReadingEntity> unchangedEntities = new ArrayList<>();
        final Map<UniqueKey, EPCReadingEntity> entitiesAlreadySavedByUniqueKey =
                entities.stream()
                        .filter(r -> isNotBlank(r.getStoreId()) &&
                                     isNotBlank(r.getSensorMacAddress()) &&
                                     Objects.nonNull(r.getReadingTimestamp()))
                        .map(r -> epcReadingDao.findByStoreIdSensorIdDataTimestamp(r.getStoreId(),
                                                                                   r.getSensorMacAddress(),
                                                                                   r.getData(),
                                                                                   r.getReadingTimestamp()))
                        .filter(Objects::nonNull)
                        .collect(toMap(epcReadingMapper::toEPCReadingEntityUniqueKey,
                                       identity(),
                                       (left,right) -> left,
                                       LinkedHashMap::new));

        final Collection<EPCReadingEntity> entitiesToSave = new ArrayList<>(entities);

        entitiesToSave.removeIf(entity -> {
            final EPCReadingService.UniqueKey key = epcReadingMapper.toEPCReadingEntityUniqueKey(entity);
            final EPCReadingEntity savedEntity = entitiesAlreadySavedByUniqueKey.get(key);
            return savedEntity != null && !Objects.deepEquals(entity, savedEntity);
        });


        final Collection<EPCReadingEntity> updatedEntitiesToSave = entitiesToSave.stream()
                                                                                 .map(entity -> Pair.of(entity,
                                                                                                        epcReadingMapper.toEPCReadingEntityUniqueKey(entity)))
                                                                                 .map(p -> Pair.of(p.getLeft(),
                                                                                                   entitiesAlreadySavedByUniqueKey.get(p.getRight())))
                                                                                 .map(p -> {
                                                                                     if (p.getRight() == null) {
                                                                                         return p.getLeft();
                                                                                     }
                                                                                     epcReadingMapper.updateEPCReadingEntity(p.getLeft(), p.getRight());
                                                                                     return p.getRight();
                                                                                 })
                                                                                 .toList();

        epcReadingDao.saveOrUpdate(updatedEntitiesToSave);
        LOGGER.info("%d of %d EPCReadingEntity were saved".formatted(size(entities), size(updatedEntitiesToSave)));
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
        return epcReadingDao.findAllByStoreId(storeId)
                            .stream()
                            .filter(entity -> start == null ||
                                              entity.getReadingTimestamp().equals(start) ||
                                              entity.getReadingTimestamp().isAfter(start))
                            .filter(entity -> end == null ||
                                              entity.getReadingTimestamp().equals(end) ||
                                              entity.getReadingTimestamp().isBefore(end))
                            .map(epcReadingMapper::toStoreEPCReading)
                            .toList();

    }
}
