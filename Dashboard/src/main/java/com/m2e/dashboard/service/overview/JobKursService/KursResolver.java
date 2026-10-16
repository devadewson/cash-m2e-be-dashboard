package com.m2e.dashboard.service.overview.JobKursService;

import com.m2e.dashboard.dto.overview.Refactor.KursResolution;
import com.m2e.dashboard.model.Overview.KursHistoryEntity;
import com.m2e.dashboard.model.Trapi.Response.KursAllResponse;
import com.m2e.dashboard.model.Trapi.Response.KursRateDTO;
import com.m2e.dashboard.model.esbRate.EsbExchangeRateResponse;
import com.m2e.dashboard.repository.KursHistoryRepository;
import com.m2e.dashboard.service.overview.Refactor.CurrencyConverterService;
import com.m2e.dashboard.service.overview.esbRate.EsbKursClient;
import com.m2e.dashboard.service.overview.esbRate.EsbKursMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sumber kurs berurutan: Trapi -> ESB counter rate -> batch terakhir di DB.
 *
 * Hasil resolve di-cache sebentar di memori.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KursResolver {

    private final GraphKursClient graphKursClient;
    private final EsbKursClient esbKursClient;
    private final EsbKursMapper esbKursMapper;
    private final KursHistoryRepository repository;
    private final CurrencyConverterService currencyConverterService;

    @Value("${job.kurs.max-age-hours:36}")
    private long maxAgeHours;

    @Value("${rate.esb.enabled:true}")
    private boolean esbEnabled;

    /** 0 untuk mematikan cache. */
    @Value("${rate.resolve-cache-seconds:300}")
    private long cacheSeconds;

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private record CachedResolution(KursResolution resolution, Instant expiresAt) {}

    private volatile CachedResolution cached;

    /** Null kalau ketiga sumber gagal. */
    public KursResolution resolve() {
        CachedResolution current = cached;
        if (cacheSeconds > 0 && current != null && Instant.now().isBefore(current.expiresAt())) {
            log.debug("[KURS-RESOLVE] Pakai cache source={}", current.resolution().getSource());
            return current.resolution();
        }

        KursResolution resolution = resolveFresh();

        if (resolution != null && cacheSeconds > 0) {
            cached = new CachedResolution(resolution, Instant.now().plusSeconds(cacheSeconds));
        }
        return resolution;
    }

    private KursResolution resolveFresh() {
        KursResolution fromTrapi = fromTrapi();
        if (fromTrapi != null) {
            return fromTrapi;
        }

        if (esbEnabled) {
            KursResolution fromEsb = fromEsb();
            if (fromEsb != null) {
                return fromEsb;
            }
        }

        return fromDatabase();
    }

    // ==================== 1. TRAPI ====================

    private KursResolution fromTrapi() {
        try {
            KursAllResponse response = graphKursClient.fetchDirect();
            if (response == null || response.getRespData() == null || response.getRespData().isEmpty()) {
                log.warn("[KURS-RESOLVE] Trapi tidak tersedia, coba ESB");
                return null;
            }

            Map<String, BigDecimal> rateMap = currencyConverterService.buildRateMap(response.getRespData());
            if (rateMap.isEmpty()) {
                // Response ada tapi semua baris tersaring, misal tenor tidak cocok
                log.warn("[KURS-RESOLVE] Trapi merespons tapi rate map kosong, coba ESB");
                return null;
            }

            KursResolution res = online("TRAPI", rateMap, firstValueDate(response.getRespData()));
            log.info("[KURS-RESOLVE] Source=TRAPI pairs={}", rateMap.size());
            return res;

        } catch (Exception e) {
            log.warn("[KURS-RESOLVE] Trapi error ({}), coba ESB", e.getMessage());
            return null;
        }
    }

    // ==================== 2. ESB ====================

    private KursResolution fromEsb() {
        try {
            EsbExchangeRateResponse response = esbKursClient.fetch();
            if (response == null) {
                log.warn("[KURS-RESOLVE] ESB tidak tersedia, fallback ke database");
                return null;
            }

            Map<String, BigDecimal> rateMap =
                    esbKursMapper.toRateMap(EsbKursClient.elementsOf(response));
            if (rateMap.isEmpty()) {
                log.warn("[KURS-RESOLVE] ESB merespons tapi tidak ada rate valid, fallback ke database");
                return null;
            }

            String timeStamp = response.getMsg().getMsgBody().getExchangeRateData().getTimeStamp();
            KursResolution res = online("ESB", rateMap, esbKursMapper.toValueDate(timeStamp));
            log.info("[KURS-RESOLVE] Source=ESB pairs={}", rateMap.size());
            return res;

        } catch (Exception e) {
            log.warn("[KURS-RESOLVE] ESB error ({}), fallback ke database", e.getMessage());
            return null;
        }
    }

    // ==================== 3. DATABASE ====================

    private KursResolution fromDatabase() {
        String batchId = repository.findLatestBatchId();
        if (batchId == null) {
            log.error("[KURS-RESOLVE] Tidak ada batch kurs di database");
            return null;
        }

        List<KursHistoryEntity> rows = repository.findByBatchId(batchId);
        if (rows.isEmpty()) {
            log.error("[KURS-RESOLVE] Batch {} kosong", batchId);
            return null;
        }

        LocalDateTime fetchTime = rows.get(0).getFetchTime();
        long ageHours = Duration.between(fetchTime, LocalDateTime.now()).toHours();

        List<KursRateDTO> dtos = new ArrayList<>(rows.size());
        for (KursHistoryEntity row : rows) {
            dtos.add(toDto(row));
        }

        KursResolution res = new KursResolution();
        res.setSource("DATABASE");
        res.setRateMap(currencyConverterService.buildRateMap(dtos));
        res.setValueDate(firstValueDate(dtos));
        res.setFetchTime(fetchTime.format(TS));
        res.setAgeHours(ageHours);
        res.setStale(ageHours > maxAgeHours);

        if (res.isStale()) {
            log.error("[KURS-RESOLVE] Source=DATABASE tapi STALE ({}h > {}h) batchId={}",
                    ageHours, maxAgeHours, batchId);
        } else {
            log.info("[KURS-RESOLVE] Source=DATABASE pairs={} age={}h batchId={}",
                    res.getRateMap().size(), ageHours, batchId);
        }
        return res;
    }

    // ==================== HELPER ====================

    private KursResolution online(String source, Map<String, BigDecimal> rateMap, String valueDate) {
        KursResolution res = new KursResolution();
        res.setSource(source);
        res.setRateMap(rateMap);
        res.setValueDate(valueDate);
        res.setFetchTime(LocalDateTime.now().format(TS));
        res.setAgeHours(0);
        res.setStale(false);
        return res;
    }

    private KursRateDTO toDto(KursHistoryEntity e) {
        KursRateDTO d = new KursRateDTO();
        d.setName(e.getRateName());
        d.setStrIndex(e.getStrIndex());
        d.setTenor(e.getTenor());
        d.setCcy(e.getCcy());
        d.setBidState(e.getBidState());
        d.setAskState(e.getAskState());
        d.setClientName(e.getClientName());
        d.setTimeStamp(e.getRateTimestamp());
        d.setBid(e.getBid());
        d.setAsk(e.getAsk());
        d.setBidAllIn(e.getBidAllIn());
        d.setAskAllIn(e.getAskAllIn());
        d.setBaseDp(e.getBaseDps());
        d.setTermsDp(e.getTermsDps());
        d.setUnitQuotation(e.getUnitQuotation());
        d.setValueDate(e.getValueDate());
        d.setValueDateType(e.getValueDateType());
        return d;
    }

    private String firstValueDate(List<KursRateDTO> rates) {
        if (rates == null) {
            return null;
        }
        return rates.stream()
                .map(KursRateDTO::getValueDate)
                .filter(v -> v != null && !v.isBlank())
                .findFirst()
                .orElse(null);
    }
}