package io.vusion.vtransmit.v2.commons.utils;

import java.util.ConcurrentModificationException;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import io.vusion.secure.logs.VusionLogger;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RetryService {
	private final VusionLogger logger = VusionLogger.getLogger(RetryService.class);

	private final BaseApplicationConfig applicationConfig;

	public void retryConcurrentModification(final Runnable consumer) {
		
		boolean success = false;
		int retryCount = 0;
		final int maxRetryCount = applicationConfig.getMaxConcurrentModificationRetryCount();
		RuntimeException currentException = null;
		
		do {
			try {
				consumer.run();
				success = true;
			} catch (final ConcurrentModificationException | DataIntegrityViolationException e) {
				logger.warn("Concurrent modification, retrying (retry " + retryCount + "/" + maxRetryCount + ")");
				currentException = e;
				retryCount++;
			}
		} while (!success && retryCount <= applicationConfig.getMaxConcurrentModificationRetryCount());
		
		if (!success) {
			throw currentException;
		}
	}
	
	public <V> V retryConcurrentModification(final RunnableWithReturn<V> consumer) {
		final AtomicReference<V> result = new AtomicReference<>();
		retryConcurrentModification(() -> result.set(consumer.run()));
		
		return result.get();
	}
	
	public interface RunnableWithReturn<V> {
		V run();
	}
	
}
