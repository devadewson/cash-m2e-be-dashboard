package com.m2e.dashboard.service.overview.JobKursService;

import com.m2e.dashboard.dto.overview.Refactor.KursListReqDTO;
import com.m2e.dashboard.model.Trapi.Response.KursAllResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphKursClient {

    private final RestTemplate restTemplate;

    @Value("${rate.api.url}")
    private String kursApiBaseUrl;

    @Value("${rate.api.tenor:TODAY}")
    private String tenor;

    @Value("${rate.api.client-name:M2U_270}")
    private String clientName;

    private static final String KURS_ENDPOINT = "/trapi/getListCcy";

    public KursAllResponse fetchDirect() {
        String fullUrl = kursApiBaseUrl + KURS_ENDPOINT;
        log.info("[KURS] POST {} | tenor={} clientName={}", fullUrl, tenor, clientName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<KursListReqDTO> request =
                new HttpEntity<>(new KursListReqDTO(tenor, clientName), headers);

        KursAllResponse response =
                restTemplate.postForObject(fullUrl, request, KursAllResponse.class);

        if (response == null || !"0".equals(response.getRespCode())) {
            log.error("[KURS] Fetch failed. respCode={}",
                    response != null ? response.getRespCode() : "null");
            return null;
        }
        if (response.getRespData() == null || response.getRespData().isEmpty()) {
            log.error("[KURS] Response empty");
            return null;
        }

        log.info("[KURS] Fetched {} rows", response.getRespData().size());
        return response;
    }
}