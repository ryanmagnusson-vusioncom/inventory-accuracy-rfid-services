package io.vusion.vtransmit.v2.commons.model;

import io.vusion.gson.utils.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder=true, setterPrefix="with", builderClassName = "Builder")
public class Store implements Serializable {

    protected String storeId;
    private EnumStoreStatus status = EnumStoreStatus.ACTIVE;
    private String name;

	public static Store fromId(String storeId) {
        return Store.builder().withStoreId(storeId).build();
    }

    @Override
    public String toString() { return GsonHelper.toJson(this); }
}
