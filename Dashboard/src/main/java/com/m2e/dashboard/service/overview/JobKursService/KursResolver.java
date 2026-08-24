package com.m2e.dashboard.service.overview.JobKursService;


import com.m2e.dashboard.dto.overview.Refactor.KursResolution;
import com.m2e.dashboard.model.Overview.KursHistoryEntity;
import com.m2e.dashboard.model.Trapi.Response.KursAllResponse;
import com.m2e.dashboard.model.Trapi.Response.KursRateDTO;
import com.m2e.dashboard.repository.KursHistoryRepository;
import com.m2e.dashboard.service.overview.AssetGraph.GraphKursClient;
import com.m2e.dashboard.service.overview.Refactor.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KursResolver {

    private final GraphKursClient graphKursClient;
    private final KursHistoryRepository repository;
    private final CurrencyConverterService currencyConverterService;

    @Value("${job.kurs.max-age-hours:36}")
    private long maxAgeHours;

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Coba Trapi dulu. Kalau mati, ambil batch terakhir dari DB. Null kalau dua-duanya gagal. */
    public KursResolution resolve() {

        // 1. Trapi
        try {
            KursAllResponse response = graphKursClient.fetchDirect();
            if (response != null && response.getRespData() != null
                    && !response.getRespData().isEmpty()) {

                KursResolution res = new KursResolution();
                res.setSource("TRAPI");
                res.setRateMap(currencyConverterService.buildRateMap(response.getRespData()));
                res.setValueDate(firstSpotValueDate(response.getRespData()));
                res.setFetchTime(LocalDateTime.now().format(TS));
                res.setAgeHours(0);
                res.setStale(false);

                log.info("[KURS-RESOLVE] Source=TRAPI pairs={}", res.getRateMap().size());
                return res;
            }
            log.warn("[KURS-RESOLVE] Trapi unavailable, fallback ke database");
        } catch (Exception e) {
            log.warn("[KURS-RESOLVE] Trapi error ({}), fallback ke database", e.getMessage());
        }

        // 2. Database
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
        res.setValueDate(firstSpotValueDate(dtos));
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

    private String firstSpotValueDate(List<KursRateDTO> rates) {
        if (rates == null) return null;
        return rates.stream()
                .filter(r -> "SPOT".equalsIgnoreCase(r.getTenor()))
                .map(KursRateDTO::getValueDate)
                .filter(v -> v != null && !v.isBlank())
                .findFirst()
                .orElse(null);
    }
}