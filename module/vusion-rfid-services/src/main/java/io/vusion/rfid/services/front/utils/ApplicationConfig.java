package io.vusion.rfid.services.front.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import io.vusion.vtransmit.v2.commons.utils.BaseApplicationConfig;
import lombok.Getter;

@Configuration
@Getter
public class ApplicationConfig extends BaseApplicationConfig {

    @Bean({ "errorMessages", "errorMessageSource"})
    public MessageSource getErrorMessages() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:errors");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
