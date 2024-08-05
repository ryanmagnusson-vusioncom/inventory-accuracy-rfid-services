package io.vusion.vtransmit.v2.commons.dao;

import io.vusion.dao.AbstractDao;
import io.vusion.rfid.data.model.StoreEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.join;


@Service
public class StoreDao extends AbstractDao<StoreEntity> {

	public StoreDao() {
		super(StoreEntity.class, "store_sequence");
	}

    public StoreEntity findByStoreId(final String storeId) {
        final List<StoreEntity> stores = getByQuery("%1$s.storeId = ?1".formatted(getTableName()), storeId);
        return isEmpty(stores) ? null : stores.get(0);
    }

    public Collection<StoreEntity> findAllStoresById(final List<String> storeIds) {
        return getByQuery("%1$s.storeId IN (?1)".formatted(getTableName()), join(storeIds.stream().map(id -> String.format("'%s'", id)), ","));
    }

}
