package io.vusion.rfid.services.model.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNullElseGet;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Getter
@Setter
public class UPCInventoryResponse extends StoreUPCResponse {
    @Getter(AccessLevel.NONE)
    private final Collection<SerializedUPC> items;

    public UPCInventoryResponse() {
        this.items = new LinkedHashSet<>();
    }

    @lombok.Builder(toBuilder = true, setterPrefix = "with")
    public UPCInventoryResponse(@lombok.Builder.ObtainVia(method="getStoreId") String storeId,
                                @lombok.Builder.ObtainVia(method="getUpc") String upc,
                                Collection<SerializedUPC> items) {
        super(storeId,upc);
        final Collection<SerializedUPC> upcs = requireNonNullElseGet(items, LinkedHashSet::new);
        if (upcs instanceof Set setOfUpcs) {
            this.items = setOfUpcs;
        } else {
            this.items = new LinkedHashSet<>(upcs);
        }
    }

    public void setItems(Collection<SerializedUPC> items) {
        this.items.clear();
        if (isNotEmpty(items)) {
            this.items.addAll(items);
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof UPCInventoryResponse;
    }

    public List<SerializedUPC> getItems() {
        return new ArrayList<>(items);
    }

    static class Builder {
        private Collection<SerializedUPC> items = new LinkedHashSet<>();
    }
}
