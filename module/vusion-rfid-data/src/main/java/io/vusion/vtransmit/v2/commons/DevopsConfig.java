package io.vusion.vtransmit.v2.commons;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "devops")
@EnableAutoConfiguration
@Getter
@Setter
public class DevopsConfig {

	private String deploymentMode;

	public boolean isMixedHfBle() {
		return "MIXED_HF_BLE".equals(deploymentMode);
	}
}
