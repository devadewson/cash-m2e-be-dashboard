package com.m2e.dashboard.config.restConfiguration;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "integration")
public class AppProperties {

    private String btsUrl;

    private String esbAccountUrl;

}