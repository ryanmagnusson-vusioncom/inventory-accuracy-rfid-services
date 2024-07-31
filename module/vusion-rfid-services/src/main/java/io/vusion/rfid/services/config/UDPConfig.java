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

    @Value("${rfid.udp.server.port:111111}")
    private Integer udpServerPort;

    @Bean
    public IntegrationFlow processUniCastUdpMessage() {
        return IntegrationFlow.from(new UnicastReceivingChannelAdapter(getUdpServerPort()))
                    .handle("RfidUdpServer", "handleMessage")
                    .get();
    }

}
