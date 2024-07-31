package io.vusion.rfid.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;

import com.microsoft.applicationinsights.attach.ApplicationInsights;

@SpringBootApplication(exclude = { JacksonAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ComponentScan(basePackages = "io.vusion")
@EnableRetry @EnableCaching
public class VtransmitRfidApplication {
	
	public static void main(final String[] args) {
		ApplicationInsights.attach();
		final ConfigurableApplicationContext context = SpringApplication.run(VtransmitRfidApplication.class, args);
		context.registerShutdownHook();
	}
}
