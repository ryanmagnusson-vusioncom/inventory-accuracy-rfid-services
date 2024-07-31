package io.vusion.rfid.domain.model;

import com.google.gson.JsonObject;
import io.vusion.gson.utils.GsonHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.right;

@Getter @Setter
public class SGTIN96 implements EPC, Serializable {

    private EPCHeader header = EPCHeader.SGTIN_96;
    private SGTINFilter filter = SGTINFilter.POINT_OF_SALE_TRADE_ITEM;
    private SGTINPartition partition = SGTINPartition.FIVE; // 3 bits
    private BigInteger companyPrefix; // 20-40 bits; default 24-bits, per partition
    private BigInteger itemReference;
    private BigInteger serialNumber; // 38 bits

    //private BigInteger gtin;


//
//    private int companyPrefixLengthInBits = 24;
//    private int companyPrefixLengthInDigits = 7;
//    private int itemReferenceLengthInBits = 20;
//    private int itemReferenceLengthInDigits = 6;

    private String upc;
    private int upcCheckDigit;
    // 38 bits

    public String toUpc() {
        return upc + upcCheckDigit;
    }

    public String toString() {
        final JsonObject json = new JsonObject();
        json.addProperty("header", Optional.ofNullable(getHeader()).map(EPCHeader::getHex).orElse(null));
        json.addProperty("filter", Optional.ofNullable(getFilter()).map(SGTINFilter::getBinary).orElse(null));
        json.addProperty("partition", Optional.ofNullable(getPartition()).map(SGTINPartition::getBinary).orElse(null));
        json.addProperty("companyPrefix", getCompanyPrefix());
        json.addProperty("itemReference", getItemReference());
        json.addProperty("serialNumber", getSerialNumber());
        return GsonHelper.getGsonNull().toJson(json);
    }

//    public int getFilterValue() {
//        return filterValue;
//    }
//
//    public void setFilterValue(int filterValue) {
//        this.filterValue = filterValue;
//    }

//    public BigInteger getCompanyPrefix() {
//        return companyPrefix;
//    }
//
//    public void setCompanyPrefix(BigInteger companyPrefix) {
//        this.companyPrefix = companyPrefix;
//    }
//
//    public int getCompanyPrefixLengthInBits() {
//        return companyPrefixLengthInBits;
//    }
//
//    public void setCompanyPrefixLengthInBits(int companyPrefixLengthInBits) {
//        this.companyPrefixLengthInBits = companyPrefixLengthInBits;
//    }
//
//    public int getCompanyPrefixLengthInDigits() {
//        return companyPrefixLengthInDigits;
//    }
//
//    public void setCompanyPrefixLengthInDigits(int companyPrefixLengthInDigits) {
//        this.companyPrefixLengthInDigits = companyPrefixLengthInDigits;
//    }
//
//    public BigInteger getItemReference() {
//        return itemReference;
//    }
//
//    public void setItemReference(BigInteger itemReference) {
//        this.itemReference = itemReference;
//    }
//
//    public int getItemReferenceLengthInBits() {
//        return itemReferenceLengthInBits;
//    }
//
//    public void setItemReferenceLengthInBits(int itemReferenceLengthInBits) {
//        this.itemReferenceLengthInBits = itemReferenceLengthInBits;
//    }
//
//    public int getItemReferenceLengthInDigits() {
//        return itemReferenceLengthInDigits;
//    }
//
//    public void setItemReferenceLengthInDigits(int itemReferenceLengthInDigits) {
//        this.itemReferenceLengthInDigits = itemReferenceLengthInDigits;
//    }
//
//    public String getUpc() {
//        return upc;
//    }
//
//    public void setUpc(String upc) {
//        this.upc = upc;
//    }
//
//    public int getUpcCheckDigit() {
//        return upcCheckDigit;
//    }
//
//    public void setUpcCheckDigit(int upcCheckDigit) {
//        this.upcCheckDigit = upcCheckDigit;
//    }
//
//    public BigInteger getSerialNumber() {
//        return serialNumber;
//    }
//
//    public void setSerialNumber(BigInteger serialNumber) {
//        this.serialNumber = serialNumber;
//    }
//
//    public int getPartition() {
//        return partition;
//    }
//
//    public void setPartition(int partition) {
//        this.partition = partition;
//    }
}
