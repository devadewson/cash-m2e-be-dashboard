package com.m2e.dashboard.service.trade;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.trade.DatamartListingProjection;
import com.m2e.dashboard.dto.trade.TradeAcctStatsProjection;
import com.m2e.dashboard.dto.trade.response.TradeAcctListingDTO;
import com.m2e.dashboard.dto.trade.response.TradeAcctStatsDTO;
import com.m2e.dashboard.enums.TradeEnum;
import com.m2e.dashboard.repository.DatamartAgreementRepository;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class TradeService {

    private final DatamartAgreementRepository datamartAgreementRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String STATS_CACHE_NAME = "tradeAcctStats";
    private static final String LISTING_CACHE_NAME = "tradeAcctListing";

    // ================= Account Stats =================

    @Cacheable(
            value = STATS_CACHE_NAME,
            key = "#userAndCorp.corpId + '_' + #tradeEnum.acctType + '_' + T(java.time.LocalDate).now()",
            unless = "#result == null || #result.msgBody == null"
    )
    public ApiResponse<TradeAcctStatsDTO> getTradeAccountStats(UserAndCorp userAndCorp, TradeEnum tradeEnum) {
        long start = System.currentTimeMillis();
        String corpId = userAndCorp.getCorpId();
        String acctType = tradeEnum.getAcctType();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        log.info("[TradeAcctStats] START corpId={}, acctType={}, filterDate={}", corpId, acctType, today);

        try {
            TradeAcctStatsProjection result =
                    datamartAgreementRepository.getAccountStats(corpId, acctType, startOfDay, endOfDay);

            long noOfAccounts = (result != null && result.getNoOfAccounts() != null)
                    ? result.getNoOfAccounts() : 0L;
            BigDecimal totalOutstandingBal = (result != null && result.getTotalOutstandingBal() != null)
                    ? result.getTotalOutstandingBal() : BigDecimal.ZERO;

            LocalDateTime now = LocalDateTime.now();

            TradeAcctStatsDTO dto = new TradeAcctStatsDTO();
            dto.setNoOfAccounts(String.valueOf(noOfAccounts));
            dto.setTotalOutstandingBal(totalOutstandingBal.setScale(2, RoundingMode.HALF_UP).toPlainString());
            dto.setLastUpdatedDate(now.format(DATE_FMT));
            dto.setLastUpdatedDateTime(now.format(DATETIME_FMT));

            long durationMs = System.currentTimeMillis() - start;
            log.info("[TradeAcctStats] SUCCESS corpId={}, acctType={}, noOfAccounts={}, totalOutstandingBal={}, durationMs={}",
                    corpId, acctType, dto.getNoOfAccounts(), dto.getTotalOutstandingBal(), durationMs);

            ApiResponse<TradeAcctStatsDTO> apiResponse = new ApiResponse<>();
            apiResponse.setMsgBody(dto);
            apiResponse.setMsgHeader(new ErrorResponse("0"));
            return apiResponse;

        } catch (Exception e) {
            long durationMs = System.currentTimeMillis() - start;
            log.error("[TradeAcctStats] FAILED corpId={}, acctType={}, durationMs={}, error={}",
                    corpId, acctType, durationMs, e.getMessage(), e);

            ApiResponse<TradeAcctStatsDTO> apiResponse = new ApiResponse<>();
            apiResponse.setMsgBody(null);
            apiResponse.setMsgHeader(new ErrorResponse("1"));
            return apiResponse;
        }
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

        String corpId = StringUtils.hasText(req.getOpenTaggedCorpId()) ? req.getOpenTaggedCorpId() : userAndCorp.getCorpId();
        String acctType = tradeEnum.getAcctType();

        log.info("[TradeAcctListing] START corpId={}, acctType={}, page={}, pageSize={}",
                corpId, acctType, req.getPage(), req.getPageSize());

        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

            Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize());

            Page<DatamartListingProjection> resultPage = datamartAgreementRepository.getAccountListing(
                    corpId, acctType, startOfDay, endOfDay, pageable
            );

            Page<TradeAcctListingSpDTO> mappedPage = resultPage.map(this::toListingDto);

            SearchCollectionResponse<TradeAcctListingSpDTO> result = new SearchCollectionResponse<>(mappedPage);

            TradeAcctListingDTO dto = new TradeAcctListingDTO();
            dto.setCorpCifNo(req.getCorpCifNo());
            dto.setDatas(result);

            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(dto);

            long durationMs = System.currentTimeMillis() - start;
            log.info("[TradeAcctListing] SUCCESS corpId={}, acctType={}, page={}, totalRecords={}, durationMs={}",
                    corpId, acctType, req.getPage(), result.getTotalRecordCount(), durationMs);

            return apiResponse;

        } catch (Exception e) {
            long durationMs = System.currentTimeMillis() - start;
            log.error("[TradeAcctListing] FAILED corpId={}, acctType={}, durationMs={}, error={}",
                    corpId, acctType, durationMs, e.getMessage(), e);

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