package io.vusion.vtransmit.v2.commons.utils;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;

import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import io.vusion.secure.logs.VusionLogger;
import jakarta.xml.bind.JAXBException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XMLUtils {

    private static final VusionLogger logger = VusionLogger.getLogger(XMLUtils.class);

    public static Jaxb2Marshaller createJAXB2Marshaller(String objectsToMarshall) {
        return createJAXB2Marshaller(isBlank(objectsToMarshall) ? null : new String[] {objectsToMarshall});
    }

    @SneakyThrows
    public static Jaxb2Marshaller createJAXB2Marshaller(String... objectsToMarshall) {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        final String[] packagesToScan = Stream.of(objectsToMarshall)
                                                  .filter(StringUtils::isNotBlank)
                                                  .distinct()
                                                  .toArray(String[]::new);

        if (ArrayUtils.isNotEmpty(packagesToScan)) {
            marshaller.setPackagesToScan(packagesToScan);
        }

        try {
            marshaller.afterPropertiesSet();
        } catch (Exception ex) {
            final String message = """
                                   Unable to initialize the JAXB2Marshaller for packages/objects: [%s]. \
                                   Reason: %s""".formatted(join(packagesToScan, ','),
                                                           ExceptionUtils.getMessage(ex));
            final JAXBException errorThrown = new JAXBException(message, ex);
            logger.error("Throwing JAXBException. " + message, ex);
            throw errorThrown;
        }
        return marshaller;
    }

    private static MarshallingHttpMessageConverter wrapJaxbMarshaller(@NonNull Jaxb2Marshaller jaxb2Marshaller) {
        Objects.requireNonNull(jaxb2Marshaller, "Jaxb2Marshaller is required");
        final MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
        xmlConverter.setMarshaller(jaxb2Marshaller);
        xmlConverter.setUnmarshaller(jaxb2Marshaller);
        return xmlConverter;
    }

    public static MarshallingHttpMessageConverter createXMLMessageConverter(String objectsToMarshall) {
        final Jaxb2Marshaller jaxb2Marshaller = createJAXB2Marshaller(objectsToMarshall);
        return wrapJaxbMarshaller(jaxb2Marshaller);
    }

    public static MarshallingHttpMessageConverter createXMLMessageConverter(String... objectsToMarshall) {
        final Jaxb2Marshaller jaxb2Marshaller = createJAXB2Marshaller(objectsToMarshall);
        return wrapJaxbMarshaller(jaxb2Marshaller);
    }
}
