package com.example.Backend.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Configuration
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RequestLoggingFilterConfig {

    @Bean
    public FilterRegistrationBean<AbstractRequestLoggingFilter> requestResponseFilter() {

        FilterRegistrationBean<AbstractRequestLoggingFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setName("Request Response Filter");
        filterRegBean.setFilter(logFilter());
        filterRegBean.setUrlPatterns(List.of("/web/*"));
        filterRegBean.setAsyncSupported(Boolean.TRUE);
        return filterRegBean;
    }

    private AbstractRequestLoggingFilter logFilter() {
        AbstractRequestLoggingFilter filter = new AbstractRequestLoggingFilter() {
            private static final String TIME_KEY = "beforeRequestTime";

            @Override
            protected void beforeRequest(HttpServletRequest httpServletRequest, String message) {
                httpServletRequest.setAttribute(TIME_KEY, Instant.now());
                logger.info(message);
            }

            @Override
            protected void afterRequest(HttpServletRequest httpServletRequest,
                                        String message) {
                Instant beforeTime = (Instant) (httpServletRequest.getAttribute(TIME_KEY));
                long requestDuration = Duration.between(beforeTime, Instant.now())
                        .toMillis();
                if (requestDuration < 5000) {
                    logger.info(String.format("%s[Duration | %d ms]", message, requestDuration));
                } else {
                    logger.info(String.format("%s[Duration LONG | %d ms]", message, requestDuration));
                }
            }

            @Override
            protected boolean shouldNotFilterAsyncDispatch() {
                return true;
            }
        };
        filter.setIncludeClientInfo(false);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(200000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("[After | ");
        filter.setBeforeMessagePrefix("[Before | ");
        return filter;
    }

}
