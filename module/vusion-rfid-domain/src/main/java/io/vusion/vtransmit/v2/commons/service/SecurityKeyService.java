package io.vusion.vtransmit.v2.commons.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import io.vusion.secure.logs.VusionLogger;
//import io.vusion.vtransmit.v2.commons.model.DiscoverLabel;

@Service
public class SecurityKeyService {

    private static final VusionLogger logger = VusionLogger.getLogger(SecurityKeyService.class);
    @Value("${apim.key}")
    private String apimKey;
    @Value("${apim.url}")
    private String apimUrl;
    private final RestTemplate restTemplate = new RestTemplate();

//    public ResponseEntity<String> postDevices(String storeId, String correlationId, String externalId, List<DiscoverLabel> devices) {
//
//        // Set headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.set("Ocp-Apim-Subscription-Key", apimKey);
//        headers.set("swCorrelationId", correlationId);
//        headers.set("swExternalId", externalId);
//        HttpEntity<Object> entity = new HttpEntity<>(devices, headers);
//        try {
//            return restTemplate.exchange(
//                    apimUrl + "/keys/v1/stores/" + storeId + "/iots/discover",
//                    HttpMethod.POST,
//                    entity,
//                    String.class);
//        } catch (HttpClientErrorException e) {
//            logger.error("POST " + apimUrl + "/keys/v1/stores/" + storeId + "/iots/discover failed due to " + e.getMessage());
//            throw e;
//        }
//    }
}
