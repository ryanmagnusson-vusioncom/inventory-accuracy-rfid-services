package io.vusion.rfid.sampling;


import io.vusion.rfid.domain.model.EPCReading;
import io.vusion.rfid.sampling.client.RFIDClient;
import io.vusion.rfid.sampling.client.RFIDUploadRunner;
import io.vusion.rfid.sampling.config.AppConfig;
import io.vusion.secure.logs.VusionLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Collection;
import java.util.Map;

@SpringBootApplication(exclude = { JacksonAutoConfiguration.class }) //, HibernateJpaAutoConfiguration.class })
@ComponentScan(basePackages = "io.vusion")
public class DataSamplingConsoleApplication implements ApplicationRunner {
    private static final VusionLogger LOGGER = VusionLogger.getLogger(DataSamplingConsoleApplication.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RFIDClient rfidClient;



    public static void main(final String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(DataSamplingConsoleApplication.class, args);
        context.registerShutdownHook();
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (true) {
            final RFIDUploadRunner runner = new RFIDUploadRunner(appConfig, rfidClient);
            final Map<String, Collection<EPCReading>> results = runner.call();

            LOGGER.info("Sleeping for %d seconds".formatted(appConfig.getUpdateInterval().toSeconds()));
            Thread.sleep(appConfig.getUpdateInterval().toMillis());
        }
    }
}
