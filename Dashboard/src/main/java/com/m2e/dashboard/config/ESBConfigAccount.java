package com.m2e.dashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ESBConfigAccount {

    @Bean
    public WebClient esbWebClient() {
        return WebClient.builder()
                .baseUrl("http://10.235.66.96:8800/id/mbi")
                .build();
    }

}