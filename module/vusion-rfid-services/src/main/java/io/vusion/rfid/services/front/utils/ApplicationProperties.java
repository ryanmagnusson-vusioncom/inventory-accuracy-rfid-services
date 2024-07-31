package io.vusion.rfid.services.front.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class ApplicationProperties {

    @Value("${apim.key}")
    private String apimKey;

    @Value("${apim.url}")
    private String apimUrl;

    @Value("${vtransmit.transmitter.ignore.provisioning:false}")
    private boolean swIgnoreProvisioning;

    @Value("${vtransmit.transmitter.ignore.ap.auth:false}")
    private boolean swIgnoreApAuth;

    @Value("${vtransmit.transmitter.update.software:true}")
    private String provisioningUpdateSoftware;

    @Value("${vtransmit.transmitter.jwt.security.enabled:false}")
    private boolean jwtSecurityEnabled;

    @Value("${vtransmit.transmitter.auto.channel:127}")
    private int defaultAutoChannel;

    @Value("${vtransmit.transmitter.bypass_claim_id:true}")
    private boolean bypassClaimIdValidation;

    @Value("${vtransmit.jwt.security.port:7354}")
    private int jwtSecurityPort;

    @Value("${vtransmit.jwt.security.dns:ap-auth-weua.vusion-dev.io}")
    private String jwtSecurityDns;

    @Value("${vtransmit.keyvault.endpoint:none}")
    private String keyVaultEndpoint;

    @Value("${vtransmit.keyvault.retry:3}")
    private int keyvaultMaxRetry;

    @Value("${vtransmit.keyvault.delay:500}")
    private int keyvaultRetryDelayInMs;

}
