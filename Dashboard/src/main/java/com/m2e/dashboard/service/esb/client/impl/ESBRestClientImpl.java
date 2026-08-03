package com.m2e.dashboard.service.esb.client.impl;

import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgHeaderReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "m2e.integration.type",
        havingValue = "REST"
)
public class ESBRestClientImpl implements ESBClient {

    private final RestTemplate restTemplate;

    private static final String ESB_URL =
            "http://esb-host/api/trade";

    @Override
    public String callHost(String requestString,
                           PropertyServiceEnum queueName) {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request =
                    new HttpEntity<>(requestString, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            ESB_URL,
                            HttpMethod.POST,
                            request,
                            String.class
                    );

            return response.getBody();

        } catch (Exception e) {
            log.error("ESB REST ERROR", e);
            return null;
        }
    }

    @Override
    public ESBMsgHeaderReqDTO constructMsgHeader(
            ESBPropertiesDTO properties) {

        return new ESBMsgHeaderReqDTO(
                properties.getEsbVer(),
                properties.getEsbSvcId(),
                properties.getEsbTxnCode(),
                properties.getEsbEnv(),
                properties.getEsbMsgId()
                        + DateTimeUtil.getCompactDateTime()
        );
    }
}