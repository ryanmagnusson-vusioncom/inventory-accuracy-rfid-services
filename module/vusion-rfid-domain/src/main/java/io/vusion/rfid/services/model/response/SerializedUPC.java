package io.vusion.rfid.services.model.response;

import io.vusion.gson.utils.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.math.BigInteger;

import static io.vusion.vtransmit.v2.commons.utils.BlankStringComparator.blanksLast;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static org.apache.commons.lang3.StringUtils.stripToNull;

@AllArgsConstructor @NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@EqualsAndHashCode
public class SerializedUPC implements Serializable, Comparable<SerializedUPC> {

    private String upc;
    @Getter @Setter
    private BigInteger serialNumber;

    public void setUPC(String upc) { this.upc = stripToNull(upc); }
    public String getUPC() { return upc; }

    @Override
    public int compareTo(@Nullable SerializedUPC other) {
        if (other == null) {
            return -1;
        }
        return comparing(SerializedUPC::getUPC, blanksLast(naturalOrder()))
                .thenComparing(SerializedUPC::getSerialNumber, nullsLast(naturalOrder()))
                .compare(this, other);

    }

    @Override
    public String toString() {
        return GsonHelper.getGsonNull().toJson(this);
    }
}
