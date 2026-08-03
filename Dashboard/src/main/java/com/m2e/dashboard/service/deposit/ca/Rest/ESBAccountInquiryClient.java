package com.m2e.dashboard.service.deposit.ca.Rest;

import com.m2e.dashboard.config.restConfiguration.AppProperties;
import com.m2e.dashboard.config.restConfiguration.BaseRestClient;
import com.m2e.dashboard.model.AccountInquiry.Request.MsgWraper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ESBAccountInquiryClient extends BaseRestClient {

    private final WebClient webClient;

    public ESBAccountInquiryClient(AppProperties properties) {

        this.webClient =
                WebClient.builder()
                        .baseUrl(properties.getEsbAccountUrl())
                        .build();
    }

    public com.m2e.dashboard.model.AccountInquiry.Response.MsgWraper
    getAccountDetail(MsgWraper request) {

        return post(
                webClient,
                "/v1/fti/AccountInfo",
                request,
                com.m2e.dashboard.model.AccountInquiry.Response.MsgWraper.class);
    }

}