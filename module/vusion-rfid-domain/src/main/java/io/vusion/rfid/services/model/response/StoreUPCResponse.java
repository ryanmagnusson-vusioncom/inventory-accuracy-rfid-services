package io.vusion.rfid.services.model.response;

import io.vusion.gson.utils.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.Serializable;

import static io.vusion.vtransmit.v2.commons.utils.BlankStringComparator.blanksLast;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class StoreUPCResponse implements Serializable, Comparable<StoreUPCResponse> {
    private String storeId;
    private String upc;

    @Override
    public String toString() { return GsonHelper.getGsonNull().toJson(this); }

    @Override
    public int compareTo(@Nullable StoreUPCResponse other) {
        if (other == null) {
            return -1;
        }

        return comparing(StoreUPCResponse::getStoreId, blanksLast(naturalOrder()))
                .thenComparing(StoreUPCResponse::getUpc, blanksLast(naturalOrder()))
                .compare(this, other);
    }
}
