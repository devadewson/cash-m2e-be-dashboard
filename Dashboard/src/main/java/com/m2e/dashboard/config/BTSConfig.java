package com.m2e.dashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BTSConfig {

    @Bean
    public WebClient btsWebClient() {
        return WebClient.builder()
                .baseUrl("http://10.230.83.144:10003/bts")
                .build();
    }

}