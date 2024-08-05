package io.vusion.rfid.services.controller;

import io.vusion.rfid.data.model.StoreEntity;
import io.vusion.rfid.services.front.validator.EPCReadingRequestValidator;
import io.vusion.rfid.services.mapping.EPCReadingMapper;
import io.vusion.rfid.services.service.EPCReadingService;
import io.vusion.secure.logs.VusionLogger;
import io.vusion.vtransmit.v2.commons.annotation.VTransmitContext;
import io.vusion.vtransmit.v2.commons.dao.StoreDao;
import io.vusion.vtransmit.v2.commons.model.EnumEventType;
import io.vusion.vtransmit.v2.commons.model.EnumPriority;
import io.vusion.vtransmit.v2.commons.model.EnumStoreStatus;
import io.vusion.vtransmit.v2.commons.model.Store;
import io.vusion.vtransmit.v2.commons.utils.metric.MonitoredService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.HttpExchange;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.strip;

@RestController
@MonitoredService
@HttpExchange(url = "/stores/{storeId}")
@RequiredArgsConstructor
@Transactional
public class StoreController extends BaseController {

    private static final VusionLogger LOGGER = VusionLogger.getLogger(EPCController.class);

    private final StoreDao storeDao;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @VTransmitContext(priority = EnumPriority.MEDIUM, eventType = EnumEventType.STORE_INFO_QUERY)
    public ResponseEntity<?> getStore(@RequestHeader final HttpHeaders headers,
                                      @PathVariable final String storeId) {
        if (isBlank(storeId)) {
            return ResponseEntity.badRequest().body("storeId is required");
        }

        final String queryableStoreId = strip(storeId).toLowerCase();
        final StoreEntity storeEntity = storeDao.findByStoreId(queryableStoreId);
        if (storeEntity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No store found for storeId: " + storeId);
        }

        final Store store = Store.builder()
                                 .withStoreId(storeEntity.getStoreId())
                                 .withStatus(EnumStoreStatus.ACTIVE)
                                 .withName(storeEntity.getStoreName()).build();
        return ResponseEntity.ok(store);
    }
}