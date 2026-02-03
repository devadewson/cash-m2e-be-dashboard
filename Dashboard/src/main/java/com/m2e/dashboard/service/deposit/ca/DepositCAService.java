package com.m2e.dashboard.service.deposit.ca;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.dto.MoreActionsRedirection;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.deposit.ca.request.*;
import com.m2e.dashboard.dto.deposit.ca.request.download.*;
import com.m2e.dashboard.dto.deposit.ca.response.*;
import com.m2e.dashboard.enums.*;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.activityinquiry.ESBDepositCAAcctActivityReqDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctDetailReqDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctListReqDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.details.ESBDepositCAFloatReqDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDetailDTO;
import com.m2e.dashboard.projection.CorpDetailProjection;
import com.m2e.dashboard.projection.DepositAcctDetailPageProjection;
import com.m2e.dashboard.projection.DepositCASwiftMT103Projection;
import com.m2e.dashboard.repository.*;
import com.m2e.dashboard.service.AsyncPaginationService;
import com.m2e.dashboard.service.FileRepoService;
import com.m2e.dashboard.service.deposit.ca.jms.DepositCAESBService;
import com.m2e.dashboard.service.deposit.ca.sp.DepositCASPService;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import com.m2e.dashboard.util.BlobUtil;
import com.m2e.dashboard.util.DateTimeUtil;
import com.m2e.dashboard.util.UtilityFunctions;
import com.m2e.model.sso.base.AccountLinkOmnibusDeposit;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.m2e.dashboard.enums.AcctProductEntitlementConst.*;
import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_DP_CA_ACCTLIST;
import static com.m2e.dashboard.enums.ReportCommonConstant.*;

@Service
public class DepositCAService {

    private static final Logger logger = LoggerFactory.getLogger(DepositCAService.class);
    private final int batchSize = 10;

    @Autowired
    DepositCAFileService fileService;

    @Autowired
    DepositCAEODBalanceFileService depositCAEODBalanceFileService;

    @Autowired
    DepositCATrxHistFileService depositCATrxHistFileService;

    @Autowired
    DepositCAPaymentHistFileService depositCAPaymentHistFileService;

    @Autowired
    DepositCAGPIStatusFileService depositCAGPIStatusFileService;

    @Autowired
    DepositCAPaymentDetailFileService depositCAPaymentDetailFileService;

    @Autowired
    DepositCAMT130FileService depositCAMT130FileService;

    @Autowired
    private DepositCAESBService depositCAESBService;

    @Autowired
    private DepositCASPService depositCASPService;

    @Autowired
    private AsyncPaginationService asyncPaginationService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountLinkOmnibusDepositRepository accountLinkOmnibusDepositRepository;

    @Autowired
    private CifRepository cifRepository;

    @Autowired
    private SwiftMT103Repository swiftMT103Repository;

    @Autowired
    private CtsReportRepository ctsReportRepository;

    @Autowired
    private CorpRepository corpRepository;

    @Autowired
    private FileRepoService fileRepoService;

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private UtilityFunctions utilityFunctions;

    public final static String SWIFT_MT103_FILE_TYPE = "MT103";
    public final static String DEFAULT_TRX_HIST_TIME_VALUE = "-";

    public ApiResponse<SearchCollectionResponse<DepositCAEODBalanceDTO>> getDepositAccountEODBalance(UserAndCorp userAndCorp, DepositCAEODBalanceReqDTO request) {
        SearchCollectionResponse<DepositCAEODBalanceDTO> result = depositCASPService.getDepositAccountEODBalanceSP(userAndCorp, request);

        ApiResponse<SearchCollectionResponse<DepositCAEODBalanceDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(result);
        return apiResponse;
    }

    public ApiResponse<DepositAcctStatsDTO> getDepositCAAccountStats(UserAndCorp userAndCorp, DepositAcctStatsReqDTO depositAcctStatsReqDTO) {
        ApiResponse<DepositAcctStatsDTO> apiResponse = new ApiResponse<>();
        DepositAcctStatsDTO depositAcctStatsDTO = new DepositAcctStatsDTO();
        //remove cache for account balances
        removeAllFromCache(userAndCorp);

        if (depositAcctStatsReqDTO.getRealTimeIndicator()) {
            ESBMetadataDTO esbMetadataDTO = new ESBMetadataDTO();
            AccountListReq req = new AccountListReq();
            req.setPage(1);
            req.setPageSize(Integer.MAX_VALUE);
            DepositAcctListingDTO spResult = depositCASPService.getDepositCAAccountListSp(userAndCorp, req);
            spResult.getDatas().getData().forEach(dto->{
                dto.setMoreActions(getMoreActions(dto, userAndCorp));
            });
            //call ESB to calculate consolidated balances for accounts across all corps if its equal or less than 20
            if (!spResult.getDatas().getData().isEmpty() && spResult.getDatas().getTotalRecordCount() <= utilityFunctions.getMaxAcctCount()) {
                Map<String, List<DepositAcctSummarySpDTO>> classifiedByProvider =
                        Optional.of(spResult.getDatas())
                                .map(SearchCollectionResponse::getData)
                                .orElseGet(List::of)
                                .stream()
                                .collect(Collectors.groupingBy(DepositAcctSummarySpDTO::getProvider));
                logger.info("deposit CA stats classifiedByProvider -> {} ", classifiedByProvider);

                Map<String, Object> esbResults = new HashMap<>();

                classifiedByProvider.entrySet().stream()
                        .filter(entry -> List.of(ProviderEnum.MY.name(), ProviderEnum.SG.name()).contains(entry.getKey()))
                        .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                        .forEach(entry -> {
                            String provider = entry.getKey();
                            List<DepositAcctSummarySpDTO> accounts = entry.getValue();

                            List<List<DepositAcctSummarySpDTO>> batches = IntStream.range(0, (accounts.size() + 9) / 10)
                                    .mapToObj(i -> accounts.subList(i * 10, Math.min((i + 1) * 10, accounts.size())))
                                    .filter(batch -> !batch.isEmpty())
                                    .toList();

                            List<DepositAcctStatsDTO> batchStats = new ArrayList<>();

                            for (List<DepositAcctSummarySpDTO> batch : batches) {
                                ESBDepositCAAcctListReqDTO reqDTO = new ESBDepositCAAcctListReqDTO();
                                reqDTO.setCorpCifNo(spResult.getCorpCifNo());
                                reqDTO.setCorpProvider(provider);
                                reqDTO.setUserAndCorp(userAndCorp);

                                SearchCollectionResponse<DepositAcctSummarySpDTO> dataWrapper = new SearchCollectionResponse<>();
                                dataWrapper.setData(batch);
                                reqDTO.setDatas(dataWrapper);

                                Object result = switch (provider) {
                                    case "MY" -> depositCAESBService.getAccountStats_MY(reqDTO).getMsgBody();
                                    case "SG" -> depositCAESBService.getAccountStats_SG(reqDTO).getMsgBody();
                                    default -> null;
                                };

                                if (result instanceof DepositAcctStatsDTO dto) {
                                    batchStats.add(dto);
                                }
                            }

                            DepositAcctStatsDTO merged = mergeStats(batchStats);
                            esbResults.put(provider, merged);
                        });

                DecimalFormat formatter = new DecimalFormat("#,##0.00");
                String mergedTotalLedgerBal = formatter.format(
                        esbResults.values().stream()
                                .filter(Objects::nonNull)
                                .map(DepositAcctStatsDTO.class::cast)
                                .map(DepositAcctStatsDTO::getTotalLedgerBal)
                                .filter(Objects::nonNull)
                                .map(bal -> bal.replaceAll(",", "")) // cleanup commas
                                .mapToDouble(bal -> {
                                    try {
                                        return Double.parseDouble(bal);
                                    } catch (NumberFormatException e) {
                                        return 0.00;
                                    }
                                })
                                .sum()
                );

                String mergedTotalAvailableBal = formatter.format(
                        esbResults.values().stream()
                                .filter(Objects::nonNull)
                                .map(DepositAcctStatsDTO.class::cast)
                                .map(DepositAcctStatsDTO::getTotalAvailableBal)
                                .filter(Objects::nonNull)
                                .map(bal -> bal.replaceAll(",", "")) // cleanup commas
                                .mapToDouble(bal -> {
                                    try {
                                        return Double.parseDouble(bal);
                                    } catch (NumberFormatException e) {
                                        return 0.00;
                                    }
                                })
                                .sum()
                );

                //set the total balances here after sum from MY and SG
                depositAcctStatsDTO.setTotalLedgerBal(mergedTotalLedgerBal);
                depositAcctStatsDTO.setTotalAvailableBal(mergedTotalAvailableBal);
                esbMetadataDTO = esbResults.values().stream()
                        .filter(Objects::nonNull)
                        .map(DepositAcctStatsDTO.class::cast)
                        .map(DepositAcctStatsDTO::getEsbMetadata)
                        .filter(Objects::nonNull)
                        .reduce((first, second) -> second)
                        .orElse(null);
                depositAcctStatsDTO.setEsbMetadata(esbMetadataDTO);
            } else {
                depositAcctStatsDTO = depositCASPService.getDepositCAAccountStatsSp(userAndCorp);
            }
            depositAcctStatsDTO.setNumberOfAcct(String.valueOf(spResult.getDatas().getTotalRecordCount()));
            //set today date time if realtime indicator is true
            depositAcctStatsDTO.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
            depositAcctStatsDTO.setLastUpdatedDate(DateTimeUtil.getTodayDate());
        } else {
             depositAcctStatsDTO = depositCASPService.getDepositCAAccountStatsSp(userAndCorp);
        }
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(depositAcctStatsDTO);
        return apiResponse;
    }

    private DepositAcctStatsDTO mergeStats(List<DepositAcctStatsDTO> statsList) {
        DepositAcctStatsDTO merged = new DepositAcctStatsDTO();
        DecimalFormat formatter = new DecimalFormat("#,##0.00");

        double totalLedger = statsList.stream()
                .map(DepositAcctStatsDTO::getTotalLedgerBal)
                .filter(Objects::nonNull)
                .mapToDouble(val -> {
                    try {
                        return Double.parseDouble(val);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }).sum();

        double totalAvailable = statsList.stream()
                .map(DepositAcctStatsDTO::getTotalAvailableBal)
                .filter(Objects::nonNull)
                .mapToDouble(val -> {
                    try {
                        return Double.parseDouble(val);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }).sum();

        merged.setTotalLedgerBal(formatter.format(totalLedger));
        merged.setTotalAvailableBal(formatter.format(totalAvailable));

        ESBMetadataDTO metadata = statsList.stream()
                .map(DepositAcctStatsDTO::getEsbMetadata)
                .filter(Objects::nonNull)
                .reduce((first, second) -> second)
                .orElse(null);
        merged.setEsbMetadata(metadata);

        return merged;
    }

    public ApiResponse<DepositAcctListingDTO> getDepositCAAccountList(UserAndCorp userAndCorp, AccountListReq req) {
        ApiResponse<DepositAcctListingDTO> apiResponse = new ApiResponse<>();
        DepositAcctListingDTO spResult = depositCASPService.getDepositCAAccountListSp(userAndCorp, req);
        ESBMetadataDTO esbMetadataDTO = new ESBMetadataDTO();
        //get acct balances from cache
        ApiResponse<DepositAcctListingDTO> finalResp = getAcctFromCache(userAndCorp, req);

        if (finalResp == null) {
            if (req.getRealTimeIndicator() && !spResult.getDatas().getData().isEmpty()) {
                List<DepositAcctSummarySpDTO> t1Data = getInitialData(spResult.getDatas());
                logger.info("Deposit CA inquiry t1Data: {}", t1Data);

                List<String> providedProviders = getProvidedProviders(t1Data);
                if (!providedProviders.isEmpty()) {
                    spResult = processProviders(providedProviders, spResult, userAndCorp);
                    spResult.getDatas().getData().forEach(dto -> {
                        boolean isClosed = dto.getAcctStatusDesc() != null && (dto.getAcctStatusDesc().equalsIgnoreCase(AcctStatusEnum.CLOSED.name()) || dto.getAcctStatusDesc().equalsIgnoreCase(AcctStatusEnum.INACTIVE.name()));
                        dto.setLedgerBalance(isClosed ? null : dto.getLedgerBalance());
                        dto.setAvailableBalance(isClosed ? null : dto.getAvailableBalance());
                        dto.setMoreActions(getMoreActions(dto, userAndCorp));
                    });
                    apiResponse.setMsgHeader(new ErrorResponse("0"));
                    apiResponse.setMsgBody(spResult);
                } else {
                    logger.info("No providers or sub-products available to process for Corporate CIF No {}", req.getCorpCifNo());
                }
            } else {
                spResult.getDatas().getData().forEach(dto -> {
                    dto.setMoreActions(getMoreActions(dto, userAndCorp));
                });
                apiResponse.setMsgHeader(new ErrorResponse("0"));
                apiResponse.setMsgBody(spResult);
            }

            finalResp = convertApiResponse(apiResponse, req.getCorpCifNo(), esbMetadataDTO);
            redisHelper.save(getRedisKey(userAndCorp, req), finalResp);
        }
        return finalResp;
    }

    private List<DepositAcctSummarySpDTO> getInitialData(SearchCollectionResponse<DepositAcctSummarySpDTO> spResult) {
        return Optional.ofNullable(spResult.getData()).orElseGet(ArrayList::new);
    }

    private List<String> getProvidedProviders(List<DepositAcctSummarySpDTO> t1Data) {
        return Optional.ofNullable(getProviders(t1Data)).orElseGet(ArrayList::new);
    }

    private DepositAcctListingDTO processProviders(List<String> providedProviders,
                                                   DepositAcctListingDTO t1Data, UserAndCorp userAndCorp) {
        DepositAcctListingDTO aggregatedResult = new DepositAcctListingDTO();
        List<DepositAcctSummarySpDTO> allData = new ArrayList<>();
        ESBMetadataDTO finalMetadata = new ESBMetadataDTO();

        String corpCifNo = t1Data.getCorpCifNo();
        aggregatedResult.setCorpCifNo(corpCifNo);

        for (String provider : providedProviders) {
            DepositAcctListingDTO providerResult = processByProvider(t1Data, userAndCorp, provider);
            if (providerResult != null && providerResult.getDatas() != null && providerResult.getDatas().getData() != null) {
                allData.addAll(providerResult.getDatas().getData());
            }
            // Optionally merge metadata if needed
            finalMetadata = providerResult.getEsbMetadata(); // or merge intelligently if multiple
        }

        SearchCollectionResponse<DepositAcctSummarySpDTO> mergedData = new SearchCollectionResponse<>();
        mergedData.setData(allData);
        mergedData.setCurrentPage(t1Data.getDatas().getCurrentPage());
        mergedData.setNextPage(t1Data.getDatas().getNextPage());
        mergedData.setTotalRecordCount(t1Data.getDatas().getTotalRecordCount());
        mergedData.setTotalPages(t1Data.getDatas().getTotalPages());

        aggregatedResult.setDatas(mergedData);
        aggregatedResult.setEsbMetadata(finalMetadata);

        return aggregatedResult;
    }


    private List<DepositAcctSummarySpDTO> filterByProvider(List<DepositAcctSummarySpDTO> data, String provider) {
        return Optional.ofNullable(data)
                .orElseGet(List::of)
                .stream()
                .filter(dto -> provider.equals(dto.getProvider()))
                .toList();
    }

    private List<String> getProviders(List<DepositAcctSummarySpDTO> data) {
        return Optional.ofNullable(data)
                .orElseGet(List::of)
                .stream()
                .map(DepositAcctSummarySpDTO::getProvider)
                .filter(provider -> provider != null && !provider.isEmpty())
                .distinct()
                .toList();
    }

    private DepositAcctListingDTO processByProvider(DepositAcctListingDTO t1Data, UserAndCorp userAndCorp, String provider) {
        DepositAcctListingDTO tData = new DepositAcctListingDTO();
        String corpCifNo = t1Data.getCorpCifNo();
        List<DepositAcctSummarySpDTO> filteredData = filterByProvider(t1Data.getDatas().getData(), provider);

        AtomicReference<DepositAcctListingDTO> tmp = new AtomicReference<>(new DepositAcctListingDTO());
        List<DepositAcctSummarySpDTO> tempList = new ArrayList<>();

        IntStream.range(0, (filteredData.size() + batchSize - 1) / batchSize)
                .mapToObj(i -> filteredData.subList(i * batchSize, Math.min((i + 1) * batchSize, filteredData.size())))
                .forEach(batch -> {
                    tmp.set(fetchDataForProvider(batch, userAndCorp, corpCifNo, provider));
                    tempList.addAll((tmp.get().getDatas().getData()));
                });
        SearchCollectionResponse<DepositAcctSummarySpDTO> dataESB = new SearchCollectionResponse<>();
        dataESB.setData(tempList);
        dataESB.setCurrentPage(t1Data.getDatas().getCurrentPage());
        dataESB.setNextPage(t1Data.getDatas().getNextPage());
        dataESB.setTotalRecordCount(t1Data.getDatas().getTotalRecordCount());
        dataESB.setTotalPages(t1Data.getDatas().getTotalPages());

        tData.setDatas(dataESB);
        tData.setEsbMetadata(tmp.get().getEsbMetadata());
        return tData;
    }

    private DepositAcctListingDTO fetchDataForProvider(List<DepositAcctSummarySpDTO> filteredData, UserAndCorp userAndCorp, String corpCifNo, String provider) {
        DepositAcctListingDTO tData = new DepositAcctListingDTO();
        SearchCollectionResponse<DepositAcctSummarySpDTO> dataESB = new SearchCollectionResponse<>();
        ESBMetadataDTO esbMetadataDTO = new ESBMetadataDTO();
        dataESB.setData(filteredData);
        tData.setDatas(dataESB);
        tData.setEsbMetadata(esbMetadataDTO);
        tData.setCorpCifNo(corpCifNo);
        logger.info("tData CA before call ESB: {}", tData);
        ApiResponse<DepositAcctListingDTO> esbResult = new ApiResponse<>();

        ESBDepositCAAcctListReqDTO esbDepositCAAcctListReqDTO = new ESBDepositCAAcctListReqDTO();
        esbDepositCAAcctListReqDTO.setDatas(dataESB);
        esbDepositCAAcctListReqDTO.setCorpCifNo(corpCifNo);
        esbDepositCAAcctListReqDTO.setUserAndCorp(userAndCorp);
        esbDepositCAAcctListReqDTO.setCorpProvider(provider);
        if (ProviderEnum.MY.name().equals(provider)) {
            esbResult = depositCAESBService.getAccountBalanceInquiry_MY(esbDepositCAAcctListReqDTO);
        } else {
            esbResult = depositCAESBService.getAccountBalanceInquiry_SG(esbDepositCAAcctListReqDTO);
        }
        logger.info("after hitting ESB CA: {}", esbResult);
        if (esbResult != null && esbResult.getMsgBody() != null && esbResult.getMsgBody().getDatas() != null) {
            tData = esbResult.getMsgBody();
        }
        logger.info("tData CA after calling ESB: {}", tData);
        return tData;
    }

    private ApiResponse<DepositAcctListingDTO> convertApiResponse(
            ApiResponse<DepositAcctListingDTO> apiResponse,
            String corpCifNo, ESBMetadataDTO esbMetadataDTO) {

        ApiResponse<DepositAcctListingDTO> apiResponseNew = new ApiResponse<>();

        apiResponseNew.setMsgHeader(apiResponse.getMsgHeader());

        DepositAcctListingDTO summaryResp = new DepositAcctListingDTO();
        summaryResp.setCorpCifNo(corpCifNo);
        summaryResp.setDatas(apiResponse.getMsgBody().getDatas());
        summaryResp.setEsbMetadata(esbMetadataDTO);

        apiResponseNew.setMsgBody(summaryResp);

        return apiResponseNew;
    }

    private MoreActionsEntitlement getMoreActions(DepositAcctSummarySpDTO dto, UserAndCorp userAndCorp){
        String makePaymentRedisKey = redisHelper.buildCacheKey(userAndCorp.getUserId(), userAndCorp.getCorpId(), USER_ACCT_PRODUCT_LIST_REDIS_KEY, PAYMENT_PRODUCT_CODE, dto.getAcctNumber(), dto.getCurrency());
        String makeBillPaymentRedisKey = redisHelper.buildCacheKey(userAndCorp.getUserId(), userAndCorp.getCorpId(), USER_ACCT_PRODUCT_LIST_REDIS_KEY, JOMPAY_BILL_PAYMENT_PRODUCT_CODE, dto.getAcctNumber(), dto.getCurrency());
        logger.debug("getMoreActions() makePaymentRedisKey Redis key: {}", makePaymentRedisKey);
        logger.debug("getMoreActions() makeBillPaymentRedisKey Redis key: {}", makeBillPaymentRedisKey);
        MoreActionsEntitlement moreActionsEntitlement = new MoreActionsEntitlement();
        moreActionsEntitlement.setMakePayment(new MoreActionsRedirection(redisHelper.isPresent(makePaymentRedisKey), RedirectModuleEnum.PAYMENT_ACTION_REDIRECT.name(), Application.CASHLITE.getValue(), PAYMENT_PRODUCT_CODE));
        moreActionsEntitlement.setMakeBillPayment(new MoreActionsRedirection(redisHelper.isPresent(makeBillPaymentRedisKey), RedirectModuleEnum.PAYMENT_ACTION_REDIRECT.name(), Application.CASHLITE.getValue(), JOMPAY_BILL_PAYMENT_PRODUCT_CODE));
        return moreActionsEntitlement;

    }

    public DownloadDTO downloadCADeposit(UserAndCorp userAndCorp, DownloadReq req){

        return switch (req.getReportType()) {
            case DEPOSIT_CA_ACCOUNTLISTING_RPT_TYPE -> downloadAcctList(userAndCorp, req);
            case DEPOSIT_CA_TRX_HISTORY_RPT_TYPE -> downloadTrxHist(userAndCorp, req);
            case DEPOSIT_CA_PAYMENT_HISTORY_RPT_TYPE -> downloadPaymentHistory(userAndCorp, req);
            case DEPOSIT_CA_PAYMENT_DETAIL_RPT_TYPE -> downloadPaymentDetail(userAndCorp, req);
            case DEPOSIT_CA_EOD_BALANCE_RPT_TYPE -> downloadEODBalance(userAndCorp, req);
            case DEPOSIT_CA_SWIFT_MT103_RPT_TYPE -> downloadMT103(req);
            case DEPOSIT_CA_GPI_STATUS_RPT_TYPE -> downloadGPIStatus(req);
            default -> throw new IllegalArgumentException("Report type is undefined");
        };
    }

    private DownloadDTO downloadAcctList(UserAndCorp userAndCorp, DownloadReq downloadReq) {
        DownloadDTO result = new DownloadDTO();
        DepositCAAcctListDownloadReqDTO req = (DepositCAAcctListDownloadReqDTO) downloadReq;
        List<DepositAcctSummarySpDTO> dataList = new ArrayList<>();
        AccountListReq accountListReq = new AccountListReq();
        accountListReq.setPage(1);
        accountListReq.setPageSize(Integer.MAX_VALUE);
        accountListReq.setCorpCifNo(req.getCorpCifNo());
        accountListReq.setSearchParam(req.getSearchParam());
        DepositAcctListingDTO spResult = depositCASPService.getDepositCAAccountListSp(userAndCorp, accountListReq);

        if (req.getRealTimeIndicator() && !spResult.getDatas().getData().isEmpty()) {
            List<DepositAcctSummarySpDTO> t1Data = getInitialData(spResult.getDatas());
            logger.info("Deposit CA inquiry t1Data: {}", t1Data);

            List<String> providedProviders = getProvidedProviders(t1Data);
            if (!providedProviders.isEmpty()) {
                spResult = processProviders(providedProviders, spResult, userAndCorp);
                spResult.getDatas().getData().forEach(dto->{
                    boolean isClosed = dto.getAcctStatusDesc() != null && (dto.getAcctStatusDesc().equalsIgnoreCase(AcctStatusEnum.CLOSED.name()) || dto.getAcctStatusDesc().equalsIgnoreCase(AcctStatusEnum.INACTIVE.name()));
                    dto.setLedgerBalance(isClosed ? null : dto.getLedgerBalance());
                    dto.setAvailableBalance(isClosed ? null : dto.getAvailableBalance());
                    dto.setMoreActions(getMoreActions(dto, userAndCorp));
                });
            } else {
                logger.info("No providers or sub-products available to process for Corporate CIF No {}", req.getCorpCifNo());
            }
        } else {
            spResult.getDatas().getData().forEach(dto->{
                dto.setMoreActions(getMoreActions(dto, userAndCorp));
            });
        }
        dataList = spResult.getDatas().getData();
        switch (req.getFormat()) {
            case CSV:
                result.setData(fileService.generateCsv(dataList));
                result.setFileName(DownloadEnum.DEPOSIT_CA_ACCTLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
                break;
            case PDF:
                result.setData(fileService.generatePdf(dataList));
                result.setFileName(DownloadEnum.DEPOSIT_CA_ACCTLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
                break;
            case TXT:
                result.setData(fileService.generateTxt(dataList));
                result.setFileName(DownloadEnum.DEPOSIT_CA_ACCTLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    private DownloadDTO downloadTrxHist(UserAndCorp userAndCorp, DownloadReq downloadReq) {
        DownloadDTO result = new DownloadDTO();
        DepositCATrxHistDownloadReqDTO req = (DepositCATrxHistDownloadReqDTO) downloadReq;

        DepositTrxHistoryReqDTO depositTrxHistoryReqDTO = new DepositTrxHistoryReqDTO();
        depositTrxHistoryReqDTO.setTrxCode(req.getTrxCode());
        depositTrxHistoryReqDTO.setAcctNumber(req.getAcctNumber());
        depositTrxHistoryReqDTO.setCurrency(req.getCurrency());
        depositTrxHistoryReqDTO.setFilters(req.getFilters());
        depositTrxHistoryReqDTO.setSearchParam(req.getSearchParam());
        depositTrxHistoryReqDTO.setPage(1);
        depositTrxHistoryReqDTO.setPageSize(Integer.MAX_VALUE);

        List<DepositTrxHistoryDTO> fusedData = new ArrayList<>();
        DepositCATrxHistoryDTO depositCATrxHistoryDTO = new DepositCATrxHistoryDTO();
        //get account currency and provider to pass to ESB
        Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetails(depositTrxHistoryReqDTO.getAcctNumber(), depositTrxHistoryReqDTO.getCurrency());
        //check if account exists or not
        if (accountData.isEmpty()) {
            logger.error("Account Not Found for: " + depositTrxHistoryReqDTO.getAcctNumber());
            return result;
        }

        ESBDepositCAAcctActivityReqDTO esbDepositCAAcctActivityReqDTO = new ESBDepositCAAcctActivityReqDTO();
        esbDepositCAAcctActivityReqDTO.setAccountNo(depositTrxHistoryReqDTO.getAcctNumber());
        esbDepositCAAcctActivityReqDTO.setTrxCode(depositTrxHistoryReqDTO.getTrxCode());
        esbDepositCAAcctActivityReqDTO.setFilters(depositTrxHistoryReqDTO.getFilters());
        esbDepositCAAcctActivityReqDTO.setCurrency(depositTrxHistoryReqDTO.getCurrency());
        esbDepositCAAcctActivityReqDTO.setSearchParam(depositTrxHistoryReqDTO.getSearchParam());
        esbDepositCAAcctActivityReqDTO.setPage(1);
        esbDepositCAAcctActivityReqDTO.setPageSize(Integer.MAX_VALUE);

        // get trx hist data from ESB
        try {
            switch (accountData.get().getProvider()) {
                case "MY" -> {
                    depositCATrxHistoryDTO = depositCAESBService.getDepositCATrxHistory_MY(esbDepositCAAcctActivityReqDTO).getMsgBody();
                }
                case "SG" -> {
                    depositCATrxHistoryDTO = depositCAESBService.getDepositCATrxHistory_SG(esbDepositCAAcctActivityReqDTO).getMsgBody();
                }
            }
            depositCATrxHistoryDTO = depositCATrxHistoryDTO != null ? depositCATrxHistoryDTO : new DepositCATrxHistoryDTO();
        } catch (Exception e) {
            depositCATrxHistoryDTO = depositCATrxHistoryDTO != null ? depositCATrxHistoryDTO : new DepositCATrxHistoryDTO();
            depositCATrxHistoryDTO.setDatas(new SearchCollectionResponse<>());
        }

        // get trx hist data from SP
        SearchCollectionResponse<DepositTrxHistoryDTO> spDepositTrxHistoryResList = Optional.ofNullable(depositCASPService.getDepositCATrxHistorySp(userAndCorp, depositTrxHistoryReqDTO))
                .orElse(new SearchCollectionResponse<>());

        List<DepositTrxHistoryDTO> esbDataList = Optional.ofNullable(depositCATrxHistoryDTO.getDatas()).map(SearchCollectionResponse::getData).orElse(new ArrayList<>());
        List<DepositTrxHistoryDTO> spDataList = Optional.of(spDepositTrxHistoryResList).map(SearchCollectionResponse::getData).orElse(new ArrayList<>());

        // if esb data is fetched only, then return it to front end with proper pagination
        if (!esbDataList.isEmpty() && spDataList.isEmpty()) {
            fusedData = sortAndFormat(esbDataList);
        } else if (!esbDataList.isEmpty() && !spDataList.isEmpty()) { // else combine both esb and sp data, with esb comes first
            List<DepositTrxHistoryDTO> fusedDepositTrxHistoryDTOList = new ArrayList<>(esbDataList);
            fusedDepositTrxHistoryDTOList.addAll(spDataList);
            //sort by desc date after combining both esb and sp data
            List<DepositTrxHistoryDTO> sortedByDate = sortAndFormat(fusedDepositTrxHistoryDTOList);
            //paginate after sorting
            fusedData = asyncPaginationService.paginateData(
                    sortedByDate,
                    depositTrxHistoryReqDTO.getPageSize(),
                    depositTrxHistoryReqDTO.getPage()
            );
        } else { //else only return spDataList
            fusedData = sortAndFormat(spDataList);
        }

        switch (req.getFormat()) {
        case CSV:
            result.setData(depositCATrxHistFileService.generateCsv(fusedData, req.getHeaders()));
            result.setFileName(DownloadEnum.DEPOSIT_CA_TRX_HISTORY_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
            break;
        case PDF:
            result.setData(depositCATrxHistFileService.generatePdf(fusedData, req.getHeaders()));
            result.setFileName(DownloadEnum.DEPOSIT_CA_TRX_HISTORY_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
            break;
        case TXT:
            result.setData(depositCATrxHistFileService.generateTxt(fusedData, req.getHeaders()));
            result.setFileName(DownloadEnum.DEPOSIT_CA_TRX_HISTORY_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
            break;
        default:
            throw new IllegalArgumentException("Report format is undefined");
        }

        //insert into file repo once file is generated
        fileRepoService.insertFileRepo(downloadReq, userAndCorp, result, accountData.get(), depositTrxHistoryReqDTO.getAcctNumber(), depositTrxHistoryReqDTO.getCurrency(),
                Product.DEPOSIT, ReportTransactionTypeEnum.TRANSACTION_HISTORY, 100);

        return result;
    }

    private DownloadDTO downloadPaymentHistory(UserAndCorp userAndCorp, DownloadReq downloadReq) {
        DownloadDTO result = new DownloadDTO();

        DepositCAPaymentHistDownloadReqDTO req = (DepositCAPaymentHistDownloadReqDTO) downloadReq;

        DepositPaymentHistoryReqDTO depositPaymentHistoryReqDTO = new DepositPaymentHistoryReqDTO();
        depositPaymentHistoryReqDTO.setAcctNumber(req.getAcctNumber());
        depositPaymentHistoryReqDTO.setCurrency(req.getCurrency());
        depositPaymentHistoryReqDTO.setPaymentType(req.getPaymentType());
        depositPaymentHistoryReqDTO.setTrxType(req.getTrxType());
        depositPaymentHistoryReqDTO.setStartDate(req.getStartDate());
        depositPaymentHistoryReqDTO.setEndDate(req.getEndDate());
        depositPaymentHistoryReqDTO.setPage(1);
        depositPaymentHistoryReqDTO.setPageSize(Integer.MAX_VALUE);

        //check if account exists or not
        Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetails(depositPaymentHistoryReqDTO.getAcctNumber(), depositPaymentHistoryReqDTO.getCurrency());
        if (accountData.isEmpty()) {
            logger.error("Account Not Found for: " + req.getAcctNumber());
            return result;
        }

        fileRepoService.insertFileRepo(downloadReq, userAndCorp, result, accountData.get(), depositPaymentHistoryReqDTO.getAcctNumber(), depositPaymentHistoryReqDTO.getCurrency(),
                Product.DEPOSIT, ReportTransactionTypeEnum.PAYMENT_DETAIL, REPORT_GENERATION_PROGRESS_STAGE_1);

        SearchCollectionResponse<DepositPaymentHistoryDTO> depositCAPaymentHistoryListSp = depositCASPService.getDepositCAPaymentHistoryListSp(userAndCorp, depositPaymentHistoryReqDTO);

        fileRepoService.insertFileRepo(downloadReq, userAndCorp, result, accountData.get(), depositPaymentHistoryReqDTO.getAcctNumber(), depositPaymentHistoryReqDTO.getCurrency(),
                Product.DEPOSIT, ReportTransactionTypeEnum.PAYMENT_DETAIL, REPORT_GENERATION_PROGRESS_STAGE_2);

        List<DepositPaymentHistoryDTO> dataList = depositCAPaymentHistoryListSp.getData();

        fileRepoService.insertFileRepo(downloadReq, userAndCorp, result, accountData.get(), depositPaymentHistoryReqDTO.getAcctNumber(), depositPaymentHistoryReqDTO.getCurrency(),
                Product.DEPOSIT, ReportTransactionTypeEnum.PAYMENT_DETAIL, REPORT_GENERATION_PROGRESS_STAGE_3);

        switch (req.getFormat()) {
            case CSV:
                result.setData(depositCAPaymentHistFileService.generateCsv(dataList));
                result.setFileName(DownloadEnum.DEPOSIT_CA_PAYMENT_HISTORY_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
                break;
            case PDF:
                result.setData(depositCAPaymentHistFileService.generatePdf(dataList));
                result.setFileName(DownloadEnum.DEPOSIT_CA_PAYMENT_HISTORY_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
                break;
            case TXT:
                result.setData(depositCAPaymentHistFileService.generateTxt(dataList));
                result.setFileName(DownloadEnum.DEPOSIT_CA_PAYMENT_HISTORY_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }


        //insert into file repo once file is generated
        fileRepoService.insertFileRepo(downloadReq, userAndCorp, result, accountData.get(), depositPaymentHistoryReqDTO.getAcctNumber(), depositPaymentHistoryReqDTO.getCurrency(),
                Product.DEPOSIT, ReportTransactionTypeEnum.PAYMENT_DETAIL, REPORT_GENERATION_PROGRESS_STAGE_4);

        return result;
    }

    private DownloadDTO downloadEODBalance(UserAndCorp userAndCorp, DownloadReq downloadReq) {
        DownloadDTO result = new DownloadDTO();
        DepositCAEODBalanceDownloadReqDTO req = (DepositCAEODBalanceDownloadReqDTO) downloadReq;
        DepositCAEODBalanceReqDTO depositCAEODBalanceReqDTO = new DepositCAEODBalanceReqDTO();
        depositCAEODBalanceReqDTO.setAcctNumber(req.getAcctNumber());
        depositCAEODBalanceReqDTO.setCurrency(req.getCurrency());
        depositCAEODBalanceReqDTO.setYear(req.getYear());
        depositCAEODBalanceReqDTO.setMonth(req.getMonth());
        depositCAEODBalanceReqDTO.setPage(1);
        depositCAEODBalanceReqDTO.setPageSize(Integer.MAX_VALUE);

        SearchCollectionResponse<DepositCAEODBalanceDTO> depositAccountEODBalanceSP = depositCASPService.getDepositAccountEODBalanceSP(userAndCorp, depositCAEODBalanceReqDTO);
        List<DepositCAEODBalanceDTO> dataList = depositAccountEODBalanceSP.getData();

        switch (req.getFormat()) {
            case CSV:
                result.setData(depositCAEODBalanceFileService.generateCsv(dataList));
                result.setFileName(DownloadEnum.DEPOSIT_CA_EOD_BALANCE_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
                break;
            case PDF:
                result.setData(depositCAEODBalanceFileService.generatePdf(dataList));
                result.setFileName(DownloadEnum.DEPOSIT_CA_EOD_BALANCE_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
                break;
            case TXT:
                result.setData(depositCAEODBalanceFileService.generateTxt(dataList));
                result.setFileName(DownloadEnum.DEPOSIT_CA_EOD_BALANCE_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    private DownloadDTO downloadPaymentDetail(UserAndCorp userAndCorp, DownloadReq downloadReq) {
        DownloadDTO result = new DownloadDTO();
        DepositCAPaymentDetailsDownloadReqDTO req = (DepositCAPaymentDetailsDownloadReqDTO) downloadReq;
        DepositTrxReferenceReqDTO depositTrxReferenceReqDTO = new DepositTrxReferenceReqDTO();
        depositTrxReferenceReqDTO.setBankRefNo(req.getBankRefNo());
        depositTrxReferenceReqDTO.setCurrency(req.getCurrency());

        DepositTrxReferenceDTO depositTrxReferenceDTO = depositCASPService.getDepositCAPaymentDetailTrxRefSP(userAndCorp, depositTrxReferenceReqDTO);
        result.setData(depositCAPaymentDetailFileService.generatePdf(depositTrxReferenceDTO));
        result.setFileName(DownloadEnum.DEPOSIT_CA_PAYMENT_DETAIL_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");

        return result;
    }

    @SneakyThrows
    public ApiResponse<DepositCAPreviewMt103DTO> previewMT103(DepositCAPreviewMt103ReqDTO req) {
        ApiResponse<DepositCAPreviewMt103DTO> apiResponse = new ApiResponse<>();
        DepositCAPreviewMt103DTO depositCAPreviewMt103DTO = new DepositCAPreviewMt103DTO();

        Optional<DepositCASwiftMT103Projection> swiftMT103 = swiftMT103Repository.findByReferenceNo(req.getBankRefNo());

        if (swiftMT103.isPresent()) {
            String result = BlobUtil.convertBlobToString(swiftMT103.get().getFileContent());
            depositCAPreviewMt103DTO.setData(result);
        }

        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(depositCAPreviewMt103DTO);
        return apiResponse;
    }

    @SneakyThrows
    public DownloadDTO downloadMT103(DownloadReq downloadReq) {
        DownloadDTO result = new DownloadDTO();
        DepositCAMT103DownloadReqDTO req = (DepositCAMT103DownloadReqDTO) downloadReq;

        Optional<DepositCASwiftMT103Projection> swiftMT103 = swiftMT103Repository.findByReferenceNo(req.getBankRefNo());
        result.setData(depositCAMT130FileService.generatePdf(swiftMT103));
        result.setFileName(req.getBankRefNo() + "_" + SWIFT_MT103_FILE_TYPE + ".pdf");

        return result;
    }

    private DownloadDTO downloadGPIStatus(DownloadReq downloadReq) {
        DownloadDTO result = new DownloadDTO();
        DepositCAGPIStatusDownloadReqDTO req = (DepositCAGPIStatusDownloadReqDTO) downloadReq;
        DepositGPIStatusReqDTO depositGPIStatusReqDTO = new DepositGPIStatusReqDTO();
        depositGPIStatusReqDTO.setUetr(req.getUetr());
        ApiResponse<DepositCAGPIStatusDTO> depositCAGPIStatusResApiResponse = depositCAESBService.getGPIStatus(depositGPIStatusReqDTO);
        DepositCAGPIStatusDTO depositCAGPIStatusDTO = depositCAGPIStatusResApiResponse.getMsgBody() != null ? depositCAGPIStatusResApiResponse.getMsgBody() : new DepositCAGPIStatusDTO();

        result.setData(depositCAGPIStatusFileService.generatePdf(depositCAGPIStatusDTO));
        result.setFileName(DownloadEnum.DEPOSIT_CA_GPI_STATUS_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");

        return result;
    }

    public ApiResponse<DepositCATrxHistoryDTO> getDepositCATrxHistory(UserAndCorp userAndCorp, DepositTrxHistoryReqDTO request) {
        ApiResponse<DepositCATrxHistoryDTO> apiResponse = new ApiResponse<>();
        DepositCATrxHistoryDTO depositCATrxHistoryDTO = new DepositCATrxHistoryDTO();
        //get account currency and provider to pass to ESB
        Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetails(request.getAcctNumber(), request.getCurrency());
        //check if account exists or not
        if (accountData.isEmpty()) {
            logger.error("Account Not Found for: " + request.getAcctNumber());
            return new ApiResponse<>(new ErrorResponse(), depositCATrxHistoryDTO);
        }

        ESBDepositCAAcctActivityReqDTO esbDepositCAAcctActivityReqDTO = new ESBDepositCAAcctActivityReqDTO();
        esbDepositCAAcctActivityReqDTO.setAccountNo(request.getAcctNumber());
        esbDepositCAAcctActivityReqDTO.setTrxCode(request.getTrxCode());
        esbDepositCAAcctActivityReqDTO.setFilters(request.getFilters());
        esbDepositCAAcctActivityReqDTO.setCurrency(request.getCurrency());
        esbDepositCAAcctActivityReqDTO.setSearchParam(request.getSearchParam());
        esbDepositCAAcctActivityReqDTO.setPage(request.getPage());
        esbDepositCAAcctActivityReqDTO.setPageSize(request.getPageSize());

        // get trx hist data from ESB
        try {
            switch (accountData.get().getProvider()) {
                case "MY" -> {
                    depositCATrxHistoryDTO = depositCAESBService.getDepositCATrxHistory_MY(esbDepositCAAcctActivityReqDTO).getMsgBody();
                }
                case "SG" -> {
                    depositCATrxHistoryDTO = depositCAESBService.getDepositCATrxHistory_SG(esbDepositCAAcctActivityReqDTO).getMsgBody();
                }
            }

            depositCATrxHistoryDTO = depositCATrxHistoryDTO != null ? depositCATrxHistoryDTO : new DepositCATrxHistoryDTO();
        } catch (Exception e) {
            depositCATrxHistoryDTO = depositCATrxHistoryDTO != null ? depositCATrxHistoryDTO : new DepositCATrxHistoryDTO();
            depositCATrxHistoryDTO.setDatas(new SearchCollectionResponse<>());
        }

        // get trx hist data from SP
        SearchCollectionResponse<DepositTrxHistoryDTO> spDepositTrxHistoryResList = Optional.ofNullable(depositCASPService.getDepositCATrxHistorySp(userAndCorp, request))
                .orElse(new SearchCollectionResponse<>());

        List<DepositTrxHistoryDTO> esbDataList = Optional.ofNullable(depositCATrxHistoryDTO.getDatas()).map(SearchCollectionResponse::getData).orElse(new ArrayList<>());
        List<DepositTrxHistoryDTO> spDataList = Optional.of(spDepositTrxHistoryResList).map(SearchCollectionResponse::getData).orElse(new ArrayList<>());

        // if esb data is fetched only, then return it to front end with proper pagination
        if (!esbDataList.isEmpty() && spDataList.isEmpty()) {
            SearchCollectionResponse<DepositTrxHistoryDTO> esbDepositTrxHistoryRes = new SearchCollectionResponse<>();
            List<DepositTrxHistoryDTO> sortedAndFormat = sortAndFormat(esbDataList);
            List<DepositTrxHistoryDTO> esbDataListOnly = asyncPaginationService.paginateData(sortedAndFormat, request.getPageSize(), request.getPage());
            int esbTotalRecordCount = Optional.of(depositCATrxHistoryDTO.getDatas())
                    .map(SearchCollectionResponse::getTotalRecordCount)
                    .orElse(0);

            int totalPage = (int) Math.ceil((double) esbTotalRecordCount / request.getPageSize());

            esbDepositTrxHistoryRes.setNextPage(request.getPage() < totalPage ? request.getPage() + 1 : -1);
            esbDepositTrxHistoryRes.setCurrentPage(request.getPage());
            esbDepositTrxHistoryRes.setTotalPages(totalPage);
            esbDepositTrxHistoryRes.setTotalRecordCount(esbTotalRecordCount);
            esbDepositTrxHistoryRes.setData(esbDataListOnly);

            depositCATrxHistoryDTO.setDatas(esbDepositTrxHistoryRes);
            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(depositCATrxHistoryDTO);
            return apiResponse;
        } else if (!esbDataList.isEmpty() && !spDataList.isEmpty()) { // else combine both esb and sp data, with esb comes first
            List<DepositTrxHistoryDTO> fusedDepositTrxHistoryDTOList = new ArrayList<>(esbDataList);
            fusedDepositTrxHistoryDTOList.addAll(spDataList);
            //sort by desc date after combining both esb and sp data
            List<DepositTrxHistoryDTO> sortedByDate = sortAndFormat(fusedDepositTrxHistoryDTOList);
            //paginate data after combining and sorting by date
            List<DepositTrxHistoryDTO> paginatedFusedData = asyncPaginationService.paginateData(sortedByDate, request.getPageSize(), request.getPage());
            logger.info("paginatedFusedData --> {}", paginatedFusedData);

            // total record count of both esb and sp data
            int esbTotalRecordCount = Optional.ofNullable(depositCATrxHistoryDTO.getDatas())
                    .map(SearchCollectionResponse::getTotalRecordCount)
                    .orElse(0);
            int spTotalRecordCount = Optional.of(spDepositTrxHistoryResList)
                    .map(SearchCollectionResponse::getTotalRecordCount)
                    .orElse(0);
            int totalRecordCount = esbTotalRecordCount + spTotalRecordCount;

            int totalPage = (int) Math.ceil((double) totalRecordCount / request.getPageSize());

            SearchCollectionResponse<DepositTrxHistoryDTO> fusedDepositTrxHistoryRes = new SearchCollectionResponse<>();
            fusedDepositTrxHistoryRes.setNextPage(request.getPage() < totalPage ? request.getPage() + 1 : -1);
            fusedDepositTrxHistoryRes.setCurrentPage(request.getPage());
            fusedDepositTrxHistoryRes.setTotalPages(totalPage);
            fusedDepositTrxHistoryRes.setTotalRecordCount(totalRecordCount);
            fusedDepositTrxHistoryRes.setData(paginatedFusedData);

            depositCATrxHistoryDTO.setDatas(fusedDepositTrxHistoryRes);

            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(depositCATrxHistoryDTO);
            return apiResponse;
        } else { //else only return spDataList
            SearchCollectionResponse<DepositTrxHistoryDTO> spDepositTrxHistoryRes = new SearchCollectionResponse<>();
            List<DepositTrxHistoryDTO> sortedAndFormat = sortAndFormat(spDataList);
            List<DepositTrxHistoryDTO> spDataListOnly = asyncPaginationService.paginateData(sortedAndFormat, request.getPageSize(), request.getPage());
            int spTotalRecordCount = Optional.of(spDepositTrxHistoryResList)
                    .map(SearchCollectionResponse::getTotalRecordCount)
                    .orElse(0);

            int totalPage = (int) Math.ceil((double) spTotalRecordCount / request.getPageSize());
            spDepositTrxHistoryRes.setNextPage(request.getPage() < totalPage ? request.getPage() + 1 : -1);
            spDepositTrxHistoryRes.setCurrentPage(request.getPage());
            spDepositTrxHistoryRes.setTotalPages(totalPage);
            spDepositTrxHistoryRes.setTotalRecordCount(spTotalRecordCount);
            spDepositTrxHistoryRes.setData(spDataListOnly);

            depositCATrxHistoryDTO.setDatas(spDepositTrxHistoryRes);
            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(depositCATrxHistoryDTO);
            return apiResponse;
        }
    }

    public ApiResponse<SearchCollectionResponse<DepositPaymentHistoryDTO>> getDepositCAPaymentHistoryList(UserAndCorp userAndCorp, DepositPaymentHistoryReqDTO request) {
        SearchCollectionResponse<DepositPaymentHistoryDTO> result = depositCASPService.getDepositCAPaymentHistoryListSp(userAndCorp, request);

        ApiResponse<SearchCollectionResponse<DepositPaymentHistoryDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(result);
        return apiResponse;
    }

    public ApiResponse<DepositTrxReferenceDTO> getDepositCAPaymentDetailTrxRef(UserAndCorp userAndCorp, DepositTrxReferenceReqDTO request) {
        DepositTrxReferenceDTO depositTrxReferenceDTO = depositCASPService.getDepositCAPaymentDetailTrxRefSP(userAndCorp, request);

        ApiResponse<DepositTrxReferenceDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse());
        apiResponse.setMsgBody(depositTrxReferenceDTO);
        return apiResponse;
    }

    public ApiResponse<SearchCollectionResponse<DepositCADebitCardTrxDTO>> getDepositCADebitCardHistoryList(UserAndCorp userAndCorp, DepositCADebitCardTrxReqDTO request) {
        SearchCollectionResponse<DepositCADebitCardTrxDTO> result = depositCASPService.getDepositCADebitCardHistoryListSp(userAndCorp, request);

        ApiResponse<SearchCollectionResponse<DepositCADebitCardTrxDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(result);
        return apiResponse;
    }

    public ApiResponse<DepositAccountDetailDTO> getDepositCAAcctDetail(DepositAccountDetailReqDTO depositAccountDetailReqDTO) {
        DepositAccountDetailDTO depositAccountDetailDTO = new DepositAccountDetailDTO();
        DepositCAAcctOmnibusDetailDTO depositCAAcctOmnibusDetailDTO = new DepositCAAcctOmnibusDetailDTO();
        //get account currency and provider to pass to ESB
        Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetails(depositAccountDetailReqDTO.getAcctNumber(), depositAccountDetailReqDTO.getCurrency());
        //check if account exists or not
        if (accountData.isEmpty()) {
            logger.error("Account Not Found for: " + depositAccountDetailReqDTO.getAcctNumber());
            return new ApiResponse<>(new ErrorResponse(), depositAccountDetailDTO);
        }
        //get corp provider to pass to ESB
        Optional<CorpDetailProjection> corpData = cifRepository.findCorpDetailByCifNo(depositAccountDetailReqDTO.getCorpCifNo());
        //check if corp data exists or not
        if (corpData.isEmpty()) {
            logger.error("Corp Detail Not Found for Corp Cif No: " + depositAccountDetailReqDTO.getCorpCifNo());
            return new ApiResponse<>(new ErrorResponse(), depositAccountDetailDTO);
        }

        ESBDepositCAAcctDetailReqDTO depositCAAcctDetailESBReqDTO = new ESBDepositCAAcctDetailReqDTO();
        depositCAAcctDetailESBReqDTO.setAccountCurrency(depositAccountDetailReqDTO.getCurrency());
        depositCAAcctDetailESBReqDTO.setAccountRegion(accountData.get().getProvider());
        depositCAAcctDetailESBReqDTO.setAccountNo(depositAccountDetailReqDTO.getAcctNumber());
        depositCAAcctDetailESBReqDTO.setAccountType(accountData.get().getAccountType());
        //append omnibus details to acct details response
        switch (accountData.get().getProvider()) {
            case "MY" -> {
                depositAccountDetailDTO = depositCAESBService.getDepositCAAcctDetails_MY(depositCAAcctDetailESBReqDTO).getMsgBody();
            }
            case "SG" -> {
                depositAccountDetailDTO = depositCAESBService.getDepositCAAcctDetails_SG(depositCAAcctDetailESBReqDTO).getMsgBody();
            }
        }

        //check if account number has omnibus parent account number
        Optional<AccountLinkOmnibusDeposit> omnibusParentAccountNo = accountLinkOmnibusDepositRepository.findByChildAccountNo(depositAccountDetailReqDTO.getAcctNumber());
        //query ESB for omnibus info if omnibus parent account number exists
        if (omnibusParentAccountNo.isPresent()) {
            ESBDepositCAAcctDetailReqDTO depositCAOmnibusESBReqDTO = new ESBDepositCAAcctDetailReqDTO();
            depositCAOmnibusESBReqDTO.setAccountCurrency(depositAccountDetailReqDTO.getCurrency());
            depositCAOmnibusESBReqDTO.setAccountRegion(accountData.get().getProvider());
            depositCAOmnibusESBReqDTO.setAccountNo(omnibusParentAccountNo.get().getParentAccountNo());
            depositCAAcctOmnibusDetailDTO = depositCAESBService.getDepositCAAcctOmnibusDetails(depositCAOmnibusESBReqDTO).getMsgBody();
        }

        //append omnibus values to acct details response
        depositAccountDetailDTO.setOmnibusODLimit(depositCAAcctOmnibusDetailDTO.getOdLimit());
        depositAccountDetailDTO.setUnUtilisedOmnibusOD(depositCAAcctOmnibusDetailDTO.getUnUtilisedOD());
        //append corp info to acct details response
        depositAccountDetailDTO.setCompanyName(corpData.get().getCorpLongName());
        depositAccountDetailDTO.setAcctName(accountData.get().getAccountPrefName() != null ? accountData.get().getAccountPrefName() : accountData.get().getProductName());
        depositAccountDetailDTO.setAcctNumber(depositAccountDetailReqDTO.getAcctNumber());
        depositAccountDetailDTO.setCurrency(depositAccountDetailReqDTO.getCurrency());
        depositAccountDetailDTO.setAcctType(accountData.get().getAccountType());
        depositAccountDetailDTO.setAcctTypeDesc(accountData.get().getAccountTypeDesc());
        depositAccountDetailDTO.setProductName(accountData.get().getProductName());
        depositAccountDetailDTO.setAcctStatusDesc(accountData.get().getAccountStatusDesc());
        depositAccountDetailDTO.setIsIslamic(accountData.get().getIsIslamic());
        //get formatted date time
        depositAccountDetailDTO.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());

        ApiResponse<DepositAccountDetailDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(depositAccountDetailDTO);
        return apiResponse;
    }

    public Optional<String> extractValueFromLabel(ESBDepositCAAccountDTO account, String label) {
        return account.getDetails().stream()
                .filter(detail -> label.equals(detail.getName()))
                .map(ESBDepositCAAccountDetailDTO::getValue)
                .findFirst();
    }

    public ApiResponse<DepositCAGPIStatusDTO> getDepositCAGPIStatus(DepositGPIStatusReqDTO request) {
        logger.info("depositGPIStatus is executing");
        return depositCAESBService.getGPIStatus(request);
    }

    public ApiResponse<DepositAcctFloatDTO> getDepositCAAcctFloatDetailESB(DepositAcctFloatReqDTO request) {
        logger.info("esbDepositCAAcctFloatDetails is executing");
        ESBDepositCAFloatReqDTO esbDepositCAFloatReqDTO = new ESBDepositCAFloatReqDTO();
        esbDepositCAFloatReqDTO.setPage(request.getPage());
        esbDepositCAFloatReqDTO.setPageSize(request.getPageSize());
        esbDepositCAFloatReqDTO.setAccountNo(request.getAcctNumber());
        esbDepositCAFloatReqDTO.setCurrency(request.getCurrency());
        esbDepositCAFloatReqDTO.setExpiryDate(request.getExpiryDate());
        return depositCAESBService.getDepositCAAcctFloatDetails(esbDepositCAFloatReqDTO);
    }

    public void removeAllFromCache(UserAndCorp userAndCorp){
        String redisKey = redisHelper.createCacheKey(BD_AM_DP_CA_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_*");
        redisHelper.deleteCachesWithPattern(redisKey);
        logger.info("removed cache for {}", redisKey);
    }

    private ApiResponse<DepositAcctListingDTO> getAcctFromCache(UserAndCorp userAndCorp, AccountListReq reqDTO) {
        return (ApiResponse<DepositAcctListingDTO>) redisHelper.getFromCache(getRedisKey(userAndCorp, reqDTO), ApiResponse.class);
    }

    private String getRedisKey(UserAndCorp userAndCorp, AccountListReq reqDTO) {
        String redisKey = null;
        if (reqDTO.getRealTimeIndicator())
            redisKey = redisHelper.createCacheKey(BD_AM_DP_CA_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "ONL" + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
        else
            redisKey = redisHelper.createCacheKey(BD_AM_DP_CA_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());

        return redisKey;
    }

    private List<DepositTrxHistoryDTO> sortAndFormat(List<DepositTrxHistoryDTO> dataList) {
        if (dataList == null || dataList.isEmpty()) return Collections.emptyList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                DateTimeFormatPatternEnum.dd_MMM_yyyy_HH_mm.getDateFormatPattern(),
                Locale.ENGLISH
        );

        return dataList.stream()
                .sorted(Comparator.comparing(
                        dto -> LocalDateTime.parse(dto.getDate(), formatter),
                        Comparator.reverseOrder()
                ))
                .map(dto -> {
                    LocalDateTime parsed = LocalDateTime.parse(dto.getDate(), formatter);
                    String formatted = formatter.format(parsed);

                    // Replace midnight time with "-"
                    if (formatted.endsWith(DateTimeFormatPatternEnum.DEFAULT_TIME_00_00.getDateFormatPattern())) {
                        formatted = formatted.replace(DateTimeFormatPatternEnum.DEFAULT_TIME_00_00.getDateFormatPattern(), DEFAULT_TRX_HIST_TIME_VALUE);
                    }

                    dto.setDate(formatted);
                    return dto;
                })
                .toList();
    }

}