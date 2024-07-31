package io.vusion.vtransmit.v2.commons.utils.metric;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * Use to debug only
 * TO EVOLVE BY USING A EVICT CACHE, otherwise current implementation will OOM over time
 *
 */
public class MetricUnicity {
	private final String name;
	private final ConcurrentHashMap<String, AtomicLong> counter = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Long, ConcurrentSkipListSet<String>> reverseCounter = new ConcurrentHashMap<>();
	private final AtomicLong call = new AtomicLong();
	private long previousCall = 0;

	public void track(final String key) {
		if (log.isInfoEnabled()) {
			final long current = counter.computeIfAbsent(key, k -> new AtomicLong()).incrementAndGet();
			if (current > 1) {
				reverseCounter.computeIfAbsent(current, k -> new ConcurrentSkipListSet<>()).add(key);
				if (current > 2) {
					reverseCounter.get(current - 1).remove(key);
				}
			}
			call.incrementAndGet();
		}
	}

	public void log() {
		final long currentCall = call.get();
		if (previousCall != currentCall && log.isInfoEnabled()) {
			log.info("Call " + name + ": " + call.get() + ", " + reverseCounter.entrySet().stream()
					.map((e) -> String.format("%d: %d [\"%s\"]", e.getKey(), e.getValue().size(),
							e.getValue().first()))
					.sorted(Comparator.reverseOrder()).collect(Collectors.joining(",")));
			previousCall = currentCall;
		}
	}

	public MetricUnicity(final String name) {
		super();
		this.name = name;
	}
}
