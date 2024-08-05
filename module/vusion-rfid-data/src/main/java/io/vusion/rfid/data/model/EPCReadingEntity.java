package io.vusion.rfid.data.model;

import io.vusion.gson.utils.GsonHelper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static io.vusion.vtransmit.v2.commons.utils.BlankStringComparator.blanksLast;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.strip;
import static org.apache.commons.lang3.StringUtils.stripToNull;

@Getter
@Setter
@DynamicUpdate
@Entity
@Table(name = "[epc_readings]",
       uniqueConstraints= {
               @UniqueConstraint(columnNames = { "store_id", "sensor_id", "reading_timestamp", "epc_data" })
       })
public class EPCReadingEntity extends AbstractStoreEntity {

//	@Column(name = "store_id")
//	private String storeId;
//    @AttributeOverride(name = "creationDate", column = @Column(name = "creation_date"))
//    private final Instant creationDate;

    @Column(name = "sensor_id")
    private String sensorId;

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

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder(toBuilder = true, setterPrefix = "with")
    public static class UniqueKey implements Serializable, Comparable<UniqueKey> {
//        private String  storeId;
//        private String  sensorId;
//        private Instant readingTimestamp;
//        private String data;

        private EPCReadingEntity entity;

//        public void setData(String data) {
//            this.data = stripToNull(data);
//        }
//
//        public void setSensorId(String sensorId) {
//            this.sensorId = stripToNull(sensorId);
//        }
//
//        public void setStoreId(String storeId) {
//            this.storeId = stripToNull(storeId);
//        }

        public String getStoreId() {
            return stripToNull(getEntity().getStoreId());
        }

        public String getSensorId() {
            return stripToNull(getEntity().getSensorId());
        }

        public String getData() {
            return stripToNull(getEntity().getData());
        }

        public Instant getReadingTimestamp() {
            return Optional.ofNullable(getEntity().getReadingTimestamp())
                           .map(ts -> ts.truncatedTo(ChronoUnit.SECONDS))
                           .orElse(null);
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(63313, 53)
                            .append(getStoreId())
                            .append(getSensorId())
                            .append(getData())
                            .append(getReadingTimestamp())
                            .build();
        }

        protected boolean canEqual(Object other) {
            return other instanceof UniqueKey;
        }

        @Override
        public boolean equals(Object obj) {
            if (!canEqual(obj)) {
                return false;
            }

            final UniqueKey other = (UniqueKey) obj;
            if (getReadingTimestamp() == null) {
                return other.getReadingTimestamp() == null;
            }

            if (other.getReadingTimestamp() == null ||
                !getReadingTimestamp().equals(other.getReadingTimestamp())) {
                return false;
            }

            return areEqual(getStoreId(), other.getStoreId()) &&
                   areEqual(getData(), other.getData(), true) &&
                   areEqual(getSensorId(), other.getSensorId(), true);
        }

        @Override
        public int compareTo(@Nullable UniqueKey other) {
            if (other == null) {
                return -1;
            }

            return comparing(UniqueKey::getStoreId, blanksLast(naturalOrder()))
                    .thenComparing(UniqueKey::getSensorId, blanksLast(naturalOrder()))
                    .thenComparing(UniqueKey::getData, blanksLast(naturalOrder()))
                    .thenComparing(UniqueKey::getReadingTimestamp, nullsLast(naturalOrder()))
                    .compare(this, other);
        }

        public static EPCReadingEntity.UniqueKey wrap(EPCReadingEntity entity) {
            final EPCReadingEntity.UniqueKey key = new UniqueKey(entity);
            return key;
        }

        public EPCReadingEntity unwrap() {
            return entity;
        }

        @Override
        public String toString() { return GsonHelper.getGsonNull().toJson(this); }
    }


    public EPCReadingEntity() { }

    @Builder(toBuilder = true, setterPrefix = "with")
    public EPCReadingEntity(@Builder.ObtainVia(method = "getId") Long id,
                            //@Builder.ObtainVia(method = "getCreationDate") Instant creationDate,
                            @Builder.ObtainVia(method = "getModificationDate") Instant modificationDate,
                            @Builder.ObtainVia(method = "getStoreId") String storeId,
                            String correlationId,
                            String data,
                            String gtin,
                            Integer rssi,
                            String sensorId,
                            BigInteger serial,
                            Instant readingTimestamp,
                            String upc) {
        super(strip(storeId));
        setId(id);
        setModificationDate(Optional.ofNullable(modificationDate).map(ts -> ts.truncatedTo(ChronoUnit.SECONDS)).orElse(null));
        //this.creationDate = requireNonNullElseGet(creationDate, Instant::now);
        this.correlationId = strip(correlationId);
        this.data = strip(data);
        this.gtin = strip(gtin);
        this.rssi = rssi;
        this.sensorId = strip(sensorId);
        this.serial = serial;
        this.readingTimestamp = Optional.ofNullable(readingTimestamp).map(ts -> ts.truncatedTo(ChronoUnit.SECONDS)).orElse(null);
        this.upc = strip(upc);
    }

    public void setSensorId(String sensorId) {
        this.sensorId = stripToNull(sensorId);
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = stripToNull(correlationId);
    }

    public void setData(String data) {
        this.data = stripToNull(data);
    }

    public void setUpc(String upc) {
        this.upc = stripToNull(upc);
    }

    public void setGtin(String gtin) {
        this.gtin = stripToNull(gtin);
    }

    @Override
	public String getFunctionalKey() {
		return String.format("%s_%s_%s_%s",
                             this.getStoreId(), this.getSensorId(), this.getData(), this.getCorrelationId());
	}

    static boolean areEqual(Instant left, Instant right) {
        if (left == right) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        return left.truncatedTo(ChronoUnit.SECONDS).equals(right.truncatedTo(ChronoUnit.SECONDS));
    }

    static boolean areEqual(Long left, Long right) {
        if (left == null) {
            return right == null;
        } else if (right == null) {
            return false;
        }

        return left.equals(right);
    }

    static boolean areEqual(String left, String right) {
        return areEqual(left, right, false);
    }

    static boolean areEqual(String left, String right, boolean ignoreCase) {
        if (isBlank(left)) {
            return isBlank(right);
        } else if (isBlank(right)) {
            return false;
        }

        return ignoreCase ? left.equalsIgnoreCase(right) : left.equals(right);
    }

    static boolean areEqual(BigInteger left, BigInteger right) {
        if (left == null) {
            return right == null;
        } else if (right == null) {
            return false;
        }

        return left.equals(right);
    }

    static boolean areEqual(Integer left, Integer right) {
        if (left == null) {
            return right == null;
        } else if (right == null) {
            return false;
        }

        return left.equals(right);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof EPCReadingEntity;
    }

    @Override
    public boolean equals(Object object) {
        if (!canEqual(object)) {
            return false;
        }

        return object instanceof EPCReadingEntity other
               && areEqual(getCreationDate(), other.getCreationDate())
               && areEqual(getModificationDate(), other.getModificationDate())
               && areEqual(getReadingTimestamp(), other.getReadingTimestamp())
               && areEqual(getId(), other.getId())
               && areEqual(getCorrelationId(), other.getCorrelationId(), true)
               && areEqual(getData(), other.getData(), true)
               && areEqual(getSensorId(), other.getSensorId(), true)
               && areEqual(getStoreId(), getStoreId())
               && areEqual(getUpc(), other.getUpc())
               && areEqual(getSerial(), other.getSerial())
               && areEqual(getRssi(), other.getRssi());
    }

    @Override
    public int hashCode() {
        final Instant readingTs = getReadingTimestamp() == null ? null : getReadingTimestamp().truncatedTo(ChronoUnit.SECONDS);
        final Instant creationTs = getCreationDate() == null ? null : getCreationDate().truncatedTo(ChronoUnit.SECONDS);
        final Instant modificationTs = getModificationDate() == null ? null : getModificationDate().truncatedTo(ChronoUnit.SECONDS);

        return new HashCodeBuilder(53, 91)
                        .append(creationTs)
                        .append(getId())
                        .append(modificationTs)
                        .append(getStoreId())
                        .append(readingTs)
                        .append(getSensorId())
                        .append(getData())
                        .append(getRssi())
                        .append(getCorrelationId())
                        .append(getUpc())
                        .append(getRssi())
                        .append(getSerial())
                        .build();
    }
}