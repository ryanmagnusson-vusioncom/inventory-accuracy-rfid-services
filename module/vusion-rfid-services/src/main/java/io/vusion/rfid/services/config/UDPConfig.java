package io.vusion.rfid.services.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.Serializable;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Getter
public class UDPConfig {


    @Value("${rfid.default.sensor-mac:}")
    private String defaultSensorMac;

    @Value("${rfid.default.store-id:lab")
    private String defaultStoreId;

    @Value("${rfid.udp.channel:inboundChannel}")
    private String channel;

    @Value("${rfid.udp.port:11111}")
    private Integer port;

    @Value("${rfid.udp.executor.core-pool-size:1}")
    private Integer corePoolSize;

    @Value("${rfid.udp.executor.max-pool-size:20}")
    private Integer maxPoolSize;

    @Value("${rfid.udp.executor.queue-capacity:99}")
    private Integer queueSize;

    @Bean
    public MessageChannel inboundChannel() {
        return new DirectChannel();
    }

    @Bean(name = "rfidUdpReceivingAdapter")
    public UnicastReceivingChannelAdapter udpReceivingAdapter() {
        UnicastReceivingChannelAdapter adapter = new UnicastReceivingChannelAdapter(getPort());
        adapter.setOutputChannel(inboundChannel());
        adapter.setTaskExecutor(getTaskExecutor());
        adapter.setOutputChannelName(getChannel());
        return adapter;
    }

    // this task executor will define how many concurrent connection UDP can handle
    TaskExecutor getTaskExecutor() {

        ThreadPoolTaskExecutor ioExec = new ThreadPoolTaskExecutor();
        ioExec.setCorePoolSize(getCorePoolSize());
        ioExec.setMaxPoolSize(getMaxPoolSize());
        ioExec.setQueueCapacity(getQueueSize());
        ioExec.setThreadNamePrefix("rfid-udp-");
        ioExec.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        ioExec.initialize();
        return ioExec;
    }

//    @Bean
//    public IntegrationFlow processUniCastUdpMessage() {
//        return IntegrationFlow.from(new UnicastReceivingChannelAdapter(getUdpServerPort()))
//                    .handle("RfidUdpServer", "handleMessage")
//                    .get();
//    }

}
