package com.m2e.dashboard.service.overview.AssetGraph;

import com.m2e.dashboard.config.CorpCifResolver;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.overview.Refactor.KursResolution;
import com.m2e.dashboard.dto.overview.request.AssetLiabilitiesGraphReqDTO;
import com.m2e.dashboard.dto.overview.response.AssetLiabilitiesGraphResDTO;
import com.m2e.dashboard.dto.overview.response.AssetLiabilitiesGraphSubResDTO;
import com.m2e.dashboard.model.Overview.AssetSnapshotEntity;
import com.m2e.dashboard.repository.AssetSnapshotRepository;
import com.m2e.dashboard.service.overview.JobKursService.KursResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Grafik asset dan liabilities dari TB_OV_ASSET_SNAPSHOT.
 *
 * Data terakhir yang dipakai adalah H-1, bukan hari ini, karena job snapshot
 * hari berjalan bisa belum selesai. Seluruh perhitungan periode berangkat dari
 * toDate yang sama
 * W  : 5 minggu yang berakhir di toDate, minggu ISO Senin sampai Minggu
 * M  : Januari sampai bulan dari toDate
 * Q  : Q1 sampai kuartal dari toDate
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetGraphService {

    private final AssetSnapshotRepository repository;
    private final KursResolver kursResolver;
    private final GraphCurrencyConverter graphConverter;
    private final CorpCifResolver corpCifResolver;

    private static final String IDR = "IDR";
    private static final int WEEK_COUNT = 5;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public ApiResponse<AssetLiabilitiesGraphResDTO> getAssetGraph(
            UserAndCorp userAndCorp, AssetLiabilitiesGraphReqDTO request) {

        if (request.getInterval() == null) {
            log.warn("[GRAPH] Interval kosong");
            return errorResponse("Interval wajib diisi (W, M, atau Q)");
        }

        String cif = corpCifResolver.resolveCif(userAndCorp.getCorpId());
        String targetCurrency = (request.getBaseCurrency() != null && !request.getBaseCurrency().isBlank())
                ? request.getBaseCurrency().trim().toUpperCase() : IDR;

        // Data terakhir = kemarin. Dipakai sebagai anchor seluruh perhitungan periode.
        LocalDate toDate = LocalDate.now().minusDays(1);

        log.info("[GRAPH] cif={} interval={} currency={} toDate={}",
                cif, request.getInterval(), targetCurrency, toDate);

        List<String> periodKeys;
        Function<AssetSnapshotEntity, String> keyExtractor;
        LocalDate fromDate;

        switch (request.getInterval()) {
            case W -> {
                periodKeys = GraphPeriodUtil.weekKeysEndingAt(toDate, WEEK_COUNT);
                keyExtractor = AssetSnapshotEntity::getWeekKey;
                fromDate = GraphPeriodUtil.firstMondayEndingAt(toDate, WEEK_COUNT);
            }
            case M -> {
                periodKeys = GraphPeriodUtil.monthKeysYearToDate(toDate);
                keyExtractor = AssetSnapshotEntity::getMonthKey;
                fromDate = GraphPeriodUtil.firstDayOfYear(toDate);
            }
            case Q -> {
                periodKeys = GraphPeriodUtil.quarterKeysYearToDate(toDate);
                keyExtractor = AssetSnapshotEntity::getQuarterKey;
                fromDate = GraphPeriodUtil.firstDayOfYear(toDate);
            }
            default -> {
                log.warn("[GRAPH] Interval tidak dikenal: {}", request.getInterval());
                return errorResponse("Invalid interval: " + request.getInterval());
            }
        }

        List<AssetSnapshotEntity> rows = repository.findForGraph(cif, fromDate, toDate);

        if (rows.isEmpty()) {
            log.warn("[GRAPH] Tidak ada snapshot untuk cif={} antara {} dan {}. "
                    + "Cek apakah job snapshot sudah jalan.", cif, fromDate, toDate);
        }

        // Posisi terakhir per periode (rows sudah ORDER BY snapshotDate ASC)
        Map<String, AssetSnapshotEntity> lastPerPeriod = new LinkedHashMap<>();
        for (AssetSnapshotEntity row : rows) {
            lastPerPeriod.put(keyExtractor.apply(row), row);
        }

        Map<String, BigDecimal> rateMap = Map.of();
        if (!IDR.equals(targetCurrency)) {
            KursResolution kurs = kursResolver.resolve();
            if (kurs == null) {
                log.error("[GRAPH] Kurs tidak tersedia dari Trapi maupun DB, target={}", targetCurrency);
                return errorResponse("Exchange rate unavailable");
            }
            rateMap = kurs.getRateMap();
            log.info("[GRAPH] Kurs source={} pairs={} age={}h",
                    kurs.getSource(), rateMap.size(), kurs.getAgeHours());
        }

        List<AssetLiabilitiesGraphSubResDTO> points = new ArrayList<>();
        for (String periodKey : periodKeys) {
            AssetSnapshotEntity row = lastPerPeriod.get(periodKey);
            if (row == null) {
                continue;
            }

            BigDecimal asset = graphConverter.fromIdr(row.getTotalAsset(), targetCurrency, rateMap);
            BigDecimal liab = graphConverter.fromIdr(row.getTotalLiabilities(), targetCurrency, rateMap);

            if (asset == null || liab == null) {
                log.error("[GRAPH] Konversi gagal IDR -> {} untuk periode {}", targetCurrency, periodKey);
                return errorResponse("Exchange rate not available for " + targetCurrency);
            }

            AssetLiabilitiesGraphSubResDTO point = new AssetLiabilitiesGraphSubResDTO();
            point.setLabel(periodKey);
            point.setAssets(asset);
            point.setLiabilities(liab);
            points.add(point);
        }

        log.info("[GRAPH] cif={} interval={} -> {} titik dari {} periode diminta",
                cif, request.getInterval(), points.size(), periodKeys.size());

        AssetLiabilitiesGraphResDTO body = new AssetLiabilitiesGraphResDTO();
        body.setAssetGraph(points);
        body.setCurrency(targetCurrency);
        body.setLastUpdatedDate(toDate.format(DATE_FORMAT));

        ApiResponse<AssetLiabilitiesGraphResDTO> response = new ApiResponse<>();
        response.setMsgHeader(new ErrorResponse("0"));
        response.setMsgBody(body);
        return response;
    }

    private ApiResponse<AssetLiabilitiesGraphResDTO> errorResponse(String message) {
        log.warn("[GRAPH] Returning error: {}", message);
        ApiResponse<AssetLiabilitiesGraphResDTO> response = new ApiResponse<>();
        response.setMsgHeader(new ErrorResponse("99"));
        return response;
    }
}