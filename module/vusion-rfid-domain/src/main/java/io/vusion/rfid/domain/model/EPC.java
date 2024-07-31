package io.vusion.rfid.domain.model;

public interface EPC {
    EPCHeader getHeader();
    EPCFilter getFilter();
    EPCFilter getPartition();
}
