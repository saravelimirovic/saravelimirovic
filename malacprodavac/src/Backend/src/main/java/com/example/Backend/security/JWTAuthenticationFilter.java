package com.example.Backend.security;

import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.session.*;

import java.io.*;
import java.util.*;

import static com.example.Backend.utils.ContextUtils.getSessionIdentifier;
import static java.lang.String.*;

public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final TokenAuthenticationService tokenAuthenticationService;

    public JWTAuthenticationFilter(String url, CustomUserDetailsService customUserDetailsService,
                                   TokenAuthenticationService tokenAuthenticationService) {
        super(url);
        this.customUserDetailsService = customUserDetailsService;
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            return tokenAuthenticationService.getAuthentication(request, customUserDetailsService);

        } catch (ExpiredJwtException e) {
            throw new SessionAuthenticationException("Token expired");
        } catch (JwtException e) {
            throw new SessionAuthenticationException("bad token");
        }

    }

    private String getName(Authentication authentication) {
        return authentication != null && authentication.getName() != null
                ? authentication.getName()
                : "unknown";
    }

    private String requestId() {
        return UUID.randomUUID()
                .toString();
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult)
            throws IOException, ServletException {

        try {
            SecurityContextHolder.getContext()
                    .setAuthentication(authResult);

//			if (POST.toString()
//					.equalsIgnoreCase(request.getMethod())) {
//				MDC.put("request body", request.getReader()
//						.lines()
//						.collect(Collectors.joining(System.lineSeparator())));
//			}

            // Setup MDC data:
            MDC.put("mdcData",
                    format(" [%s | %s]", getName(authResult), requestId()));
            MDC.put("requestUrl", request.getRequestURI());
            MDC.put("sessionIdentifier", getSessionIdentifier());

            String reqMethod = format("[%s]", request.getMethod());
            String web = request.getRequestURI()
                    .startsWith("/web") ? reqMethod : "";
            String mob = request.getRequestURI()
                    .startsWith("/mob") ? reqMethod : "";

            MDC.put("mob", mob);
            MDC.put("web", web);
            chain.doFilter(request, response);

        } finally {
            // Tear down MDC data:
            // ( Important! Cleans up the ThreadLocal data again )
            MDC.clear();
        }
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest req, HttpServletResponse res, AuthenticationException failed)
            throws IOException {
        res.sendError(401, failed.getMessage());
    }
}
