package io.vusion.rfid.domain.model;

/**
 * A three bit value used to indicate more specifically what an EPC is for.
 */
public interface EPCFilter {

    int getValue();
    String getBinary();
}
