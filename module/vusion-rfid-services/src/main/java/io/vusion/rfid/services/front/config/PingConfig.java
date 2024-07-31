package io.vusion.rfid.services.front.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vusion.gson.utils.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Configuration
public class PingConfig {

	@ConfigurationProperties(prefix="ping")
	@Bean
	public PingProperties getPingConfig() {
		return new PingProperties();
	}
	
    @Setter
	@AllArgsConstructor @NoArgsConstructor
    @SuperBuilder(toBuilder = true, setterPrefix = "with")
    public static class PingPriorityProperties {

        private Boolean required = false;

        @Override
        public String toString() {
            return GsonHelper.toJson(this);
        }
    }

    @Getter @Setter
    @AllArgsConstructor @NoArgsConstructor
    @Builder(toBuilder = true, setterPrefix = "with")
    public static class PingProperties {
        private PingPriorityProperties priority;

        @Override
        public String toString() {
            return GsonHelper.toJson(this);
        }
    }

    @Override
    public String toString() {
        return GsonHelper.toJson(this);
    }

}
