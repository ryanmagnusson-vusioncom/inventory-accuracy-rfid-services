package io.vusion.rfid.sampling.client;


import io.vusion.rfid.domain.model.EPCReading;
import io.vusion.rfid.domain.model.SGTINPartition;
import io.vusion.rfid.sampling.config.AppConfig;
import io.vusion.rfid.sampling.config.UPCProperties;
import io.vusion.secure.logs.VusionLogger;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.plus.jndi.Link;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static java.util.Objects.requireNonNullElseGet;

@RequiredArgsConstructor
public class RFIDUploadRunner implements Callable<Map<String, Collection<EPCReading>>> {
    private static final VusionLogger LOGGER = VusionLogger.getLogger(RFIDUploadRunner.class);
    private final AppConfig appConfig;
    private final RFIDClient rfidClient;

    UPCProperties getUpcProperties(String upc) {
        return appConfig.getItems().computeIfAbsent(upc, nbr -> UPCProperties.builder()
                                                                             .withUpc(nbr)
                                                                             .withPartition(SGTINPartition.FIVE)
                                                                             .build());
    }


    @Override
    public Map<String, Collection<EPCReading>> call() throws Exception {
        final Map<String, Collection<EPCReading>> results = new LinkedHashMap<>();

        appConfig.getItems().forEach((upc, props) -> {
            final UPCProperties upcProps = requireNonNullElseGet(props, () -> getUpcProperties(upc));
            LOGGER.info("Uploading scan events for UPC: %s, props: %s".formatted(upc, props));
            results.put(upc, rfidClient.uploadSensorData(props));
        });

        return results;
    }
}
