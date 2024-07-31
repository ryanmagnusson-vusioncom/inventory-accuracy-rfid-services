package io.vusion.rfid.services.front.validator;

import io.vusion.rfid.domain.model.EPCReading;
import io.vusion.rfid.domain.model.EPCSensor;
import io.vusion.rfid.domain.model.StoreEPCSensorReading;
import io.vusion.vtransmit.v2.commons.exceptions.ValidationException;
import io.vusion.vtransmit.v2.commons.model.Store;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class EPCReadingRequestValidator extends RequestValidator<EPCReading> {
	public final int MAXIMUM_DEVICES_FLASHED_BY_REQUEST = 1000;

	@Override
	public int getMaxRequestElements() {
		return MAXIMUM_DEVICES_FLASHED_BY_REQUEST;
	}

    public Collection<StoreEPCSensorReading> validate(String storeId, String macAddress, Optional<String> body) {
        //final Store store = Store.fromId(storeId);
        //final EPCSensor sensor = EPCSensor.builder().withMacAddress(sensorId).build();
        final Collection<EPCReading> readings = validate(storeId, body);
        return readings.stream().map(r -> StoreEPCSensorReading.builder()
                                                               .withStoreId(storeId)
                                                               .withMacAddress(macAddress)
                                                               .withRssi(r.getRssi())
                                                               .withData(r.getData())
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

        //checkNotTooManyDevices(store, request);
		if (request.getTimestamp() == null) {
			request.setTimestamp(Instant.now());
		}
		
		return request;
	}
}
