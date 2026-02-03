package com.m2e.dashboard.service.af.hp;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.AccountConditions;
import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.dto.MoreActionsRedirection;
import com.m2e.dashboard.dto.af.request.AFTrxHistoryFilterDTO;
import com.m2e.dashboard.dto.af.request.hp.AFHPAcctDetailReqDTO;
import com.m2e.dashboard.dto.af.request.hp.AFHPAcctStatsReqDTO;
import com.m2e.dashboard.dto.af.request.hp.AFHPTrxHistReqDTO;
import com.m2e.dashboard.dto.af.request.hp.download.AFHPAcctListDownloadReq;
import com.m2e.dashboard.dto.af.request.hp.download.AFHPTrxHistDownloadReq;
import com.m2e.dashboard.dto.af.response.hp.*;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.base.af.AFAccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.*;
import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.jms.af.common.ESBAFAcctActivityReqDTO;
import com.m2e.dashboard.jms.af.hp.request.account.balanceinquiry.ESBAFHPAcctListReqDTO;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.mapper.af.hp.ESBAFHPAcctDetailsMergeMapper;
import com.m2e.dashboard.projection.DepositAcctDetailPageProjection;
import com.m2e.dashboard.repository.AccountLinkHPBHPRepository;
import com.m2e.dashboard.repository.AccountProductTypeMatrixRepository;
import com.m2e.dashboard.repository.AccountRepository;
import com.m2e.dashboard.repository.SysParamRepository;
import com.m2e.dashboard.service.AsyncPaginationService;
import com.m2e.dashboard.service.FileRepoService;
import com.m2e.dashboard.service.af.hp.jms.AFHPESBService;
import com.m2e.dashboard.service.af.hp.sp.AFHirePurchaseSPService;
import com.m2e.dashboard.sp.af.hp.request.AFHPAcctDetailSpParam;
import com.m2e.dashboard.sp.af.hp.request.AFHPAcctListSpParam;
import com.m2e.dashboard.sp.af.hp.request.AFHPAcctStatsSpParam;
import com.m2e.dashboard.sp.af.hp.request.AFHPTrxHistSpParam;
import com.m2e.dashboard.util.DateTimeUtil;
import com.m2e.dashboard.util.UtilityFunctions;
import com.m2e.model.sso.base.AccountProductTypeMatrix;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.m2e.dashboard.enums.AcctProductEntitlementConst.LOAN_AND_CARD_PAYMENT_PRODUCT_CODE;
import static com.m2e.dashboard.enums.AcctProductEntitlementConst.USER_ACCT_PRODUCT_LIST_REDIS_KEY;
import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_AF_HP_ACCTLIST;
import static com.m2e.dashboard.enums.ReportCommonConstant.*;
import static com.m2e.dashboard.util.ESBValueUtil.removeLeadingZeroes;

@Service
@Slf4j
@RequiredArgsConstructor
public class AFHirePurchaseService {
    private final int batchSize = 10;

    @Value("${cache.ttl}")
    private Duration cacheDuration;

    private final AFHPAcctListFileService afHPAcctListFileService;
    private final AFHPTrxHistFileService afHPTrxHistFileService;
    private final AFHirePurchaseSPService afHirePurchaseSPService;
    private final AccountRepository accountRepository;
    private final AFHPESBService afhpesbService;
    private final RedisHelper redisHelper;
    private final AccountProductTypeMatrixRepository matrixRepository;
    private final SysParamRepository sysParamRepository;
    private final FileRepoService fileRepoService;
    private final AsyncPaginationService asyncPaginationService;
    private final AccountLinkHPBHPRepository accountLinkHPBHPRepository;
    private final ESBAFHPAcctDetailsMergeMapper mergeMapper;

    public ApiResponse<AFHPAcctStatsDTO> hirePurchaseAccountStats(UserAndCorp userAndCorp, AFHPAcctStatsReqDTO request) {
        log.info("hirePurchaseAccountStats");
        removeAllFromCache(userAndCorp);
        ApiResponse<AFHPAcctStatsDTO> apiResponse = new ApiResponse<>();

        AFHPAcctStatsSpParam afhpAcctStatsSpParam = new AFHPAcctStatsSpParam();
        afhpAcctStatsSpParam.setUserId(userAndCorp.getUserId());
        afhpAcctStatsSpParam.setCorpId(userAndCorp.getCorpId());

        AFHPAcctStatsDTO result = afHirePurchaseSPService.afHPAcctStatsSp(afhpAcctStatsSpParam);

        if (Boolean.TRUE.equals(request.getRealTimeIndicator())) {
            result.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
        }

        apiResponse.setMsgBody(result);
        apiResponse.setMsgHeader(new ErrorResponse("0"));

        return apiResponse;
    }

    public ApiResponse<AFHPAcctSummaryResp> hirePurchaseAccountListing(AFAccountListReq request, UserAndCorp userAndCorp) {
        log.info("hirePurchaseAccountListing");
        ApiResponse<SearchCollectionResponse<AFHPAcctListDTO>> apiResponse = new ApiResponse<>();

        ApiResponse<AFHPAcctSummaryResp> finalResp = getAcctFromCache(userAndCorp, request);

        if (finalResp == null) {
            AFHPAcctListSpParam afhpAcctListSpParam = new AFHPAcctListSpParam();
            afhpAcctListSpParam.setUserId(userAndCorp.getUserId());
            afhpAcctListSpParam.setCorpId(userAndCorp.getCorpId());
            afhpAcctListSpParam.setCorpCifNo(request.getCorpCifNo());
            afhpAcctListSpParam.setSearchParam(request.getSearchParam());

            BasePageRequest pageRequest = new BasePageRequest();
            pageRequest.setPage(request.getPage());
            pageRequest.setPageSize(request.getPageSize());

            SearchCollectionResponse<AFHPAcctListDTO> spResult = afHirePurchaseSPService.afHPAccountListingSP(afhpAcctListSpParam, pageRequest);
            if (!spResult.getData().isEmpty()) {
                spResult.getData().forEach(dto -> {
                    dto.setMoreActions(getMoreActions(dto, userAndCorp));
                });
            }

            ESBMetadataDTO esbMetadataDTO = new ESBMetadataDTO();
            if (request.isRealTimeIndicator()) {
                log.info("T real time");
                List<AFHPAcctListDTO> t1Data = getInitialData(spResult);
                log.info("HP inquiry t1Data: {}", t1Data);

                List<String> providedProviders = getProvidedProviders(t1Data);
                List<String> providedSubProducts = getProvidedSubProducts(t1Data);
                List<AFHPAcctListDTO> tData = new ArrayList<>();

                if (!providedProviders.isEmpty() && !providedSubProducts.isEmpty()) {
                    AFAcctListESBDTO afAcctListESBDTO = processProvidersAndSubProducts(providedProviders, providedSubProducts, t1Data, userAndCorp);
                    tData = (List<AFHPAcctListDTO>) afAcctListESBDTO.getAcctListing();
                    log.info("HP inquiry tData: {}", tData);
                    esbMetadataDTO = afAcctListESBDTO.getEsbMetadata();
                } else {
                    log.info("No providers or sub-products available to process for Corporate CIF No {}", request.getCorpCifNo());
                }

                updateBalanceValues(tData, t1Data);
                log.info("post-updateBalanceValues t1Data: {}", t1Data);
                spResult.setData(t1Data);
            }

            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(spResult);

            finalResp = convertApiResponse(apiResponse, request.getCorpCifNo(), esbMetadataDTO);

            redisHelper.save(getRedisKey(userAndCorp, request), finalResp, cacheDuration.toMinutes(), TimeUnit.MINUTES);
        }
        return finalResp;
    }

    private MoreActionsEntitlement getMoreActions(AFHPAcctListDTO dto, UserAndCorp userAndCorp) {
        String makePaymentRedisKey = redisHelper.buildCacheKey(userAndCorp.getUserId(), userAndCorp.getCorpId(), USER_ACCT_PRODUCT_LIST_REDIS_KEY, LOAN_AND_CARD_PAYMENT_PRODUCT_CODE, dto.getAcctNumber(), dto.getCurrency());
        log.debug("getMoreActions() makePaymentRedisKey Redis key: {}", makePaymentRedisKey);
        MoreActionsEntitlement moreActionsEntitlement = new MoreActionsEntitlement();
        moreActionsEntitlement.setMakePayment(new MoreActionsRedirection(redisHelper.isPresent(makePaymentRedisKey), RedirectModuleEnum.PAYMENT_ACTION_REDIRECT.name(), Application.CASHLITE.getValue(), LOAN_AND_CARD_PAYMENT_PRODUCT_CODE));
        return moreActionsEntitlement;
    }

    public ApiResponse hirePurchaseAcctDetail(UserAndCorp userAndCorp, AFHPAcctDetailReqDTO request) {
        AFHPAcctDetailSpParam afhpAcctDetailSpParam = new AFHPAcctDetailSpParam();
        afhpAcctDetailSpParam.setAcctNumber(request.getAcctNumber());
        afhpAcctDetailSpParam.setAccountLevel(request.getAccountLevel().name());
        afhpAcctDetailSpParam.setUserId(userAndCorp.getUserId());
        afhpAcctDetailSpParam.setCorpId(userAndCorp.getCorpId());

        switch (request.getProvider()) {
            case MY:
                return handleMYProvider(request, afhpAcctDetailSpParam);
            case SG:
                return handleSGProvider(request, afhpAcctDetailSpParam);
            default:
                log.error("Provider is unrecognized");
                return new ApiResponse<>(new ErrorResponse("Provider is unrecognized"), null);
        }
    }

    private DepositAcctDetailPageProjection getAccountData(String acctNumber, String currency) {
        return accountRepository.findAcctDetails(acctNumber, currency)
                .orElse(null);
    }

    private ApiResponse<?> handleMYProvider(AFHPAcctDetailReqDTO request, AFHPAcctDetailSpParam afhpAcctDetailSpParam) {
        ESBAcctListingReqDTO esbAcctListingReqDTO = new ESBAcctListingReqDTO(request.getAcctNumber(), request.getCurrency(), getAcctTypeCode(request.getProvider().name()), request.getProvider().name());
        switch (request.getAccountLevel()) {
            case FACILITY:
                return processFacilityLevel(esbAcctListingReqDTO, afhpAcctDetailSpParam);
            case ACCOUNT:
                return processAccountLevel(esbAcctListingReqDTO, afhpAcctDetailSpParam);
            default:
                log.error("Invalid account level indicator");
                return new ApiResponse<>(new ErrorResponse("Invalid account level indicator"), null);
        }
    }

    private ApiResponse<AFHPMasterAcctDetailDTO> processFacilityLevel(ESBAcctListingReqDTO request, AFHPAcctDetailSpParam afhpAcctDetailSpParam) {
        AFHPMasterAcctDetailDTO afhpMasterAcctDetailDTO = afHirePurchaseSPService.afHPFacilityDetailMYSP(afhpAcctDetailSpParam);
        boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(afhpMasterAcctDetailDTO.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(afhpMasterAcctDetailDTO.getAcctStatusDesc());
        log.info("HP facility sp: {}", afhpMasterAcctDetailDTO);

        ApiResponse<AFHPMasterAcctDetailDTO> response = afhpesbService.getAFHPMasterAcctDetails(request);
        log.info("HP facility esb: {}", response);

        if (response.getMsgBody() != null) {
            afhpMasterAcctDetailDTO.setProductType(response.getMsgBody().getAcctType());
            afhpMasterAcctDetailDTO.setAcctType(response.getMsgBody().getAcctType());
            afhpMasterAcctDetailDTO.setFinancedAmt(isClosed ? null : response.getMsgBody().getFinancedAmt());
            afhpMasterAcctDetailDTO.setFacilityLimit(isClosed ? null : response.getMsgBody().getFacilityLimit());
            afhpMasterAcctDetailDTO.setOutstandingBalance(response.getMsgBody().getOutstandingBalance());
            afhpMasterAcctDetailDTO.setTotalPenalty(response.getMsgBody().getTotalPenalty());
            afhpMasterAcctDetailDTO.setTotalUnitExpired(response.getMsgBody().getTotalUnitExpired());
            afhpMasterAcctDetailDTO.setDateApproved(response.getMsgBody().getDateApproved());
            afhpMasterAcctDetailDTO.setNumberOfDaysAllowed(response.getMsgBody().getNumberOfDaysAllowed());
            afhpMasterAcctDetailDTO.setEsbMetadata(response.getMsgBody().getEsbMetadata());
        }

        return new ApiResponse<>(new ErrorResponse("0"), afhpMasterAcctDetailDTO);
    }

    private ApiResponse<AFHPAcctDetailDTO> processAccountLevel(ESBAcctListingReqDTO request, AFHPAcctDetailSpParam afhpAcctDetailSpParam) {
        AFHPAcctDetailDTO afhpAcctDetailDTO = afHirePurchaseSPService.afHPAccountDetailMYSP(afhpAcctDetailSpParam);
        boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(afhpAcctDetailDTO.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(afhpAcctDetailDTO.getAcctStatusDesc());
        log.info("HP account sp: {}", afhpAcctDetailDTO);

        ApiResponse<AFHPAcctDetailDTO> response = afhpesbService.getAFHPAcctDetails(request);
        log.info("HP account esb: {}", response);

        if (response.getMsgBody() != null) {
            afhpAcctDetailDTO.setVehicalReg(response.getMsgBody().getVehicalReg());
            afhpAcctDetailDTO.setFinancedAmt(isClosed ? null : response.getMsgBody().getFinancedAmt());
            afhpAcctDetailDTO.setOtherCharges(isClosed ? null : response.getMsgBody().getOtherCharges());
            afhpAcctDetailDTO.setOutstandingBalance(isClosed ? null : response.getMsgBody().getOutstandingBalance());
            afhpAcctDetailDTO.setSettlementAmt(isClosed ? null : response.getMsgBody().getSettlementAmt());
            afhpAcctDetailDTO.setMonthlyInstalment(isClosed ? null : response.getMsgBody().getMonthlyInstalment());
            afhpAcctDetailDTO.setInterestRate(isClosed ? null : response.getMsgBody().getInterestRate());
            afhpAcctDetailDTO.setDueDate(isClosed ? null : DateTimeUtil.convertDateFormat(response.getMsgBody().getDueDate()));
            afhpAcctDetailDTO.setNumberOfInstalmentPaid(isClosed ? null : response.getMsgBody().getNumberOfInstalmentPaid());
            afhpAcctDetailDTO.setFinancingPeriod(isClosed ? null : response.getMsgBody().getFinancingPeriod());

            afhpAcctDetailDTO.setAmountDue(isClosed ? null : response.getMsgBody().getInstalmentDue());
            afhpAcctDetailDTO.setNumberOfMonthsDue(isClosed ? null : response.getMsgBody().getNumberOfMonthsDue());
            afhpAcctDetailDTO.setInstalmentDue(isClosed ? null : response.getMsgBody().getInstalmentDue());
            afhpAcctDetailDTO.setOverdueInterest(isClosed ? null : response.getMsgBody().getOverdueInterest());
            afhpAcctDetailDTO.setLateCharges(isClosed ? null : response.getMsgBody().getLateCharges());
            afhpAcctDetailDTO.setEsbMetadata(response.getMsgBody().getEsbMetadata());

            DepositAcctDetailPageProjection acctDetailPageProjection = getAccountData(afhpAcctDetailDTO.getAcctNumber(), afhpAcctDetailDTO.getCurrency());
            boolean isIslamic =  acctDetailPageProjection != null && "Y".equalsIgnoreCase(acctDetailPageProjection.getIsIslamic());
            afhpAcctDetailDTO.setIslamic(isIslamic);
        }

        return new ApiResponse<>(new ErrorResponse("0"), afhpAcctDetailDTO);
    }

    private ApiResponse<?> handleSGProvider(AFHPAcctDetailReqDTO request, AFHPAcctDetailSpParam afhpAcctDetailSpParam) {
        AFHPSGAcctDetailDTO afhpAcctDetailDTO = afHirePurchaseSPService.afHPAccountDetailSGSP(afhpAcctDetailSpParam, com.m2e.dashboard.dto.af.response.hp.AFHPSGAcctDetailDTO.class);
        log.info("HP account sg sp: {}", afhpAcctDetailDTO);

        ESBAcctListingReqDTO esbAcctListingReqDTO = new ESBAcctListingReqDTO(request.getAcctNumber(), request.getCurrency(), getAcctTypeCode(request.getProvider().name()), request.getProvider().name());
        ApiResponse<AFHPSGAcctDetailDTO> response = afhpesbService.getAFHPSGAcctDetails(esbAcctListingReqDTO);
        log.info("HP account sg esb: {}", response);

        if (response.getMsgBody() != null) {
            AFHPSGAcctDetailDTO afhpAcctDetailESBDTO = response.getMsgBody();
            mergeMapper.mergeForProviderSG(
                    afhpAcctDetailDTO,
                    afhpAcctDetailESBDTO,
                    new AccountConditions(
                            afhpAcctDetailDTO::getAcctStatusDesc,
                            afhpAcctDetailESBDTO::getTlastIndicator
                    )
            );

            DepositAcctDetailPageProjection acctDetailPageProjection = getAccountData(afhpAcctDetailDTO.getAcctNumber(), afhpAcctDetailDTO.getCurrency());
            boolean isIslamic =  acctDetailPageProjection != null && "Y".equalsIgnoreCase(acctDetailPageProjection.getIsIslamic());
            afhpAcctDetailDTO.setIslamic(isIslamic);
        }

        return new ApiResponse<>(new ErrorResponse("0"), afhpAcctDetailDTO);
    }

    public ApiResponse<AFHPTrxHistoryResp> hirePurchaseTrxHistory(AFHPTrxHistReqDTO request, UserAndCorp userAndCorp) {
        // Prepare parameters for stored procedure
        AFHPTrxHistSpParam afhpTrxHistSpParam = prepareAfhpTrxHistSpParam(request, userAndCorp);

        ApiResponse<AFHPTrxHistoryResp> apiResponse = new ApiResponse<>();
        AFHPTrxHistoryResp afTrxHistoryDTO = new AFHPTrxHistoryResp();
        BasePageRequest pageRequest = preparePageRequest(request);

        // get trx hist data from SP
        SearchCollectionResponse<AFHPTrxHistDTO> spResult = Optional.ofNullable(afHirePurchaseSPService.afHPTrxHistorySP(afhpTrxHistSpParam, pageRequest, AFHPTrxHistDTO.class))
                .orElse(new SearchCollectionResponse<>());

        switch (request.getProvider()) {
            case MY -> {
                ESBAFAcctActivityReqDTO acctActivityReqDTO = new ESBAFAcctActivityReqDTO(request.getAcctNumber(),
                        request.getSearchParam(), null,
                        request.getProvider(), request.getCurrency(),
                        request.getMbbLastKey(),
                        getAcctTypeCode(request.getProvider().name()), request.getFilters(),
                        request.getPage(), request.getPageSize());

                // get trx hist data from ESB
                afTrxHistoryDTO = afhpesbService.getAFHPTrxHistory(acctActivityReqDTO).getMsgBody();

                List<AFHPTrxHistDTO> esbDataList = Optional.ofNullable(afTrxHistoryDTO.getDatas()).map(SearchCollectionResponse::getData).orElse(new ArrayList<>());
                List<AFHPTrxHistDTO> spDataList = Optional.of(spResult).map(SearchCollectionResponse::getData).orElse(new ArrayList<>());

                // if esb data is equal or more than pageSize, then return it to front end
                if (esbDataList.size() >= request.getPageSize()) {
                    apiResponse.setMsgHeader(new ErrorResponse("0"));
                    apiResponse.setMsgBody(afTrxHistoryDTO);
                    return apiResponse;
                    // else combine both esb and sp data, with esb comes first
                } else {
                    if (!esbDataList.isEmpty()) {
                        int sublistToIndex = Math.min(esbDataList.size(), request.getPageSize());
                        esbDataList = esbDataList.subList(0, sublistToIndex);
                    }
                    int remainingSize = request.getPageSize() - esbDataList.size();
                    log.info("remainingSize --> {}", remainingSize);

                    List<AFHPTrxHistDTO> fusedTrxHistoryDTOList = new ArrayList<>(esbDataList);
                    fusedTrxHistoryDTOList.addAll(spDataList.subList(0, Math.min(remainingSize, spDataList.size())));
                    // total record count of both esb and sp data
                    int esbTotalRecordCount = Optional.ofNullable(afTrxHistoryDTO.getDatas())
                            .map(SearchCollectionResponse::getTotalRecordCount)
                            .orElse(0);
                    int spTotalRecordCount = Optional.of(spResult)
                            .map(SearchCollectionResponse::getTotalRecordCount)
                            .orElse(0);
                    int totalRecordCount = esbTotalRecordCount + spTotalRecordCount;

                    afTrxHistoryDTO.setDatas(asyncPaginationService.constructSearchCollectionResponse(fusedTrxHistoryDTOList, request.getPageSize(), request.getPage(), totalRecordCount));
                }
            }
            case SG -> afTrxHistoryDTO.setDatas(spResult);
        }

        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(afTrxHistoryDTO);
        return apiResponse;
    }

    private <T> List<T> filterData(List<T> data, AFTrxHistoryFilterDTO filters) {
        return data.stream()
                .filter(item -> {
                    if (item instanceof AFHPTrxHistDTO dto) {
                        boolean isTxnCodeMatch = filters.getTrxCode().equals(dto.getTrxCode());

                        boolean isDateMatch = isBetween(
                                filters.getStartDate() + filters.getStartTime(),
                                filters.getEndDate() + filters.getEndTime(),
                                dto.getTrxDate()
                        );

                        boolean isAmountMatch = isBetween(
                                filters.getAmtFrom(),
                                filters.getAmtTo(),
                                dto.getDebit() != null ? dto.getDebit() : dto.getCredit()
                        );

                        return isTxnCodeMatch && isDateMatch && isAmountMatch;
                    }
                    return false;
                })
                .toList();
    }

    private boolean isBetween(String start, String end, String value) {
        // Implement comparison logic for date, time, or numeric ranges
        if (value == null || start == null || end == null) return false;

        try {
            return value.compareTo(start) >= 0 && value.compareTo(end) <= 0;
        } catch (Exception e) {
            log.error("Error comparing values: start={}, end={}, value={}.", start, end, value, e);
            return false;
        }
    }

    private AFHPTrxHistSpParam prepareAfhpTrxHistSpParam(AFHPTrxHistReqDTO request, UserAndCorp userAndCorp) {
        AFHPTrxHistSpParam param = new AFHPTrxHistSpParam();
        param.setAcctNumber(StringUtils.hasText(request.getAcctNumber()) ? request.getAcctNumber() : null);
        param.setSearchParam(StringUtils.hasText(request.getSearchParam()) ? request.getSearchParam() : null);
        param.setTrxCode(StringUtils.hasText(request.getFilters().getTrxCode()) ? request.getFilters().getTrxCode() : null);
        param.setStartDateTime(
                StringUtils.hasText(request.getFilters().getStartDate()) && StringUtils.hasText(request.getFilters().getStartTime())
                        ? DateTimeUtil.convert12HourDateTime(request.getFilters().getStartDate(), request.getFilters().getStartTime(), true)
                        : null
        );
        param.setEndDateTime(
                StringUtils.hasText(request.getFilters().getEndDate()) && StringUtils.hasText(request.getFilters().getEndTime())
                        ? DateTimeUtil.convert12HourDateTime(request.getFilters().getEndDate(), request.getFilters().getEndTime(), false)
                        : null
        );
        param.setAmtFrom(StringUtils.hasText(request.getFilters().getAmtFrom()) ? request.getFilters().getAmtFrom() : null);
        param.setAmtTo(StringUtils.hasText(request.getFilters().getAmtTo()) ? request.getFilters().getAmtTo() : null);
        param.setUserId(StringUtils.hasText(userAndCorp.getUserId()) ? userAndCorp.getUserId() : null);
        param.setCorpId(StringUtils.hasText(userAndCorp.getCorpId()) ? userAndCorp.getCorpId() : null);
        return param;
    }


    private BasePageRequest preparePageRequest(AFHPTrxHistReqDTO request) {
        BasePageRequest pageRequest = new BasePageRequest();
        pageRequest.setPage(request.getPage());
        pageRequest.setPageSize(request.getPageSize());
        return pageRequest;
    }


    public DownloadDTO downloadHirePurchase(DownloadReq request, UserAndCorp userAndCorp) {

        return switch (request.getReportType()) {
            case AF_HP_ACCOUNT_LISTING_RPT_TYPE -> downloadAcctListing(request, userAndCorp);
            case AF_HP_TRX_HISTORY_RPT_TYPE -> downloadTrxHistory(request, userAndCorp);
            default -> throw new IllegalArgumentException("Report type is undefined");
        };
    }

    public DownloadDTO downloadAcctListing(DownloadReq req, UserAndCorp userAndCorp) {

        AFHPAcctListDownloadReq request = (AFHPAcctListDownloadReq) req;

        AFHPAcctListSpParam afhpAcctListSpParam = new AFHPAcctListSpParam();
        afhpAcctListSpParam.setUserId(userAndCorp.getUserId());
        afhpAcctListSpParam.setCorpId(StringUtils.hasText(request.getOpenTaggedCorpId()) ? request.getOpenTaggedCorpId() : userAndCorp.getCorpId());
        afhpAcctListSpParam.setSearchParam(request.getSearchParam());
        afhpAcctListSpParam.setCorpCifNo(request.getCorpCifNo());

        BasePageRequest pageRequest = new BasePageRequest();
        pageRequest.setPage(1);
        pageRequest.setPageSize(Integer.MAX_VALUE);

        SearchCollectionResponse<AFHPAcctListDTO> result = afHirePurchaseSPService.afHPAccountListingSP(afhpAcctListSpParam, pageRequest);

        if (request.getRealTimeIndicator().equals(Boolean.TRUE)) {
            log.info("T real time");
            List<AFHPAcctListDTO> t1Data = getInitialData(result);
            List<String> providedProviders = getProvidedProviders(t1Data);
            List<String> providedSubProducts = getProvidedSubProducts(t1Data);
            List<AFHPAcctListDTO> tData = new ArrayList<>();
            AFAcctListESBDTO afAcctListESBDTO = new AFAcctListESBDTO();

            if (!providedProviders.isEmpty() && !providedSubProducts.isEmpty()) {
                afAcctListESBDTO = processProvidersAndSubProducts(providedProviders, providedSubProducts, t1Data, userAndCorp);
                tData = (List<AFHPAcctListDTO>) afAcctListESBDTO.getAcctListing();
            } else {
                log.info("No providers or sub-products available to process for Corporate {}", afhpAcctListSpParam.getCorpId());
            }

            updateBalanceValues(tData, t1Data);
            result.setData(t1Data);
        }

        DownloadDTO resultDownload = new DownloadDTO();
        resultDownload.setData(afHPAcctListFileService.generateFile(result.getData(), req.getFormat()));
        resultDownload.setFileName(DownloadEnum.AF_HP_ACCOUNT_LISTING_FILENAME.getDescription() + System.currentTimeMillis() + "." + req.getFormat().name().toLowerCase());

        return resultDownload;
    }

    public DownloadDTO downloadTrxHistory(DownloadReq req, UserAndCorp userAndCorp) {
        AFHPTrxHistDownloadReq request = (AFHPTrxHistDownloadReq) req;
        DownloadDTO resultDownload = new DownloadDTO();

        Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetailByAcctNumberAndProvider(request.getAcctNumber(), request.getProvider().name());

        if (accountData.isEmpty()) {
            log.error("Account Not Found for: " + request.getAcctNumber());
            return new DownloadDTO();
        }

        fileRepoService.insertFileRepo(request, userAndCorp, resultDownload, accountData.get(), request.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_1);

        // Create the transaction history request
        AFHPTrxHistReqDTO trxHistoryRequest = new AFHPTrxHistReqDTO();
        trxHistoryRequest.setProvider(request.getProvider());
        trxHistoryRequest.setAcctNumber(request.getAcctNumber());
        trxHistoryRequest.setSearchParam(request.getSearchParam());
        trxHistoryRequest.setFilters(request.getFilters());
        trxHistoryRequest.setPage(1); // For full data
        trxHistoryRequest.setPageSize(Integer.MAX_VALUE); // Retrieve all records

        fileRepoService.insertFileRepo(request, userAndCorp, resultDownload, accountData.get(), request.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_2);

        // Call the existing hirePurchaseTrxHistory method
        ApiResponse<?> apiResponse = hirePurchaseTrxHistory(trxHistoryRequest, userAndCorp);

        fileRepoService.insertFileRepo(request, userAndCorp, resultDownload, accountData.get(), request.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_3);

        byte[] fileData;
        String fileName;

        // Handle the response based on the provider
        switch (request.getProvider()) {
            case MY: {
                if (apiResponse.getMsgBody() instanceof AFHPTrxHistoryResp myResponse) {
                    List<AFHPTrxHistDTO> data = myResponse.getDatas().getData();
                    fileData = afHPTrxHistFileService.generateFile(data, req.getFormat(), request.getHeaders());
                    fileName = DownloadEnum.AF_HP_TRX_HISTORY_FILENAME.getDescription() + "MY_" + System.currentTimeMillis() + "." + req.getFormat().name().toLowerCase();
                } else {
                    throw new IllegalStateException("Unexpected response type for MY provider");
                }
                break;
            }
            case SG: {
                if (apiResponse.getMsgBody() instanceof AFHPTrxHistoryResp sgResponse) {
                    List<AFHPTrxHistDTO> data = sgResponse.getDatas().getData();
                    fileData = afHPTrxHistFileService.generateFile(data, req.getFormat(), request.getHeaders());
                    fileName = DownloadEnum.AF_HP_TRX_HISTORY_FILENAME.getDescription() + "SG_" + System.currentTimeMillis() + "." + req.getFormat().name().toLowerCase();
                } else {
                    throw new IllegalStateException("Unexpected response type for SG provider");
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid provider indicator");
        }

        // Construct the download result
        resultDownload.setData(fileData);
        resultDownload.setFileName(fileName);

        fileRepoService.insertFileRepo(request, userAndCorp, resultDownload, accountData.get(), request.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_4);

        return resultDownload;
    }

    private ApiResponse<AFHPAcctSummaryResp> convertApiResponse(
            ApiResponse<SearchCollectionResponse<AFHPAcctListDTO>> apiResponse,
            String corpCifNo, ESBMetadataDTO esbMetadataDTO) {

        ApiResponse<AFHPAcctSummaryResp> apiResponseNew = new ApiResponse<>();

        apiResponseNew.setMsgHeader(apiResponse.getMsgHeader());

        AFHPAcctSummaryResp summaryResp = new AFHPAcctSummaryResp();
        summaryResp.setCorpCifNo(corpCifNo);
        summaryResp.setDatas(apiResponse.getMsgBody());
        summaryResp.setEsbMetadata(esbMetadataDTO);

        apiResponseNew.setMsgBody(summaryResp);

        return apiResponseNew;
    }

    private List<AFHPAcctListDTO> filterBySubProduct(List<AFHPAcctListDTO> data, String subProduct) {
        return Optional.ofNullable(data)
                .orElseGet(List::of)
                .stream()
                .filter(dto -> subProduct.equals(dto.getSubProduct()))
                .toList();
    }

    private List<AFHPAcctListDTO> filterBySubProductAndProvider(List<AFHPAcctListDTO> data, String subProduct, String provider) {
        return Optional.ofNullable(data)
                .orElseGet(List::of)
                .stream()
                .filter(dto -> subProduct.equals(dto.getSubProduct()) && provider.equals(dto.getProvider()))
                .toList();
    }

    private List<AFHPAcctListDTO> filterByProvider(List<AFHPAcctListDTO> data, String provider) {
        return Optional.ofNullable(data)
                .orElseGet(List::of)
                .stream()
                .filter(dto -> provider.equals(dto.getProvider()))
                .toList();
    }

    private List<String> getProviders(List<AFHPAcctListDTO> data) {
        return Optional.ofNullable(data)
                .orElseGet(List::of)
                .stream()
                .map(AFHPAcctListDTO::getProvider)
                .filter(provider -> provider != null && !provider.isEmpty())
                .distinct()
                .toList();
    }

    private List<String> getSubProducts(List<AFHPAcctListDTO> data) {
        return Optional.ofNullable(data)
                .orElseGet(List::of)
                .stream()
                .map(AFHPAcctListDTO::getSubProduct)
                .filter(subProduct -> subProduct != null && !subProduct.isEmpty())
                .distinct()
                .toList();
    }

    private List<AFHPAcctListDTO> getInitialData(SearchCollectionResponse<AFHPAcctListDTO> spResult) {
        return Optional.ofNullable(spResult.getData()).orElseGet(ArrayList::new);
    }

    private List<String> getProvidedProviders(List<AFHPAcctListDTO> t1Data) {
        return Optional.ofNullable(getProviders(t1Data)).orElseGet(ArrayList::new);
    }

    private List<String> getProvidedSubProducts(List<AFHPAcctListDTO> t1Data) {
        return Optional.ofNullable(getSubProducts(t1Data)).orElseGet(ArrayList::new);
    }

    private AFAcctListESBDTO processProvidersAndSubProducts(List<String> providedProviders, List<String> providedSubProducts,
                                                            List<AFHPAcctListDTO> t1Data, UserAndCorp userAndCorp) {
        AtomicReference<AFAcctListESBDTO> temp = new AtomicReference<>(new AFAcctListESBDTO());
        providedProviders.forEach(provider -> {
            if (provider.equals(ProviderEnum.MY.name())) {
                temp.set(processMYProvider(providedSubProducts, t1Data, userAndCorp));
            } else {
                temp.set(processOtherProviders(provider, t1Data));
            }
        });
        return temp.get();
    }

    private AFAcctListESBDTO processMYProvider(List<String> providedSubProducts, List<AFHPAcctListDTO> t1Data, UserAndCorp userAndCorp) {
        AFAcctListESBDTO tData = new AFAcctListESBDTO();
        providedSubProducts.forEach(subProduct -> {
            List<AFHPAcctListDTO> filteredData = filterBySubProductAndProvider(t1Data, subProduct, ProviderEnum.MY.name());
            AtomicReference<AFAcctListESBDTO> tmp = new AtomicReference<>(new AFAcctListESBDTO());
            List<AFHPAcctListDTO> tempList = new ArrayList<>();

            IntStream.range(0, (filteredData.size() + batchSize - 1) / batchSize)
                    .forEach(i -> {
                        int start = i * batchSize;
                        int end = Math.min(start + batchSize, filteredData.size());
                        List<AFHPAcctListDTO> batch = filteredData.subList(start, end);

                        tmp.set(fetchDataForMYProvider(subProduct, batch));
                        if (tmp.get().getAcctListing() != null && !tmp.get().getAcctListing().isEmpty())
                            tempList.addAll((List<AFHPAcctListDTO>) tmp.get().getAcctListing());
                    });

            tData.setAcctListing(tempList);
            tData.setEsbMetadata(tmp.get().getEsbMetadata());

        });
        return tData;
    }

    private AFAcctListESBDTO fetchDataForMYProvider(String subProduct, List<AFHPAcctListDTO> filteredData) {
        ESBAFHPAcctListReqDTO esbafhpAcctListReqDTO = new ESBAFHPAcctListReqDTO();
        esbafhpAcctListReqDTO.setAcctType(getAcctTypeCode(ProviderEnum.MY.name()));
        esbafhpAcctListReqDTO.setDatas(filteredData);

        if (subProduct.equalsIgnoreCase("HP")) {
            return afhpesbService.getAFHPAccountBalanceInquiry(esbafhpAcctListReqDTO).getMsgBody();
        } else {
            return afhpesbService.getAFHPMasterAccountBalanceInquiry(esbafhpAcctListReqDTO).getMsgBody();
        }
    }

    private AFAcctListESBDTO processOtherProviders(String provider, List<AFHPAcctListDTO> t1Data) {
        List<AFHPAcctListDTO> filteredData = filterByProvider(t1Data, provider);
        AFAcctListESBDTO afAcctListESBDTO = new AFAcctListESBDTO();
        AtomicReference<AFAcctListESBDTO> temp = new AtomicReference<>(new AFAcctListESBDTO());
        List<AFHPAcctListDTO> acctTemp = new ArrayList<>();

        ESBAFHPAcctListReqDTO esbafhpAcctListReqDTO = new ESBAFHPAcctListReqDTO();
        esbafhpAcctListReqDTO.setAcctType(getAcctTypeCode(provider));

        IntStream.range(0, (filteredData.size() + batchSize - 1) / batchSize)
                .forEach(i -> {
                    int start = i * batchSize;
                    int end = Math.min(start + batchSize, filteredData.size());
                    List<AFHPAcctListDTO> batch = filteredData.subList(start, end);

                    esbafhpAcctListReqDTO.setDatas(batch);
                    temp.set(afhpesbService.getAFHPSGAccountBalanceInquiry(esbafhpAcctListReqDTO).getMsgBody());
                    if (temp.get().getAcctListing() != null && !temp.get().getAcctListing().isEmpty())
                        acctTemp.addAll((List<AFHPAcctListDTO>) temp.get().getAcctListing());
                });

        afAcctListESBDTO.setAcctListing(acctTemp);
        afAcctListESBDTO.setEsbMetadata(temp.get().getEsbMetadata());
        return afAcctListESBDTO;
    }

    private void updateBalanceValues(List<AFHPAcctListDTO> tData, List<AFHPAcctListDTO> t1Data) {
        if (tData != null && !tData.isEmpty()) {
            Map<String, AFHPAcctListDTO> tDataMap = tData.stream()
                    .collect(Collectors.toMap(
                            dto -> removeLeadingZeroes(dto.getAcctNumber()), dto -> dto));

            t1Data.forEach(account -> {
                AFHPAcctListDTO matchingData = tDataMap.get(account.getAcctNumber());

                if (matchingData != null) {
                    Optional<String> bhpInd = accountLinkHPBHPRepository.findByAccountNo(account.getAcctNumber());
                    boolean isBHP = bhpInd.isPresent() && bhpInd.get().equalsIgnoreCase("1");
                    boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(account.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(account.getAcctStatusDesc());

                    account.setOutstandingBalance(isClosed ? null : matchingData.getOutstandingBalance());
                    account.setFinancedAmt(isClosed ? null : matchingData.getFinancedAmt());
                    account.setInstalmentDue(isClosed ? null : matchingData.getInstalmentDue());
                    account.setMonthlyInstalment(isClosed ? null : matchingData.getMonthlyInstalment());
                    account.setSettlementAmt(isClosed ? null : matchingData.getSettlementAmt());
                    account.setOtherCharges(isClosed ? null : matchingData.getOtherCharges());
                    account.setInstalmentAmt(isClosed ? null : matchingData.getInstalmentAmt());
                    account.setBHP(isBHP);
                    account.setTlastIndicator(matchingData.getTlastIndicator());
                } else {
                    account.setOutstandingBalance(null);
                    account.setFinancedAmt(null);
                    account.setInstalmentDue(null);
                    account.setMonthlyInstalment(null);
                    account.setSettlementAmt(null);
                    account.setOtherCharges(null);
                    account.setInstalmentAmt(null);
                    account.setBHP(false);
                    account.setTlastIndicator(null);
                }
            });
        } else {
            t1Data.forEach(dto -> {
                dto.setOutstandingBalance(null);
                dto.setFinancedAmt(null);
                dto.setInstalmentDue(null);
                dto.setMonthlyInstalment(null);
                dto.setSettlementAmt(null);
                dto.setOtherCharges(null);
                dto.setInstalmentAmt(null);
            });
        }
    }

    private String getAcctTypeCode(String provider) {
        Optional<AccountProductTypeMatrix> matrix = matrixRepository.findByAcctTypeAndProv(AcctTypEnum.HP.name(), provider);
        if (matrix.isEmpty()) {
            log.error("Account Type Not Found");
            return null;
        }

        return matrix.get().getEsbAccountTypeCode();
    }

    private String getOutstandingBalance(List<AFHPAcctListDTO> accountList) {
        if (accountList == null) {
            return null; // Return zero if DTO or data is null
        }
        BigDecimal totalBalance = accountList.stream()
                .map(account -> account.getOutstandingBalance() != null ?
                        new BigDecimal(account.getOutstandingBalance().replaceAll(",", ""))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalBalance.toPlainString();
    }

    private String getFinancedAmt(List<AFHPAcctListDTO> accountList) {
        if (accountList == null) {
            return null; // Return zero if DTO or data is null
        }
        BigDecimal totalBalance = accountList.stream()
                .map(account -> account.getFinancedAmt() != null
                        ? new BigDecimal(account.getFinancedAmt().replaceAll(",", ""))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalBalance.toPlainString();
    }

    private String getRedisKey(UserAndCorp userAndCorp, AFAccountListReq reqDTO) {
        String redisKey = null;
        if (reqDTO.isRealTimeIndicator())
            redisKey = redisHelper.createCacheKey(BD_AM_AF_HP_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "ONL" + reqDTO.getViewByIndicator().name() + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
        else
            redisKey = redisHelper.createCacheKey(BD_AM_AF_HP_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + reqDTO.getViewByIndicator().name() + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());

        return redisKey;
    }

    private ApiResponse<AFHPAcctSummaryResp> getAcctFromCache(UserAndCorp userAndCorp, AFAccountListReq reqDTO) {
        return (ApiResponse<AFHPAcctSummaryResp>) redisHelper.getFromCache(getRedisKey(userAndCorp, reqDTO), ApiResponse.class);
    }

    public void removeAllFromCache(UserAndCorp userAndCorp){
        String redisKey = redisHelper.createCacheKey(BD_AM_AF_HP_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_*");
        redisHelper.deleteCachesWithPattern(redisKey);
        log.info("removed cache for {}", redisKey);
    }
}
