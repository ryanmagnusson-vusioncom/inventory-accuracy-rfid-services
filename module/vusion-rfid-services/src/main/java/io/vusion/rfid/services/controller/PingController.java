package io.vusion.rfid.services.controller;

//import io.vusion.rfid.services.front.service.PingDeviceService;
import io.vusion.rfid.services.front.validator.PingValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.HttpExchange;

@RestController
@HttpExchange(url = "/api/v1/stores/{storeId}", contentType = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PingController extends BaseController {
	
	private final PingValidator pingValidator;
//	private final PingDeviceService pingService;
	
//	@PostMapping("labels/ping")
//	@VTransmitContext(eventType = EnumEventType.PING)
//	public ResponseEntity<Object> pingLabel(@PathVariable final String storeId, @RequestBody final Optional<String> payload) {
//		final List<PingRequest> pings = pingValidator.validate(storeId, payload);
//		return execute(storeId, "ping request", store -> {
//			final List<String> pingResults = pingService.pingDevices(store, pings);
//			return new ResponseEntity<>(new BaseResponse("ping " + pingResults.size() + " labels"), HttpStatus.OK);
//		});
//	}
}
