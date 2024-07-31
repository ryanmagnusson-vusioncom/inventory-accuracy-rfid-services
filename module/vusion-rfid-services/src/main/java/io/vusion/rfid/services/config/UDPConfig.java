package io.vusion.rfid.services.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;

@Configuration
@Getter @Setter
public class UDPConfig {

    @Value("${rfid.udp.server.port:11111}")
    private Integer udpServerPort;

    @Value("${rfid.default.sensor.mac:EC62608F4AE0}")
    private String defaultSensorMac;

    @Value("${rfid.default.store.id:lab")
    private String defaultStoreId;


    @Bean
    public IntegrationFlow processUniCastUdpMessage() {
        return IntegrationFlow.from(new UnicastReceivingChannelAdapter(getUdpServerPort()))
                    .handle("RfidUdpServer", "handleMessage")
                    .get();
    }

}
