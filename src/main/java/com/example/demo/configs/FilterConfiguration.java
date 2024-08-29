package com.example.demo.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bimaone.platform.core.authentication.security.interceptors.RequestDecryptionFilter;
import com.bimaone.platform.core.authentication.security.interceptors.ResponseEncryptionFilter;

//@Profile("production")
@Configuration
@ConditionalOnProperty(prefix = "spring.profiles", name = "active", havingValue = "asia-g2-app-prod")
public class FilterConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilterConfiguration.class);

	//the filters will be registered for "production" profiles not for any other profiles.
	@Bean
	public FilterRegistrationBean<RequestDecryptionFilter> requestDecryptionFilterRegistration() {
		LOGGER.info("Calling FilterRegistrationBean for RequestDecryptionFilter");
		FilterRegistrationBean<RequestDecryptionFilter> registrationBean = 
				new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestDecryptionFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(1);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<ResponseEncryptionFilter> responseEncryptionFilterRegistration() {
		LOGGER.info("Calling FilterRegistrationBean for ResponseEncryptionFilter");
		FilterRegistrationBean<ResponseEncryptionFilter> registrationBean = 
				new FilterRegistrationBean<>();
		registrationBean.setFilter(new ResponseEncryptionFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(2);
		return registrationBean;
	}
}
