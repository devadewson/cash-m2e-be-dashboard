package com.m2e.dashboard.service.overview.esbRate;

import com.m2e.dashboard.model.esbRate.EsbExchangeRateRequest;
import com.m2e.dashboard.model.esbRate.EsbExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Klien ESB counter rate. Mengembalikan null kalau response tidak bisa dipakai. */
@Slf4j
@Service
@RequiredArgsConstructor
public class EsbKursClient {

    private final RestTemplate restTemplate;

    @Value("${rate.esb.url}")
    private String esbUrl;

    private static final String STATUS_SUCCESS = "0";
    private static final DateTimeFormatter MSG_ID_TS = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");

    /** Pembeda dua request di milidetik yang sama. */
    private final AtomicInteger sequence = new AtomicInteger();

    public EsbExchangeRateResponse fetch() {

        if (esbUrl == null || esbUrl.isBlank()) {
            log.warn("[KURS-ESB] rate.esb.url tidak dikonfigurasi, ESB dilewati");
            return null;
        }

        String msgId = nextMsgId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EsbExchangeRateRequest> request =
                new HttpEntity<>(EsbExchangeRateRequest.of(msgId), headers);

        log.info("[KURS-ESB] POST {} | msgId={}", esbUrl, msgId);

        EsbExchangeRateResponse response =
                restTemplate.postForObject(esbUrl, request, EsbExchangeRateResponse.class);

        if (response == null || response.getMsg() == null || response.getMsg().getMsgHeader() == null) {
            log.error("[KURS-ESB] Response kosong | msgId={}", msgId);
            return null;
        }

        EsbExchangeRateResponse.MsgHeader header = response.getMsg().getMsgHeader();
        if (!STATUS_SUCCESS.equals(header.getStatusCode())) {
            log.error("[KURS-ESB] StatusCode={} desc={} | msgId={}",
                    header.getStatusCode(), header.getStatusDesc(), msgId);
            return null;
        }

        List<EsbExchangeRateResponse.ExchangeRateElement> elements = elementsOf(response);
        if (elements.isEmpty()) {
            log.error("[KURS-ESB] ExchangeRateElement kosong | msgId={}", msgId);
            return null;
        }

        log.info("[KURS-ESB] Fetched {} baris | msgId={}", elements.size(), msgId);
        return response;
    }

    public static List<EsbExchangeRateResponse.ExchangeRateElement> elementsOf(EsbExchangeRateResponse response) {
        if (response == null || response.getMsg() == null
                || response.getMsg().getMsgBody() == null
                || response.getMsg().getMsgBody().getExchangeRateData() == null
                || response.getMsg().getMsgBody().getExchangeRateData().getExchangeRateElement() == null) {
            return List.of();
        }
        return response.getMsg().getMsgBody().getExchangeRateData().getExchangeRateElement();
    }

    /** Contoh: API26101310254717301 — awalan tetap, timestamp, dua digit urutan. */
    private String nextMsgId() {
        int seq = Math.floorMod(sequence.getAndIncrement(), 100);
        return "API" + LocalDateTime.now().format(MSG_ID_TS) + String.format("%02d", seq);
    }
}