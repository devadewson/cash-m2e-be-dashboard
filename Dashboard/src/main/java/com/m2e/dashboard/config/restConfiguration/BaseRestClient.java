package com.m2e.dashboard.config.restConfiguration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public abstract class BaseRestClient {

    protected <REQ, RES> RES post(
            WebClient client,
            String uri,
            REQ request,
            Class<RES> responseType) {

        log.info("POST {}", uri);
        log.debug("Request : {}", request);

        RES response =
                client.post()
                        .uri(uri)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(responseType)
                        .block();

        log.debug("Response : {}", response);

        return response;
    }

}