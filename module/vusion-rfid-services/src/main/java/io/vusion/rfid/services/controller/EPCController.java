package io.vusion.rfid.services.controller;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.rfid.domain.model.StoreEPCSensorReading;
import io.vusion.rfid.services.front.validator.EPCReadingRequestValidator;
import io.vusion.rfid.services.mapping.EPCReadingMapper;
import io.vusion.rfid.services.model.response.EPCReadingResponse;
import io.vusion.rfid.services.service.EPCReadingService;
import io.vusion.secure.logs.VusionLogger;
import io.vusion.vtransmit.v2.commons.annotation.VTransmitContext;
import io.vusion.vtransmit.v2.commons.model.EnumEventType;
import io.vusion.vtransmit.v2.commons.model.EnumPriority;
import io.vusion.vtransmit.v2.commons.utils.metric.MonitoredService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.HttpExchange;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@MonitoredService
@HttpExchange(url = "/epc/stores/{storeId}", contentType = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Transactional
public class EPCController extends BaseController {
	
	private static final VusionLogger LOGGER = VusionLogger.getLogger(EPCController.class);

    private final EPCReadingRequestValidator epcReadingRequestValidator;
	private final EPCReadingService epcReadingService;
    private final EPCReadingMapper  epcReadingMapper;
	
	@PostMapping("sensors/{sensorId}")
	@VTransmitContext(priority = EnumPriority.HIGH, eventType = EnumEventType.EPC_SENSOR_READING)
	public ResponseEntity<?> uploadReadings(@RequestHeader final HttpHeaders headers,
			                                @PathVariable final String storeId,
                                            @PathVariable final String sensorId,
			                                @RequestBody final Optional<String> body) {
		
		final Collection<StoreEPCSensorReading> epcReadings = epcReadingRequestValidator.validate(storeId, sensorId, body);
		return execute(storeId, "upload epc readings", store -> {
			epcReadingService.saveAll(epcReadings);
			return success("%d EPC readings uploaded for store '%s' and sensor '%s'".formatted(
                                size(epcReadings), storeId, sensorId));
		});
	}
	
	@GetMapping
	@VTransmitContext(priority = EnumPriority.MEDIUM, eventType = EnumEventType.EPC_SENSOR_QUERY)
	public ResponseEntity<?> getReadings(@RequestHeader final HttpHeaders headers,
                                         @PathVariable final String storeId,
                                         @RequestParam(defaultValue="") final String from,
                                         @RequestParam(defaultValue="") final String until) {
        return getReadings(headers, storeId, null, from, until);
	}

    @GetMapping("/sensor/{sensorId}")
    @VTransmitContext(priority = EnumPriority.MEDIUM, eventType = EnumEventType.EPC_SENSOR_QUERY)
    public ResponseEntity<?> getReadings(@RequestHeader final HttpHeaders headers,
                                         @PathVariable final String storeId,
                                         @PathVariable final String sensorId,
                                         @RequestParam(defaultValue="") final String from,
                                         @RequestParam(defaultValue="") final String until) {

        final Instant startingFrom = isBlank(from) ? Instant.EPOCH : GsonHelper.toDate(from).toInstant();
        final Instant endingAt = isBlank(until) ? Instant.now() : GsonHelper.toDate(until).toInstant();

        return execute(storeId, "retrieve epc readings", store -> {
            final Collection<StoreEPCSensorReading> readings = epcReadingService.findAllByDateTimeRange(storeId, startingFrom, endingAt);
            final Collection<EPCReadingResponse> responses = readings.stream().map(epcReadingMapper::toEPCReadingResponse).toList();
            return ResponseEntity.ok(responses);
        });
    }
}
