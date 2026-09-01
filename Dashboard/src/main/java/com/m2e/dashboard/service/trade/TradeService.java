package com.m2e.dashboard.service.trade;

import com.m2e.dashboard.config.CorpCifResolver;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.overview.Refactor.KursResolution;
import com.m2e.dashboard.dto.trade.DatamartListingProjection;
import com.m2e.dashboard.dto.trade.TradeAcctStatsByCurrencyProjection;
import com.m2e.dashboard.dto.trade.response.TradeAcctListingDTO;
import com.m2e.dashboard.dto.trade.response.TradeAcctStatsDTO;
import com.m2e.dashboard.enums.TradeEnum;
import com.m2e.dashboard.repository.DatamartAgreementRepository;
import com.m2e.dashboard.service.overview.AssetGraph.GraphCurrencyConverter;
import com.m2e.dashboard.service.overview.JobKursService.KursResolver;
import com.m2e.dashboard.sp.trade.response.TradeAcctListingSpDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradeService {

    private final DatamartAgreementRepository datamartAgreementRepository;
    private final CorpCifResolver corpCifResolver;
    private final KursResolver kursResolver;
    private final GraphCurrencyConverter graphConverter;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String STATS_CACHE_NAME = "tradeAcctStats";
    private static final String LISTING_CACHE_NAME = "tradeAcctListing";

    // ================= Account Stats =================
    @Cacheable(
            value = STATS_CACHE_NAME,
            key = "#userAndCorp.corpId + '_' + #tradeEnum.acctType + '_' "
                    + "+ (#baseCurrency != null and !#baseCurrency.isBlank() "
                    + "   ? #baseCurrency.trim().toUpperCase() : 'IDR') + '_' "
                    + "+ T(java.time.LocalDate).now()",
            unless = "#result == null || #result.msgBody == null"
    )
    public ApiResponse<TradeAcctStatsDTO> getTradeAccountStats(
            UserAndCorp userAndCorp, TradeEnum tradeEnum, String baseCurrency) {

        long start = System.currentTimeMillis();
        String acctType = tradeEnum.getAcctType();
        String targetCurrency = (baseCurrency != null && !baseCurrency.isBlank())
                ? baseCurrency.trim().toUpperCase() : "IDR";

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        try {
            String cifNo = corpCifResolver.resolveCif(userAndCorp.getCorpId());

            log.info("[TradeAcctStats] START cif={}, acctType={}, baseCurrency={}, filterDate={}",
                    cifNo, acctType, targetCurrency, today);

            List<TradeAcctStatsByCurrencyProjection> groups =
                    datamartAgreementRepository.getAccountStats(
                            cifNo, acctType, startOfDay, endOfDay);

            if (groups.isEmpty()) {
                log.warn("[TradeAcctStats] Tidak ada data untuk cif={}, acctType={}, tanggal={}. "
                        + "Cek apakah job SFTP sudah jalan hari ini.", cifNo, acctType, today);
                return successStats(buildStatsDto(0L, BigDecimal.ZERO, targetCurrency));
            }

            boolean needsConversion = groups.stream()
                    .anyMatch(g -> g.getCurrency() != null
                            && !targetCurrency.equalsIgnoreCase(g.getCurrency().trim()));

            Map<String, BigDecimal> rateMap = Map.of();
            if (needsConversion) {
                KursResolution kurs = kursResolver.resolve();
                if (kurs == null) {
                    log.error("[TradeAcctStats] Kurs tidak tersedia dari Trapi maupun DB, cif={}", cifNo);
                    return failureStats();
                }
                rateMap = kurs.getRateMap();
                log.info("[TradeAcctStats] Kurs source={} pairs={} age={}h",
                        kurs.getSource(), rateMap.size(), kurs.getAgeHours());
            }

            long totalAccounts = 0L;
            BigDecimal totalConverted = BigDecimal.ZERO;

            for (TradeAcctStatsByCurrencyProjection g : groups) {
                totalAccounts += (g.getNoOfAccounts() != null ? g.getNoOfAccounts() : 0L);

                BigDecimal amount = g.getTotalOutstandingBal() != null
                        ? g.getTotalOutstandingBal() : BigDecimal.ZERO;

                BigDecimal converted = graphConverter.convert(
                        amount, g.getCurrency(), targetCurrency, rateMap);

                if (converted == null) {
                    log.error("[TradeAcctStats] Gagal konversi {} -> {} untuk cif={}. "
                                    + "Total dibatalkan agar tidak menyesatkan.",
                            g.getCurrency(), targetCurrency, cifNo);
                    return failureStats();
                }

                log.debug("[TradeAcctStats] {} {} -> {} {}",
                        amount, g.getCurrency(), converted, targetCurrency);

                totalConverted = totalConverted.add(converted);
            }

            TradeAcctStatsDTO dto = buildStatsDto(totalAccounts, totalConverted, targetCurrency);

            long durationMs = System.currentTimeMillis() - start;
            log.info("[TradeAcctStats] SUCCESS cif={}, acctType={}, currencies={}, "
                            + "noOfAccounts={}, total={} {}, durationMs={}",
                    cifNo, acctType, groups.size(), totalAccounts,
                    dto.getTotalOutstandingBal(), targetCurrency, durationMs);

            return successStats(dto);

        } catch (Exception e) {
            long durationMs = System.currentTimeMillis() - start;
            log.error("[TradeAcctStats] FAILED corpId={}, acctType={}, durationMs={}, error={}",
                    userAndCorp.getCorpId(), acctType, durationMs, e.getMessage(), e);
            return failureStats();
        }
    }

    private TradeAcctStatsDTO buildStatsDto(long noOfAccounts, BigDecimal total, String currency) {
        LocalDateTime now = LocalDateTime.now();
        TradeAcctStatsDTO dto = new TradeAcctStatsDTO();
        dto.setNoOfAccounts(String.valueOf(noOfAccounts));
        dto.setTotalOutstandingBal(total.setScale(2, RoundingMode.HALF_UP).toPlainString());
        dto.setBaseCurrency(currency);
        dto.setLastUpdatedDate(now.format(DATE_FMT));
        dto.setLastUpdatedDateTime(now.format(DATETIME_FMT));
        return dto;
    }

    private ApiResponse<TradeAcctStatsDTO> successStats(TradeAcctStatsDTO dto) {
        ApiResponse<TradeAcctStatsDTO> res = new ApiResponse<>();
        res.setMsgBody(dto);
        res.setMsgHeader(new ErrorResponse("0"));
        return res;
    }

    private ApiResponse<TradeAcctStatsDTO> failureStats() {
        ApiResponse<TradeAcctStatsDTO> res = new ApiResponse<>();
        res.setMsgBody(null);
        res.setMsgHeader(new ErrorResponse("1"));
        return res;
    }

    @CacheEvict(value = STATS_CACHE_NAME, allEntries = true)
    public void evictAllTradeAcctStatsCache() {
        log.info("[TradeAcctStats] All cache entries evicted for {}", STATS_CACHE_NAME);
    }

    // ================= Account List =================

    @Cacheable(
            value = LISTING_CACHE_NAME,
            key = "(T(org.springframework.util.StringUtils).hasText(#req.openTaggedCorpId) ? #req.openTaggedCorpId : #userAndCorp.corpId)"
                    + " + '_' + #tradeEnum.acctType + '_' + #req.page + '_' + #req.pageSize + '_' + T(java.time.LocalDate).now()",
            unless = "#result == null || #result.msgBody == null"
    )
    public ApiResponse<TradeAcctListingDTO> getTradeAccountListing(UserAndCorp userAndCorp, AccountListReq req, TradeEnum tradeEnum) {
        long start = System.currentTimeMillis();
        ApiResponse<TradeAcctListingDTO> apiResponse = new ApiResponse<>();

        String acctType = tradeEnum.getAcctType();

        try {
            String targetCorpId = StringUtils.hasText(req.getOpenTaggedCorpId())
                    ? req.getOpenTaggedCorpId()
                    : userAndCorp.getCorpId();

            String cifNo = corpCifResolver.resolveCif(targetCorpId);

            log.info("[TradeAcctListing] START corpId={}, cif={}, acctType={}, page={}, pageSize={}",
                    targetCorpId, cifNo, acctType, req.getPage(), req.getPageSize());

            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

            Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize());

            Page<DatamartListingProjection> resultPage = datamartAgreementRepository.getAccountListing(
                    cifNo, acctType, startOfDay, endOfDay, pageable
            );

            Page<TradeAcctListingSpDTO> mappedPage = resultPage.map(this::toListingDto);

            SearchCollectionResponse<TradeAcctListingSpDTO> result = new SearchCollectionResponse<>(mappedPage);

            TradeAcctListingDTO dto = new TradeAcctListingDTO();
            dto.setCorpCifNo(cifNo);
            dto.setDatas(result);

            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(dto);

            long durationMs = System.currentTimeMillis() - start;
            log.info("[TradeAcctListing] SUCCESS cif={}, acctType={}, page={}, totalRecords={}, durationMs={}",
                    cifNo, acctType, req.getPage(), result.getTotalRecordCount(), durationMs);

            return apiResponse;

        } catch (Exception e) {
            long durationMs = System.currentTimeMillis() - start;
            log.error("[TradeAcctListing] FAILED corpId={}, acctType={}, durationMs={}, error={}",
                    userAndCorp.getCorpId(), acctType, durationMs, e.getMessage(), e);

            apiResponse.setMsgHeader(new ErrorResponse("1"));
            apiResponse.setMsgBody(null);
            return apiResponse;
        }
    }

    @CacheEvict(value = LISTING_CACHE_NAME, allEntries = true)
    public void evictAllTradeAcctListingCache() {
        log.info("[TradeAcctListing] All cache entries evicted for {}", LISTING_CACHE_NAME);
    }

    private TradeAcctListingSpDTO toListingDto(DatamartListingProjection p) {
        return TradeAcctListingSpDTO.builder()
                .refNumber(p.getRefNo())
                .facilityType(p.getProductType())
                .currency(p.getCurrency())
                .initialAmount(p.getInitialAmount() != null
                        ? p.getInitialAmount().setScale(2, RoundingMode.HALF_UP).toPlainString()
                        : "0.00")
                .outstandingAmount(p.getOutstandingAmount() != null
                        ? p.getOutstandingAmount().setScale(2, RoundingMode.HALF_UP).toPlainString()
                        : "0.00")
                .tenor(p.getTenor() != null ? String.valueOf(p.getTenor()) : null)
                .releaseDate(p.getReleaseDate() != null
                        ? java.sql.Timestamp.valueOf(p.getReleaseDate())
                        : null)
                .maturityDate(p.getMaturityDate() != null
                        ? java.sql.Date.valueOf(p.getMaturityDate())
                        : null)
                .drawerName(p.getDrawer())
                .remitingBank(p.getRemitingBank())
                .beneficiaryName(p.getBeneficiary())
                .applicantName(p.getApplicant())
                .issuingBank(p.getIssuingBank())
                .numberOfItems(null)
                .build();
    }
}