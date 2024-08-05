package io.vusion.rfid.services.service;

import org.junit.jupiter.api.Test;
import org.springframework.integration.ip.udp.UnicastSendingMessageHandler;
import org.springframework.integration.support.MessageBuilder;

public class TestUDPService {

//    private String udpServiceURL = "http://172.206.129.70";
    //private String udpServiceURL = "172.206.129.70";
    private String udpServiceURL = "localhost";


    @Test
    void sendMessage() {
        UnicastSendingMessageHandler handler =
                new UnicastSendingMessageHandler(udpServiceURL, 11111);
        String payload = "30340199D431D203070172BE";
        byte[] paybloadBytes = {0x3, 0x0, 0x3, 0x4, 0x0, 0x1, 0x9, 0x9,
                                0xD, 0x4, 0x3, 0x1, 0xD, 0x2, 0x0, 0x3,
                                0x0, 0x7, 0x0, 0x1, 0x7, 0x2, 0xB, 0xE};

        handler.handleMessage(MessageBuilder.withPayload(paybloadBytes).build());
    }

    @Test
    void sendMessageUsingClient() {
        String payload = "30340199D431D203070172BE";
        final TestUDPClient client = new TestUDPClient(udpServiceURL);
        client.sendMessageAsASCIIBytes(payload);
    }
}
