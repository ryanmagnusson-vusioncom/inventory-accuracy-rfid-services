package io.vusion.vtransmit.v2.commons.utils.metric;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricCounter {
	private final String name;
	private final AtomicLong counter = new AtomicLong();
	private final AtomicLong call = new AtomicLong();
	private long previousCounter = 0;
	private long previousCall = 0;
	private long previousLog = 0;
	private final Float[] perSecondOver5m;
	private int over5mIndex = 0;

	public void inc(final long delta) {
		counter.addAndGet(delta);
		call.incrementAndGet();
	}

	public void log() {
		final long currentCounter = counter.get();
		final long currentCall = call.get();
		final long now = System.currentTimeMillis();
		final float perMinute = (currentCounter - previousCounter) / ((now - previousLog) / 1000f);
		perSecondOver5m[over5mIndex++ % 5] = perMinute;
		if (previousCounter != currentCounter && log.isInfoEnabled()) {
			final float perSecondO5m = (float) Arrays.stream(perSecondOver5m)
					.mapToDouble(d -> d)
					.average()
					.orElse(0.0);
			final float avgCounterPerCall = (currentCounter - previousCounter) / (currentCall - previousCall);
			final float perHour = perMinute * 60f * 60f;
			final float perHourO5m = perSecondO5m * 60f * 60f;
			log.info("Counter {}: {} of {}, over 1m [{}/s, {}/h, avg {} per call], over 5m [{}/s, {}/h]", name,
					currentCounter - previousCounter, currentCounter, String.format("%.0f", perMinute),
					String.format("%.0f", perHour), String.format("%.0f", avgCounterPerCall),
					String.format("%.0f", perSecondO5m), String.format("%.0f", perHourO5m));
			previousCounter = currentCounter;
			previousCall = currentCall;
		}
		previousLog = now;
	}

	public MetricCounter(final String name) {
		super();
		this.name = name;
		perSecondOver5m = new Float[5];
		for (int i = 0; i < perSecondOver5m.length; i++) {
			perSecondOver5m[i] = 0f;
		}
	}

	public long get() {
		return counter.get();
	}
}
