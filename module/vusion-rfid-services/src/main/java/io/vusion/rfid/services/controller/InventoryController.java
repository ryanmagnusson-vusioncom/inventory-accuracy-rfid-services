package io.vusion.rfid.services.controller;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.rfid.domain.model.SGTIN96;
import io.vusion.rfid.domain.model.StoreEPCSensorReading;
import io.vusion.rfid.domain.utils.EPCUtility;
import io.vusion.rfid.services.front.validator.EPCReadingRequestValidator;
import io.vusion.rfid.services.mapping.EPCReadingMapper;
import io.vusion.rfid.services.model.response.EPCReadingResponse;
import io.vusion.rfid.services.model.response.SerializedGTIN;
import io.vusion.rfid.services.model.response.SerializedUPC;
import io.vusion.rfid.services.model.response.StoreUPCResponse;
import io.vusion.rfid.services.model.response.UPCInventoryResponse;
import io.vusion.rfid.services.model.response.UPCInventorySnapshot;
import io.vusion.rfid.services.service.EPCReadingService;
import io.vusion.secure.logs.VusionLogger;
import io.vusion.vtransmit.v2.commons.annotation.VTransmitContext;
import io.vusion.vtransmit.v2.commons.model.EnumEventType;
import io.vusion.vtransmit.v2.commons.model.EnumPriority;
import io.vusion.vtransmit.v2.commons.utils.metric.MonitoredService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.HttpExchange;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static java.util.Collections.reverseOrder;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@MonitoredService
@HttpExchange(url = "/inventory/stores/{storeId}")
@RequiredArgsConstructor
@Transactional
public class InventoryController extends BaseController {

    private static final VusionLogger LOGGER = VusionLogger.getLogger(EPCController.class);

    private final EPCReadingRequestValidator epcReadingRequestValidator;
    private final EPCReadingService epcReadingService;
    private final EPCReadingMapper  epcReadingMapper;

    @GetMapping(path = "items", produces = MediaType.APPLICATION_JSON_VALUE)
    @VTransmitContext(priority = EnumPriority.MEDIUM, eventType = EnumEventType.EPC_SENSOR_QUERY)
    public ResponseEntity<?> getItems(@RequestHeader final HttpHeaders headers,
                                      @PathVariable final String storeId,
                                      @RequestParam(defaultValue="") final String from,
                                      @RequestParam(defaultValue="") final String until) {
        return getItems(headers, storeId, null, from, until);
    }

    @GetMapping(path = "upc/{code}/items", produces = MediaType.APPLICATION_JSON_VALUE)
    @VTransmitContext(priority = EnumPriority.MEDIUM, eventType = EnumEventType.EPC_SENSOR_QUERY)
    public ResponseEntity<?> getItems(@RequestHeader final HttpHeaders headers,
                                      @PathVariable final String storeId,
                                      @PathVariable final String code,
                                      @RequestParam(defaultValue="") final String from,
                                      @RequestParam(defaultValue="") final String until) {

        final Instant startingFrom = isBlank(from) ? Instant.EPOCH : GsonHelper.toDate(from).toInstant();
        final Instant endingAt = isBlank(until) ? Instant.now() : GsonHelper.toDate(until).toInstant();

        return execute(storeId, "retrieve epc readings", store -> {
            final Collection<StoreEPCSensorReading> readings = epcReadingService.findAllByUPCDateTimeRange(storeId, code, startingFrom, endingAt);
            final Collection<UPCInventoryResponse> items = readings.stream()
                                                                   .map(epcReadingMapper::toEPCReadingResponse)
                                                                   .map(r -> Pair.of(new StoreUPCResponse(r.getStoreId(), r.getUpc()),
                                                                                     SerializedGTIN.builder()
                                                                                                   .withEpc(r.getData())
                                                                                                   .withUpc(r.getUpc())
                                                                                                    .withSerialNumber(r.getSerial())
                                                                                                    .build()))
                                                                   .collect(groupingBy(Pair::getLeft, LinkedHashMap::new, mapping(Pair::getRight, toList())))
                                                                   .entrySet()
                                                                   .stream()
                                                                   .map(entry -> UPCInventoryResponse.builder()
                                                                                                     .withStoreId(entry.getKey().getStoreId())
                                                                                                     .withUpc(entry.getKey().getUpc())
                                                                                                     .withItems(new ArrayList<>(entry.getValue()))
                                                                                                     .build())
                                                                   .toList();
            LOGGER.info("Returning with %d items: %s".formatted(size(items), GsonHelper.getGsonNull().toJson(items)));

            return ResponseEntity.ok(items);
        });
    }

    Map<UPCInventorySnapshot, Set<String>> toSnapshots(Collection<StoreEPCSensorReading> readings) {
        if (isEmpty(readings)) {
            return emptyMap();
        }

        final List<Pair<UPCInventorySnapshot, String>> snapshots = readings.stream()
                                                                           .map(r -> {
                                                                                final SGTIN96 sgtin = EPCUtility.parseSgtin96(r.getData());
                                                                                final UPCInventorySnapshot snapshot = UPCInventorySnapshot.builder()
                                                                                                                                          .withTimestamp(r.getTimestamp())
                                                                                                                                          .withUpc(sgtin.getUpc())
                                                                                                                                          .build();
                                                                                return Pair.of(snapshot, r.getData());
                                                                             })
                                                                             .toList();

        return snapshots.stream()
                        .collect(groupingBy(Pair::getLeft,
                                            LinkedHashMap::new,
                                            mapping(Pair::getRight, toSet())));

    }

    @GetMapping(path = "upc/{code}/snapshots", produces = MediaType.APPLICATION_JSON_VALUE)
    @VTransmitContext(priority = EnumPriority.MEDIUM, eventType = EnumEventType.EPC_SENSOR_QUERY)
    public ResponseEntity<?> getUPCInventorySnapshots(@RequestHeader final HttpHeaders headers,
                                                       @PathVariable final String storeId,
                                                       @PathVariable final String code,
                                                       @RequestParam(defaultValue="") final String from,
                                                       @RequestParam(defaultValue="") final String until,
                                                       @RequestParam(defaultValue="300") final Integer interval) {

        final Instant startingFrom = isBlank(from) ? Instant.EPOCH : GsonHelper.toDate(from).toInstant();
        final Instant endingAt = isBlank(until) ? Instant.now() : GsonHelper.toDate(until).toInstant();

        final Collection<StoreEPCSensorReading> readings = epcReadingService.findAllByUPCDateTimeRange(storeId, code, startingFrom, endingAt);

        final Map<UPCInventorySnapshot, Set<String>> snapshotsWithData = toSnapshots(readings);
        final Collection<UPCInventorySnapshot> snapshots = snapshotsWithData.entrySet()
                                                                            .stream()
                                                                            .map(entry -> {
                                                                                entry.getKey().setCount(size(entry.getValue()));
                                                                                return entry.getKey();
                                                                            })
                                                                            .toList();

        LOGGER.info("Returning with %d total snapshots: %s".formatted(
                size(snapshots), GsonHelper.getGsonNull().toJson(snapshots)));


        return ResponseEntity.ok(snapshots);
    }


    @GetMapping(path = "snapshots", produces = MediaType.APPLICATION_JSON_VALUE)
    @VTransmitContext(priority = EnumPriority.MEDIUM, eventType = EnumEventType.EPC_SENSOR_QUERY)
    public ResponseEntity<?> getItemInventorySnapshots(@RequestHeader final HttpHeaders headers,
                                      @PathVariable final String storeId,
                                      @PathVariable final String code,
                                      @RequestParam(defaultValue="") final String from,
                                      @RequestParam(defaultValue="") final String until,
                                      @RequestParam(defaultValue="300") final Integer interval) {

        final Instant startingFrom = isBlank(from) ? Instant.EPOCH : GsonHelper.toDate(from).toInstant();
        final Instant endingAt = isBlank(until) ? Instant.now() : GsonHelper.toDate(until).toInstant();

        final Collection<StoreEPCSensorReading> readings = epcReadingService.findAllByUPCDateTimeRange(storeId, null, startingFrom, endingAt);

        final Map<UPCInventorySnapshot, Set<String>> snapshotsWithData = toSnapshots(readings);
        final Collection<UPCInventorySnapshot> snapshots = snapshotsWithData.entrySet()
                                                                            .stream()
                                                                            .map(entry -> {
                                                                                entry.getKey().setCount(size(entry.getValue()));
                                                                                return entry.getKey();
                                                                            })
                                                                            .toList();

        final Instant intervalStart = snapshots.stream().map(UPCInventorySnapshot::getTimestamp).filter(Objects::nonNull).sorted(reverseOrder()).findFirst().orElse(startingFrom);

        final List<Instant> intervals = new ArrayList<>();
        Instant nextInterval = intervalStart;
        while (nextInterval.isBefore(endingAt)) {
            intervals.add(nextInterval);
            nextInterval = nextInterval.plusSeconds(interval);
        }
        intervals.add(nextInterval.plusSeconds(interval));


        final Map<Instant, Collection<UPCInventorySnapshot>> snapshotsByInterval = new LinkedHashMap<>();
        final Iterator<Instant> intervalIterator = intervals.iterator();
        Instant nextSliceStarts = intervalIterator.next().truncatedTo(ChronoUnit.SECONDS);
        while (intervalIterator.hasNext()) {
            Instant previousSliceStarts = nextSliceStarts;
            nextSliceStarts = intervalIterator.next();
            if (nextSliceStarts == null) {
                final Collection<UPCInventorySnapshot> remaining = snapshots.stream().filter(snap -> snap.getTimestamp().isAfter(previousSliceStarts)).toList();
                snapshotsByInterval.computeIfAbsent(previousSliceStarts, k -> new ArrayList<>()).addAll(remaining);
            } else {
                nextSliceStarts = nextSliceStarts.truncatedTo(ChronoUnit.SECONDS);
                final Instant finalizedNextSliceStarts = nextSliceStarts;
                final Collection<UPCInventorySnapshot> timeslice = snapshots.stream()
                                                                            .filter(snap -> {
                                                                                final Instant ts = snap.getTimestamp().truncatedTo(ChronoUnit.SECONDS);
                                                                                return ts.equals(previousSliceStarts) ||
                                                                                       (ts.isAfter(previousSliceStarts) && ts.isBefore(finalizedNextSliceStarts));
                                                                            }).toList();
                snapshotsByInterval.computeIfAbsent(previousSliceStarts, k -> new ArrayList<>()).addAll(timeslice);
            }
        }

        LOGGER.info("Returning with %d intervals of %d seconds each and %d total snapshots".formatted(
                        size(snapshotsByInterval),
                        interval,
                        snapshotsByInterval.values().stream().mapToInt(Collection::size).sum()
                        ));

        return ResponseEntity.ok(snapshotsByInterval);
    }
}

