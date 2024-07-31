package io.vusion.vtransmit.v2.commons.utils;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;

import io.vusion.gson.utils.GsonHelper;

@Configuration
@EnableAutoConfiguration
public class DefaultGsonConfig {
	
	// FIXME ELA: this gson is not the default Spring Serializer
	// -> To be fixed
	
	// For defining another default Gson, use annotation: @Primary
	@Bean()
	public Gson gson() {
		return GsonHelper.getGson();
	}
}