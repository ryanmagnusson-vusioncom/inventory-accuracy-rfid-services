package io.vusion.vtransmit.v2.commons.dao;

import io.vusion.dao.AbstractDao;
import io.vusion.rfid.data.model.SensorEntity;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SensorDao extends AbstractDao<SensorEntity> {

	public SensorDao() {
		super(SensorEntity.class, "switch_back_sequence");
	}

//    public SensorEntity getById(final Long id) {
//        return ("id = ?1", id);
//    }

	public SensorEntity getSensor(final String storeId, final String macAddress) {
		return getOneByQuery("storeId = ?1 AND macAddress = ?2", storeId, macAddress);
	}
	
	public List<SensorEntity> getSensors(final String storeId) {
		return getEntityManager()
				.createQuery("FROM SensorEntity s WHERE s.storeId <= ?1 order by storeId", SensorEntity.class)
				.setParameter(1, storeId)
				.getResultList();
	}
}
