package io.vusion.vtransmit.v2.commons.dao;

import java.time.Instant;
import java.util.Collection;

import io.vusion.dao.AbstractDao;
import io.vusion.rfid.data.model.EPCReadingEntity;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;


@Service
public class EPCReadingDao extends AbstractDao<EPCReadingEntity> {

	public EPCReadingDao() {
		super(EPCReadingEntity.class, "epc_readings_sequence");
	}

    public Collection<EPCReadingEntity> findByStoreIdSensorIdTimestamp(final String storeId,
                                                                       final String macAddress,
                                                                       final Instant timestamp) {
        return getByQuery("""
                          %1$s.storeId = ?1 \
                          AND %1$s.sensorId = ?2 \
                          AND %1$s.readingTimestamp = ?3""".formatted(getTableName()), storeId, macAddress, timestamp);
    }

    public EPCReadingEntity findByStoreIdSensorIdDataTimestamp(final String storeId,
                                                               final String macAddress,
                                                               final String data,
                                                               final Instant timestamp) {
        return findByStoreIdSensorIdTimestamp(storeId, macAddress, timestamp)
                       .stream()
                       .filter(entity -> isBlank(data) ? isBlank(entity.getData()) : equalsIgnoreCase(data, entity.getData()))
                       .findFirst()
                       .orElse(null);
    }

	public Collection<EPCReadingEntity> findAllByStoreId(final String storeId) {
		return getByQuery(getTableName() + ".storeId = ?1", storeId);
	}

	public Collection<EPCReadingEntity> findAllByStoreIdAndTimestampRange(final String storeId, final Instant from, final Instant to) {
        return getByQuery(String.format("%1$s.storeId = ?1 AND %$1s.readingTimestamp <= ?2 AND %1$s.readingTimestamp >= ?3", getTableName()), storeId, from, to);
    }

    public Collection<EPCReadingEntity> findAllByStoreIdAndUPC(final String storeId, final String upc) {
        return getByQuery(String.format("%1$s.storeId = ?1 AND %$1s.upc = ?2", getTableName()), storeId, upc);
    }

}
