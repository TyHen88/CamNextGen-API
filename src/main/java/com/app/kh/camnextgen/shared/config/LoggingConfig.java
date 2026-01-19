package com.app.kh.camnextgen.shared.config;

import com.app.kh.camnextgen.shared.logging.CorrelationIdFilter;
import com.app.kh.camnextgen.shared.logging.RequestResponseLoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Bean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter() {
        FilterRegistrationBean<CorrelationIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CorrelationIdFilter());
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> requestResponseLoggingFilter() {
        FilterRegistrationBean<RequestResponseLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RequestResponseLoggingFilter());
        registration.setOrder(2);
        return registration;
    }
}
