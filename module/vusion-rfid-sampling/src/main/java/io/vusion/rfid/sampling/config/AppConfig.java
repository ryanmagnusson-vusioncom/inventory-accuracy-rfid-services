package io.vusion.rfid.sampling.config;

import io.vusion.rfid.domain.model.SGTINPartition;
import io.vusion.secure.logs.VusionLogger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.length;

@Configuration
@Getter @Setter
@NoArgsConstructor
public class AppConfig {

    private static final VusionLogger LOGGER = VusionLogger.getLogger(AppConfig.class);

    @Getter @Setter
    static class UPCPropertyMap extends LinkedHashMap<String, UPCProperties> {

        @Override
        public UPCProperties put(String key, UPCProperties value) {
            if (value != null) {
                if (isBlank(value.getUpc())) {
                    value.setUpc(key);
                }

                if (isBlank(value.getCompany())) {
                    if (value.getPrefixDigits() != null) {
                        value.setCompany(left(leftPad(key, 13, '0'), value.getPrefixDigits()));
                    } else if (value.getPartition() != null) {
                        value.setCompany(left(leftPad(key, 13, '0'), value.getPartition().getCompany().getDigits()));
                    } else {
                        value.setCompany(left(key, key.length() / 2));
                    }
                }

                if (value.getPartition() == null) {
                    if (value.getPrefixDigits() == null || value.getPrefixDigits() < 1) {
                        value.setPrefixDigits(value.getCompany().length());
                    }
                    final SGTINPartition partition = SGTINPartition.stream().filter(part -> value.getPrefixDigits() == part.getCompany().getDigits()).findFirst().orElse(null);
                    value.setPartition(partition);
                    value.setPrefixDigits(Optional.ofNullable(partition).map(SGTINPartition::getCompany).map(SGTINPartition.Segment::getDigits).orElse(null));
                    LOGGER.debug("Identified partition: %s for prefix: '%s' and length: %d".formatted(partition, value.getCompany(), value.getPrefixDigits()));
                }
            }
            return super.put(key, value);
        }

        @Override
        public void putAll(Map<? extends String, ? extends UPCProperties> m) {
            if (isNotEmpty(m)) {
                m.forEach(this::put);
            }
        }

        @Override
        public UPCProperties compute(String key, BiFunction<? super String, ? super UPCProperties, ? extends UPCProperties> remappingFunction) {
            return super.compute(key, remappingFunction);
        }
    }

    @Value("${rfid.data.service.host:localhost:8080}")
    private String serviceHost;

    @Value("${rfid.data.store:lab}")
    private String storeId;

    @Value("${rfid.data.sensor:FE7486598A76}")
    private String sensorId;

    @Value("${rfid.data.alt-sensor:}")
    private String altSensorId;

    @Value("${rfid.data.update.interval:120}")
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration updateInterval;


    @ConfigurationProperties(prefix="rfid.data.items")
    @Bean
    public Map<String, UPCProperties> getItems() {
        return new UPCPropertyMap();
    }



    @Bean
    public RestClient getServiceClientBuilder() {
        return RestClient.builder()
                         .requestFactory(new HttpComponentsClientHttpRequestFactory())
                         .messageConverters(converters -> converters.add(new GsonHttpMessageConverter()))
                         .baseUrl("http://localhost:8080/epc/stores/{storeId}/sensors/{sensorId}")
                         .defaultUriVariables(Map.of("host", getServiceHost(),
                                                     "storeId", getStoreId(),
                                                     "sensorId", getSensorId()))
                         //.defaultHeader("My-Header", "Foo")
                         //.requestInterceptor(myCustomInterceptor)
                         //.requestInitializer(myCustomInitializer)
                         .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                         .build();
    }
}
