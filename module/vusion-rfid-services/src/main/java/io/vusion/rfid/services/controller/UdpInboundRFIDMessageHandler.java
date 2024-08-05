package io.vusion.rfid.services.controller;

import io.vusion.rfid.domain.model.StoreEPCSensorReading;
import io.vusion.rfid.services.config.UDPConfig;
import io.vusion.rfid.services.service.EPCReadingService;
import io.vusion.secure.logs.VusionLogger;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@MessageEndpoint
@RequiredArgsConstructor
public class UdpInboundRFIDMessageHandler {

    private final static VusionLogger LOGGER = VusionLogger.getLogger(UdpInboundRFIDMessageHandler.class);

    private final UDPConfig udpConfig;
    private final EPCReadingService epcReadingService;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("UDPServer initialized with port: %s".formatted(Optional.ofNullable(udpConfig).map(UDPConfig::getPort).orElse(null)));
    }

    @ServiceActivator(inputChannel = "inboundChannel")
    public void handleMessage(Message message, @Headers Map<String, Object> headerMap) {
        final byte[] payload = (byte[]) message.getPayload();
        if (ArrayUtils.isEmpty(payload)) {
            LOGGER.info("Received UDP empty payload");
            return;
        }

        final String text = new String(payload);
        LOGGER.info("Received UDP payload: %s".formatted(text));

//        final StringBuilder buffer = new StringBuilder(data.length * 2 + 2);
//        for (byte b : data) {
//            buffer.append(String.format("%02X", b));
//        }
//        final String epcData = buffer.toString();
//        LOGGER.debug("Received a reading with EPC: %s".formatted(epcData));
        final StoreEPCSensorReading reading = StoreEPCSensorReading.builder()
                                                                   .withStoreId(udpConfig.getDefaultStoreId())
                                                                   .withSensorId(udpConfig.getDefaultSensorMac())
                                                                   .withData(text)
                                                                   .withTimestamp(Instant.now())
                                                                   .withRssi(0)
                                                                   .build();
        epcReadingService.save(reading);
        try {
            sendAcknowledgement(message);
        } catch (IOException e) {
            LOGGER.error("Error sending ACK message for message: '%s'; %s".formatted(text, ExceptionUtils.getMessage(e)), e);
        }
    }

    void sendAcknowledgement(Message<byte[]> message) throws IOException {
        String ackMessage = "OK";
        final MessageHeaders headers = message.getHeaders();
        final String remoteAddress = Objects.toString(headers.get("ip_address"), "");
        final String remotePortHeader = Objects.toString(headers.get("ip_port"), "");

        Integer remotePortNumber;
        if (isBlank(remotePortHeader)) {
            LOGGER.info("No report port found for remote IP address: %s, using same as UDP accepting: %d".formatted(remoteAddress, udpConfig.getPort()));
            remotePortNumber = udpConfig.getPort();
        } else {
            try {
                remotePortNumber = Integer.parseInt(remotePortHeader);
            } catch (NumberFormatException e) {
                LOGGER.error("Invalid remote port: '%s', using same as UDP accepting: %d".formatted(remotePortHeader));
                remotePortNumber = udpConfig.getPort();
            }
        }

        final InetAddress remoteInetAddress = InetAddress.getByName(remoteAddress);
        final DatagramPacket ackPacket = new DatagramPacket(ackMessage.getBytes(), ackMessage.length(),
                                                            remoteInetAddress, remotePortNumber);

        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.send(ackPacket);
        }
    }
}
