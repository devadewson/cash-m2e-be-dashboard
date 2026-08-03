package com.m2e.dashboard.service.overview.Refactor;

import com.m2e.dashboard.model.Trapi.Response.KursAllResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class KursService {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${rate.api.url}")
    private String kursApiBaseUrl;

    private static final String KURS_ENDPOINT = "/trapi/getKursAll";
    private static final String KURS_REDIS_KEY = "kurs-all";
    private static final long KURS_TTL_MINUTES = 60;

    public KursAllResponse getKursAll() {
        // ===== STEP 1: CEK REDIS DULU =====
        try {
            Object cached = redisTemplate.opsForValue().get(KURS_REDIS_KEY);
            if (cached != null) {
                log.info("KURS FOUND IN REDIS! Key: {}", KURS_REDIS_KEY);
                return (KursAllResponse) cached;
            }
            log.info("KURS NOT IN REDIS. Will fetch from API...");
        } catch (Exception e) {
            log.warn("Cannot check Redis, will fetch from API: {}", e.getMessage());
        }

        // ===== STEP 2: FETCH DARI API =====
        String fullUrl = kursApiBaseUrl + KURS_ENDPOINT;
        log.info("Fetching kurs from API: {}", fullUrl);

        try {
            KursAllResponse response = restTemplate.getForObject(fullUrl, KursAllResponse.class);

            if (response != null && "0".equals(response.getRespCode())) {
                log.info("Kurs API success. Total data: {}",
                        response.getRespData() != null ? response.getRespData().size() : 0);

                // ===== STEP 3: SIMPAN KE REDIS =====
                try {
                    redisTemplate.opsForValue().set(
                            KURS_REDIS_KEY,
                            response,
                            KURS_TTL_MINUTES,
                            TimeUnit.MINUTES
                    );
                    log.info("KURS SAVED TO REDIS! Key: {}, TTL: {} minutes",
                            KURS_REDIS_KEY, KURS_TTL_MINUTES);
                } catch (Exception e) {
                    log.error("Failed to save kurs to Redis: {}", e.getMessage(), e);
                }

                return response;
            } else {
                log.warn("Kurs API response not success. respCode: {}",
                        response != null ? response.getRespCode() : "null");
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to fetch kurs from API: {}", e.getMessage(), e);
            return null;
        }
    }
}