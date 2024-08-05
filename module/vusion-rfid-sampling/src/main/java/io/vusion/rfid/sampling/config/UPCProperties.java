package io.vusion.rfid.sampling.config;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.rfid.domain.model.EPCHeader;
import io.vusion.rfid.domain.model.SGTINPartition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder(toBuilder = true, setterPrefix = "with")
public class UPCProperties {
    private String upc;
    private String company;
    private Integer prefixDigits = null;
    private SGTINPartition partition = null;
    private Integer count = 10;
    private BigInteger serialStart = BigInteger.valueOf(13002437200L);

    public Integer getPrefixDigits() {
        if (prefixDigits == null) {
            return partition == null ? null : partition.getCompany().getDigits();
        }
        return prefixDigits;
    }

    @Override
    public String toString() { return GsonHelper.getGsonNull().toJson(this); }
}
