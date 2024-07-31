package io.vusion.vtransmit.v2.commons;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "vtransmit.retry")
public class RetryConfig {

	@Value("${vtransmit.retry.maxAttempts:25}")
	private int maxAttempts;
	@Value("${vtransmit.retry.minDelayInMs:10}")
	private int minDelayInMs;
	@Value("${vtransmit.retry.maxDelayInMs:60000}")
	private int maxDelayInMs;
	//
	// @Bean
	// public RetryConfigCustomizer retryCustomizer() {
	// return RetryConfigCustomizer
	// .of("backendA", builder -> builder
	// .intervalFunction(intervalWithExponentialRandomBackoff)
	// .maxAttempts(maxAttempts));
	// }

	// @Autowired
	// private RetryRegistry registry;
	//
	// @PostConstruct
	// public void postConstruct() {
	// registry.getEventPublisher()
	// .onEntryAdded(added -> {
	// added.getAddedEntry()
	// .getEventPublisher()
	// .onRetry(ev -> log.info("#### RetryRegistryEventListener message: {}", ev));
	// });
	// }
}