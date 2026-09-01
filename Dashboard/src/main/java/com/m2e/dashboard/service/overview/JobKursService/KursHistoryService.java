package com.m2e.dashboard.service.overview.JobKursService;


import com.m2e.dashboard.dto.overview.Refactor.KursHistoryResultDTO;
import com.m2e.dashboard.model.Overview.KursHistoryEntity;
import com.m2e.dashboard.model.Trapi.Response.KursAllResponse;
import com.m2e.dashboard.model.Trapi.Response.KursRateDTO;
import com.m2e.dashboard.repository.KursHistoryRepository;
import com.m2e.dashboard.service.overview.Refactor.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KursHistoryService {

    private final GraphKursClient graphKursClient;
    private final KursHistoryRepository repository;
    private final CurrencyConverterService currencyConverterService;

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Ambil getKursAll dari Trapi lalu simpan seluruh barisnya.*/
    @Transactional
    public KursHistoryResultDTO fetchAndStore() {
        KursHistoryResultDTO result = new KursHistoryResultDTO();
        long startNano = System.nanoTime();

        LocalDateTime fetchTime = LocalDateTime.now();
        String batchId = UUID.randomUUID().toString();
        result.setBatchId(batchId);
        result.setFetchTime(fetchTime.format(TS));
        result.setKursDate(fetchTime.toLocalDate().toString());

        log.info("[KURS-HIST] START batchId={}", batchId);

        try {
            KursAllResponse response = graphKursClient.fetchDirect();

            if (response == null) {
                result.setStatus("FAILED");
                result.setMessage("Trapi tidak merespons atau respCode bukan 0");
                log.error("[KURS-HIST] Trapi unavailable, nothing stored");
                return result;
            }

            List<KursRateDTO> rates = response.getRespData();
            if (rates == null || rates.isEmpty()) {
                result.setStatus("EMPTY");
                result.setMessage("Trapi merespons tapi respData kosong");
                log.error("[KURS-HIST] Empty respData, nothing stored");
                return result;
            }

            List<KursHistoryEntity> entities = new ArrayList<>(rates.size());
            int spotCount = 0;
            String spotValueDate = null;

            for (KursRateDTO rate : rates) {
                if ("SPOT".equalsIgnoreCase(rate.getTenor())) {
                    spotCount++;
                    if (spotValueDate == null && rate.getValueDate() != null) {
                        spotValueDate = rate.getValueDate();
                    }
                }
                entities.add(toEntity(rate, batchId, fetchTime));
            }

            repository.saveAll(entities);

            Map<String, BigDecimal> rateMap = currencyConverterService.buildRateMap(rates);

            result.setStatus("SUCCESS");
            result.setTotalRows(entities.size());
            result.setSpotRows(spotCount);
            result.setValidPairs(rateMap.size());
            result.setSpotValueDate(spotValueDate);
            result.setMessage(entities.size() + " baris kurs tersimpan");

            log.info("[KURS-HIST] END batchId={} rows={} spot={} pairs={} valueDate={}",
                    batchId, entities.size(), spotCount, rateMap.size(), spotValueDate);

        } catch (Exception e) {
            log.error("[KURS-HIST] Fatal error batchId={}", batchId, e);
            throw e;
        } finally {
            result.setDurationMs((System.nanoTime() - startNano) / 1_000_000);
        }

        return result;
    }

    @Transactional
    public int cleanup(int retentionDays) {
        LocalDate before = LocalDate.now().minusDays(retentionDays);
        int deleted = repository.deleteOlderThan(before);
        log.info("[KURS-HIST] Cleanup: {} rows deleted (before {})", deleted, before);
        return deleted;
    }

    private KursHistoryEntity toEntity(KursRateDTO dto, String batchId, LocalDateTime fetchTime) {
        KursHistoryEntity e = new KursHistoryEntity();
        e.setBatchId(batchId);
        e.setKursDate(fetchTime.toLocalDate());
        e.setFetchTime(fetchTime);
        e.setRateName(dto.getName());
        e.setStrIndex(dto.getStrIndex());
        e.setTenor(dto.getTenor());
        e.setCcy(dto.getCcy());
        e.setBidState(dto.getBidState());
        e.setAskState(dto.getAskState());
        e.setClientName(dto.getClientName());
        e.setRateTimestamp(dto.getTimeStamp());
        e.setBid(dto.getBid());
        e.setAsk(dto.getAsk());
        e.setBidAllIn(dto.getBidAllIn());
        e.setAskAllIn(dto.getAskAllIn());
        e.setBaseDps(dto.getBaseDp());
        e.setTermsDps(dto.getTermsDp());
        e.setUnitQuotation(dto.getUnitQuotation());
        e.setValueDate(dto.getValueDate());
        e.setValueDateType(dto.getValueDateType());
        e.setCreatedDate(LocalDateTime.now());
        return e;
    }
}