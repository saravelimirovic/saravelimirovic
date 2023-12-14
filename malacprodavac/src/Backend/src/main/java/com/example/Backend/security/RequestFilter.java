package com.example.Backend.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.*;
import org.springframework.core.*;
import org.springframework.core.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;

import java.io.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestFilter.class);
    private static volatile boolean requestsEnabled;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        if (isRequestsDisabled()) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.sendError(HttpStatus.REQUEST_TIMEOUT.value());
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public static boolean isRequestsDisabled() {
        return !requestsEnabled;
    }

    public static void enableRequests() {
        requestsEnabled = true;
        LOG.info("Requests enabled!");
    }

    public static void setApplicationStartupFinished() {
        LOG.info("Startup finished!");
    }
}
