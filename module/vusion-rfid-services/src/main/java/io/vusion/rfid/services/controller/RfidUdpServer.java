package io.vusion.rfid.services.controller;

import io.vusion.rfid.domain.model.StoreEPCSensorReading;
import io.vusion.rfid.services.config.UDPConfig;
import io.vusion.rfid.services.service.EPCReadingService;
import io.vusion.secure.logs.VusionLogger;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RfidUdpServer {

    private final static VusionLogger LOGGER = VusionLogger.getLogger(RfidUdpServer.class);

    private final UDPConfig udpConfig;
    private final EPCReadingService epcReadingService;
    public void handleMessage(Message message) {
        final byte[] data = (byte[])message.getPayload();
        if (ArrayUtils.isEmpty(data)) {
            LOGGER.info("Empty payload received.");
            return;
        }

        final StringBuilder buffer = new StringBuilder(data.length * 2 + 2);
        for (byte b : data) {
            buffer.append(String.format("%02X", b));
        }
        final String epcData = buffer.toString();
        LOGGER.debug("Received a reading with EPC: %s".formatted(epcData));
        final StoreEPCSensorReading reading = StoreEPCSensorReading.builder()
                                                                   .withStoreId(udpConfig.getDefaultStoreId())
                                                                   .withMacAddress(udpConfig.getDefaultSensorMac())
                                                                   .withData(epcData)
                                                                   .withTimestamp(Instant.now())
                                                                   .withRssi(0)
                                                                   .build();
        epcReadingService.save(reading);
    }
}
