package io.vusion.rfid.data.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "[store_sensors]")
public class SensorEntity extends AbstractStoreEntity {

    @Column(name = "mac_address")
    private String macAddress;

    private String location;

    @Override
    public String getFunctionalKey() {
        return "%s_%s".formatted(getStoreId(), getMacAddress());
    }
}
