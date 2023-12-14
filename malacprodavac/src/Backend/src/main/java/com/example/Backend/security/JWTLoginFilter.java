package com.example.Backend.security;

import com.example.Backend.dto.AuthenticationW;
import com.fasterxml.jackson.databind.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.util.matcher.*;

import java.io.*;


public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final TokenAuthenticationService tokenAuthenticationService;

    public JWTLoginFilter(String loginUrl, AuthenticationManager authManager,
                          TokenAuthenticationService tokenAuthenticationService) {
        super(new AntPathRequestMatcher(loginUrl, "POST"));
        setAuthenticationManager(authManager);
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException {
        AccountCredentials credentials = new ObjectMapper().readValue(request.getInputStream(), AccountCredentials.class);
        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword()));
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth)
            throws IOException {
        SecurityContextHolder.getContext()
                .setAuthentication(auth);
        CustomUserDetails currentUserDetails = (CustomUserDetails) auth.getPrincipal();
        String jwt = tokenAuthenticationService.createAuthenticationTokenWeb(currentUserDetails, auth.getAuthorities());

        AuthenticationW.AuthenticationWBuilder authenticationWBuilder = AuthenticationW.builder();

        authenticationWBuilder.authorities(currentUserDetails.getRoles())
                .firstName(currentUserDetails.getFirstName())
                .lastName(currentUserDetails.getLastName())
                .id(currentUserDetails.getId());

        authenticationWBuilder.authenticated(true);
        authenticationWBuilder.token(jwt);

        res.setHeader("Content-Type", "application/json");
        res.getWriter()
                .write(new ObjectMapper().writeValueAsString(
                        authenticationWBuilder.build()
                ));
        res.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest req, HttpServletResponse res, AuthenticationException failed)
            throws IOException {

        String message = failed instanceof UsernameNotFoundException ?
                failed.getMessage() :
                "Login failed";

        res.setStatus(HttpStatus.UNAUTHORIZED.value());

        res.setContentType("application/json");
        new ObjectMapper().writeValue(res.getOutputStream(), message);
    }
}
