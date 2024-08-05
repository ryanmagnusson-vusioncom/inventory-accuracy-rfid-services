package io.vusion.vtransmit.v2.commons.repository;

import io.vusion.rfid.domain.model.StoreEPCSensorReading;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface EPCRepository {

    void save(StoreEPCSensorReading reading);

    void saveAll(Collection<StoreEPCSensorReading> readings);

    Collection<StoreEPCSensorReading> getAll();

    Collection<StoreEPCSensorReading> findAll(String store);

    Collection<StoreEPCSensorReading> findAllByDate(String store, LocalDate date);

    Collection<StoreEPCSensorReading> findAllByDateTimeRange(String store, Instant start, Instant end);

    Collection<StoreEPCSensorReading> findAllBySensorDateTimeRange(String store, String sensor, Instant start, Instant end);
    Collection<StoreEPCSensorReading> findAllByUPCDateTimeRange(String store, String upc, Instant start, Instant end);
    Collection<StoreEPCSensorReading> findAllBySensorUPCDateTimeRange(String store, String sensor, String upc, Instant start, Instant end);
}
