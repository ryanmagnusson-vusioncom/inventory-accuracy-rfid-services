package io.vusion.vtransmit.v2.commons.dao;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import io.vusion.dao.AbstractDao;
import io.vusion.rfid.data.model.EPCReadingEntity;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;


@Service
public class EPCReadingDao extends AbstractDao<EPCReadingEntity> {

	public EPCReadingDao() {
		super(EPCReadingEntity.class, "epc_reads_sequence");
	}

    public Collection<EPCReadingEntity> findByStoreIdMacAddressTimestamp(final String storeId,
                                                                         final String macAddress,
                                                                         final Instant timestamp) {
        return getByQuery("""
                          %1$s.storeId = ?1 \
                          AND %1$s.sensorMacAddress = ?2 \
                          AND %1$s.timestamp = ?3""".formatted(getTableName()), storeId, macAddress, timestamp);
    }

    public EPCReadingEntity findByStoreIdSensorIdDataTimestamp(final String storeId,
                                                               final String macAddress,
                                                               final String data,
                                                               final Instant timestamp) {
        return findByStoreIdMacAddressTimestamp(storeId, macAddress, timestamp)
                       .stream()
                       .filter(entity -> isBlank(data) ? isBlank(entity.getData()) : equalsIgnoreCase(data, entity.getData()))
                       .findFirst()
                       .orElse(null);
    }

	public Collection<EPCReadingEntity> findAllByStoreId(final String storeId) {
		return getByQuery(getTableName() + ".storeId = ?1", storeId);
	}

	public Collection<EPCReadingEntity> findAllByStoreIdAndTimestampRange(final String storeId, final Instant from, final Instant to) {
        return getByQuery(String.format("%1$s.storeId = ?1 AND %$1s.timestamp <= ?2 AND %1$s.timestamp >= ?3", getTableName()), storeId, from, to);
    }

    public Collection<EPCReadingEntity> findAllByStoreIdAndUPC(final String storeId, final String upc) {
        return getByQuery(String.format("%1$s.storeId = ?1 AND %$1s.upc = ?2", getTableName()), storeId, upc);
    }

}
