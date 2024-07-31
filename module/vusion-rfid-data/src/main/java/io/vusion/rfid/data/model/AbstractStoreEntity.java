package io.vusion.rfid.data.model;

import io.vusion.dao.model.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@MappedSuperclass
@Getter @Setter
//@SuperBuilder(toBuilder = true, setterPrefix = "with")
public abstract class AbstractStoreEntity extends AbstractEntity {

	@Column(name = "store_id")
	protected String storeId;
	
	protected AbstractStoreEntity() { }

	public AbstractStoreEntity(String storeId) {
		this.storeId = storeId;
	}

}

