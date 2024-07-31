package io.vusion.rfid.services.front;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vusion.vtransmit.v2.commons.filter.Slf4jMDCFilter;

@Configuration
public class Slf4jMDCFilterConfig {

    @Bean
    FilterRegistrationBean<Slf4jMDCFilter> servletRegistrationBean() {
		final FilterRegistrationBean<Slf4jMDCFilter> registrationBean = new FilterRegistrationBean<>();
		final Slf4jMDCFilter log4jMDCFilterFilter = new Slf4jMDCFilter();
		registrationBean.setFilter(log4jMDCFilterFilter);
		registrationBean.setOrder(2);

		return registrationBean;
	}
}