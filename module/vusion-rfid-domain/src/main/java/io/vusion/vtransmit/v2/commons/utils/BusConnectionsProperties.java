package io.vusion.vtransmit.v2.commons.utils;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.vusion.servicebus.ServiceBusDefinition;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "bus")
public class BusConnectionsProperties {

    @Value("${topic.prefix:}")
    private String topicPrefix;

    private static final String SUBSCRIPTION_SUFFIX = "Subscription";

    @Setter
    @Getter
    private Map<String, Map<String, ServiceBusDefinition>> connections;

    @PostConstruct
    public void init() {
        if(connections != null) {
            for (Map.Entry<String, Map<String, ServiceBusDefinition>> outerEntry : connections.entrySet()) {
                for (Map.Entry<String, ServiceBusDefinition> innerEntry : outerEntry.getValue().entrySet()) {
                    ServiceBusDefinition serviceBusDefinition = innerEntry.getValue();
                    if (serviceBusDefinition.getTopicName() == null) {
                        serviceBusDefinition.setTopicName(innerEntry.getKey());
                    }
                    serviceBusDefinition.setTopicName(topicPrefix + serviceBusDefinition.getTopicName());
                    if (serviceBusDefinition.getSubscriptionName() == null) {
                        serviceBusDefinition.setSubscriptionName(innerEntry.getKey() + SUBSCRIPTION_SUFFIX);
                    }
                }
            }
        }
    }
}
