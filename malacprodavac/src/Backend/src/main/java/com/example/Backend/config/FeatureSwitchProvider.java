package com.example.Backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("malac")
public class FeatureSwitchProvider implements InitializingBean {

    private String url;
    @Override
    public void afterPropertiesSet() {

    }
}
