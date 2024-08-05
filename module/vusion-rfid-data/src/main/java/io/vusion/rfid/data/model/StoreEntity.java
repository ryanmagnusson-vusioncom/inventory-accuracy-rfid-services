package io.vusion.rfid.data.model;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.vtransmit.v2.commons.model.EnumStoreStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.Serializable;

import static io.vusion.vtransmit.v2.commons.utils.BlankStringComparator.blanksLast;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;
import static org.apache.commons.lang3.StringUtils.stripToNull;

@Getter
@Setter
@DynamicUpdate
@Entity
@Table(name = "[store]",
    uniqueConstraints= {
        @UniqueConstraint(columnNames = { "store_id" })
})
@NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder = true, setterPrefix = "with")
public class StoreEntity extends AbstractStoreEntity implements Serializable, Comparable<StoreEntity> {
    @Column(name = "store_name")
    private String storeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnumStoreStatus status = EnumStoreStatus.ACTIVE;

    @Override
    public void setStoreId(String storeId) {
        super.setStoreId(stripToEmpty(storeId));
    }

    public void setStoreName(String storeName) {
        this.storeName = stripToNull(storeName);
    }

    @Override
    public String getFunctionalKey() {
        return getStoreId();
    }

    @Override
    public String toString() { return GsonHelper.getGsonNull().toString(); }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(113, 103)
                        .append(getId())
                        .append(getStoreId())
                        .build();
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof StoreEntity;
    }

    @Override
    public boolean equals(Object object) {
        return canEqual(object) && super.equals(object) && getStoreId().equals(((StoreEntity) object).getStoreId());
    }

    @Override
    public int compareTo(@Nullable StoreEntity other) {
        if (other == null) {
            return -1;
        }

        return comparing(StoreEntity::getStoreId, blanksLast(naturalOrder()))
                .thenComparing(StoreEntity::getId, nullsLast(naturalOrder()))
                .compare(this, other);
    }
}
