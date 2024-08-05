package io.vusion.rfid.sampling.client;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.rfid.domain.model.EPCReading;
import io.vusion.rfid.domain.model.SGTIN96;
import io.vusion.rfid.domain.model.SGTINFilter;
import io.vusion.rfid.domain.model.SGTINPartition;
import io.vusion.rfid.domain.utils.EPCUtility;
import io.vusion.rfid.sampling.config.AppConfig;
import io.vusion.rfid.sampling.config.UPCProperties;
import io.vusion.secure.logs.VusionLogger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNullElse;
import static java.util.stream.Collectors.toCollection;
import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.right;
import static org.apache.commons.lang3.StringUtils.stripStart;

@Getter
@Component
@RequiredArgsConstructor
public class RFIDClient {
    private static final VusionLogger LOGGER = VusionLogger.getLogger(RFIDClient.class);
    private static final Random RANDOM = new SecureRandom();
    private final AppConfig appConfig;
    private final RestClient restClient;

    private static final String URI_PATTERN = "http://{host}/epc/stores/{storeId}/sensors/{sensorId}";

    UPCProperties getUpcProperties(String upc) {
        return appConfig.getItems().computeIfAbsent(upc, nbr -> UPCProperties.builder()
                                                                             .withUpc(nbr)
                                                                             .withPartition(SGTINPartition.FIVE)
                                                                             .build());
    }

    List<EPCReading> loadReadings(UPCProperties properties) {
        final int randomInt = RANDOM.nextInt(1, 100);
        final boolean trimSomeReads = randomInt % 4 == 0 && RANDOM.nextBoolean();

        final List<EPCReading> readings =  IntStream.rangeClosed(0, properties.getCount())
                        .mapToObj(offset -> {
                            final BigInteger serial = properties.getSerialStart().add(BigInteger.valueOf(offset));
                            final SGTIN96 serializedGTIN = toSGTIN(properties, serial);
                            final String epcData = toEPC(serializedGTIN);
                            final SGTIN96 sgtin = EPCUtility.parseSgtin96(epcData);


                            return EPCReading.builder()
                                             .withData(toEPC(toSGTIN(properties, serial)))
                                             .withRssi(RANDOM.nextInt(40, 120) * -1)
                                             .withTimestamp(Instant.now())
                                             .build();
                        })
                        .map(EPCReading.class::cast)
                        .collect(toCollection(ArrayList::new));

        if (trimSomeReads) {
            LOGGER.info("Trimming... ");
            readings.remove(RANDOM.nextInt(0, readings.size()));
            if (RANDOM.nextBoolean()) {
                readings.remove(RANDOM.nextInt(0, readings.size()));
            }
        }
        return readings;
    }

    public Collection<EPCReading> uploadSensorData(String store, String sensor, UPCProperties properties) {
        final Collection<EPCReading> readings = loadReadings(properties);
        return uploadEPCReadings(restClient.post().uri(URI_PATTERN, Map.of("host", appConfig.getServiceHost(),
                                                                           "storeId", store,
                                                                           "sensorId", sensor)),
                                 readings);
    }

    SGTIN96 toSGTIN(String upc) {
        final UPCProperties properties = appConfig.getItems().computeIfAbsent(upc, nbr -> UPCProperties.builder()
                                                                                                       .withUpc(nbr)
                                                                                                       .withPartition(SGTINPartition.FIVE)
                                                                                                       .build());
        return toSGTIN(properties);
    }

    SGTIN96 toSGTIN(UPCProperties properties) {

        SGTINPartition partition = properties.getPartition();
        if (partition == null) {
            partition = SGTINPartition.FIVE;
        }

        final String upcDigits = leftPad(properties.getUpc(), 13, '0');
        final SGTIN96 sgtin = new SGTIN96();
        final BigInteger companyBigInt = new BigInteger(stripStart(left(upcDigits, partition.getCompany().getDigits()), "0"));
        sgtin.setCompanyPrefix(companyBigInt);

        final BigInteger itemBigInt = new BigInteger(stripStart(right(upcDigits, partition.getItemReference().getDigits()), "0"));
        sgtin.setItemReference(itemBigInt);
        sgtin.setPartition(partition);
        sgtin.setFilter(SGTINFilter.POINT_OF_SALE_TRADE_ITEM);
        return sgtin;
    }

    SGTIN96 toSGTIN(UPCProperties properties, BigInteger serial) {
        final SGTIN96 sgtin = toSGTIN(properties);
        sgtin.setSerialNumber(serial);
        return sgtin;
    }
    String toEPC(SGTIN96 sgtin) {
        // 0 - 7 is the header
        // 8, 8 + 3 is the filter
        // 11, 11 + 3 is the partition
        // 14, 14 + company.bits is prefix
        // 14 + company.bits, 14 + company.bits + item.reference.bits
        // 58, 58 _ 38 serial
        final String binaryHeader = sgtin.getHeader().getBinary(); ///
        final String binaryFilter = sgtin.getFilter().getBinary();
        final String binaryPartition = sgtin.getPartition().getBinary();

        LOGGER.info("Header (%s) bits: %s".formatted(sgtin.getHeader().getHex(), binaryHeader));
        LOGGER.info("Filter (%d) bits: %s".formatted(sgtin.getFilter().getValue(), binaryFilter));
        LOGGER.info("Partition (%d) bits: %s".formatted(sgtin.getPartition().getValue(), binaryPartition));

        final StringBuilder buffer = new StringBuilder(120).append(binaryHeader).append('|')
                                                                   .append(binaryFilter).append('|')
                                                                   .append(binaryPartition).append('|');

        String binary = binaryHeader + binaryFilter + binaryPartition;
        String binaryCompanyPrefix = sgtin.getCompanyPrefix().toString(2);
        binaryCompanyPrefix = leftPad(binaryCompanyPrefix, sgtin.getPartition().getCompany().getBits(), '0');
        binaryCompanyPrefix = right(binaryCompanyPrefix, sgtin.getPartition().getCompany().getBits());
        LOGGER.info("Company prefix (%d) bits: %s".formatted(sgtin.getPartition().getCompany().getBits(), binaryCompanyPrefix));
        buffer.append(binaryCompanyPrefix).append('|');
        binary += binaryCompanyPrefix;

        String binaryItemReference = sgtin.getItemReference().toString(2);
        binaryItemReference = leftPad(binaryItemReference, sgtin.getPartition().getItemReference().getBits(), '0');
        binaryItemReference = right(binaryItemReference, sgtin.getPartition().getItemReference().getBits());
        LOGGER.info("Item reference (%d) bits: %s".formatted(sgtin.getPartition().getItemReference().getBits(), binaryItemReference));
        buffer.append(binaryItemReference).append('|');

        binary += binaryItemReference;

        String binarySerial = requireNonNullElse(sgtin.getSerialNumber(), BigInteger.ZERO).toString(2);
        binarySerial = leftPad(binarySerial, 38, '0');
        binarySerial = right(binarySerial, 38);
        LOGGER.info("Serial (%d) bits: %s".formatted(sgtin.getSerialNumber(), binarySerial));
        buffer.append(binarySerial);
        binary += binarySerial;
        LOGGER.info("EPC (%d bits) binary string: %s".formatted(binary.length(), binary));


        return new BigInteger(binary, 2).toString(16);
    }

    Collection<EPCReading> uploadEPCReadings(RestClient.RequestBodySpec spec, Collection<EPCReading> readings) {
        final String jsonBody = GsonHelper.toJson(readings);
        final ResponseEntity<String> response = spec.body(jsonBody)
                                                    .retrieve()
                                                    .toEntity(String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            final AtomicInteger eIndex = new AtomicInteger();
            LOGGER.error(String.format("Failed upload of %d EPC scans. HTTP status: %d %s: %s\n%s",
                                       readings.size(),
                                       response.getStatusCode().value(),
                                       HttpStatus.resolve(response.getStatusCode().value()).getReasonPhrase(),
                                       response.getBody(),
                                       readings.stream().map(r -> String.format("%n\t[%d: %s]", eIndex.incrementAndGet(), GsonHelper.toJson(r)))));
            throw new RuntimeException();
        }
        return readings;
    }

    public Collection<EPCReading> uploadSensorData(UPCProperties properties) {
        final Collection<EPCReading> readings = loadReadings(properties);
        return uploadEPCReadings(restClient.post(), readings);
    }

}
