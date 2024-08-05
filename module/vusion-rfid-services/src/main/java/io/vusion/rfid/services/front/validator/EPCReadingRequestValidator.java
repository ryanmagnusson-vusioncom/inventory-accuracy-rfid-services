package io.vusion.rfid.services.front.validator;

import io.vusion.rfid.domain.model.EPCReading;
import io.vusion.rfid.domain.model.StoreEPCSensorReading;
import io.vusion.vtransmit.v2.commons.exceptions.ValidationException;
import io.vusion.vtransmit.v2.commons.model.Store;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.strip;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;
import static org.apache.commons.lang3.StringUtils.stripToNull;

@Component
public class EPCReadingRequestValidator extends RequestValidator<EPCReading> {
	public final int MAXIMUM_DEVICES_FLASHED_BY_REQUEST = 1000;

	@Override
	public int getMaxRequestElements() {
		return MAXIMUM_DEVICES_FLASHED_BY_REQUEST;
	}

    protected String validateSensorId(String sensorId) {
        if (isBlank(sensorId)) {
            throw new ValidationException("SensorId is required");
        }

        return strip(sensorId).replaceAll("[:\\-\\s]+", "").toUpperCase();
    }

    public Collection<StoreEPCSensorReading> validate(String storeId, String sensorId, Optional<String> body) {
        final Collection<EPCReading> readings = validate(stripToEmpty(storeId), body);
        return readings.stream().map(r -> StoreEPCSensorReading.builder()
                                                               .withStoreId(stripToEmpty(storeId))
                                                               .withSensorId(validateSensorId(sensorId))
                                                               .withRssi(r.getRssi())
                                                               .withData(stripToEmpty(r.getData()).toUpperCase())
                                                               .withTimestamp(r.getTimestamp())
                                                               .build())
                                .map(StoreEPCSensorReading.class::cast)
                                .toList();
    }

	@Override
	protected EPCReading validate(final Store store, final EPCReading request) {
		if (isBlank(request.getData())) {
            throw new ValidationException("Data missing for at store: %s and EPCReading: %s".formatted(store.getStoreId(), request));
        }

        request.setData(strip(request.getData()).toUpperCase());

		if (request.getTimestamp() == null) {
			request.setTimestamp(Instant.now());
		}
		
		return request;
	}
}
