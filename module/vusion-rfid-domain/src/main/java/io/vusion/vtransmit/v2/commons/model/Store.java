package io.vusion.vtransmit.v2.commons.model;

import io.vusion.gson.utils.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Entity
//@Table(name = "[store]")
//@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder=true, setterPrefix="with", builderClassName = "Builder")
public class Store {

    protected String storeId;
    private EnumStoreStatus status = EnumStoreStatus.ACTIVE;

	public static Store fromId(String storeId) {
        return Store.builder().withStoreId(storeId).build();
    }

    @Override
    public String toString() { return GsonHelper.toJson(this); }
}
