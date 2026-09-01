package com.m2e.dashboard.service.overview.JobKursService;


import com.m2e.dashboard.model.Trapi.Response.KursAllResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphKursClient {

    private final RestTemplate restTemplate;

    @Value("${rate.api.url}")
    private String kursApiBaseUrl;

    private static final String KURS_ENDPOINT = "/trapi/getKursAll";

    public KursAllResponse fetchDirect() {
        String fullUrl = kursApiBaseUrl + KURS_ENDPOINT;
        log.info("[GRAPH] Fetching kurs direct from Trapi (no cache): {}", fullUrl);

        KursAllResponse response = restTemplate.getForObject(fullUrl, KursAllResponse.class);

        if (response == null || !"0".equals(response.getRespCode())) {
            log.error("[GRAPH] Kurs fetch failed. respCode={}",
                    response != null ? response.getRespCode() : "null");
            return null;
        }
        if (response.getRespData() == null || response.getRespData().isEmpty()) {
            log.error("[GRAPH] Kurs response empty");
            return null;
        }
        return response;
    }
}
