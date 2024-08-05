package io.vusion.rfid.services.model.response;

import io.vusion.gson.utils.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.Instant;

import static io.vusion.vtransmit.v2.commons.utils.BlankStringComparator.blanksLast;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true, setterPrefix = "with")
public class UPCInventorySnapshot implements Serializable, Comparable<UPCInventorySnapshot> {
    @EqualsAndHashCode.Include
    private Instant timestamp;
    @EqualsAndHashCode.Include
    private String upc;
    private Integer count;

    @Override
    public int compareTo(@Nullable UPCInventorySnapshot other) {
        if (other == null) {
            return -1;
        }

        return comparing(UPCInventorySnapshot::getTimestamp, nullsLast(naturalOrder()))
                .thenComparing(UPCInventorySnapshot::getUpc, blanksLast(naturalOrder()))
                .compare(this, other);
    }

    @Override
    public String toString() { return GsonHelper.getGsonNull().toJson(this); }
}
