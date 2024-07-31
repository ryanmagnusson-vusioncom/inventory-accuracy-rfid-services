package io.vusion.rfid.services.service;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.rfid.data.model.EPCReadingEntity;
import io.vusion.rfid.domain.model.StoreEPCSensorReading;
import io.vusion.rfid.services.front.utils.FrontExecutionContext;
import io.vusion.rfid.services.mapping.EPCReadingMapper;
import io.vusion.rfid.services.mapping.EPCSensorMapper;
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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static io.vusion.vtransmit.v2.commons.utils.BlankStringComparator.blanksLast;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.stripToNull;

@Service
@RequiredArgsConstructor
public class EPCReadingService implements EPCRepository {

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
        //final Map<UniqueKey, EPCReadingEntity> entitiesAlreadySaved =
        final List<EPCReadingEntity> entitiesAlreadySaved =
                readings.stream()
                        .map(r -> epcReadingDao.findByStoreIdSensorIdDataTimestamp(r.getStoreId(),
                                                                                  r.getMacAddress(),
                                                                                  r.getData(),
                                                                                  r.getTimestamp()))
                        .toList();

        final Map<UniqueKey, EPCReadingEntity> entitiesAlreadySavedByUniqueKey =
                        entitiesAlreadySaved.stream()
                                            .filter(Objects::nonNull)
                        .collect(toMap(epcReadingMapper::toEPCReadingEntityUniqueKey,
                                       identity(),
                                       (left,right) -> left,
                                       LinkedHashMap::new));


        final Collection<EPCReadingEntity> updatesToSave = readings.stream()
                                                                   .map(r -> Pair.of(epcReadingMapper.toEPCReadingEntityUniqueKey(r), r))
                                                                   .map(p -> Pair.of(p.getRight(),
                                                                                     entitiesAlreadySavedByUniqueKey.computeIfAbsent(p.getLeft(),
                                                                                                                  key -> epcReadingMapper.toEPCReadingEntity(FrontExecutionContext.getCorrelationId(),
                                                                                                                                                             p.getRight()))))
                                                                   .map(p -> epcReadingMapper.updateEPCReadingEntity(p.getLeft(), p.getRight()))
                                                                   .toList();

        epcReadingDao.saveOrUpdate(updatesToSave);
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
                            .map(entity -> Pair.of(entity, Optional.ofNullable(entity.getTimestamp())
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
                                    entity.getTimestamp().equals(start) ||
                                    entity.getTimestamp().isAfter(start))
                            .filter(entity -> end == null ||
                                    entity.getTimestamp().equals(end) ||
                                    entity.getTimestamp().isBefore(end))
                            .map(epcReadingMapper::toStoreEPCReading)
                            .toList();

    }
}
