package io.vusion.rfid.data.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigInteger;
import java.time.Instant;

import static java.util.Objects.requireNonNullElseGet;

@Getter
@Setter
@DynamicUpdate
@Entity
@Table(name = "[epc_readings]")
public class EPCReadingEntity extends AbstractStoreEntity {

//	@Column(name = "store_id")
//	private String storeId;
    @AttributeOverride(name = "creationDate", column = @Column(name = "creation_date"))
    private final Instant creationDate;

    @Column(name = "sensor_mac_address")
    private String sensorMacAddress;

    @Column(name = "correlation_id")
	private String correlationId;
	
	@Column(name = "epc_data")
	private String data;

    @Column(name = "epc_gtin")
    private String gtin;

    @Column(name = "item_upc")
    private String upc;

    @Column(name = "epc_serial")
    private BigInteger serial;

    @Column(name = "reading_timestamp")
    private Instant readingTimestamp;

    @Column(name = "epc_rssi")
    private Integer rssi;

    public EPCReadingEntity() {
        this.creationDate = Instant.now();
    }

    @Builder(toBuilder = true, setterPrefix = "with")
    public EPCReadingEntity(@Builder.ObtainVia(method = "getId") Long id,
                            @Builder.ObtainVia(method = "getCreationDate") Instant creationDate,
                            @Builder.ObtainVia(method = "getModificationDate") Instant modificationDate,
                            @Builder.ObtainVia(method = "getStoreId") String storeId,
                            String correlationId,
                            String data,
                            String gtin,
                            Integer rssi,
                            String sensorMacAddress,
                            BigInteger serial,
                            Instant readingTimestamp,
                            String upc) {
        super(storeId);
        setId(id);
        setModificationDate(modificationDate);
        this.creationDate = requireNonNullElseGet(creationDate, Instant::now);
        this.correlationId = correlationId;
        this.data = data;
        this.gtin = gtin;
        this.rssi = rssi;
        this.sensorMacAddress = sensorMacAddress;
        this.serial = serial;
        this.readingTimestamp = readingTimestamp;
        this.upc = upc;
    }

    @Override
	public String getFunctionalKey() {
		return String.format("%s_%s_%s_%s",
                             this.getStoreId(), this.getSensorMacAddress(), this.getData(), this.getCorrelationId());
	}

    @Override
    public int hashCode() {
        return new HashCodeBuilder(53, 91)
                        .append(getCreationDate())
                        .append(getId())
                        .append(getModificationDate())
                        .append(getStoreId())
                        .append(getReadingTimestamp())
                        .append(getSensorMacAddress())
                        .append(getData())
                        .append(getRssi())
                        .append(getCorrelationId())
                        .append(getGtin())
                        .append(getUpc())
                        .append(getRssi())
                        .build();
    }
}