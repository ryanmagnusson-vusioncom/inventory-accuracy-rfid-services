package io.vusion.rfid.services.model.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;

import static io.vusion.vtransmit.v2.commons.utils.BlankStringComparator.blanksLast;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.StringUtils.stripToNull;

@AllArgsConstructor @NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@EqualsAndHashCode(callSuper = true)
public class SerializedGTIN extends SerializedUPC {

    private String epc;

    public void setEPC(String code) { this.epc = stripToNull(code); }
    public String getEPC() { return epc; }

    @Override
    public int compareTo(@Nullable SerializedUPC other) {
        final int computedDiff = super.compareTo(other);
        if (computedDiff != 0) {
            return computedDiff;
        }
        if (other instanceof SerializedGTIN gtin) {
            return comparing(SerializedGTIN::getEPC).compare(this, gtin);
        }
        return 0;
    }
}
