package com.example.Backend.security;

import com.example.Backend.config.CustomAuthenticationEntryPoint;
import com.example.Backend.config.FeatureSwitchProvider;
import com.example.Backend.entity.UserRoles;
import com.fasterxml.jackson.databind.*;
import lombok.*;
import org.springframework.boot.autoconfigure.security.*;
import org.springframework.boot.web.servlet.*;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.*;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.*;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class HttpSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final TokenAuthenticationService tokenAuthenticationService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final FeatureSwitchProvider featureSwitchProvider;

//  ovde se stavljaju endpointi za koje ne proverava token
    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain resourceFilterChain(HttpSecurity http) throws Exception {
        String[] resources = {
            "/web/auth/**", "/web/companyProba" // registracija
        };

        http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatchers(auth -> auth.requestMatchers(HttpMethod.OPTIONS)
                        .requestMatchers(resources))
                .authorizeHttpRequests(auth -> auth.anyRequest()
                        .permitAll())
                .requestCache(RequestCacheConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.applyPermitDefaultValues();

                            for (HttpMethod httpMethod : HttpMethod.values()) {
                                config.addAllowedMethod(httpMethod);
                            }
                            return config;
                        }))
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/web/**")
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/web/me")
                                .permitAll()
                                .requestMatchers("/web/admin/**")
                                .hasRole(UserRoles.ADMIN.toString())
                                .requestMatchers(HttpMethod.POST, "/login**", "/web/login")
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .addFilterBefore(new JWTLoginFilter("/web/login", authenticationManager(), tokenAuthenticationService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTAuthenticationFilter("/web/**", customUserDetailsService, tokenAuthenticationService), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider())
                .authenticationManager(authenticationManager())
                .exceptionHandling(
                        exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .sessionManagement(
                        httpSecuritySessionManagementConfigurer ->
                                httpSecuritySessionManagementConfigurer.invalidSessionUrl(featureSwitchProvider.getUrl())
                                        .maximumSessions(-1)
                                        .expiredUrl(featureSwitchProvider.getUrl())
                );

        return http.build();
    }

    private AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    private DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(encoder());
        provider.setHideUserNotFoundExceptions(true);
        return provider;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
