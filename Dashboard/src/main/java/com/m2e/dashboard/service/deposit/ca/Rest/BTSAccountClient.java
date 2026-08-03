package com.m2e.dashboard.service.deposit.ca.Rest;

import com.m2e.dashboard.config.restConfiguration.AppProperties;
import com.m2e.dashboard.config.restConfiguration.BaseRestClient;
import com.m2e.dashboard.model.BTS.Request.Summary;
import com.m2e.dashboard.model.BTS.Response.Sumary.SummaryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BTSAccountClient extends BaseRestClient {

    private final WebClient webClient;

    public BTSAccountClient(AppProperties properties) {

        this.webClient =
                WebClient.builder()
                        .baseUrl(properties.getBtsUrl())
                        .build();
    }

    public SummaryResponse getAccountSummary(Summary request) {

        return post(
                webClient,
                "/dashboard/v1/summary",
                request,
                SummaryResponse.class);
    }

}