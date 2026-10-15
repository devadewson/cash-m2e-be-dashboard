package com.m2e.dashboard.service.trade;

import com.m2e.dashboard.config.CorpCifResolver;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.overview.Refactor.KursResolution;
import com.m2e.dashboard.dto.trade.TradeAcctStatsByCurrencyProjection;
import com.m2e.dashboard.dto.trade.response.TradeAcctListingDTO;
import com.m2e.dashboard.dto.trade.response.TradeAcctStatsDTO;
import com.m2e.dashboard.enums.TradeEnum;
import com.m2e.dashboard.projection.DatamartListingProjection;
import com.m2e.dashboard.repository.DatamartAgreementRepository;
import com.m2e.dashboard.service.overview.AssetGraph.GraphCurrencyConverter;
import com.m2e.dashboard.service.overview.Datamart.DatamartDateRange;
import com.m2e.dashboard.service.overview.Datamart.DatamartDateResolver;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Stats dan listing trade dari DATAMART_AGREEMENT, tanggal muat terakhir.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TradeService {

    private final DatamartAgreementRepository datamartAgreementRepository;
    private final CorpCifResolver corpCifResolver;
    private final KursResolver kursResolver;
    private final GraphCurrencyConverter graphConverter;
    private final DatamartDateResolver dateResolver;

    private static final String IDR = "IDR";
    private static final String CODE_SUCCESS = "0";
    private static final String CODE_FAILED = "1";
    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String STATS_CACHE_NAME = "tradeAcctStats";
    private static final String LISTING_CACHE_NAME = "tradeAcctListing";

    // ================= Account Stats =================

    /**
     * Produk mana saja yang dihitung untuk satu TradeEnum pada endpoint stats.
     *
     * Import LC/SKBDN dan Export LC/SKBDN Advising mencakup acceptance-nya,
     * karena issuance dan akseptasi adalah tahap dari produk yang sama.
     * Listing tetap per produk karena FE menampilkannya di dua tabel terpisah.
     */
    private List<String> resolveStatsProductTypes(TradeEnum tradeEnum) {
        return switch (tradeEnum) {
            case IMPORT_LC_SKBDN -> List.of(
                    TradeEnum.IMPORT_LC_SKBDN.getAcctType(),
                    TradeEnum.IMPORT_LC_SKBDN_ACCEPTANCE.getAcctType());

            case EXPORT_LC_SKBDN_ADVISING -> List.of(
                    TradeEnum.EXPORT_LC_SKBDN_ADVISING.getAcctType(),
                    TradeEnum.EXPORT_LC_SKBDN_ACCEPTANCE.getAcctType());

            default -> List.of(tradeEnum.getAcctType());
        };
    }

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
        List<String> acctTypes = resolveStatsProductTypes(tradeEnum);
        String targetCurrency = (baseCurrency != null && !baseCurrency.isBlank())
                ? baseCurrency.trim().toUpperCase() : IDR;

        String cifNo;
        try {
            cifNo = corpCifResolver.resolveCif(userAndCorp.getCorpId());
        } catch (Exception e) {
            log.error("[TradeAcctStats] CIF gagal di-resolve corpId={}: {}",
                    userAndCorp.getCorpId(), e.getMessage());
            return failureStats("CIF tidak ditemukan untuk korporasi ini");
        }

        try {
            Optional<DatamartDateRange> range = dateResolver.trade();
            if (range.isEmpty()) {
                log.warn("[TradeAcctStats] Belum ada data trade di database");
                return successStats(buildStatsDto(0L, BigDecimal.ZERO, targetCurrency, null));
            }
            DatamartDateRange r = range.get();

            log.info("[TradeAcctStats] START cif={}, acctTypes={}, baseCurrency={}, dataDate={}",
                    cifNo, acctTypes, targetCurrency, r.dataDate());

            List<TradeAcctStatsByCurrencyProjection> groups =
                    datamartAgreementRepository.getAccountStats(
                            cifNo, acctTypes, r.startOfDay(), r.endOfDay());

            if (groups.isEmpty()) {
                log.info("[TradeAcctStats] Tidak ada data untuk cif={}, acctTypes={}, dataDate={}",
                        cifNo, acctTypes, r.dataDate());
                return successStats(buildStatsDto(0L, BigDecimal.ZERO, targetCurrency, r.loadedAt()));
            }

            boolean needsConversion = groups.stream()
                    .anyMatch(g -> g.getCurrency() == null
                            || !targetCurrency.equalsIgnoreCase(g.getCurrency().trim()));

            Map<String, BigDecimal> rateMap = Map.of();
            if (needsConversion) {
                KursResolution kurs = kursResolver.resolve();
                if (kurs == null) {
                    log.error("[TradeAcctStats] Kurs tidak tersedia, cif={}", cifNo);
                    return failureStats("Kurs tidak tersedia untuk konversi ke " + targetCurrency);
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
                    log.error("[TradeAcctStats] Gagal konversi {} -> {} untuk cif={}. Total dibatalkan.",
                            g.getCurrency(), targetCurrency, cifNo);
                    return failureStats("Kurs " + g.getCurrency() + " ke " + targetCurrency + " tidak ditemukan");
                }

                totalConverted = totalConverted.add(converted);
            }

            TradeAcctStatsDTO dto = buildStatsDto(totalAccounts, totalConverted, targetCurrency, r.loadedAt());

            log.info("[TradeAcctStats] SUCCESS cif={}, acctTypes={}, currencies={}, "
                            + "noOfAccounts={}, total={} {}, durationMs={}",
                    cifNo, acctTypes, groups.size(), totalAccounts,
                    dto.getTotalOutstandingBal(), targetCurrency, System.currentTimeMillis() - start);

            return successStats(dto);

        } catch (Exception e) {
            log.error("[TradeAcctStats] FAILED cif={}, acctTypes={}, durationMs={}, error={}",
                    cifNo, acctTypes, System.currentTimeMillis() - start, e.getMessage(), e);
            return failureStats("Data tidak dapat diproses saat ini");
        }
    }

    /** lastUpdated mengikuti waktu muat data; kalau belum ada data, waktu sekarang. */
    private TradeAcctStatsDTO buildStatsDto(long noOfAccounts, BigDecimal total, String currency,
                                            LocalDateTime loadedAt) {
        LocalDateTime updatedAt = (loadedAt != null) ? loadedAt : LocalDateTime.now();
        TradeAcctStatsDTO dto = new TradeAcctStatsDTO();
        dto.setNoOfAccounts(String.valueOf(noOfAccounts));
        dto.setTotalOutstandingBal(total.setScale(2, RoundingMode.HALF_UP).toPlainString());
        dto.setBaseCurrency(currency);
        dto.setLastUpdatedDate(updatedAt.format(DATE_FMT));
        dto.setLastUpdatedDateTime(updatedAt.format(DATETIME_FMT));
        return dto;
    }

    private ApiResponse<TradeAcctStatsDTO> successStats(TradeAcctStatsDTO dto) {
        ApiResponse<TradeAcctStatsDTO> res = new ApiResponse<>();
        res.setMsgBody(dto);
        res.setMsgHeader(new ErrorResponse(CODE_SUCCESS));
        return res;
    }

    private ApiResponse<TradeAcctStatsDTO> failureStats(String message) {
        ErrorResponse header = new ErrorResponse(CODE_FAILED);
        header.setErrorMessage(message);
        ApiResponse<TradeAcctStatsDTO> res = new ApiResponse<>();
        res.setMsgBody(null);
        res.setMsgHeader(header);
        return res;
    }

    @CacheEvict(value = STATS_CACHE_NAME, allEntries = true)
    public void evictAllTradeAcctStatsCache() {
        log.info("[TradeAcctStats] All cache entries evicted for {}", STATS_CACHE_NAME);
    }

    // ================= Account List =================

    /**
     * Listing per satu productType. Issuance dan acceptance sengaja TIDAK digabung,
     * karena FE menampilkannya sebagai dua tabel terpisah.
     */
    @Cacheable(
            value = LISTING_CACHE_NAME,
            key = "(T(org.springframework.util.StringUtils).hasText(#req.openTaggedCorpId) ? #req.openTaggedCorpId : #userAndCorp.corpId)"
                    + " + '_' + #tradeEnum.acctType + '_' + #req.page + '_' + #req.pageSize + '_' + T(java.time.LocalDate).now()",
            unless = "#result == null || #result.msgBody == null"
    )
    public ApiResponse<TradeAcctListingDTO> getTradeAccountListing(
            UserAndCorp userAndCorp, AccountListReq req, TradeEnum tradeEnum) {

        long start = System.currentTimeMillis();
        ApiResponse<TradeAcctListingDTO> apiResponse = new ApiResponse<>();
        String acctType = tradeEnum.getAcctType();

        try {
            String targetCorpId = StringUtils.hasText(req.getOpenTaggedCorpId())
                    ? req.getOpenTaggedCorpId()
                    : userAndCorp.getCorpId();

            String cifNo = corpCifResolver.resolveCif(targetCorpId);

            // FE mengirim page mulai 0, sesuai PageRequest
            Pageable pageable = toPageable(req.getPage(), req.getPageSize());

            Page<TradeAcctListingSpDTO> mappedPage;
            Optional<DatamartDateRange> range = dateResolver.trade();

            if (range.isEmpty()) {
                log.warn("[TradeAcctListing] Belum ada data trade di database");
                mappedPage = Page.empty(pageable);
            } else {
                DatamartDateRange r = range.get();

                log.info("[TradeAcctListing] START corpId={}, cif={}, acctType={}, page={}, pageSize={}, dataDate={}",
                        targetCorpId, cifNo, acctType, pageable.getPageNumber(), pageable.getPageSize(), r.dataDate());

                Page<DatamartListingProjection> resultPage = datamartAgreementRepository.getAccountListing(
                        cifNo, acctType, r.startOfDay(), r.endOfDay(), pageable);

                mappedPage = resultPage.map(this::toListingDto);
            }

            SearchCollectionResponse<TradeAcctListingSpDTO> result = new SearchCollectionResponse<>(mappedPage);

            TradeAcctListingDTO dto = new TradeAcctListingDTO();
            dto.setCorpCifNo(cifNo);
            dto.setDatas(result);

            apiResponse.setMsgHeader(new ErrorResponse(CODE_SUCCESS));
            apiResponse.setMsgBody(dto);

            log.info("[TradeAcctListing] SUCCESS cif={}, acctType={}, page={}, totalRecords={}, durationMs={}",
                    cifNo, acctType, pageable.getPageNumber(), result.getTotalRecordCount(),
                    System.currentTimeMillis() - start);

            return apiResponse;

        } catch (Exception e) {
            log.error("[TradeAcctListing] FAILED corpId={}, acctType={}, durationMs={}, error={}",
                    userAndCorp.getCorpId(), acctType, System.currentTimeMillis() - start, e.getMessage(), e);

            ErrorResponse header = new ErrorResponse(CODE_FAILED);
            apiResponse.setMsgHeader(header);
            apiResponse.setMsgBody(null);
            return apiResponse;
        }
    }

    @CacheEvict(value = LISTING_CACHE_NAME, allEntries = true)
    public void evictAllTradeAcctListingCache() {
        log.info("[TradeAcctListing] All cache entries evicted for {}", LISTING_CACHE_NAME);
    }

    private Pageable toPageable(Integer page, Integer pageSize) {
        int p = (page == null || page < 0) ? 0 : page;
        int size = (pageSize == null || pageSize <= 0) ? DEFAULT_PAGE_SIZE : pageSize;
        return PageRequest.of(p, size);
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