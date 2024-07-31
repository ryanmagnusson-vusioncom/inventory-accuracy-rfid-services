package io.vusion.rfid.domain.model;

import io.vusion.gson.utils.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder = true, setterPrefix = "with", builderClassName = "Builder")
public class EPCSensor {
    private Long id;
    private String macAddress;
    private String location;

    @Override
    public String toString() { return GsonHelper.getGsonNull().toJson(this); }
}
