//package com.m2e.dashboard.service.loan.tlf;
//
//import com.m2e.dashboard.auth.JwtUtil;
//import com.m2e.dashboard.config.UserAndCorp;
//import com.m2e.dashboard.dto.HostProductDto;
//import com.m2e.dashboard.dto.MoreActionsEntitlement;
//import com.m2e.dashboard.dto.MoreActionsRedirection;
//import com.m2e.dashboard.dto.base.AccountStatisticDTO;
//import com.m2e.dashboard.dto.base.DownloadDTO;
//import com.m2e.dashboard.dto.base.DownloadReq;
//import com.m2e.dashboard.dto.base.loan.LNAcctListReq;
//import com.m2e.dashboard.dto.base.loan.LNReq;
//import com.m2e.dashboard.dto.common.ApiResponse;
//import com.m2e.dashboard.dto.common.ErrorResponse;
//import com.m2e.dashboard.dto.common.SearchCollectionResponse;
//import com.m2e.dashboard.dto.loan.tlf.LNTLFAccountType;
//import com.m2e.dashboard.dto.loan.tlf.request.LNTLFDetailReqDTO;
//import com.m2e.dashboard.dto.loan.tlf.request.LNTLFTranchesDetailReqDTO;
//import com.m2e.dashboard.dto.loan.tlf.request.LNTLFTranchesListReqDTO;
//import com.m2e.dashboard.dto.loan.tlf.request.LNTrxHistoryReqDTO;
//import com.m2e.dashboard.dto.loan.tlf.request.download.LNTLFAcctListingDownloadReqDTO;
//import com.m2e.dashboard.dto.loan.tlf.request.download.LNTLFTranchesDownloadReqDTO;
//import com.m2e.dashboard.dto.loan.tlf.request.download.LNTLFTrxHistoryDownloadReqDTO;
//import com.m2e.dashboard.dto.loan.tlf.response.*;
//import com.m2e.dashboard.dto.loan.tlf.response.acctdetail.*;
//import com.m2e.dashboard.enums.*;
//import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
//import com.m2e.dashboard.jms.loan.tlf.request.tranch.details.ESBLoanTLFTranchesReqDTO;
//import com.m2e.dashboard.mapper.loan.tlf.LoanTLFMapper;
//import com.m2e.dashboard.projection.AcctInfoProjection;
//import com.m2e.dashboard.projection.AcctStatusProjection;
//import com.m2e.dashboard.repository.AccountRepository;
//import com.m2e.dashboard.repository.CifRepository;
//import com.m2e.dashboard.repository.HostMatrixProductRepository;
//import com.m2e.dashboard.service.loan.tlf.jms.LNTLFESBService;
//import com.m2e.dashboard.sp.loan.tlf.request.LNTLFAcctDetailSpParam;
//import com.m2e.dashboard.sp.loan.tlf.request.LNTLFAcctListingSpParam;
//import com.m2e.dashboard.sp.loan.tlf.request.LNTLFAcctStatsSpParam;
//import com.m2e.dashboard.sp.loan.tlf.request.LNTLFTrxHistorySpParam;
//import com.m2e.dashboard.sp.loan.tlf.response.LNTLFAcctListingSpDTO;
//import com.m2e.dashboard.sp.loan.tlf.response.LNTLFTrxHistorySpDTO;
//import com.m2e.dashboard.util.DateTimeUtil;
//import com.m2e.dashboard.util.UtilityFunctions;
//import com.m2e.utils.esb.enums.loan.tlf.FitasLoanGrading;
//import com.m2e.utils.jdbc.model.dto.BaseSpDto;
//import com.m2e.utils.redis.helper.RedisHelper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.*;
//import java.util.stream.IntStream;
//
//import static com.m2e.dashboard.enums.AcctProductEntitlementConst.LOAN_AND_CARD_PAYMENT_PRODUCT_CODE;
//import static com.m2e.dashboard.enums.AcctProductEntitlementConst.USER_ACCT_PRODUCT_LIST_REDIS_KEY;
//import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_LN_TLF_ACCTLIST;
//import static com.m2e.dashboard.enums.SPEnum.SP_LN_TL_ACCT_LIST;
//import static com.m2e.dashboard.enums.SPEnum.SP_LN_TL_ACCT_STATS;
//import static com.m2e.dashboard.enums.TermLoanTypeEnum.*;
//import static com.m2e.dashboard.util.UtilityFunctions.formatThousandsSeparatorAndCents;
//import static com.m2e.dashboard.util.UtilityFunctions.setPaginationData;
//
//@RequiredArgsConstructor
//@Service
//@Slf4j
//public class LNTLFService {
//    private static final int batchSize = 10;
//    private static final String CONV = "conventional account list";
//    private static final String CONV_FORIEGN = "conventional foreign account list";
//    private static final String ISLAMIC = "islamic account list";
//    private static final String ISLAMIC_FORIEGN = "islamic foreign account list";
//    private static final String FITAS = "fitas account list";
//    private static final String TOTAL_DRAWDOWN = "totalDrawdownAmount";
//    private static final String TOTAL_OUTSTANDING = "totalOutstandingBalance";
//    private static final String DASH = "-";
//
//    private final JwtUtil jwtUtil;
//    private final UtilityFunctions utilityFunctions;
//    private final ModelMapper modelMapper;
//    private final LNTLFSPService lntlfSpService;
//    private final LNTLFFileService fileService;
//    private final LNTLFTrxHistoryFileService historyFileService;
//    private final LNTLFTranchFileService tranchFileService;
//    private final HostMatrixProductRepository hostProductMatrixRepository;
//    private final AccountRepository accountRepository;
//    private final CifRepository cifRepository;
//    private final LNTLFESBService lntlfesbService;
//    private final RedisHelper redisHelper;
//    private final LoanTLFMapper loanTlfMapper;
//
//    public ApiResponse<AccountStatisticDTO> getLNTLFAccountStatistic(String auth, LNReq request) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//
//        removeAllFromCache(userAndCorp);
//
//        LNTLFAcctStatsSpParam lntlfAcctStatsSpParam = new LNTLFAcctStatsSpParam();
//        lntlfAcctStatsSpParam.setCorpId(userAndCorp.getCorpId());
//        lntlfAcctStatsSpParam.setUserId(userAndCorp.getUserId());
//        LNTLFAccountStatisticDTO lntlfAccountStatisticDTO = lntlfSpService.accountStatisticsTLF(lntlfAcctStatsSpParam, SP_LN_TL_ACCT_STATS.getValue());
//
//        if (request.isRealTimeIndicator()) {
//            // Only display Number of Accounts
//            lntlfAccountStatisticDTO.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
//            lntlfAccountStatisticDTO.setConsolidatedOutstandingBalanceAmount(null);
//            lntlfAccountStatisticDTO.setConsolidatedDrawdownAmount(null);
//        }
//
//        ApiResponse<AccountStatisticDTO> apiResponse = new ApiResponse<>();
//        apiResponse.setMsgHeader(new ErrorResponse("0"));
//        apiResponse.setMsgBody(lntlfAccountStatisticDTO);
//
//        return apiResponse;
//    }
//
//    private Map<String, BigDecimal> getRealtimeAccountStatisticFromESB(UserAndCorp userAndCorp, LNAcctListReq request) {
//        // Get list of all term loan acct from DB
//        BaseSpDto<LNTLFAcctListingSpParam, LNTLFAcctListingSpDTO> resultBaseSpDto = getAcctListSpResult(userAndCorp, request);
//
//        List<LNTLFDTO> spAcctList = getAccountListResult(resultBaseSpDto.getResults());
//        Map<TermLoanTypeEnum, Map<String, List<LNTLFDTO>>> categorizedAcctLists = categorizeAcctLists(spAcctList);
//        LNTLFAcctListingDTO realtimeAccounts = getESBResultList(spAcctList, categorizedAcctLists);
//        Map<String, BigDecimal> esbAcctStatsValueMap = new HashMap<>();
//
//        if(realtimeAccounts.getEsbMetadata().getIsESBError().equals(true)) {
//            log.error("Failed ESB call for Term Loan stats");
//            esbAcctStatsValueMap.put(TOTAL_DRAWDOWN, null);
//            esbAcctStatsValueMap.put(TOTAL_OUTSTANDING, null);
//            return esbAcctStatsValueMap;
//        }
//
//        List<LNTLFDTO> realtimeAcctListData = realtimeAccounts.getDatas().getData();
//        BigDecimal totalDrawdownAmount = new BigDecimal(0);
//        BigDecimal totalOutstandingBalance = new BigDecimal(0);
//
//        boolean invalidESBDrawdownAmount = false;
//        boolean invalidESBOutstandingBalance = false;
//        for (LNTLFDTO realtimeAcct : realtimeAcctListData) {
//            if (realtimeAcct.getDrawdownAmount() == null) {
//                log.error("Null realtime data for drawdown amount for account {} {}", realtimeAcct.getAcctNumber(), realtimeAcct.getAccountCurrency());
//                invalidESBDrawdownAmount = true;
//            } else {
//                log.info("adding realtimeAcct drawdownAmount: {}", realtimeAcct.getDrawdownAmount());
//                totalDrawdownAmount = totalDrawdownAmount.add(new BigDecimal(realtimeAcct.getDrawdownAmount().replace(",", ""))).setScale(2, RoundingMode.HALF_UP);
//            }
//
//            if (realtimeAcct.getOutstandingBalance() == null) {
//                log.error("Null realtime data for outstanding balance amount for account {} {}", realtimeAcct.getAcctNumber(), realtimeAcct.getAccountCurrency());
//                invalidESBOutstandingBalance = true;
//            } else {
//                log.info("adding realtimeAcct outstandingBalance: {}", realtimeAcct.getOutstandingBalance());
//                totalOutstandingBalance = totalOutstandingBalance.add(new BigDecimal(realtimeAcct.getOutstandingBalance().replace(",", ""))).setScale(2, RoundingMode.HALF_UP);
//            }
//        }
//        esbAcctStatsValueMap.put(TOTAL_DRAWDOWN, invalidESBDrawdownAmount ? null : totalDrawdownAmount);
//        esbAcctStatsValueMap.put(TOTAL_OUTSTANDING, invalidESBOutstandingBalance ? null : totalOutstandingBalance);
//        return esbAcctStatsValueMap;
//    }
//
//    private BaseSpDto<LNTLFAcctListingSpParam, LNTLFAcctListingSpDTO> getAcctListSpResult(UserAndCorp userAndCorp, LNAcctListReq request) {
//        LNTLFAcctListingSpParam lntlfAcctListingSpParam = new LNTLFAcctListingSpParam();
//        lntlfAcctListingSpParam.setCorpId(userAndCorp.getCorpId());
//        lntlfAcctListingSpParam.setUserId(userAndCorp.getUserId());
//        lntlfAcctListingSpParam.setSearchParam(StringUtils.hasText(request.getSearchParam()) ? request.getSearchParam() : null);
//        lntlfAcctListingSpParam.setCorpCifNo(StringUtils.hasText(request.getCorpCifNo()) ? request.getCorpCifNo() : null);
//        lntlfAcctListingSpParam.setPage(request.getPage());
//        lntlfAcctListingSpParam.setPageSize(request.getPageSize());
//        return lntlfSpService.accountListingTLF(lntlfAcctListingSpParam, SP_LN_TL_ACCT_LIST.getValue());
//    }
//
//    public ApiResponse<LNTLFAcctListingDTO> getTLFAccountListing(String auth, LNAcctListReq request) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        ApiResponse<LNTLFAcctListingDTO> apiResponse;
//
//        apiResponse = getAcctFromCache(userAndCorp, request);
//
//        if (apiResponse == null) {
//            apiResponse = new ApiResponse<>();
//            SearchCollectionResponse<LNTLFDTO> result = new SearchCollectionResponse<>();
//            LNTLFAcctListingDTO lntlfAcctListingDTO = new LNTLFAcctListingDTO();
//            // Get account list with T-1 data from database (already sorted)
//            BaseSpDto<LNTLFAcctListingSpParam, LNTLFAcctListingSpDTO> resultBaseSpDto = getAcctListSpResult(userAndCorp, request);
//
//            if (request.isRealTimeIndicator()) {
//                log.info("T case");
//                LNTLFAcctListingDTO realtimeAccounts = new LNTLFAcctListingDTO();
//                List<LNTLFDTO> realtimeAcctListData = new ArrayList<>();
//                if (resultBaseSpDto.getResults() != null) {
//                    List<LNTLFDTO> spAcctList = getAccountListResult(resultBaseSpDto.getResults());
//                    // Categorize accounts by MY, SG SIBS, SG FITAS
//                    Map<TermLoanTypeEnum, Map<String, List<LNTLFDTO>>> categorizedAcctLists = categorizeAcctLists(spAcctList);
//                    // Get T data from ESB for MY and SG SIBS
//                    realtimeAccounts = getESBResultList(spAcctList, categorizedAcctLists);
//                    realtimeAcctListData = realtimeAccounts.getDatas().getData();
//                    // Hide T-1 SG FITAS data
//                    hideSGFitasAccountBalances(categorizedAcctLists);
//                    // Process each account
//                    realtimeAcctListData.forEach(dto -> {
//                        // Hide amount if account is not active
//                        if (AcctStatusEnum.CLOSED.name().equalsIgnoreCase(dto.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(dto.getAcctStatusDesc())) {
//                            dto.setDrawdownAmount(null);
//                            dto.setOutstandingBalance(null);
//                        }
//                        // Set make payment entitlement
//                        dto.setMoreActions(getMoreActions(dto.getAcctNumber(), dto.getAccountCurrency(), userAndCorp));
//                    });
//                    log.info("LNTLF realtime result list: {}", realtimeAcctListData);
//                }
//
//                setPaginationData(request, result, resultBaseSpDto, realtimeAcctListData);
//
//                lntlfAcctListingDTO.setEsbMetadata(realtimeAccounts.getEsbMetadata());
//            } else {
//                log.info("T-1 case");
//                List<LNTLFDTO> spAcctList = new ArrayList<>();
//                if (resultBaseSpDto.getResults() != null) {
//                    spAcctList = getAccountListResult(resultBaseSpDto.getResults());
//                }
//                setPaginationData(request, result, resultBaseSpDto, spAcctList);
//                result.getData().forEach(dto -> dto.setMoreActions(getMoreActions(dto.getAcctNumber(), dto.getAccountCurrency(), userAndCorp)));
//                log.info("TLF Acct List SP result: {}", utilityFunctions.convertJSONtoString(result));
//            }
//
//            lntlfAcctListingDTO.setCorpCifNo(request.getCorpCifNo());
//            lntlfAcctListingDTO.setDatas(result);
//
//            apiResponse.setMsgHeader(new ErrorResponse("0"));
//            apiResponse.setMsgBody(lntlfAcctListingDTO);
//
//            redisHelper.save(getRedisKey(userAndCorp, request), apiResponse);
//        }
//        return apiResponse;
//    }
//
//    private List<LNTLFDTO> getAccountListResult(List<LNTLFAcctListingSpDTO> spResultList) {
//        return spResultList.stream().map(loanTlfMapper::toLnTlfDto).toList();
//    }
//
//    private MoreActionsEntitlement getMoreActions(String accountNumber, String currency, UserAndCorp userAndCorp) {
//        String makePaymentRedisKey = redisHelper.buildCacheKey(userAndCorp.getUserId(), userAndCorp.getCorpId(), USER_ACCT_PRODUCT_LIST_REDIS_KEY, LOAN_AND_CARD_PAYMENT_PRODUCT_CODE, accountNumber, currency);
//        log.debug("getMoreActions() makePaymentRedisKey Redis key: {}", makePaymentRedisKey);
//        MoreActionsEntitlement moreActionsEntitlement = new MoreActionsEntitlement();
//        moreActionsEntitlement.setMakePayment(new MoreActionsRedirection(redisHelper.isPresent(makePaymentRedisKey), RedirectModuleEnum.PAYMENT_ACTION_REDIRECT.name(), Application.CASHLITE.getValue(), LOAN_AND_CARD_PAYMENT_PRODUCT_CODE));
//        return moreActionsEntitlement;
//    }
//
//    private Map<TermLoanTypeEnum, Map<String, List<LNTLFDTO>>> categorizeAcctLists(List<LNTLFDTO> tlfAcctList) {
//        Map<TermLoanTypeEnum, Map<String, List<LNTLFDTO>>> acctListByCountry = new HashMap<>();
//
//        List<LNTLFDTO> convAcctListMY = new ArrayList<>();
//        List<LNTLFDTO> convForeignAcctListMY = new ArrayList<>();
//        List<LNTLFDTO> islamicAcctListMY = new ArrayList<>();
//        List<LNTLFDTO> islamicForeignAcctListMY = new ArrayList<>();
//
//        List<LNTLFDTO> convAcctListSIBSSG = new ArrayList<>();
//        List<LNTLFDTO> convForeignAcctListSIBSSG = new ArrayList<>();
//        List<LNTLFDTO> islamicAcctListSIBSSG = new ArrayList<>();
//        List<LNTLFDTO> islamicForeignAcctListSIBSSG = new ArrayList<>();
//
//        List<LNTLFDTO> acctListFitasSG = new ArrayList<>();
//
//        log.info("categorizeAcctLists() tlfAcctList: {}",utilityFunctions.convertJSONtoString(tlfAcctList));
//        tlfAcctList.forEach(tlfAcct -> {
//            switch (tlfAcct.getProvider()) {
//                case "MY" -> {
//                    if (tlfAcct.getAccountTypeIndicators().isIslamic()) {
//                        if (tlfAcct.getAccountTypeIndicators().isForeign()) {
//                            log.info("Islamic foreign");
//                            islamicForeignAcctListMY.add(tlfAcct);
//                        } else {
//                            log.info("Islamic");
//                            islamicAcctListMY.add(tlfAcct);
//                        }
//                    } else {
//                        if (tlfAcct.getAccountTypeIndicators().isForeign()) {
//                            log.info("Conventional foreign");
//                            convForeignAcctListMY.add(tlfAcct);
//                        } else {
//                            log.info("Conventional");
//                            convAcctListMY.add(tlfAcct);
//                        }
//                    }
//                } case "SG" -> {
//                    // Note: since FITAS calls db, it does not need to be categorized, only SIBS accounts need to be categorized
//                    if (!tlfAcct.getAccountTypeIndicators().isFITAS()) {
//                        if (tlfAcct.getAccountTypeIndicators().isIslamic()) {
//                            if (tlfAcct.getAccountTypeIndicators().isForeign()) {
//                                log.info("Islamic foreign SIBS");
//                                islamicForeignAcctListSIBSSG.add(tlfAcct);
//                            } else {
//                                log.info("Islamic SIBS");
//                                islamicAcctListSIBSSG.add(tlfAcct);
//                            }
//                        } else {
//                            if (tlfAcct.getAccountTypeIndicators().isForeign()) {
//                                log.info("Conventional foreign SIBS");
//                                convForeignAcctListSIBSSG.add(tlfAcct);
//                            } else {
//                                log.info("Conventional SIBS");
//                                convAcctListSIBSSG.add(tlfAcct);
//                            }
//                        }
//                    } else {
//                        log.info("FITAS");
//                        acctListFitasSG.add(tlfAcct);
//                    }
//                } default -> log.info("Invalid provider");
//            }
//        });
//
//        acctListByCountry.put(MY, Map.of(
//                CONV, convAcctListMY,
//                CONV_FORIEGN, convForeignAcctListMY,
//                ISLAMIC, islamicAcctListMY,
//                ISLAMIC_FORIEGN, islamicForeignAcctListMY)
//        );
//        acctListByCountry.put(SG_SIBS, Map.of(
//                CONV, convAcctListSIBSSG,
//                CONV_FORIEGN, convForeignAcctListSIBSSG,
//                ISLAMIC, islamicAcctListSIBSSG,
//                ISLAMIC_FORIEGN, islamicForeignAcctListSIBSSG)
//        );
//        acctListByCountry.put(SG_FITAS, Map.of(
//                FITAS, acctListFitasSG)
//        );
//        log.info("categorizeAcctLists() acctListByCountry: {}",utilityFunctions.convertJSONtoString(acctListByCountry));
//        return acctListByCountry;
//    }
//
//    /**
//     * Fetch T data from ESB.
//     *
//     * @param originalSpAcctList result from database SP
//     * @param acctListByCountry return value of categorizeAcctLists
//     * @return result from database SP with account balances updated in place wrapped in API response DTO
//     */
//    private LNTLFAcctListingDTO getESBResultList(List<LNTLFDTO> originalSpAcctList, Map<TermLoanTypeEnum, Map<String, List<LNTLFDTO>>> acctListByCountry) {
//        LNTLFAcctListingDTO lntlfAcctListing = new LNTLFAcctListingDTO();
//
//        acctListByCountry.forEach((termLoanTypeEnum, categorizedAcctLists) -> {
//            switch(termLoanTypeEnum) {
//                case MY:
//                    log.info("getESBResultList() provider: MY");
//                    categorizedAcctLists.forEach((type, spAcctlist) -> {
//                        if (!spAcctlist.isEmpty()) {
//                            log.info("start getESBResultList() spAcctlist: {}", utilityFunctions.convertJSONtoString(spAcctlist));
//                            List<LNTLFDTO> esbAcctList = new ArrayList<>();
//
//                            // Get data from different mq properties
//                            switch (type) {
//                                case CONV:
////                                    tlfDetail = lntlfesbService.getAccountBalanceInquiryMYConv(spAcctlist);
//                                    IntStream.range(0, (spAcctlist.size() + batchSize - 1) / batchSize)
//                                            .mapToObj(i -> spAcctlist.subList(i * batchSize, Math.min((i + 1) * batchSize, spAcctlist.size())))
//                                            .forEach(groupedAcct -> {
//                                                ApiResponse<LNTLFAcctListingDTO> tlfBalanceInquiryResult = lntlfesbService.getAccountBalanceInquiryMYConv(groupedAcct);
//                                                if (tlfBalanceInquiryResult.getMsgBody().getDatas() == null) {
//                                                    log.info("Null Term Loan MY Conventional balance inquiry returned by ESB Service");
//                                                } else {
//                                                    List<LNTLFDTO> esbAcctBalInquiryResultList = tlfBalanceInquiryResult.getMsgBody().getDatas().getData();
//                                                    ESBMetadataDTO esbMetadata = tlfBalanceInquiryResult.getMsgBody().getEsbMetadata();
//                                                    if (esbAcctBalInquiryResultList != null) {
//                                                        groupedAcct.forEach(spAcct -> {
//                                                            esbAcctBalInquiryResultList.forEach(data -> {
//                                                                if (spAcct.getAcctNumber().equals(data.getAcctNumber())) {
//                                                                    spAcct.setDrawdownAmount(data.getDrawdownAmount());
//                                                                    spAcct.setOutstandingBalance(data.getOutstandingBalance());
//                                                                }
//                                                            });
//                                                        });
//                                                    }
//                                                    esbAcctList.addAll(groupedAcct);
//                                                    if (!lntlfAcctListing.getEsbMetadata().getIsESBError()) {
//                                                        lntlfAcctListing.setEsbMetadata(esbMetadata);
//                                                    }
//                                                }
//                                            });
//                                    break;
//                                case CONV_FORIEGN:
//                                    IntStream.range(0, (spAcctlist.size() + batchSize - 1) / batchSize)
//                                            .mapToObj(i -> spAcctlist.subList(i * batchSize, Math.min((i + 1) * batchSize, spAcctlist.size())))
//                                            .forEach(groupedAcct -> {
//                                                ApiResponse<LNTLFAcctListingDTO> tlfBalanceInquiryResult = lntlfesbService.getAccountBalanceInquiryMYForeignConv(groupedAcct);
//                                                if (tlfBalanceInquiryResult.getMsgBody().getDatas() == null) {
//                                                    log.info("Null Term Loan MY Foreign Conventional balance inquiry returned by ESB Service");
//                                                } else {
//                                                    List<LNTLFDTO> esbAcctBalInquiryResultList = tlfBalanceInquiryResult.getMsgBody().getDatas().getData();
//                                                    ESBMetadataDTO esbMetadata = tlfBalanceInquiryResult.getMsgBody().getEsbMetadata();
//                                                    if (esbAcctBalInquiryResultList != null) {
//                                                        groupedAcct.forEach(spAcct -> {
//                                                            esbAcctBalInquiryResultList.forEach(data -> {
//                                                                if (spAcct.getAcctNumber().equals(data.getAcctNumber())) {
//                                                                    spAcct.setDrawdownAmount(data.getDrawdownAmount());
//                                                                    spAcct.setOutstandingBalance(data.getOutstandingBalance());
//                                                                }
//                                                            });
//                                                        });
//                                                    }
//                                                    esbAcctList.addAll(groupedAcct);
//                                                    if (!lntlfAcctListing.getEsbMetadata().getIsESBError()) {
//                                                        lntlfAcctListing.setEsbMetadata(esbMetadata);
//                                                    }
//                                                }
//                                            });
//                                    break;
//                                case ISLAMIC:
//                                    IntStream.range(0, (spAcctlist.size() + batchSize - 1) / batchSize)
//                                            .mapToObj(i -> spAcctlist.subList(i * batchSize, Math.min((i + 1) * batchSize, spAcctlist.size())))
//                                            .forEach(groupedAcct -> {
//                                                ApiResponse<LNTLFAcctListingDTO> tlfBalanceInquiryResult = lntlfesbService.getAccountBalanceInquiryMYIslamic(groupedAcct);
//                                                if (tlfBalanceInquiryResult.getMsgBody().getDatas() == null) {
//                                                    log.info("Null Term Loan MY Islamic balance inquiry returned by ESB Service");
//                                                } else {
//                                                    List<LNTLFDTO> esbAcctBalInquiryResultList = tlfBalanceInquiryResult.getMsgBody().getDatas().getData();
//                                                    ESBMetadataDTO esbMetadata = tlfBalanceInquiryResult.getMsgBody().getEsbMetadata();
//                                                    if (esbAcctBalInquiryResultList != null) {
//                                                        groupedAcct.forEach(spAcct -> {
//                                                            esbAcctBalInquiryResultList.forEach(data -> {
//                                                                if (spAcct.getAcctNumber().equals(data.getAcctNumber())) {
//                                                                    spAcct.setDrawdownAmount(data.getDrawdownAmount());
//                                                                    spAcct.setOutstandingBalance(data.getOutstandingBalance());
//                                                                }
//                                                            });
//                                                        });
//                                                    }
//                                                    esbAcctList.addAll(groupedAcct);
//                                                    if (!lntlfAcctListing.getEsbMetadata().getIsESBError()) {
//                                                        lntlfAcctListing.setEsbMetadata(esbMetadata);
//                                                    }
//                                                }
//                                            });
//                                    break;
//                                case ISLAMIC_FORIEGN:
//                                    IntStream.range(0, (spAcctlist.size() + batchSize - 1) / batchSize)
//                                            .mapToObj(i -> spAcctlist.subList(i * batchSize, Math.min((i + 1) * batchSize, spAcctlist.size())))
//                                            .forEach(groupedAcct -> {
//                                                ApiResponse<LNTLFAcctListingDTO> tlfBalanceInquiryResult = lntlfesbService.getAccountBalanceInquiryMYForeignIslamic(groupedAcct);
//                                                if (tlfBalanceInquiryResult.getMsgBody().getDatas() == null) {
//                                                    log.info("Null Term Loan MY Foreign Islamic balance inquiry returned by ESB Service");
//                                                } else {
//                                                    List<LNTLFDTO> esbAcctBalInquiryResultList = tlfBalanceInquiryResult.getMsgBody().getDatas().getData();
//                                                    ESBMetadataDTO esbMetadata = tlfBalanceInquiryResult.getMsgBody().getEsbMetadata();
//                                                    if (esbAcctBalInquiryResultList != null) {
//                                                        groupedAcct.forEach(spAcct -> {
//                                                            esbAcctBalInquiryResultList.forEach(data -> {
//                                                                if (spAcct.getAcctNumber().equals(data.getAcctNumber())) {
//                                                                    spAcct.setDrawdownAmount(data.getDrawdownAmount());
//                                                                    spAcct.setOutstandingBalance(data.getOutstandingBalance());
//                                                                }
//                                                            });
//                                                        });
//                                                    }
//                                                    esbAcctList.addAll(groupedAcct);
//                                                    if (!lntlfAcctListing.getEsbMetadata().getIsESBError()) {
//                                                        lntlfAcctListing.setEsbMetadata(esbMetadata);
//                                                    }
//                                                }
//                                            });
//                                    break;
//                                default:
//                                    log.error("Error at LnTLFService (MY) getESBResultList()");
//                                    break;
//                            }
//                        } else {
//                            log.info("spAcctlist is empty for type {}", type);
//                        }
//                    });
//                    break;
//                case SG_SIBS: // note: FITAS does not support real-time data
//                    log.info("getESBResultList() provider: SG SIBS");
//                    categorizedAcctLists.forEach((type, spAcctlist) -> {
//                        if (!spAcctlist.isEmpty()) {
//                            ApiResponse<LNTLFAcctListingDTO> tlfDetail;
//                            List<LNTLFDTO> esbAcctBalInquiry;
//                            List<LNTLFDTO> esbData;
//
//                            // Get data from different mq properties
//                            switch (type) {
//                                case CONV:
//                                    tlfDetail = lntlfesbService.getAccountBalanceInquirySGSIBSConv(spAcctlist);
//                                    lntlfAcctListing.setEsbMetadata(tlfDetail.getMsgBody().getEsbMetadata());
//                                    if (tlfDetail.getMsgBody().getDatas() == null) {
//                                        log.info("Null Term Loan SG SIBS Conventional balance inquiry returned by ESB Service");
//                                        break;
//                                    }
//                                    esbAcctBalInquiry = tlfDetail.getMsgBody().getDatas().getData();
//                                    if (esbAcctBalInquiry != null) {
//                                        esbData = esbAcctBalInquiry;
//                                        spAcctlist.forEach(lntlfdto -> esbData.forEach(data -> {
//                                            if (lntlfdto.getAcctNumber().equals(data.getAcctNumber())) {
//                                                lntlfdto.setDrawdownAmount(data.getDrawdownAmount());
//                                                lntlfdto.setOutstandingBalance(data.getOutstandingBalance());
//                                                lntlfdto.setOverdue(data.isOverdue());
//                                            }
//                                        }));
//                                    }
//                                    break;
//                                case ISLAMIC:
//                                    tlfDetail = lntlfesbService.getAccountBalanceInquirySGSIBSIslamic(spAcctlist);
//                                    lntlfAcctListing.setEsbMetadata(tlfDetail.getMsgBody().getEsbMetadata());
//                                    if (tlfDetail.getMsgBody().getDatas() == null) {
//                                        log.info("Null Term Loan SG SIBS Islamic balance inquiry returned by ESB Service");
//                                        break;
//                                    }
//                                    esbAcctBalInquiry = tlfDetail.getMsgBody().getDatas().getData();
//                                    if (esbAcctBalInquiry != null) {
//                                        esbData = esbAcctBalInquiry;
//                                        spAcctlist.forEach(lntlfdto -> esbData.forEach(data -> {
//                                            if (lntlfdto.getAcctNumber().equals(data.getAcctNumber())) {
//                                                lntlfdto.setDrawdownAmount(data.getDrawdownAmount());
//                                                lntlfdto.setOutstandingBalance(data.getOutstandingBalance());
//                                                lntlfdto.setHasSellingPrice(data.isHasSellingPrice());
//                                                lntlfdto.setOverdue(data.isOverdue());
//                                            }
//                                        }));
//                                    }
//                                    break;
//                                default:
//                                    log.error("Error at LnTLFService (SG) getESBResultList()");
//                                    break;
//                            }
//                        } else {
//                            log.info("spAcctlist is empty");
//                        }
//                    });
//                    break;
//                default: break;
//            }
//        });
//
//        SearchCollectionResponse<LNTLFDTO> termLoanData = new SearchCollectionResponse<>();
//        termLoanData.setData(originalSpAcctList);
//
//        lntlfAcctListing.setDatas(termLoanData);
//        return lntlfAcctListing;
//    }
//
//    private void hideSGFitasAccountBalances(Map<TermLoanTypeEnum, Map<String, List<LNTLFDTO>>> categorizedAcctLists) {
//        log.info("hideSGFitasAccountBalances() hiding SG FITAS accounts' balances...");
//        categorizedAcctLists.forEach((accountType, fitasAcctList) -> {
//            if (accountType == SG_FITAS) {
//                fitasAcctList.forEach((type, spAcctlist) -> {
//                    if (!spAcctlist.isEmpty()) {
//                        spAcctlist.forEach(acct->{
//                            acct.setDrawdownAmount(null);
//                            acct.setOutstandingBalance(null);
//                            acct.setMoreActions(new MoreActionsEntitlement());
//                        });
//                    }
//                });
//            }
//        });
//    }
//
//    public DownloadDTO downloadTLF(String auth, DownloadReq request) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        log.debug("executing downloadTLF()");
//        DownloadDTO result = new DownloadDTO();
//        switch (request.getReportType()) {
//            case LOAN_TLF_ACCOUNTLISTING_RPT_TYPE -> {
//                downloadAccountListing(result, userAndCorp, request);
//            }
//            case LOAN_TLF_TRXHISTORIES_RPT_TYPE -> {
//                LNTLFTrxHistoryDownloadReqDTO requestData = (LNTLFTrxHistoryDownloadReqDTO) request;
//                downloadTrxHistory(result, requestData, auth);
//            }
//            case LOAN_TLF_TRANCHES_RPT_TYPE -> {
//                LNTLFTranchesDownloadReqDTO requestData = (LNTLFTranchesDownloadReqDTO) request;
//                downloadTranchesPerAccount(result, requestData);
//            }
//            default -> {
//                throw new IllegalArgumentException("Report type is undefined");
//            }
//        }
//        return result;
//    }
//
//    public DownloadDTO downloadAccountListing(DownloadDTO response, UserAndCorp userAndCorp, DownloadReq downloadRequest) {
//        log.info("downloadAccountListing");
//        LNTLFAcctListingDownloadReqDTO request = (LNTLFAcctListingDownloadReqDTO) downloadRequest;
//
//        LNAcctListReq lnAcctListReq = new LNAcctListReq();
//        lnAcctListReq.setPage(1);
//        lnAcctListReq.setPageSize(Integer.MAX_VALUE);
//        lnAcctListReq.setSearchParam(request.getSearchParam());
//        lnAcctListReq.setCorpCifNo(request.getCorpCifNo());
//
//        LNTLFAcctListingSpParam lntlfAcctListingSpParam = new LNTLFAcctListingSpParam();
//        lntlfAcctListingSpParam.setCorpId(userAndCorp.getCorpId());
//        lntlfAcctListingSpParam.setUserId(userAndCorp.getUserId());
//        lntlfAcctListingSpParam.setSearchParam(StringUtils.hasText(request.getSearchParam()) ? request.getSearchParam() : null);
//        lntlfAcctListingSpParam.setCorpCifNo(StringUtils.hasText(request.getCorpCifNo()) ? request.getCorpCifNo() : null);
//        lntlfAcctListingSpParam.setPage(lnAcctListReq.getPage());
//        lntlfAcctListingSpParam.setPageSize(lnAcctListReq.getPageSize());
//
//        BaseSpDto<LNTLFAcctListingSpParam, LNTLFAcctListingSpDTO> resultBaseSpDto = lntlfSpService.accountListingTLF(lntlfAcctListingSpParam, SP_LN_TL_ACCT_LIST.getValue());
//        List<LNTLFDTO> consolidatedAcctList = new ArrayList<>();
//
//        if (request.getRealTimeIndicator().equals(Boolean.TRUE) && resultBaseSpDto.getResults()!=null) {
//            // Get T data from ESB
//            List<LNTLFDTO> spAcctList = getAccountListResult(resultBaseSpDto.getResults());
//            Map<TermLoanTypeEnum, Map<String, List<LNTLFDTO>>> categorizedAcctLists = categorizeAcctLists(spAcctList);
//            LNTLFAcctListingDTO esbAcctListResponse = getESBResultList(spAcctList, categorizedAcctLists);
//            List<LNTLFDTO> esbAcctList = esbAcctListResponse.getDatas().getData();
//            log.info("LNTLF esb result list: {}", esbAcctList);
//            consolidatedAcctList.addAll(esbAcctList);
//        }
//
//        // Get T-1 data from DB
//        if (resultBaseSpDto.getResults()!=null) {
//            List<LNTLFDTO> dbData = getAccountListResult(resultBaseSpDto.getResults());
//            consolidatedAcctList.addAll(dbData);
//            log.info("LNTLF sp result list: {}", dbData);
//        }
//
//        consolidatedAcctList.forEach(dto -> {
//            dto.setDrawdownAmount(formatThousandsSeparatorAndCents(dto.getDrawdownAmount()));
//            dto.setOutstandingBalance(formatThousandsSeparatorAndCents(dto.getOutstandingBalance()));
//        });
//
//        switch (request.getFormat()) {
//            case CSV:
//                response.setData(fileService.generateCsv(consolidatedAcctList));
//                response.setFileName(DownloadEnum.LOAN_TLF_ACCLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
//                break;
//            case PDF:
//                response.setData(fileService.generatePdf(consolidatedAcctList));
//                response.setFileName(DownloadEnum.LOAN_TLF_ACCLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
//                break;
//            case TXT:
//                response.setData(fileService.generateTxt(consolidatedAcctList));
//                response.setFileName(DownloadEnum.LOAN_TLF_ACCLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
//                break;
//            default:
//                throw new IllegalArgumentException("Report format is undefined");
//        }
//        return response;
//    }
//
//    public void downloadTrxHistory(DownloadDTO response, LNTLFTrxHistoryDownloadReqDTO request, String auth) {
//        log.info("downloadTrxHistory");
//
//        LNTrxHistoryReqDTO trxHistorySearchReqDTO = LNTrxHistoryReqDTO.builder()
//                .accountTypeIndicators(request.getAccountTypeIndicators())
//                .productCode(request.getProductCode())
//                .acctNumber(request.getAcctNumber())
//                .currency(request.getCurrency())
//                .provider(request.getProvider())
//                .searchParam(request.getSearchParam())
//                .filters(request.getFilter())
//                .page(1)
//                .pageSize(Integer.MAX_VALUE)
//                .build();
//
//        List<LNTLFTrxHistoryDTO> fusedData = getTrxHistory(trxHistorySearchReqDTO, auth).getMsgBody().getDatas().getData();
//        generateTrxHistoryFile(response, fusedData, request);
//    }
//
//    private void generateTrxHistoryFile(DownloadDTO response, List<LNTLFTrxHistoryDTO> data, LNTLFTrxHistoryDownloadReqDTO request) {
//        switch (request.getFormat()) {
//            case CSV:
//                response.setData(historyFileService.generateCsv(data));
//                response.setFileName(DownloadEnum.LOAN_TLF_TRXHISTORIES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
//                break;
//            case PDF:
//                response.setData(historyFileService.generatePdf(data));
//                response.setFileName(DownloadEnum.LOAN_TLF_TRXHISTORIES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
//                break;
//            case TXT:
//                response.setData(historyFileService.generateTxt(data));
//                response.setFileName(DownloadEnum.LOAN_TLF_TRXHISTORIES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
//                break;
//            default:
//                throw new IllegalArgumentException("Report format is undefined");
//        }
//    }
//
//    public void downloadTranchesPerAccount(DownloadDTO response, LNTLFTranchesDownloadReqDTO request) {
//        log.info("executing downloadTranchesPerAccount()");
//
//        LNTLFTranchesListReqDTO lntlfTrancheListReqDTO = new LNTLFTranchesListReqDTO();
//        lntlfTrancheListReqDTO.setAcctNumber(request.getAcctNumber());
//        lntlfTrancheListReqDTO.setAccountCurrency(request.getAccountCurrency());
//        lntlfTrancheListReqDTO.setSearchParam(request.getSearchParam());
//        lntlfTrancheListReqDTO.setPage(1);
//        lntlfTrancheListReqDTO.setPageSize(Integer.MAX_VALUE);
//
//        List<LNTLFTrancheDTO> data = getTranches(lntlfTrancheListReqDTO).getMsgBody().getDatas().getData();
//
//        switch (request.getFormat()) {
//            case CSV:
//                response.setData(tranchFileService.generateCsv(data));
//                response.setFileName(DownloadEnum.LOAN_TLF_TRANCHES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
//                break;
//            case PDF:
//                response.setData(tranchFileService.generatePdf(data));
//                response.setFileName(DownloadEnum.LOAN_TLF_TRANCHES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
//                break;
//            case TXT:
//                response.setData(tranchFileService.generateTxt(data));
//                response.setFileName(DownloadEnum.LOAN_TLF_TRANCHES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
//                break;
//            default:
//                throw new IllegalArgumentException("Report format is undefined");
//        }
//    }
//
//    public ApiResponse<LNTLFDetailDTO> getTLFAccountDetail(String auth, LNTLFDetailReqDTO request) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        log.info("executing getTLFAccountDetail() with request: {}", request);
//
//        LNTLFDetailDTO tlfDetail;
//        Optional<AcctInfoProjection> acctInfo = Optional.empty();
//        switch (request.getAccountTypeIndicators().getProvider()) {
//            case "MY" -> {
//                Optional<AcctStatusProjection> acctStatus = accountRepository.findAcctSts(request.getAcctNumber());
//                if (request.getAccountTypeIndicators().isIslamic()) {
//                    if (request.getAccountTypeIndicators().isForeign()) {
//                        log.info("MY islamic foreign");
//                        LNTLFDetailMYForeignIslamicDTO financingMYIslamicDTO = lntlfesbService.getLoanTLFAcctFinancingDetailsMYIslamic(request).getMsgBody();
//                        financingMYIslamicDTO.setProductType(LNProductType.ISLAMIC);
//                        financingMYIslamicDTO.setLastUpdated(DateTimeUtil.getCurrentTime());
//                        if (!(acctStatus.isPresent() && "ACTIVE".equals(acctStatus.get().getAcctStatusDesc()))) {
//                            financingMYIslamicDTO.setApprovedAmount(null);
//                            financingMYIslamicDTO.setDrawdownAmount(null);
//                            financingMYIslamicDTO.setOutstandingBalanceAmount(null);
//                            financingMYIslamicDTO.setInstallmentAmount(null);
//                            financingMYIslamicDTO.setInstallmentDueAmount(null);
//                            financingMYIslamicDTO.setOutstandingCostOfFinanceAmount(null);
//                            financingMYIslamicDTO.setPaymentInAdvanceAmount(null);
//                            financingMYIslamicDTO.setSellingPriceAmount(null);
//                            financingMYIslamicDTO.setProfitOnCostOutstandingAmount(null);
//                            financingMYIslamicDTO.setCompensationFeeOnCOF(null);
//                            financingMYIslamicDTO.setCompensationFeeOnProfit(null);
//                            financingMYIslamicDTO.setMiscellaneousChargesAmount(null);
//                        }
//                        tlfDetail = financingMYIslamicDTO;
//                    } else {
//                        log.info("MY islamic");
//                        LNTLFDetailIslamicDTO loanMYIslamicDTO = lntlfesbService.getLoanTLFAcctLoanDetailsMYIslamic(request).getMsgBody();
//                        if (!(acctStatus.isPresent() && "ACTIVE".equals(acctStatus.get().getAcctStatusDesc()))) {
//                            loanMYIslamicDTO.setApprovedAmount(null);
//                            loanMYIslamicDTO.setDrawdownAmount(null);
//                            loanMYIslamicDTO.setOutstandingBalanceAmount(null);
//                            loanMYIslamicDTO.setOutstandingAmount(null);
//                            loanMYIslamicDTO.setInstallmentAmount(null);
//                            loanMYIslamicDTO.setInstallmentDueAmount(null);
//                            loanMYIslamicDTO.setOutstandingGraceProfitPrd(null);
//                            loanMYIslamicDTO.setOutstandingCostOfFinanceAmount(null);
//                            loanMYIslamicDTO.setOutstandingCompensationFee(null);
//                            loanMYIslamicDTO.setPaymentInAdvanceAmount(null);
//                            loanMYIslamicDTO.setSellingPriceAmount(null);
//                            loanMYIslamicDTO.setProfitOnCostOfFundsAmount(null);
//                            loanMYIslamicDTO.setMiscellaneousChargesAmount(null);
//                            loanMYIslamicDTO.setLatePaymentChargesAmount(null);
//                        }
//                        loanMYIslamicDTO.setProductType(LNProductType.ISLAMIC);
//                        loanMYIslamicDTO.setLastUpdated(DateTimeUtil.getCurrentTime());
//                        tlfDetail = loanMYIslamicDTO;
//                    }
//                } else {
//                    if (request.getAccountTypeIndicators().isForeign()) {
//                        log.info("MY conventional foreign");
//                        LNTLFDetailMYForeignConventionalDTO financingMYConventionalDTO = lntlfesbService.getLoanTLFAcctFinancingDetailsMYConv(request).getMsgBody();
//                        if (!(acctStatus.isPresent() && "ACTIVE".equals(acctStatus.get().getAcctStatusDesc()))) {
//                            financingMYConventionalDTO.setApprovedAmount(null);
//                            financingMYConventionalDTO.setDrawdownAmount(null);
//                            financingMYConventionalDTO.setOutstandingBalanceAmount(null);
//                            financingMYConventionalDTO.setOutstandingAmount(null);
//                            financingMYConventionalDTO.setInstallmentAmount(null);
//                            financingMYConventionalDTO.setInstallmentDueAmount(null);
//                            financingMYConventionalDTO.setInterestDueAmount(null);
//                            financingMYConventionalDTO.setPaymentInAdvanceAmount(null);
//                            financingMYConventionalDTO.setPenaltyOnPrincipal(null);
//                            financingMYConventionalDTO.setPenaltyOnInterest(null);
//                            financingMYConventionalDTO.setOutstandingCommitmentFees(null);
//                            financingMYConventionalDTO.setMiscellaneousChargesAmount(null);
//                        }
//                        financingMYConventionalDTO.setProductType(LNProductType.CONVENTIONAL);
//                        financingMYConventionalDTO.setLastUpdated(DateTimeUtil.getCurrentTime());
//                        tlfDetail = financingMYConventionalDTO;
//                    } else {
//                        log.info("MY conventional");
//                        LNTLFDetailConventionalDTO myConventionalDTO = lntlfesbService.getLoanTLFAcctLoanDetailsMYConv(request).getMsgBody();
//                        if (!(acctStatus.isPresent() && "ACTIVE".equals(acctStatus.get().getAcctStatusDesc()))) {
//                            myConventionalDTO.setApprovedAmount(null);
//                            myConventionalDTO.setDrawdownAmount(null);
//                            myConventionalDTO.setOutstandingBalanceAmount(null);
//                            myConventionalDTO.setOutstandingAmount(null);
//                            myConventionalDTO.setInstallmentAmount(null);
//                            myConventionalDTO.setInstallmentDueAmount(null);
//                            myConventionalDTO.setInterestDueAmount(null);
//                            myConventionalDTO.setPaymentInAdvanceAmount(null);
//                            myConventionalDTO.setMiscellaneousChargesAmount(null);
//                            myConventionalDTO.setCommitmentFeesAmount(null);
//                            myConventionalDTO.setPenaltyInterest(null);
//                        }
//                        myConventionalDTO.setProductType(LNProductType.CONVENTIONAL);
//                        myConventionalDTO.setLastUpdated(DateTimeUtil.getCurrentTime());
//                        tlfDetail = myConventionalDTO;
//                    }
//                }
//
//                acctInfo = accountRepository.findAcctInfo(
//                        request.getAcctNumber(),
//                        userAndCorp.getUserId(),
//                        userAndCorp.getCorpId(),
//                        request.getCorpCifNo(),
//                        "LOAN",
//                        "TL",
//                        "MY"
//                );
//                if (acctInfo.isEmpty()) {
//                    log.info("cannot find account info");
//                }
//            } case "SG" -> {
//                if (request.getAccountTypeIndicators().isFITAS()) {
//                    // SG FITAS
//                    Optional<AcctStatusProjection> acctStatus = accountRepository.findFitasAcctSts(request.getAcctNumber());
//                    if (request.getAccountTypeIndicators().isIslamic()) {
//                        log.info("SG FITAS Islamic");
//                        LNTLFDetailSGIslamicFITASDTO sgFitasIslamicDTO = getDetailSGFitasIslamicWithSp(new LNTLFAcctDetailSpParam(request.getAcctNumber(), request.getCurrency(), userAndCorp.getUserId(), userAndCorp.getCorpId()));
//                        if (!(acctStatus.isPresent() && "ACTIVE".equals(acctStatus.get().getAcctStatusDesc()))) {
//                            sgFitasIslamicDTO.setApprovedAmount(null);
//                            sgFitasIslamicDTO.setDrawdownAmount(null);
//                            sgFitasIslamicDTO.setOutstandingBalanceAmount(null);
//                            sgFitasIslamicDTO.setInstallmentAmount(null);
//                            sgFitasIslamicDTO.setOverdueInstallmentAmount(null);
//                            sgFitasIslamicDTO.setOriginalFinancingAmount(null);
//                            sgFitasIslamicDTO.setRepaymentAmount(null);
//                            sgFitasIslamicDTO.setLatePaymentChargesAmount(null);
//                        }
//                        hideValuesIfAbnormalLoanGrading(sgFitasIslamicDTO);
//                        sgFitasIslamicDTO.setProductType(LNProductType.ISLAMIC);
//                        tlfDetail = sgFitasIslamicDTO;
//                    } else {
//                        log.info("SG FITAS Conventional");
//                        LNTLFDetailSGConventionalFITASDTO sgFitasConventionalDTO = getDetailSGFitasConvWithSp(new LNTLFAcctDetailSpParam(request.getAcctNumber(), request.getCurrency(), userAndCorp.getUserId(), userAndCorp.getCorpId()));
//                        if (!(acctStatus.isPresent() && "ACTIVE".equals(acctStatus.get().getAcctStatusDesc()))) {
//                            sgFitasConventionalDTO.setApprovedAmount(null);
//                            sgFitasConventionalDTO.setDrawdownAmount(null);
//                            sgFitasConventionalDTO.setOutstandingBalanceAmount(null);
//                            sgFitasConventionalDTO.setOverdueInstallmentAmount(null);
//                            sgFitasConventionalDTO.setOriginalLoanAmount(null);
//                            sgFitasConventionalDTO.setRepaymentAmount(null);
//                            sgFitasConventionalDTO.setLateInterestAmount(null);
//                        }
//                        hideValuesIfAbnormalLoanGrading(sgFitasConventionalDTO);
//                        sgFitasConventionalDTO.setProductType(LNProductType.CONVENTIONAL);
//                        tlfDetail = sgFitasConventionalDTO;
//                    }
//                } else {
//                    // SG SIBS
//                    Optional<AcctStatusProjection> acctStatus = accountRepository.findAcctSts(request.getAcctNumber());
//                    if (request.getAccountTypeIndicators().isIslamic()) {
//                        log.info("SG SIBS islamic");
//                        LNTLFDetailSGSIBSDTO loanSGSIBSIslamicDTO = lntlfesbService.getLoanTLFAcctLoanDetailsSGIslamic(request).getMsgBody();
//                        if (!(acctStatus.isPresent() && "ACTIVE".equals(acctStatus.get().getAcctStatusDesc()))) {
//                            loanSGSIBSIslamicDTO.setApprovedAmount(null);
//                            loanSGSIBSIslamicDTO.setDrawdownAmount(null);
//                            loanSGSIBSIslamicDTO.setOutstandingBalanceAmount(null);
//                            loanSGSIBSIslamicDTO.setMonthlyInstallmentAmount(null);
//                            loanSGSIBSIslamicDTO.setInterestAmount(null);
//                            loanSGSIBSIslamicDTO.setDefaultLateInterest(null);
//                            loanSGSIBSIslamicDTO.setTotalPrincipalAmountBilledAmount(null);
//                        }
//                        loanSGSIBSIslamicDTO.setProductType(LNProductType.ISLAMIC);
//                        loanSGSIBSIslamicDTO.setLastUpdated(DateTimeUtil.getCurrentTime());
//
//                        tlfDetail = loanSGSIBSIslamicDTO;
//                    } else {
//                        log.info("SG SIBS conventional");
//                        LNTLFDetailSGSIBSDTO loanSGSIBSConventionalDTO = lntlfesbService.getLoanTLFAcctLoanDetailsSGConv(request).getMsgBody();
//                        if (!(acctStatus.isPresent() && "ACTIVE".equals(acctStatus.get().getAcctStatusDesc()))) {
//                            loanSGSIBSConventionalDTO.setApprovedAmount(null);
//                            loanSGSIBSConventionalDTO.setDrawdownAmount(null);
//                            loanSGSIBSConventionalDTO.setOutstandingBalanceAmount(null);
//                            loanSGSIBSConventionalDTO.setMonthlyInstallmentAmount(null);
//                            loanSGSIBSConventionalDTO.setInterestAmount(null);
//                            loanSGSIBSConventionalDTO.setDefaultLateInterest(null);
//                            loanSGSIBSConventionalDTO.setTotalPrincipalAmountBilledAmount(null);
//                        }
//                        loanSGSIBSConventionalDTO.setProductType(LNProductType.CONVENTIONAL);
//                        loanSGSIBSConventionalDTO.setLastUpdated(DateTimeUtil.getCurrentTime());
//
//                        tlfDetail = loanSGSIBSConventionalDTO;
//                    }
//
//                    acctInfo = accountRepository.findAcctInfo(
//                            request.getAcctNumber(),
//                            userAndCorp.getUserId(),
//                            userAndCorp.getCorpId(),
//                            request.getCorpCifNo(),
//                            "LOAN",
//                            "TLS",
//                            "SG"
//                    );
//                    if (acctInfo.isEmpty()) {
//                        log.info("cannot find account info");
//                    }
//                }
//            }
//            default -> throw new IllegalArgumentException("Provider is undefined");
//        }
//
//        // If FITAS, tlfDetail will have the information already
//        tlfDetail.setProductName(acctInfo.map(AcctInfoProjection::getProductName).orElse(tlfDetail.getProductName()));
//        tlfDetail.setAcctStatusCode(acctInfo.map(AcctInfoProjection::getAcctStatusCode).orElse(tlfDetail.getAcctStatusCode()));
//        tlfDetail.setAcctStatusDesc(acctInfo.map(AcctInfoProjection::getAcctStatusDesc).orElse(tlfDetail.getAcctStatusDesc()));
//        tlfDetail.setAcctName(acctInfo.map(AcctInfoProjection::getAcctName).orElse(tlfDetail.getAcctName()));
//
//        tlfDetail.setProductCode(acctInfo.map(AcctInfoProjection::getProductCode).orElse(request.getProductCode()));
//        tlfDetail.setAccountTypeIndicators(request.getAccountTypeIndicators());
//        tlfDetail.setAcctNumber(request.getAcctNumber());
//        tlfDetail.setCurrency(request.getCurrency());
//        Optional<String> corpName = cifRepository.findFirstCorpShortNameByCifNo(request.getCorpCifNo());
//        tlfDetail.setAccountOwnerCompany(corpName.orElse(null));
//        tlfDetail.setMoreActions(getMoreActions(tlfDetail.getAcctNumber(), tlfDetail.getCurrency(), userAndCorp));
//        return new ApiResponse<>(new ErrorResponse(), tlfDetail);
//    }
//
//    /**
//     * @see <a href="https://jira.maybank.com.my/browse/M2UBIZM25-90602">User story</a>
//     */
//    private static void hideValuesIfAbnormalLoanGrading(LNTLFDetailSGIslamicFITASDTO sgFitasIslamicDTO) {
//        if (sgFitasIslamicDTO.getLoanGrading() != FitasLoanGrading.NORMAL) {
//            sgFitasIslamicDTO.setInstallmentAmount(null);
//            sgFitasIslamicDTO.setOverdueInstallmentAmount(null);
//            sgFitasIslamicDTO.setRepaymentAmount(null);
//            sgFitasIslamicDTO.setLatePaymentChargesAmount(null);
//            sgFitasIslamicDTO.setNextProfitRepaymentDate(DASH);
//            sgFitasIslamicDTO.setNextPrincipalRepaymentDate(DASH);
//        }
//    }
//
//    /**
//     * @see <a href="https://jira.maybank.com.my/browse/M2UBIZM25-90602">User story</a>
//     */
//    private static void hideValuesIfAbnormalLoanGrading(LNTLFDetailSGConventionalFITASDTO sgFitasConventionalDTO) {
//        if (sgFitasConventionalDTO.getLoanGrading() != FitasLoanGrading.NORMAL) {
//            sgFitasConventionalDTO.setRepaymentAmount(null);
//            sgFitasConventionalDTO.setLateInterestAmount(null);
//            sgFitasConventionalDTO.setNextInterestPaymentDate(DASH);
//        }
//    }
//
//    private LNTLFAccountType retreiveTLFAccountType(HostProductDto hostProduct) {
//        if (hostProduct.getProvider() != null && hostProduct.getIsForeign() != null && hostProduct.getIsIslamic() != null && hostProduct.getIsFitas() != null) {
//            return new LNTLFAccountType(
//                    hostProduct.getProvider(),
//                    hostProduct.getIsForeign().equalsIgnoreCase("Y"),
//                    hostProduct.getIsIslamic().equalsIgnoreCase("Y"),
//                    hostProduct.getIsFitas().equalsIgnoreCase("Y")
//            );
//        }
//        log.info("Invalid hostProduct dto");
//        return new LNTLFAccountType();
//    }
//
//    public ApiResponse<LNTLFTrancheListDTO> getTranches(LNTLFTranchesListReqDTO request) {
//        log.info("executing getTranches() with param {}", request);
//        ApiResponse<LNTLFTrancheListDTO> apiResponse = new ApiResponse<>();
//
//        // Assume only MY TL has tranches
//        Optional<HostProductDto> hostProductDto = hostProductMatrixRepository.findByAccountNumber(request.getAcctNumber(), request.getAccountCurrency());
//
//        if (hostProductDto.isPresent()) {
//            ESBLoanTLFTranchesReqDTO esbTranchesRequest = modelMapper.map(request, ESBLoanTLFTranchesReqDTO.class);
//            if (hostProductDto.get().getIsIslamic().equalsIgnoreCase("Y")) {
//                log.info("islamic");
//                apiResponse = lntlfesbService.getLoanTLFTranchesIslamic(esbTranchesRequest);
//            } else {
//                log.info("conventional");
//                apiResponse = lntlfesbService.getLoanTLFTranchesConv(esbTranchesRequest);
//            }
//        } else {
//            apiResponse.setMsgHeader(new ErrorResponse("0"));
//            apiResponse.setMsgBody(new LNTLFTrancheListDTO());
//        }
//        return apiResponse;
//    }
//
//    public ApiResponse<?> getTLFTranchesDetail(LNTLFTranchesDetailReqDTO request) {
//        log.info("getTLFTranchesDetail");
//        //real time only
//        ApiResponse<?> apiResponse = new ApiResponse<>();
//
//        // Assume only MY TL has tranches
//        Optional<HostProductDto> hostProductDto = hostProductMatrixRepository.findByAccountNumber(request.getAcctNumber(), request.getCurrency());
//        if (hostProductDto.isPresent()) {
//            HostProductDto hostProduct = hostProductDto.get();
//            LNTLFAccountType accountType = retreiveTLFAccountType(hostProduct);
//
//            log.info("account type: {}", accountType);
//            if (accountType.isForeign()) {
//                if (accountType.isIslamic()) {
//                    apiResponse = lntlfesbService.getLoanTLFTrancheIslamicDetail(request);
//                } else {
//                    apiResponse = lntlfesbService.getLoanTLFTrancheConvDetail(request);
//                }
//            }
//        } else {
//            log.error("Host Product not found for account {} {}", request.getAcctNumber(), request.getCurrency());
//            apiResponse.setMsgHeader(new ErrorResponse("0"));
//            apiResponse.setMsgBody(null);
//        }
//
//        return apiResponse;
//    }
//
//    public ApiResponse<LNTLFTrxHistoryResponseDTO> getTrxHistory(LNTrxHistoryReqDTO request, String auth) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        LNTLFTrxHistoryResponseDTO trxHistory = new LNTLFTrxHistoryResponseDTO();
//        ApiResponse<LNTLFTrxHistoryResponseDTO> trxHistoryResponse = new ApiResponse<>();
//
//        if ("SG".equals(request.getAccountTypeIndicators().getProvider())) {
//            // T-1 case
//            LNTLFTrxHistorySpParam lntlfTrxHistorySpParam = getTrxHistorySpParam(request, userAndCorp);
//            trxHistory.setDatas(getTrxHistoryWithSp(lntlfTrxHistorySpParam));
//        } else {
//            // T case
//            ApiResponse<LNTLFTrxHistoryResponseDTO> realtimeTrxHistory = getTrxHistoryFromESB(request);
//
//            LNTLFTrxHistorySpParam lntlfTrxHistorySpParam = getTrxHistorySpParam(request, userAndCorp);
//
//            SearchCollectionResponse<LNTLFTrxHistoryDTO> spResult;
//            if (realtimeTrxHistory.getMsgBody()!=null && realtimeTrxHistory.getMsgBody().getDatas()!=null && !realtimeTrxHistory.getMsgBody().getDatas().getData().isEmpty()){
//                SearchCollectionResponse<LNTLFTrxHistoryDTO> realtimeDataList = realtimeTrxHistory.getMsgBody().getDatas();
//                if (realtimeDataList.getData().size() < request.getPageSize()) {
//                    log.info("ESB Data size < request page size");
//                    spResult = getTrxHistoryWithSp(lntlfTrxHistorySpParam);
//                    trxHistory = combineEsbAndSpTrxHistory(realtimeTrxHistory.getMsgBody(), spResult, request);
//                } else {
//                    log.info("ESB Data size == request page size");
//                    trxHistory.setDatas(realtimeDataList);
//                    trxHistory.setEsbMetadata(realtimeTrxHistory.getMsgBody().getEsbMetadata());
//                }
//            } else {
//                log.info("ESB result is null or empty");
//                trxHistory.setDatas(getTrxHistoryWithSp(lntlfTrxHistorySpParam));
//            }
//        }
//
//        trxHistoryResponse.setMsgHeader(new ErrorResponse("0"));
//        trxHistoryResponse.setMsgBody(trxHistory);
//        return trxHistoryResponse;
//    }
//
//    private ApiResponse<LNTLFTrxHistoryResponseDTO> getTrxHistoryFromESB(LNTrxHistoryReqDTO request) {
//        log.info("getTrxHistoryFromESB()");
//        ApiResponse<LNTLFTrxHistoryResponseDTO> trxHistory = new ApiResponse<>();
//
//        try {
//            switch (request.getProvider()) {
//                case "MY" -> {
//                    if (request.getAccountTypeIndicators().isIslamic()) {
//                        if (request.getAccountTypeIndicators().isForeign()) {
//                            log.info("MY islamic foreign");
//                            trxHistory = lntlfesbService.getLoanTLFTrxHistoryMYForeignIslamic(request);
//                        } else {
//                            log.info("MY islamic");
//                            trxHistory = lntlfesbService.getLoanTLFTrxHistoryMYIslamic(request);
//                        }
//                    } else {
//                        if (request.getAccountTypeIndicators().isForeign()) {
//                            log.info("MY conventional foreign");
//                            trxHistory = lntlfesbService.getLoanTLFTrxHistoryMYForeignConv(request);
//                        } else {
//                            log.info("MY conventional");
//                            trxHistory = lntlfesbService.getLoanTLFTrxHistoryMYConv(request);
//                        }
//                    }
//                }
//                default -> {}
//            }
//        } catch (Exception e) {
//
//        }
//
//        return trxHistory;
//    }
//
//    private LNTLFTrxHistoryResponseDTO combineEsbAndSpTrxHistory(LNTLFTrxHistoryResponseDTO esbResponse, SearchCollectionResponse<LNTLFTrxHistoryDTO> spData, LNTrxHistoryReqDTO request) {
//        List<LNTLFTrxHistoryDTO> esbDataList = esbResponse.getDatas().getData();
//        List<LNTLFTrxHistoryDTO> spDataList = spData.getData();
//
//        LNTLFTrxHistoryResponseDTO combinedTrxHistory = new LNTLFTrxHistoryResponseDTO();
//        int remainingSize = request.getPageSize() - esbDataList.size();
//        log.info("remainingSize --> {}", remainingSize);
//
//        List<LNTLFTrxHistoryDTO> fusedLoanTrxHistoryResList = new ArrayList<>(esbDataList);
//        fusedLoanTrxHistoryResList.addAll(spDataList.subList(0, Math.min(remainingSize, spDataList.size())));
//
//        // total record count of both esb and sp data
//        int esbTotalRecordCount = Optional.of(esbResponse.getDatas())
//                .map(SearchCollectionResponse::getTotalRecordCount)
//                .orElse(0);
//        int spTotalRecordCount = Optional.of(spData)
//                .map(SearchCollectionResponse::getTotalRecordCount)
//                .orElse(0);
//        int totalRecordCount = esbTotalRecordCount + spTotalRecordCount;
//
//        if(spData.getTotalPages() == -1){
//            spData.setTotalPages(0);
//        }
//        int totalPage = (esbResponse.getDatas() != null && esbResponse.getDatas().getTotalPages() != null ? esbResponse.getDatas().getTotalPages() : 0)
//                + spData.getTotalPages();
//
//        // fuse both data from both ESB and sp
//        SearchCollectionResponse<LNTLFTrxHistoryDTO> fusedLoanTLTrxHistoryRes = new SearchCollectionResponse<>();
//        fusedLoanTLTrxHistoryRes.setNextPage(request.getPage() >= totalPage ? totalPage : request.getPage() + 1);
//        fusedLoanTLTrxHistoryRes.setCurrentPage(request.getPage());
//        fusedLoanTLTrxHistoryRes.setTotalPages(totalPage);
//        fusedLoanTLTrxHistoryRes.setTotalRecordCount(totalRecordCount);
//        fusedLoanTLTrxHistoryRes.setData(fusedLoanTrxHistoryResList);
//
//        combinedTrxHistory.setEsbMetadata(esbResponse.getEsbMetadata());
//        combinedTrxHistory.setDatas(fusedLoanTLTrxHistoryRes);
//        return combinedTrxHistory;
//    }
//
//    private LNTLFTrxHistorySpParam getTrxHistorySpParam(LNTrxHistoryReqDTO request, UserAndCorp userAndCorp) {
//        LNTLFTrxHistorySpParam lntlfTrxHistorySpParam = new LNTLFTrxHistorySpParam(request.getAcctNumber(), request.getCurrency(), request.getProvider(), userAndCorp.getCorpId(), userAndCorp.getUserId(), request.getAccountTypeIndicators().isFITAS(), request.getSearchParam(), request.getFilters().getTrxCode());
//        if (!request.getFilters().getAmtFrom().isEmpty() && !request.getFilters().getAmtTo().isEmpty()) {
//            lntlfTrxHistorySpParam.setAmtFrom(new BigDecimal(request.getFilters().getAmtFrom()));
//            lntlfTrxHistorySpParam.setAmtTo(new BigDecimal(request.getFilters().getAmtTo()));
//        }
//        if (StringUtils.hasText(request.getFilters().getStartDate()) && StringUtils.hasText(request.getFilters().getEndDate()) && StringUtils.hasText(request.getFilters().getStartTime()) && StringUtils.hasText(request.getFilters().getEndTime())) {
//            lntlfTrxHistorySpParam.setStartDateTime(DateTimeUtil.convert12HourDateTime(request.getFilters().getStartDate(), request.getFilters().getStartTime(), true));
//            lntlfTrxHistorySpParam.setEndDateTime(DateTimeUtil.convert12HourDateTime(request.getFilters().getEndDate(), request.getFilters().getEndTime(), false));
//        }
//        lntlfTrxHistorySpParam.setPage(request.getPage());
//        lntlfTrxHistorySpParam.setPageSize(request.getPageSize());
//        log.info("lntlfTrxHistorySpParam: {}", lntlfTrxHistorySpParam);
//        return lntlfTrxHistorySpParam;
//    }
//
//    private SearchCollectionResponse<LNTLFTrxHistoryDTO> getTrxHistoryWithSp(
//            LNTLFTrxHistorySpParam lntlfTrxHistorySpParam
//    ) {
//        log.info("Retrieving trx history with stored procedure");
//        SearchCollectionResponse<LNTLFTrxHistoryDTO> result = new SearchCollectionResponse<>();
//
//        BaseSpDto<LNTLFTrxHistorySpParam, LNTLFTrxHistorySpDTO> resultBaseSpDto = lntlfSpService.trxHistories(lntlfTrxHistorySpParam);
//        setPaginationData(
//                lntlfTrxHistorySpParam,
//                result,
//                resultBaseSpDto,
//                resultBaseSpDto.getResults()
//                        .stream()
//                        .map(
//                                spResult -> new LNTLFTrxHistoryDTO(
//                                        spResult.getDate(),
//                                        spResult.getTrxReference(),
//                                        spResult.getTrxDesc(),
//                                        spResult.getDebit(),
//                                        spResult.getCredit(),
//                                        lntlfTrxHistorySpParam.getCurrency()))
//                        .toList());
//        return result;
//    }
//
//    private LNTLFDetailSGConventionalFITASDTO getDetailSGFitasConvWithSp(LNTLFAcctDetailSpParam lntlfTrxHistorySpParam) {
//        log.info("Retrieving SG FITAS Conventional with stored procedure");
//        BaseSpDto<LNTLFAcctDetailSpParam, LNTLFDetailSGConventionalFITASDTO> resultBaseSpDto = lntlfSpService.detailSGFitasConventional(lntlfTrxHistorySpParam);
//        List<LNTLFDetailSGConventionalFITASDTO> spResult = resultBaseSpDto.getResults();
//        if (!spResult.isEmpty()) {
//            return spResult.get(0);
//        } else {
//            return new LNTLFDetailSGConventionalFITASDTO();
//        }
//    }
//
//    private LNTLFDetailSGIslamicFITASDTO getDetailSGFitasIslamicWithSp(LNTLFAcctDetailSpParam lntlfTrxHistorySpParam) {
//        log.info("Retrieving SG FITAS Islamic with stored procedure");
//        BaseSpDto<LNTLFAcctDetailSpParam, LNTLFDetailSGIslamicFITASDTO> resultBaseSpDto = lntlfSpService.detailSGFitasIslamic(lntlfTrxHistorySpParam);
//        List<LNTLFDetailSGIslamicFITASDTO> spResult = resultBaseSpDto.getResults();
//        if (!spResult.isEmpty()) {
//            return spResult.get(0);
//        } else {
//            return new LNTLFDetailSGIslamicFITASDTO();
//        }
//    }
//
//    private String getRedisKey(UserAndCorp userAndCorp, LNAcctListReq reqDTO) {
//        if (reqDTO.isRealTimeIndicator()) {
//            return redisHelper.createCacheKey(BD_AM_LN_TLF_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "ONL" + "_" + reqDTO.getCorpCifNo() + "_" +reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
//        } else {
//            return redisHelper.createCacheKey(BD_AM_LN_TLF_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + "_" + reqDTO.getCorpCifNo() + "_" +reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
//        }
//    }
//
//    private ApiResponse<LNTLFAcctListingDTO> getAcctFromCache(UserAndCorp userAndCorp, LNAcctListReq reqDTO) {
//        return (ApiResponse<LNTLFAcctListingDTO>) redisHelper.getFromCache(getRedisKey(userAndCorp, reqDTO), ApiResponse.class);
//    }
//
//    public void removeAllFromCache(UserAndCorp userAndCorp){
//        String redisKey = redisHelper.createCacheKey(BD_AM_LN_TLF_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_*");
//        redisHelper.deleteCachesWithPattern(redisKey);
//        log.info("removed cache for {}", redisKey);
//    }
//}