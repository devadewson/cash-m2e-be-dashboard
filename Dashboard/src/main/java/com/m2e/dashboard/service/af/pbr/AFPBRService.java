package com.m2e.dashboard.service.af.pbr;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.dto.MoreActionsRedirection;
import com.m2e.dashboard.dto.af.request.pbr.AFPBRAccountDetailReq;
import com.m2e.dashboard.dto.af.request.pbr.AFPBRAcctStatsReqDTO;
import com.m2e.dashboard.dto.af.request.pbr.AFPBRSubaccountListReq;
import com.m2e.dashboard.dto.af.request.pbr.AFPBRTrxHistoryReq;
import com.m2e.dashboard.dto.af.request.pbr.download.AFPBRAccountListingDownloadReq;
import com.m2e.dashboard.dto.af.request.pbr.download.AFPBRSubaccountListingDownloadReq;
import com.m2e.dashboard.dto.af.request.pbr.download.AFPBRTrxHistoryDownloadReq;
import com.m2e.dashboard.dto.af.response.pbr.*;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.base.af.AFAccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.*;
import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.jms.af.common.ESBAFAcctActivityReqDTO;
import com.m2e.dashboard.jms.af.pbr.request.account.ESBPBRAcctListingReqDTO;
import com.m2e.dashboard.jms.af.pbr.request.account.balanceinquiry.ESBAFPBRAcctListReqDTO;
import com.m2e.dashboard.jms.af.pbr.request.account.details.ESBPBRUnitListingReqDTO;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.projection.DepositAcctDetailPageProjection;
import com.m2e.dashboard.repository.AccountProductTypeMatrixRepository;
import com.m2e.dashboard.repository.AccountRepository;
import com.m2e.dashboard.repository.AccountStatusRepository;
import com.m2e.dashboard.service.AsyncPaginationService;
import com.m2e.dashboard.service.FileRepoService;
import com.m2e.dashboard.service.af.pbr.jms.AFPBRESBService;
import com.m2e.dashboard.service.af.pbr.sp.AFPBRSPService;
import com.m2e.dashboard.sp.af.pbr.request.*;
import com.m2e.dashboard.util.DateTimeUtil;
import com.m2e.model.sso.base.AccountProductTypeMatrix;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static com.m2e.dashboard.enums.AcctProductEntitlementConst.LOAN_AND_CARD_PAYMENT_PRODUCT_CODE;
import static com.m2e.dashboard.enums.AcctProductEntitlementConst.USER_ACCT_PRODUCT_LIST_REDIS_KEY;
import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_AF_PBR_ACCTLIST;
import static com.m2e.dashboard.enums.ReportCommonConstant.*;

@Service
@RequiredArgsConstructor
public class AFPBRService {
    private static final Logger logger = LoggerFactory.getLogger(AFPBRService.class);

    private final int batchSize = 10;

    @Value("${cache.ttl}")
    private Duration cacheDuration;


    private final JwtUtil jwtUtil;
    private final AFPBRViewByFacilityFileService viewByFacilityFileService;
    private final AFPBRViewByAccountFileService viewByAccountFileService;
    private final AFPBRSubaccountFileService subaccountFileService;
    private final AFPBRTrxHistoryFileService trxHistoryFileService;
    private final AFPBRESBService afpbresbService;
    private final AFPBRSPService afpbrspService;
    private final RedisHelper redisHelper;
    private final AccountStatusRepository accountStatusRepository;
    private final FileRepoService fileRepoService;
    private final AccountRepository accountRepository;
    private final AccountProductTypeMatrixRepository matrixRepository;
    private final ObjectMapper objectMapper;
    private final AsyncPaginationService asyncPaginationService;

    public <T> ApiResponse<AFPBRAcctSummaryDTO> getPbrAccountListing(String auth, AFAccountListReq request) {
        logger.info("Enter getPbrAccountListing: {}", request);

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        ApiResponse<AFPBRAcctSummaryDTO> apiResponse;

        apiResponse = getAcctFromCache(userAndCorp, request);

        if (apiResponse == null) {
            AFPBRAcctSummaryDTO afpbrAcctSummaryDTO = new AFPBRAcctSummaryDTO();
            SearchCollectionResponse<T> t1Data;

            AFPBRAcctListSpParam acctListSpParam = new AFPBRAcctListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), request.getCorpCifNo(), request.getSearchParam());
            acctListSpParam.setPage(request.getPage());
            acctListSpParam.setPageSize(request.getPageSize());

            if (request.isRealTimeIndicator()) {
                logger.info("realtime T");
                ESBMetadataDTO esbMetadataDTO = new ESBMetadataDTO();

                switch (request.getViewByIndicator()) {
                    case ACCOUNT -> {
                        logger.info("account");
                        SearchCollectionResponse<AFPBRViewByAccountDTO> spResult = afpbrspService.acctListing(acctListSpParam);
                        List<AFPBRViewByAccountDTO> temp = spResult.getData();

                        if (!temp.isEmpty()) {
                            //continue query from ESB if not empty
                            AFAcctListESBDTO afAcctListESBDTO = getESBAcctResult(temp);
                            List<?> tData = afAcctListESBDTO.getAcctListing();
                            esbMetadataDTO = afAcctListESBDTO.getEsbMetadata();

                            if (tData != null && !tData.isEmpty()) {
                                logger.info("account temp: {}", temp);
                                List<AFPBRViewByAccountDTO> accounts = (List<AFPBRViewByAccountDTO>) tData;
                                logger.info("tData: {}", accounts);

                                //set all value to null before mapping
                                temp.forEach(dto -> {
                                    dto.setSettlementAmount(null);
                                    dto.setFinancedAmount(null);
                                    dto.setAmountDue(null);
                                    dto.setOutstandingBalance(null);
                                    dto.setMoreActions(getMoreActions(dto, userAndCorp));
                                });

                                // Set balance values from ESB Host for real-time data
                                accounts.forEach(account -> {
                                    String accountNumber = account.getAcctNumber();
                                    String settlementAmount = account.getSettlementAmount();
                                    String financedAmount = account.getFinancedAmount();
                                    String amountDue = account.getAmountDue();
                                    String outstandingBalance = account.getOutstandingBalance();

                                    temp.stream()
                                            .filter(dto -> dto.getAcctNumber().equals(accountNumber))
                                            .forEach(dto -> {
                                                boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(dto.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(dto.getAcctStatusDesc());
                                                dto.setSettlementAmount(isClosed ? null : settlementAmount);
                                                dto.setFinancedAmount(isClosed ? null : financedAmount);
                                                dto.setAmountDue(isClosed ? null : amountDue);
                                                dto.setOutstandingBalance(isClosed ? null : outstandingBalance);
                                            });
                                });

                            } else {
                                //set null if ESB host returns no values
                                temp.forEach(dto -> {
                                    dto.setSettlementAmount(null);
                                    dto.setFinancedAmount(null);
                                    dto.setAmountDue(null);
                                    dto.setOutstandingBalance(null);
                                    dto.setMoreActions(getMoreActions(dto, userAndCorp));
                                });
                            }
                        }
                        t1Data = new SearchCollectionResponse<>(spResult.getTotalPages(), spResult.getCurrentPage(), spResult.getNextPage(), spResult.getTotalRecordCount());
                        t1Data.setData((List<T>) temp);
                        afpbrAcctSummaryDTO.setDatas(t1Data);
                        afpbrAcctSummaryDTO.setEsbMetadata(esbMetadataDTO);

                        logger.info("result: {}", t1Data);
                    }
                    case FACILITY -> {
                        logger.info("facility");
                        SearchCollectionResponse<AFPBRViewByFacilityDTO> spResult = afpbrspService.masterAcctListing(acctListSpParam);
                        List<AFPBRViewByFacilityDTO> temp = spResult.getData();

                        if (!temp.isEmpty()) {
                            //continue query from ESB if not empty
                            AFAcctListESBDTO afAcctListESBDTO = getESBFacilityResult(temp);
                            logger.info("facility esb: {}", afAcctListESBDTO.getAcctListing());
                            List<?> tData = afAcctListESBDTO.getAcctListing();
                            esbMetadataDTO = afAcctListESBDTO.getEsbMetadata();

                            if (tData != null && !tData.isEmpty()) {
                                logger.info("facility temp: {}", temp);
                                List<AFPBRViewByFacilityDTO> accounts = (List<AFPBRViewByFacilityDTO>) tData;
                                logger.info("tData: {}", accounts);

                                //set all amount to null before start mapping
                                temp.forEach(dto -> {
                                    dto.setTotalSettlementAmount(null);
                                    dto.setTotalFacilityLimit(null);
                                    dto.setMoreActions(getMoreActions(new AFPBRViewByAccountDTO(dto.getMasterAcctNumber(), dto.getAccountCurrency()), userAndCorp));
                                });

                                // Set balance values from ESB Host for real-time data
                                accounts.forEach(account -> {
                                    String accountNumber = account.getMasterAcctNumber();
                                    String facilityLimit = account.getTotalFacilityLimit();
                                    String settlementAmount = account.getTotalSettlementAmount();

                                    temp.stream()
                                            .filter(dto -> dto.getMasterAcctNumber().equals(accountNumber))
                                            .forEach(dto -> {
                                                boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(dto.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(dto.getAcctStatusDesc());
                                                dto.setTotalSettlementAmount(isClosed ? null : settlementAmount);
                                                dto.setTotalFacilityLimit(isClosed ? null : facilityLimit);
                                            });
                                });

                            } else {
                                //set null if ESB host returns no values
                                temp.forEach(dto -> {
                                    dto.setTotalSettlementAmount(null);
                                    dto.setTotalFacilityLimit(null);
                                    dto.setMoreActions(getMoreActions(new AFPBRViewByAccountDTO(dto.getMasterAcctNumber(), dto.getAccountCurrency()), userAndCorp));
                                });
                            }
                        }
                        t1Data = new SearchCollectionResponse<>(spResult.getTotalPages(), spResult.getCurrentPage(), spResult.getNextPage(), spResult.getTotalRecordCount());
                        t1Data.setData((List<T>) temp);
                        afpbrAcctSummaryDTO.setDatas(t1Data);
                        afpbrAcctSummaryDTO.setEsbMetadata(esbMetadataDTO);
                        logger.info("result: {}", t1Data);

                    }
                    default -> throw new IllegalArgumentException("View By indicator is invalid");
                }
            } else {
                switch (request.getViewByIndicator()) {
                    case ACCOUNT -> {
                        SearchCollectionResponse<AFPBRViewByAccountDTO> spResult = afpbrspService.acctListing(acctListSpParam);
                        if (spResult.getData() != null) {
                            spResult.getData().forEach(dto -> dto.setMoreActions(getMoreActions(dto, userAndCorp)));
                        }
                        afpbrAcctSummaryDTO.setDatas(spResult);
                    }
                    case FACILITY -> {
                        SearchCollectionResponse<AFPBRViewByFacilityDTO> spResult = afpbrspService.masterAcctListing(acctListSpParam);
                        if (spResult.getData() != null) {
                            spResult.getData().forEach(dto -> dto.setMoreActions(getMoreActions(new AFPBRViewByAccountDTO(dto.getMasterAcctNumber(), dto.getAccountCurrency()), userAndCorp)));
                        }
                        afpbrAcctSummaryDTO.setDatas(spResult);
                    }
                    default -> throw new IllegalArgumentException("View by indicator is unrecognized");
                }
            }

            afpbrAcctSummaryDTO.setCorpCifNo(request.getCorpCifNo());
            apiResponse = new ApiResponse<>(new ErrorResponse("0"), afpbrAcctSummaryDTO);
        }

        redisHelper.save(getRedisKey(userAndCorp, request), apiResponse, cacheDuration.toMinutes(), TimeUnit.MINUTES);

        return apiResponse;
    }

    private MoreActionsEntitlement getMoreActions(AFPBRViewByAccountDTO dto, UserAndCorp userAndCorp) {
        String makePaymentRedisKey = redisHelper.buildCacheKey(userAndCorp.getUserId(), userAndCorp.getCorpId(), USER_ACCT_PRODUCT_LIST_REDIS_KEY, LOAN_AND_CARD_PAYMENT_PRODUCT_CODE, dto.getMasterAcctNumber(), dto.getAccountCurrency());
        logger.debug("getMoreActions() makePaymentRedisKey Redis key: {}", makePaymentRedisKey);
        MoreActionsEntitlement moreActionsEntitlement = new MoreActionsEntitlement();
        moreActionsEntitlement.setMakePayment(new MoreActionsRedirection(redisHelper.isPresent(makePaymentRedisKey), RedirectModuleEnum.PAYMENT_ACTION_REDIRECT.name(), Application.CASHLITE.getValue(), LOAN_AND_CARD_PAYMENT_PRODUCT_CODE));
        return moreActionsEntitlement;
    }

    public ApiResponse<AFPBRSubAcctSummaryDTO> getPbrSubaccountListing(String auth, AFPBRSubaccountListReq req) {
        logger.info("Enter getPbrSubaccountListing: {}", req);
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        SearchCollectionResponse<AFPBRUnitSummaryDTO> searchCollectionResponse = null;
        AFPBRSubAcctSummaryDTO afpbrSubAcctSummaryDTO = new AFPBRSubAcctSummaryDTO();
        AtomicReference<ESBMetadataDTO> esbMetadataDTO = new AtomicReference<>(new ESBMetadataDTO());

        AFPBRSubAcctListSpParam acctListSpParam = new AFPBRSubAcctListSpParam(req.getMasterAcctNumber(), req.getSearchParam(), userAndCorp.getUserId(), userAndCorp.getCorpId());
        acctListSpParam.setPageSize(req.getPageSize());
        acctListSpParam.setPage(req.getPage());

        SearchCollectionResponse<AFPBRUnitSummaryDTO> spResult = afpbrspService.subAcctListing(acctListSpParam);
        List<AFPBRUnitSummaryDTO> t1Data = spResult.getData();
        t1Data.forEach(data -> data.setMasterAcctNumber(req.getMasterAcctNumber()));

        if (!t1Data.isEmpty()) {
            List<AFPBRUnitSummaryDTO> tData = new ArrayList<>();

            ESBAFPBRAcctListReqDTO<AFPBRUnitSummaryDTO> acctListReqDTO = new ESBAFPBRAcctListReqDTO<>();
            acctListReqDTO.setAcctType(getAcctTypeCode(ProviderEnum.MY.name()));

            IntStream.range(0, (t1Data.size() + batchSize - 1) / batchSize)
                    .forEach(i -> {
                        int start = i * batchSize;
                        int end = Math.min(start + batchSize, t1Data.size());
                        List<AFPBRUnitSummaryDTO> batch = t1Data.subList(start, end);

                        acctListReqDTO.setDatas(batch);
                        AFAcctListESBDTO esbdto =
                                afpbresbService.getAFPBRUnitBalanceInquiry(acctListReqDTO).getMsgBody();
                        esbMetadataDTO.set(esbdto.getEsbMetadata());
                        if (esbdto.getAcctListing() != null && !esbdto.getAcctListing().isEmpty())
                            tData.addAll((List<AFPBRUnitSummaryDTO>) esbdto.getAcctListing());
                    });

            if (!tData.isEmpty()) {
                logger.info("t1Data: {}", t1Data);
                logger.info("tData: {}", tData);
                // Set balance values from ESB Host for real-time data
                tData.forEach(account -> {
                    String accountNumber = account.getUnitNumber();
                    String settlementAmount = account.getSettlementAmount();
                    String financedAmount = account.getFinancedAmount();
                    String amountDue = account.getAmountDue();
                    String outstandingBalance = account.getOutstandingBalance();

                    t1Data.stream()
                            .filter(dto -> dto.getUnitNumber().equals(accountNumber))
                            .forEach(dto -> {
                                boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(dto.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(dto.getAcctStatusDesc());

                                dto.setSettlementAmount(isClosed ? null : settlementAmount);
                                dto.setFinancedAmount(isClosed ? null : financedAmount);
                                dto.setAmountDue(isClosed ? null : amountDue);
                                dto.setOutstandingBalance(isClosed ? null : outstandingBalance);
                            });
                });

                t1Data.forEach(dto -> dto.setMoreActions(getMoreActions(new AFPBRViewByAccountDTO(dto.getMasterAcctNumber(), dto.getAccountCurrency()), userAndCorp)));
            } else {
                //set null if ESB host returns no values
                t1Data.forEach(dto -> {
                    dto.setSettlementAmount(null);
                    dto.setFinancedAmount(null);
                    dto.setAmountDue(null);
                    dto.setOutstandingBalance(null);
                    dto.setMoreActions(getMoreActions(new AFPBRViewByAccountDTO(dto.getMasterAcctNumber(), dto.getAccountCurrency()), userAndCorp));
                });
            }
        }
        logger.info("result: {}", t1Data);

        searchCollectionResponse = new SearchCollectionResponse<>(spResult.getTotalPages(), spResult.getCurrentPage(), spResult.getNextPage(), spResult.getTotalRecordCount());
        searchCollectionResponse.setData(t1Data);
        afpbrSubAcctSummaryDTO.setDatas(searchCollectionResponse);
        afpbrSubAcctSummaryDTO.setEsbMetadata(esbMetadataDTO.get());

        return new ApiResponse<>(new ErrorResponse("0"), afpbrSubAcctSummaryDTO);
    }

    public ApiResponse<AFPBRStatisticDTO> getPbrAccountStatistic(String auth, AFPBRAcctStatsReqDTO request) {
        logger.info("Enter getPbrAccountStatistic: {}", request);

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        removeAllFromCache(userAndCorp);

        AFPBRAcctStatsSpParam afpbrAcctStatsSpParam = new AFPBRAcctStatsSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), request.getViewByIndicator().name());

        AFPBRStatisticDTO result = afpbrspService.statsViewByAcct(afpbrAcctStatsSpParam);

        if (Boolean.TRUE.equals(request.getRealTimeIndicator())) {
//            AFPBRAcctListSpParam acctListSpParam = new AFPBRAcctListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), null, null);
//            acctListSpParam.setPage(1);
//            acctListSpParam.setPageSize(Integer.MAX_VALUE);
//
//            switch (request.getViewByIndicator()) {
//                case ACCOUNT -> {
//                    logger.info("account");
//                    SearchCollectionResponse<AFPBRViewByAccountDTO> spResult = afpbrspService.acctListing(acctListSpParam);
//
//                    if (spResult.getTotalRecordCount() > 0) {
//                        AFAcctListESBDTO afAcctListESBDTO = getESBAcctResult(spResult.getData());
//                        result.setTotalFinancedAmount(getFinancedAmount((List<AFPBRViewByAccountDTO>) afAcctListESBDTO.getAcctListing()));
//                        result.setTotalSettlementAmount(getSettlementAmount((List<AFPBRViewByAccountDTO>) afAcctListESBDTO.getAcctListing()));
//                    }
//
//                }
//                case FACILITY -> {
//                    logger.info("facility");
//                    SearchCollectionResponse<AFPBRViewByFacilityDTO> spResult = afpbrspService.masterAcctListing(acctListSpParam);
//
//                    if (spResult.getTotalRecordCount() > 0) {
//                        AFAcctListESBDTO afAcctListESBDTO = getESBFacilityResult(spResult.getData());
//
//                        result.setTotalFinancedAmount(getTotalFacilityLimit((List<AFPBRViewByFacilityDTO>) afAcctListESBDTO.getAcctListing()));
//                        result.setTotalSettlementAmount(getTotalSettlementAmount((List<AFPBRViewByFacilityDTO>) afAcctListESBDTO.getAcctListing()));
//                    }
//                }
//                default -> throw new IllegalArgumentException("View By indicator is invalid");
//            }

            result.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
        }

        ApiResponse<AFPBRStatisticDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(result);
        return apiResponse;
    }

    public ApiResponse<?> getPbrAccountDetail(String auth, AFPBRAccountDetailReq request) {
        logger.info("Enter getPbrAccountDetail: {}", request);
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        AFPBRDetailSpParam afpbrDetailSpParam = new AFPBRDetailSpParam(request.getMasterAcctNumber(), request.getAcctNumber(), userAndCorp.getCorpId(), userAndCorp.getUserId());

        switch (request.getAccountLevelEnum()) {
            case FACILITY -> {
                AFPBRMasterAccountDetailDTO t1Data = afpbrspService.masterAcctDetail(afpbrDetailSpParam);

                ESBPBRAcctListingReqDTO acctListingReqDTO = new ESBPBRAcctListingReqDTO(t1Data.getAcctNumber(),
                        t1Data.getCurrency(),
                        getAcctTypeCode(ProviderEnum.MY.name()),
                        ProviderEnum.MY.name());

                AFPBRMasterAccountDetailDTO tData = afpbresbService.getAFPBRAcctDetails(acctListingReqDTO).getMsgBody();
                if (tData != null) {
                    if (tData.getPbrStatus() != null) {
                        Optional<String> status = accountStatusRepository.findDescription01ByCodeAndProvider(Integer.parseInt(tData.getPbrStatus()), "MY");
                        t1Data.setPbrStatus(status.orElse(tData.getPbrStatus()));
                    }

                    boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(t1Data.getPbrStatus()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(t1Data.getPbrStatus());

                    t1Data.setTotalFinancedAmount(isClosed ? null : tData.getTotalFinancedAmount());
                    t1Data.setTotalSettlementAmount(isClosed ? null : tData.getTotalSettlementAmount());
                    t1Data.setConsolidatedUnutilisedAmount(isClosed ? null : tData.getConsolidatedUnutilisedAmount());
                    t1Data.setDateApproved(DateTimeUtil.convertDateddMMyyyy(tData.getDateApproved()));
                    t1Data.setFacilityLimit(isClosed ? null : tData.getFacilityLimit());
                    t1Data.setTotalPenalty(isClosed ? null : tData.getTotalPenalty());
                    t1Data.setTotalUnitExpired(tData.getTotalUnitExpired());
                    t1Data.setNumberOfDays(tData.getNumberOfDays());
                    t1Data.setIslamic("Y".equalsIgnoreCase(t1Data.getIslamicFlag()));
                    t1Data.setEsbMetadata(tData.getEsbMetadata());
                    t1Data.setMoreActions(getMoreActions(new AFPBRViewByAccountDTO(request.getAcctNumber(), request.getCurrency()), userAndCorp));
                }

                return new ApiResponse<>(new ErrorResponse("0"), t1Data);
            }
            case ACCOUNT -> {
                AFPBRSubaccountDetailDTO t1Data = afpbrspService.accountDetail(afpbrDetailSpParam);

                ESBPBRUnitListingReqDTO acctListingReqDTO = new ESBPBRUnitListingReqDTO(request.getMasterAcctNumber(),
                        request.getAcctNumber(),
                        request.getCurrency(),
                        getAcctTypeCode(ProviderEnum.MY.name()),
                        ProviderEnum.MY.name());

                AFPBRSubaccountDetailDTO tData = afpbresbService.getAFPBRUnitDetails(acctListingReqDTO).getMsgBody();
                if (tData != null) {
                    if (tData.getUnitStatus() != null) {
                        Optional<String> status = accountStatusRepository.findDescription01ByCodeAndProvider(Integer.parseInt(tData.getUnitStatus()), "MY");
                        t1Data.setUnitStatus(status.orElse(tData.getUnitStatus()));
                    }

                    boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(t1Data.getUnitStatus()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(t1Data.getUnitStatus());

                    t1Data.setNumberOfDays(tData.getNumberOfDays());
                    t1Data.setFinancedAmount(isClosed ? null : tData.getFinancedAmount());
                    t1Data.setSettlementAmount(isClosed ? null : tData.getSettlementAmount());
                    t1Data.setPurchasedPrice(isClosed ? null : tData.getPurchasedPrice());
                    t1Data.setPenaltyAmount(isClosed ? null : tData.getPenaltyAmount());
                    t1Data.setAmountDue(tData.getAmountDue());
                    t1Data.setOutstandingBalance(tData.getOutstandingBalance());
                    t1Data.setEngineNumber(tData.getEngineNumber());
                    t1Data.setChassisNumber(tData.getChassisNumber());
                    t1Data.setExpiryDate(DateTimeUtil.convertDateddMMyyyy(tData.getExpiryDate()));
                    t1Data.setDateFinanced(DateTimeUtil.convertDateddMMyyyy(tData.getDateFinanced()));
                    t1Data.setIslamic("Y".equalsIgnoreCase(t1Data.getIslamicFlag()));
                    t1Data.setEsbMetadata(tData.getEsbMetadata());
                    t1Data.setMoreActions(getMoreActions(new AFPBRViewByAccountDTO(t1Data.getAcctNumber(), request.getCurrency()), userAndCorp));
                }

                return new ApiResponse<>(new ErrorResponse("0"), t1Data);
            }
            default -> throw new IllegalArgumentException("Account Level indicator is invalid");
        }

    }

    public ApiResponse<AFPBRTrxHistorySummaryDTO> pbrTrxHistory(AFPBRTrxHistoryReq request, String auth) {
        logger.info("Enter pbrTrxHistory: {}", request);
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        ApiResponse<AFPBRTrxHistorySummaryDTO> apiResponse = new ApiResponse<>();
        AFPBRTrxHistorySummaryDTO afTrxHistoryDTO = new AFPBRTrxHistorySummaryDTO();

        ESBAFAcctActivityReqDTO esbafAcctActivityReqDTO = new ESBAFAcctActivityReqDTO(request.getAcctNumber(),
                request.getSearchParam(),
                null,
                ProviderEnum.MY,
                request.getCurrency(),
                request.getMbbLastKey(),
                getAcctTypeCode(ProviderEnum.MY.name()),
                request.getFilters(),
                request.getPage(),
                request.getPageSize());

        // get trx hist data from ESB
        try {
            afTrxHistoryDTO = afpbresbService.getAFPBRTrxHistory(esbafAcctActivityReqDTO).getMsgBody();
            afTrxHistoryDTO = afTrxHistoryDTO != null ? afTrxHistoryDTO : new AFPBRTrxHistorySummaryDTO();
        } catch (Exception e) {
            afTrxHistoryDTO = afTrxHistoryDTO != null ? afTrxHistoryDTO : new AFPBRTrxHistorySummaryDTO();
            afTrxHistoryDTO.setDatas(new SearchCollectionResponse<>());
        }

        // get trx hist data from SP
        SearchCollectionResponse<AFPBRTrxHistoryDTO> spResult = Optional.ofNullable(afpbrspService.trxHistory(getTrxHistorySpParam(request, userAndCorp)))
                .orElse(new SearchCollectionResponse<>());

        List<AFPBRTrxHistoryDTO> esbDataList = Optional.ofNullable(afTrxHistoryDTO.getDatas()).map(SearchCollectionResponse::getData).orElse(new ArrayList<>());
        List<AFPBRTrxHistoryDTO> spDataList = Optional.of(spResult).map(SearchCollectionResponse::getData).orElse(new ArrayList<>());

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
            logger.info("remainingSize --> {}", remainingSize);

            List<AFPBRTrxHistoryDTO> fusedTrxHistoryDTOList = new ArrayList<>(esbDataList);
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

            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(afTrxHistoryDTO);
            return apiResponse;
        }
    }

    public DownloadDTO downloadPbrList(String auth, DownloadReq request) {
        logger.info("Enter downloadPbrList: {}", request);
        DownloadDTO result = new DownloadDTO();
        switch (request.getReportType()) {
            case AF_PBR_ACCLISTING_RPT_TYPE -> {
                AFPBRAccountListingDownloadReq requestData = (AFPBRAccountListingDownloadReq) request;
                downloadPbrAccountList(result, requestData, auth);
            }
            case AF_PBR_SUBACCLISTING_RPT_TYPE -> {
                AFPBRSubaccountListingDownloadReq requestData = (AFPBRSubaccountListingDownloadReq) request;
                downloadPbrSubaccountList(result, requestData, auth);
            }
            case AF_PBR_TRXHISTORIES_RPT_TYPE -> {
                AFPBRTrxHistoryDownloadReq requestData = (AFPBRTrxHistoryDownloadReq) request;
                downloadPbrTrxHistoryList(result, requestData, auth);
            }
            default -> {
                throw new IllegalArgumentException("Report type is undefined");
            }
        }
        return result;
    }

    public DownloadDTO downloadPbrAccountList(DownloadDTO response, AFPBRAccountListingDownloadReq request, String auth) {

        AFAccountListReq accountListReq = new AFAccountListReq(request.getRealTimeIndicator(), request.getSearchParam(), request.getCorpCifNo(), request.getViewByIndicator());
        accountListReq.setPage(1);
        accountListReq.setPageSize(Integer.MAX_VALUE);

        ApiResponse<AFPBRAcctSummaryDTO> data = objectMapper.convertValue(getPbrAccountListing(auth, accountListReq), new TypeReference<ApiResponse<AFPBRAcctSummaryDTO>>() {});
        switch (request.getViewByIndicator()) {
            case ACCOUNT -> {
                List<AFPBRViewByAccountDTO> accountData = Optional.ofNullable(data)
                        .map(ApiResponse::getMsgBody)
                        .map(body -> {
                            try {
                                return (body instanceof AFPBRAcctSummaryDTO)
                                        ? (AFPBRAcctSummaryDTO) body
                                        : objectMapper.convertValue(body, AFPBRAcctSummaryDTO.class);
                            } catch (IllegalArgumentException e) {
                                logger.warn("Failed to convert msgBody to AFPBRAcctSummaryDTO: {}", e.getMessage(), e);
                                return null;
                            }
                        })
                        .map(AFPBRAcctSummaryDTO::getDatas)
                        .map(SearchCollectionResponse::getData)
                        .map(obj -> {
                            if (obj instanceof List<?>) {
                                List<?> list = (List<?>) obj;
                                if (list.stream().allMatch(AFPBRViewByAccountDTO.class::isInstance)) {
                                    @SuppressWarnings("unchecked")
                                    List<AFPBRViewByAccountDTO> typed = (List<AFPBRViewByAccountDTO>) list;
                                    return typed;
                                }
                            }
                            try {
                                return objectMapper.convertValue(obj, new TypeReference<List<AFPBRViewByAccountDTO>>() {});
                            } catch (IllegalArgumentException e) {
                                logger.warn("Failed to convert data to List<AFPBRViewByAccountDTO>: {}", e.getMessage(), e);
                                return java.util.Collections.<AFPBRViewByAccountDTO>emptyList();
                            }
                        })
                        .orElseGet(java.util.Collections::emptyList);

                logger.info("Result account list: {}", accountData);
                switch (request.getFormat()) {
                    case CSV:
                        response.setData(viewByAccountFileService.generateCsv(accountData));
                        response.setFileName(DownloadEnum.AF_PBR_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
                        break;
                    case PDF:
                        response.setData(viewByAccountFileService.generatePdf(accountData));
                        response.setFileName(DownloadEnum.AF_PBR_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
                        break;
                    case TXT:
                        response.setData(viewByAccountFileService.generateTxt(accountData));
                        response.setFileName(DownloadEnum.AF_PBR_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
                        break;
                    default:
                        throw new IllegalArgumentException("Report format is invalid");
                }
            }
            case FACILITY -> {
                List<AFPBRViewByFacilityDTO> facilityData = Optional.ofNullable(data)
                        .map(ApiResponse::getMsgBody)
                        .map(body -> {
                            try {
                                return (body instanceof AFPBRAcctSummaryDTO)
                                        ? (AFPBRAcctSummaryDTO) body
                                        : objectMapper.convertValue(body, AFPBRAcctSummaryDTO.class);
                            } catch (IllegalArgumentException e) {
                                logger.warn("Failed to convert msgBody to AFPBRAcctSummaryDTO: {}", e.getMessage(), e);
                                return null;
                            }
                        })
                        .map(AFPBRAcctSummaryDTO::getDatas)
                        .map(SearchCollectionResponse::getData)
                        .map(obj -> {
                            if (obj instanceof List<?>) {
                                List<?> list = (List<?>) obj;
                                if (list.stream().allMatch(AFPBRViewByFacilityDTO.class::isInstance)) {
                                    @SuppressWarnings("unchecked")
                                    List<AFPBRViewByFacilityDTO> typed = (List<AFPBRViewByFacilityDTO>) list;
                                    return typed;
                                }
                            }
                            try {
                                return objectMapper.convertValue(obj, new com.fasterxml.jackson.core.type.TypeReference<List<AFPBRViewByFacilityDTO>>() {});
                            } catch (IllegalArgumentException e) {
                                logger.warn("Failed to convert data to List<AFPBRViewByFacilityDTO>: {}", e.getMessage(), e);
                                return java.util.Collections.<AFPBRViewByFacilityDTO>emptyList();
                            }
                        })
                        .orElseGet(java.util.Collections::emptyList);
                logger.info("Result facilityData list: {}", facilityData);

                switch (request.getFormat()) {
                    case CSV:
                        response.setData(viewByFacilityFileService.generateCsv(facilityData));
                        response.setFileName(DownloadEnum.AF_PBR_FACLISTING_FILENAME.getDescription() + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
                        break;
                    case PDF:
                        response.setData(viewByFacilityFileService.generatePdf(facilityData));
                        response.setFileName(DownloadEnum.AF_PBR_FACLISTING_FILENAME.getDescription() + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
                        break;
                    case TXT:
                        response.setData(viewByFacilityFileService.generateTxt(facilityData));
                        response.setFileName(DownloadEnum.AF_PBR_FACLISTING_FILENAME.getDescription() + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
                        break;
                    default:
                        throw new IllegalArgumentException("Report format is invalid");
                }
            }
            default -> throw new IllegalArgumentException("View By indicator is invalid");
        }
        return response;
    }

    public DownloadDTO downloadPbrSubaccountList(DownloadDTO response, AFPBRSubaccountListingDownloadReq request, String auth) {
        AFPBRSubaccountListReq listReq = new AFPBRSubaccountListReq();
        listReq.setMasterAcctNumber(request.getAcctNumber());
        listReq.setSearchParam(request.getSearchParam());
        listReq.setPage(1);
        listReq.setPageSize(Integer.MAX_VALUE);

        List<AFPBRUnitSummaryDTO> data = (List<AFPBRUnitSummaryDTO>) getPbrSubaccountListing(auth, listReq).getMsgBody().getDatas().getData();

        switch (request.getFormat()) {
            case CSV:
                response.setData(subaccountFileService.generateCsv(data));
                response.setFileName(DownloadEnum.AF_PBR_SUBACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
                break;
            case PDF:
                response.setData(subaccountFileService.generatePdf(data));
                response.setFileName(DownloadEnum.AF_PBR_SUBACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
                break;
            case TXT:
                response.setData(subaccountFileService.generateTxt(data));
                response.setFileName(DownloadEnum.AF_PBR_SUBACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return response;
    }

    public DownloadDTO downloadPbrTrxHistoryList(DownloadDTO response, AFPBRTrxHistoryDownloadReq request, String auth) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        AFPBRTrxHistoryReq trxHistoryReq = new AFPBRTrxHistoryReq();

        Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetails(trxHistoryReq.getAcctNumber(), trxHistoryReq.getCurrency());

        if (accountData.isEmpty()) {
            logger.error("Account Not Found for: " + trxHistoryReq.getAcctNumber());
            return new DownloadDTO();
        }

        fileRepoService.insertFileRepo(request, userAndCorp, response, accountData.get(), trxHistoryReq.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_1);

        trxHistoryReq.setPage(1);
        trxHistoryReq.setPageSize(Integer.MAX_VALUE);
        trxHistoryReq.setSearchParam(request.getSearchParam());
        trxHistoryReq.setAcctNumber(request.getAcctNumber());
        trxHistoryReq.setFilters(request.getFilters());
        trxHistoryReq.setCurrency(accountData.get().getCurrency());

        fileRepoService.insertFileRepo(request, userAndCorp, response, accountData.get(), trxHistoryReq.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_2);

        List<AFPBRTrxHistoryDTO> trxHistories = pbrTrxHistory(trxHistoryReq, auth).getMsgBody().getDatas().getData();

        fileRepoService.insertFileRepo(request, userAndCorp, response, accountData.get(), trxHistoryReq.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_3);

        switch (request.getFormat()) {
            case CSV:
                response.setData(trxHistoryFileService.generateCsv(trxHistories, request.getHeaders()));
                response.setFileName(DownloadEnum.AF_PBR_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
                break;
            case PDF:
                response.setData(trxHistoryFileService.generatePdf(trxHistories, request.getHeaders()));
                response.setFileName(DownloadEnum.AF_PBR_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
                break;
            case TXT:
                response.setData(trxHistoryFileService.generateTxt(trxHistories, request.getHeaders()));
                response.setFileName(DownloadEnum.AF_PBR_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }

        fileRepoService.insertFileRepo(request, userAndCorp, response, accountData.get(), trxHistoryReq.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_4);

        return response;
    }

    private AFPBRTrxHistorySpParam getTrxHistorySpParam(AFPBRTrxHistoryReq request, UserAndCorp userAndCorp) {
        AFPBRTrxHistorySpParam afpbrTrxHistorySpParam = new AFPBRTrxHistorySpParam(request.getAcctNumber(), userAndCorp.getCorpId(), userAndCorp.getUserId());
        afpbrTrxHistorySpParam.setSearchParam(request.getSearchParam());
        afpbrTrxHistorySpParam.setPage(request.getPage());
        afpbrTrxHistorySpParam.setPageSize(request.getPageSize());
        afpbrTrxHistorySpParam.setTrxCode(request.getFilters().getTrxCode());
        if (!request.getFilters().getAmtFrom().isEmpty() && !request.getFilters().getAmtTo().isEmpty()) {
            afpbrTrxHistorySpParam.setAmtFrom(new BigDecimal(request.getFilters().getAmtFrom()));
            afpbrTrxHistorySpParam.setAmtTo(new BigDecimal(request.getFilters().getAmtTo()));
        }
        if (!request.getFilters().getStartDate().isEmpty() && !request.getFilters().getStartTime().isEmpty() && !request.getFilters().getEndDate().isEmpty() && !request.getFilters().getEndTime().isEmpty()) {
            afpbrTrxHistorySpParam.setStartDateTime(StringUtils.hasText(request.getFilters().getStartDate()) ? DateTimeUtil.convert12HourDateTime(request.getFilters().getStartDate(), request.getFilters().getStartTime(), true) : null);
            afpbrTrxHistorySpParam.setEndDateTime(StringUtils.hasText(request.getFilters().getEndDate()) ? DateTimeUtil.convert12HourDateTime(request.getFilters().getEndDate(), request.getFilters().getEndTime(), false) : null);
        }
        afpbrTrxHistorySpParam.setPage(request.getPage());
        afpbrTrxHistorySpParam.setPageSize(request.getPageSize());
        return afpbrTrxHistorySpParam;
    }

    private String getTotalFacilityLimit(List<AFPBRViewByFacilityDTO> accountList) {
        if (accountList == null) {
            return null; // Return zero if DTO or data is null
        }
        BigDecimal totalBalance = accountList.stream()
                .map(account -> account.getTotalFacilityLimit() != null
                        ? new BigDecimal(account.getTotalFacilityLimit().replaceAll(",", ""))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalBalance.toPlainString();
    }

    private String getTotalSettlementAmount(List<AFPBRViewByFacilityDTO> accountList) {
        if (accountList == null) {
            return null; // Return zero if DTO or data is null
        }
        BigDecimal totalBalance = accountList.stream()
                .map(account -> account.getTotalSettlementAmount() != null
                        ? new BigDecimal(account.getTotalSettlementAmount().replaceAll(",", ""))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalBalance.toPlainString();
    }

    private String getSettlementAmount(List<AFPBRViewByAccountDTO> accountList) {
        if (accountList == null) {
            return null; // Return zero if DTO or data is null
        }
        BigDecimal totalBalance = accountList.stream()
                .map(account -> account.getSettlementAmount() != null
                        ? new BigDecimal(account.getSettlementAmount().replaceAll(",", ""))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalBalance.toPlainString();
    }

    private String getFinancedAmount(List<AFPBRViewByAccountDTO> accountList) {
        if (accountList == null) {
            return null; // Return zero if DTO or data is null
        }
        BigDecimal totalBalance = accountList.stream()
                .map(account -> account.getFinancedAmount() != null
                        ? new BigDecimal(account.getFinancedAmount().replaceAll(",", ""))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalBalance.toPlainString();
    }

    private String getAcctTypeCode(String provider) {
        Optional<AccountProductTypeMatrix> matrix = matrixRepository.findByAcctTypeAndProv(AcctTypEnum.PBR.name(), provider);
        if (matrix.isEmpty()) {
            logger.error("Account type not found");
            return null;
        }

        return matrix.get().getEsbAccountTypeCode();
    }

    private AFAcctListESBDTO getESBAcctResult(List<AFPBRViewByAccountDTO> spResult) {
        AtomicReference<AFAcctListESBDTO> afAcctListESBDTO = new AtomicReference<>(new AFAcctListESBDTO());
        List<AFPBRViewByAccountDTO> acctTemp = new ArrayList<>();

        ESBAFPBRAcctListReqDTO<AFPBRViewByAccountDTO> acctListReqDTO = new ESBAFPBRAcctListReqDTO<>();
        acctListReqDTO.setAcctType(getAcctTypeCode(ProviderEnum.MY.name()));

        IntStream.range(0, (spResult.size() + batchSize - 1) / batchSize)
                .forEach(i -> {
                    int start = i * batchSize;
                    int end = Math.min(start + batchSize, spResult.size());
                    List<AFPBRViewByAccountDTO> batch = spResult.subList(start, end);

                    acctListReqDTO.setDatas(batch);
                    afAcctListESBDTO.set(afpbresbService.getAFPBRAccountBalanceInquiry(acctListReqDTO).getMsgBody());
                    if (afAcctListESBDTO.get().getAcctListing() != null && !afAcctListESBDTO.get().getAcctListing().isEmpty())
                        acctTemp.addAll((List<AFPBRViewByAccountDTO>) afAcctListESBDTO.get().getAcctListing());

                });

        afAcctListESBDTO.get().setAcctListing(acctTemp);
        return afAcctListESBDTO.get();
    }

    private AFAcctListESBDTO getESBFacilityResult(List<AFPBRViewByFacilityDTO> spResult) {
        AtomicReference<AFAcctListESBDTO> afAcctListESBDTO = new AtomicReference<>(new AFAcctListESBDTO());
        List<AFPBRViewByFacilityDTO> acctTemp = new ArrayList<>();

        ESBAFPBRAcctListReqDTO<AFPBRViewByFacilityDTO> acctListReqDTO = new ESBAFPBRAcctListReqDTO<>();
        acctListReqDTO.setAcctType(getAcctTypeCode(ProviderEnum.MY.name()));

        IntStream.range(0, (spResult.size() + batchSize - 1) / batchSize)
                .forEach(i -> {
                    int start = i * batchSize;
                    int end = Math.min(start + batchSize, spResult.size());
                    List<AFPBRViewByFacilityDTO> batch = spResult.subList(start, end);

                    acctListReqDTO.setDatas(batch);
                    afAcctListESBDTO.set(afpbresbService.getAFPBRMasterAccountBalanceInquiry(acctListReqDTO).getMsgBody());
                    if (afAcctListESBDTO.get().getAcctListing() != null && !afAcctListESBDTO.get().getAcctListing().isEmpty())
                        acctTemp.addAll((List<AFPBRViewByFacilityDTO>) afAcctListESBDTO.get().getAcctListing());

                });

        afAcctListESBDTO.get().setAcctListing(acctTemp);
        return afAcctListESBDTO.get();
    }

    private String getRedisKey(UserAndCorp userAndCorp, AFAccountListReq reqDTO) {
        String redisKey = null;
        if (reqDTO.isRealTimeIndicator())
            redisKey = redisHelper.createCacheKey(BD_AM_AF_PBR_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "ONL" + reqDTO.getViewByIndicator().name() + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
        else
            redisKey = redisHelper.createCacheKey(BD_AM_AF_PBR_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + reqDTO.getViewByIndicator().name() + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());

        return redisKey;
    }

    private ApiResponse<AFPBRAcctSummaryDTO> getAcctFromCache(UserAndCorp userAndCorp, AFAccountListReq reqDTO) {
        return (ApiResponse<AFPBRAcctSummaryDTO>) redisHelper.getFromCache(getRedisKey(userAndCorp, reqDTO), ApiResponse.class);
    }

    public void removeAllFromCache(UserAndCorp userAndCorp){
        String redisKey = redisHelper.createCacheKey(BD_AM_AF_PBR_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_*");
        redisHelper.deleteCachesWithPattern(redisKey);
        logger.info("removed cache for {}", redisKey);
    }
}
