package com.m2e.dashboard.service.overview.AssetGraph;


import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.overview.request.AssetLiabilitiesGraphReqDTO;
import com.m2e.dashboard.dto.overview.response.AssetLiabilitiesGraphResDTO;
import com.m2e.dashboard.dto.overview.response.AssetLiabilitiesGraphSubResDTO;
import com.m2e.dashboard.model.Overview.AssetSnapshotEntity;
import com.m2e.dashboard.model.Trapi.Response.KursAllResponse;
import com.m2e.dashboard.repository.AssetSnapshotRepository;
import com.m2e.dashboard.service.overview.Refactor.CurrencyConverterService;
import com.m2e.dashboard.service.overview.Refactor.KursService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetGraphService {

    private final AssetSnapshotRepository repository;
    private final KursService kursService;
    private final CurrencyConverterService currencyConverterService;
    private final GraphCurrencyConverter graphConverter;

    private static final int WEEK_COUNT = 8;
    private static final int MONTH_COUNT = 12;
    private static final int QUARTER_COUNT = 8;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public ApiResponse<AssetLiabilitiesGraphResDTO> getAssetGraph(
            UserAndCorp userAndCorp, AssetLiabilitiesGraphReqDTO request) {

        String cif = userAndCorp.getCorpId();
        String targetCurrency = request.getBaseCurrency() != null
                ? request.getBaseCurrency().toUpperCase() : "IDR";

        log.info("[GRAPH] cif={} interval={} currency={}",
                cif, request.getInterval(), targetCurrency);

        List<String> periodKeys;
        Function<AssetSnapshotEntity, String> keyExtractor;
        LocalDate fromDate;

        switch (request.getInterval()) {
            case W -> {
                periodKeys = GraphPeriodUtil.lastWeekKeys(WEEK_COUNT);
                keyExtractor = AssetSnapshotEntity::getWeekKey;
                fromDate = LocalDate.now().minusWeeks(WEEK_COUNT);
            }
            case M -> {
                periodKeys = GraphPeriodUtil.lastMonthKeys(MONTH_COUNT);
                keyExtractor = AssetSnapshotEntity::getMonthKey;
                fromDate = LocalDate.now().minusMonths(MONTH_COUNT);
            }
            case Q -> {
                periodKeys = GraphPeriodUtil.lastQuarterKeys(QUARTER_COUNT);
                keyExtractor = AssetSnapshotEntity::getQuarterKey;
                fromDate = LocalDate.now().minusMonths(QUARTER_COUNT * 3L);
            }
            default -> throw new IllegalArgumentException(
                    "Invalid interval: " + request.getInterval());
        }

        List<AssetSnapshotEntity> rows = repository.findForGraph(cif, fromDate);

        // Ambil posisi terakhir per periode (rows ORDER BY snapshotDate ASC)
        Map<String, AssetSnapshotEntity> lastPerPeriod = new LinkedHashMap<>();
        for (AssetSnapshotEntity row : rows) {
            lastPerPeriod.put(keyExtractor.apply(row), row);
        }

        Map<String, BigDecimal> rateMap = Map.of();
        if (!"IDR".equals(targetCurrency)) {
            KursAllResponse kurs = kursService.getKursAll();
            if (kurs == null || kurs.getRespData() == null) {
                log.error("[GRAPH] Kurs unavailable for target {}", targetCurrency);
                return errorResponse("Exchange rate unavailable");
            }
            rateMap = currencyConverterService.buildRateMap(kurs.getRespData());
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
                log.error("[GRAPH] Conversion failed IDR->{}", targetCurrency);
                return errorResponse("Exchange rate not available for " + targetCurrency);
            }

            AssetLiabilitiesGraphSubResDTO point = new AssetLiabilitiesGraphSubResDTO();
            point.setLabel(periodKey);
            point.setAssets(asset);
            point.setLiabilities(liab);
            points.add(point);
        }

        AssetLiabilitiesGraphResDTO body = new AssetLiabilitiesGraphResDTO();
        body.setAssetGraph(points);
        body.setCurrency(targetCurrency);
        body.setLastUpdatedDate(OffsetDateTime.now().minusDays(1).format(DATE_FORMAT));

        ApiResponse<AssetLiabilitiesGraphResDTO> response = new ApiResponse<>();
        response.setMsgHeader(new ErrorResponse("0"));
        response.setMsgBody(body);
        return response;
    }

    private ApiResponse<AssetLiabilitiesGraphResDTO> errorResponse(String message) {
        ApiResponse<AssetLiabilitiesGraphResDTO> response = new ApiResponse<>();
        ErrorResponse header = new ErrorResponse("99");
        header.setResponseCode(message);
        response.setMsgHeader(header);
        return response;
    }
}
