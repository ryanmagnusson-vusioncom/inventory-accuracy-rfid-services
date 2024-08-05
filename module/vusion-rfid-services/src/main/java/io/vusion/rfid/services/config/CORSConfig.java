package io.vusion.rfid.services.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Configuration
public class CORSConfig {

    @NoArgsConstructor
    @Getter @Setter
    public static class CORSMappedPaths {
        private String mapping;
        private Collection<String> paths = new LinkedHashSet<>();
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class CORSProperties {
        private Map<String, List<String>> mappings = new LinkedHashMap<>();
    }

    @ConfigurationProperties(prefix = "cors.allowed.origins")
    @Bean
    public CORSProperties corsProperties() {
        return new CORSProperties();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(CORSProperties corsProperties) {
        final Map<String, List<String>> mappings = corsProperties.getMappings();

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
//                mappings.forEach((mapping, origins) -> {
//                    final String[] arrayOfOrigins = origins.toArray(new String[0]);
//                    registry.addMapping(mapping).allowedOrigins(arrayOfOrigins);
//                });
//
                registry.addMapping("/epc/stores/**").allowedOriginPatterns("http://localhost:[*]","http://127.0.0.1:[*]","http://172.206.129.70:[*]");
                registry.addMapping("/inventory/stores/**").allowedOriginPatterns("http://localhost:[*]","http://127.0.0.1:[*]","http://172.206.129.70:[*]");
                registry.addMapping("/stores/**").allowedOriginPatterns("http://localhost:[*]","http://127.0.0.1:[*]","http://172.206.129.70:[*]");
            }
        };
    }
}
