package com.example.Backend.config;

import com.example.Backend.security.RequestFilter;
import com.example.Backend.service.UserService;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.context.event.*;
import org.springframework.context.*;
import org.springframework.stereotype.*;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final UserService userService;
    private final FeatureSwitchProvider featureSwitchProvider;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        userService.restoreTokens();

        RequestFilter.setApplicationStartupFinished();
        RequestFilter.enableRequests();

    }
}