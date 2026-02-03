package com.m2e.dashboard.service.deposit.fd;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.dto.MoreActionsRedirection;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.deposit.fd.request.DepositFDAcctCertificatesReqDTO;
import com.m2e.dashboard.dto.deposit.fd.request.DepositFDAcctDetailReqDTO;
import com.m2e.dashboard.dto.deposit.fd.request.DepositFDAcctStatsReqDTO;
import com.m2e.dashboard.dto.deposit.fd.request.DepositFDTrxHistoryReqDTO;
import com.m2e.dashboard.dto.deposit.fd.request.download.DepositFDAccountListingDownloadReqDTO;
import com.m2e.dashboard.dto.deposit.fd.request.download.DepositFDCertificateListingDownloadReqDTO;
import com.m2e.dashboard.dto.deposit.fd.request.download.DepositFDTrxHistoryDownloadReqDTO;
import com.m2e.dashboard.dto.deposit.fd.response.*;
import com.m2e.dashboard.enums.*;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry.ESBDepositFDAcctDetailReqDTO;
import com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry.ESBDepositFDAcctListReqDTO;
import com.m2e.dashboard.jms.deposit.fd.request.account.certdetails.ESBDepositFDCertsReqDTO;
import com.m2e.dashboard.projection.CorpDetailProjection;
import com.m2e.dashboard.projection.DepositAcctDetailPageProjection;
import com.m2e.dashboard.repository.AccountRepository;
import com.m2e.dashboard.repository.CifRepository;
import com.m2e.dashboard.repository.CorpRepository;
import com.m2e.dashboard.service.deposit.fd.jms.DepositFDESBService;
import com.m2e.dashboard.service.deposit.fd.sp.DepositFDSPService;
import com.m2e.dashboard.sp.deposit.fd.request.DepositFDAcctStatsSpParam;
import com.m2e.dashboard.sp.deposit.fd.response.DepositFDAcctSummarySpDTO;
import com.m2e.dashboard.util.DateTimeUtil;
import com.m2e.dashboard.util.UtilityFunctions;
import com.m2e.utils.common.enums.DateTimeFormatPatternEnum;
import com.m2e.utils.esb.enums.deposit.fd.CertificateStatus;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.m2e.dashboard.enums.AcctProductEntitlementConst.*;
import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_DP_FD_ACCTLIST;

@RequiredArgsConstructor
@Slf4j
@Service
public class DepositFDService {
    private final int batchSize = 10;
    private static final String BAD_ACCOUNT_NUMBER = "0";

    private final DepositFDCertificateFileService certificateFileService;
    private final DepositFDAccountFileService accountFileService;
    private final DepositFDTrxHistoryFileService trxHistoryFileService;
    private final DepositFDSPService depositFDSPService;
    private final DepositFDESBService depositFDESBService;
    private final AccountRepository accountRepository;
    private final CifRepository cifRepository;
    private final CorpRepository corpRepository;
    private final RedisHelper redisHelper;
    private final UtilityFunctions utilityFunctions;

    public ApiResponse<DepositFDAcctStatsDTO> getDepositFDAcctStats(DepositFDAcctStatsReqDTO request, UserAndCorp userAndCorp) {
        DepositFDAcctStatsSpParam depositFDAcctStatsSpParam = new DepositFDAcctStatsSpParam();
        depositFDAcctStatsSpParam.setUserId(userAndCorp.getUserId());
        depositFDAcctStatsSpParam.setCorpId(userAndCorp.getCorpId());
        DepositFDAcctStatsDTO depositFDAcctStatsDTO = new DepositFDAcctStatsDTO();
        //remove cache for account balances
        removeAllFromCache(userAndCorp);

        if (request.getRealTimeIndicator()) {
            ESBMetadataDTO esbMetadataDTO = new ESBMetadataDTO();
            AccountListReq req = new AccountListReq();
            req.setPage(1);
            req.setPageSize(Integer.MAX_VALUE);
            DepositFDAcctSummaryDTO spResult = depositFDSPService.getDepositFDAccountListingSP(userAndCorp, req);
            //call ESB to calculate consolidated balances for accounts across all corps if its equal or less than 20
            if (!spResult.getDatas().getData().isEmpty() && spResult.getDatas().getTotalRecordCount() <= utilityFunctions.getMaxAcctCount()) {
                Map<String, List<DepositFDAcctSummarySpDTO>> classifiedByProvider =
                        Optional.of(spResult.getDatas())
                                .map(SearchCollectionResponse::getData)
                                .orElseGet(List::of)
                                .stream()
                                .filter(dto -> dto.getProvider() != null)
                                .collect(Collectors.groupingBy(DepositFDAcctSummarySpDTO::getProvider));
                log.info("deposit FD stats classifiedByProvider -> {} ", classifiedByProvider);

                Map<String, Object> esbResults = new HashMap<>();

                classifiedByProvider.entrySet().stream()
                        .filter(entry -> List.of(ProviderEnum.MY.name(), ProviderEnum.SG.name()).contains(entry.getKey()))
                        .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                        .forEach(entry -> {
                            String provider = entry.getKey();
                            List<DepositFDAcctSummarySpDTO> accounts = entry.getValue();

                            List<List<DepositFDAcctSummarySpDTO>> batches = IntStream.range(0, (accounts.size() + 9) / 10)
                                    .mapToObj(i -> accounts.subList(i * 10, Math.min((i + 1) * 10, accounts.size())))
                                    .filter(batch -> !batch.isEmpty())
                                    .toList();

                            List<DepositFDAcctStatsDTO> batchStats = new ArrayList<>();

                            for (List<DepositFDAcctSummarySpDTO> batch : batches) {
                                ESBDepositFDAcctListReqDTO reqDTO = new ESBDepositFDAcctListReqDTO();
                                reqDTO.setCorpCifNo(spResult.getCorpCifNo());
                                reqDTO.setCorpProvider(provider);

                                SearchCollectionResponse<DepositFDAcctSummarySpDTO> dataWrapper = new SearchCollectionResponse<>();
                                dataWrapper.setData(batch);
                                reqDTO.setDatas(dataWrapper);

                                Object result = switch (provider) {
                                    case "MY" -> depositFDESBService.getAccountStats_MY(reqDTO).getMsgBody();
                                    case "SG" -> depositFDESBService.getAccountStats_SG(reqDTO).getMsgBody();
                                    default -> null;
                                };

                                if (result instanceof DepositFDAcctStatsDTO dto) {
                                    batchStats.add(dto);
                                }
                            }

                            DepositFDAcctStatsDTO merged = mergeStats(batchStats);
                            esbResults.put(provider, merged);
                        });

                DecimalFormat formatter = new DecimalFormat("#,##0.00");
                String mergedTotalCertCount = String.valueOf(
                    esbResults.values().stream()
                            .filter(Objects::nonNull)
                            .map(DepositFDAcctStatsDTO.class::cast)
                            .map(DepositFDAcctStatsDTO::getTotalOfCertificates)
                            .filter(Objects::nonNull)
                            .mapToInt(certCount -> {
                                try {
                                    return Integer.parseInt(certCount);
                                } catch (NumberFormatException e) {
                                    return 0;
                                }
                            })
                            .sum()
                );

                String mergedConsolidatedPrincipalAmt = formatter.format(
                    esbResults.values().stream()
                        .filter(Objects::nonNull)
                        .map(DepositFDAcctStatsDTO.class::cast)
                        .map(DepositFDAcctStatsDTO::getConsolidatedPrincipalAmt)
                        .filter(Objects::nonNull)
                        .map(bal -> bal.replaceAll(",", "")) // cleanup commas
                        .mapToDouble(amt -> {
                            try {
                                return Double.parseDouble(amt);
                            } catch (NumberFormatException e) {
                                return 0.00;
                            }
                        })
                        .sum()
                );

                //set the total amounts here after sum from MY and SG
                depositFDAcctStatsDTO.setTotalOfCertificates(mergedTotalCertCount);
                depositFDAcctStatsDTO.setConsolidatedPrincipalAmt(mergedConsolidatedPrincipalAmt);
                esbMetadataDTO = esbResults.values().stream()
                        .filter(Objects::nonNull)
                        .map(DepositFDAcctStatsDTO.class::cast)
                        .map(DepositFDAcctStatsDTO::getEsbMetadata)
                        .filter(Objects::nonNull)
                        .reduce((first, second) -> second)
                        .orElse(null);
                depositFDAcctStatsDTO.setEsbMetadata(esbMetadataDTO);
            } else {
                depositFDAcctStatsDTO = depositFDSPService.getDepositFDAccountStatsSP(depositFDAcctStatsSpParam);
            }
            depositFDAcctStatsDTO.setNumberOfAcct(String.valueOf(spResult.getDatas().getTotalRecordCount()));
            //set today date time if realtime indicator is true
            depositFDAcctStatsDTO.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
            depositFDAcctStatsDTO.setLastUpdatedDate(DateTimeUtil.getTodayDate());
        } else {
            depositFDAcctStatsDTO = depositFDSPService.getDepositFDAccountStatsSP(depositFDAcctStatsSpParam);
        }
        ApiResponse<DepositFDAcctStatsDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgBody(depositFDAcctStatsDTO);
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        return apiResponse;
    }

    private DepositFDAcctStatsDTO mergeStats(List<DepositFDAcctStatsDTO> statsList) {
        DepositFDAcctStatsDTO merged = new DepositFDAcctStatsDTO();
        DecimalFormat formatter = new DecimalFormat("#,##0.00");

        double totalConsolidated = statsList.stream()
                .map(DepositFDAcctStatsDTO::getConsolidatedPrincipalAmt)
                .filter(Objects::nonNull)
                .mapToDouble(val -> {
                    try {
                        return Double.parseDouble(val);
                    } catch (NumberFormatException e) {
                        return 0.00;
                    }
                }).sum();

        String totalCert = String.valueOf(
                statsList.stream()
                .map(DepositFDAcctStatsDTO::getTotalOfCertificates)
                .filter(Objects::nonNull)
                .mapToInt(val -> {
                    try {
                        return Integer.parseInt(val);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }).sum()
        );

        merged.setConsolidatedPrincipalAmt(formatter.format(totalConsolidated));
        merged.setTotalOfCertificates(totalCert);

        ESBMetadataDTO metadata = statsList.stream()
                .map(DepositFDAcctStatsDTO::getEsbMetadata)
                .filter(Objects::nonNull)
                .reduce((first, second) -> second)
                .orElse(null);
        merged.setEsbMetadata(metadata);

        return merged;
    }

    public ApiResponse<DepositFDAcctSummaryDTO> getDepositFDAcctList(AccountListReq request, UserAndCorp userAndCorp) {
        ApiResponse<DepositFDAcctSummaryDTO> apiResponse = new ApiResponse<>();
        ESBMetadataDTO esbMetadataDTO = new ESBMetadataDTO();
        //get acct balances from cache
        ApiResponse<DepositFDAcctSummaryDTO> finalResp = getAcctFromCache(userAndCorp, request);

        if (finalResp == null) {
            DepositFDAcctSummaryDTO spResult = depositFDSPService.getDepositFDAccountListingSP(userAndCorp, request);

            if (request.getRealTimeIndicator() && !spResult.getDatas().getData().isEmpty()) {
                List<DepositFDAcctSummarySpDTO> t1Data = getInitialData(spResult.getDatas());
                log.info("Deposit FD inquiry t1Data: {}", t1Data);

                List<String> providedProviders = getProvidedProviders(t1Data);
                if (!providedProviders.isEmpty()) {
                    spResult = processProviders(providedProviders, spResult, userAndCorp);
                    spResult.getDatas().getData().forEach(dto -> {
                        boolean isClosed = dto.getAcctStatusDesc() != null && (dto.getAcctStatusDesc().equalsIgnoreCase(AcctStatusEnum.CLOSED.name()) || dto.getAcctStatusDesc().equalsIgnoreCase(AcctStatusEnum.INACTIVE.name()));
                        dto.setTotalPrincipalAmt(isClosed ? null : dto.getTotalPrincipalAmt());
                        dto.setNoOfCertificates(isClosed ? null : dto.getNoOfCertificates());
                        dto.setMoreActions(getAcctSpecificMoreActions(dto, userAndCorp));
                    });
                    apiResponse.setMsgHeader(new ErrorResponse("0"));
                    apiResponse.setMsgBody(spResult);
                } else {
                    log.info("No providers or sub-products available to process for Corporate CIF No {}", request.getCorpCifNo());
                }
            } else {
                spResult.getDatas().getData().forEach(dto -> {
                    boolean isClosed = dto.getAcctStatusDesc() != null && (dto.getAcctStatusDesc().equals("CLOSED") || dto.getAcctStatusDesc().equals("INACTIVE"));
                    dto.setTotalPrincipalAmt(isClosed ? null : dto.getTotalPrincipalAmt());
                    dto.setNoOfCertificates(isClosed ? null : dto.getNoOfCertificates());
                    dto.setMoreActions(getAcctSpecificMoreActions(dto, userAndCorp));
                });
                apiResponse.setMsgHeader(new ErrorResponse("0"));
                apiResponse.setMsgBody(spResult);
            }

            finalResp = convertApiResponse(apiResponse, request.getCorpCifNo(), esbMetadataDTO);
            redisHelper.save(getRedisKey(userAndCorp, request), finalResp);
        }
        return finalResp;
    }

    private List<DepositFDAcctSummarySpDTO> getInitialData(SearchCollectionResponse<DepositFDAcctSummarySpDTO> spResult) {
        return Optional.ofNullable(spResult.getData()).orElseGet(ArrayList::new);
    }

    private List<String> getProvidedProviders(List<DepositFDAcctSummarySpDTO> t1Data) {
        return Optional.ofNullable(getProviders(t1Data)).orElseGet(ArrayList::new);
    }

    private DepositFDAcctSummaryDTO processProviders(List<String> providedProviders,
                                                     DepositFDAcctSummaryDTO t1Data, UserAndCorp userAndCorp) {
        DepositFDAcctSummaryDTO aggregatedResult = new DepositFDAcctSummaryDTO();
        List<DepositFDAcctSummarySpDTO> allData = new ArrayList<>();
        ESBMetadataDTO finalMetadata = new ESBMetadataDTO();

        String corpCifNo = t1Data.getCorpCifNo();
        aggregatedResult.setCorpCifNo(corpCifNo);

        for (String provider : providedProviders) {
            DepositFDAcctSummaryDTO providerResult = processByProvider(t1Data, userAndCorp, provider);
            if (providerResult != null && providerResult.getDatas() != null && providerResult.getDatas().getData() != null) {
                allData.addAll(providerResult.getDatas().getData());
            }
            // Optionally merge metadata if needed
            finalMetadata = providerResult.getEsbMetadata(); // or merge intelligently if multiple
        }

        SearchCollectionResponse<DepositFDAcctSummarySpDTO> mergedData = new SearchCollectionResponse<>();
        mergedData.setData(allData);
        mergedData.setCurrentPage(t1Data.getDatas().getCurrentPage());
        mergedData.setNextPage(t1Data.getDatas().getNextPage());
        mergedData.setTotalRecordCount(t1Data.getDatas().getTotalRecordCount());
        mergedData.setTotalPages(t1Data.getDatas().getTotalPages());

        aggregatedResult.setDatas(mergedData);
        aggregatedResult.setEsbMetadata(finalMetadata);

        return aggregatedResult;
    }


    private List<DepositFDAcctSummarySpDTO> filterByProvider(List<DepositFDAcctSummarySpDTO> data, String provider) {
        return Optional.ofNullable(data)
                .orElseGet(List::of)
                .stream()
                .filter(dto -> provider.equals(dto.getProvider()))
                .toList();
    }

    private List<String> getProviders(List<DepositFDAcctSummarySpDTO> data) {
        return Optional.ofNullable(data)
                .orElseGet(List::of)
                .stream()
                .map(DepositFDAcctSummarySpDTO::getProvider)
                .filter(provider -> provider != null && !provider.isEmpty())
                .distinct()
                .toList();
    }

    private DepositFDAcctSummaryDTO processByProvider(DepositFDAcctSummaryDTO t1Data, UserAndCorp userAndCorp, String provider) {
        DepositFDAcctSummaryDTO tData = new DepositFDAcctSummaryDTO();
        String corpCifNo = t1Data.getCorpCifNo();
        List<DepositFDAcctSummarySpDTO> filteredData = filterByProvider(t1Data.getDatas().getData(), provider);

        AtomicReference<DepositFDAcctSummaryDTO> tmp = new AtomicReference<>(new DepositFDAcctSummaryDTO());
        List<DepositFDAcctSummarySpDTO> tempList = new ArrayList<>();

        IntStream.range(0, (filteredData.size() + batchSize - 1) / batchSize)
                .mapToObj(i -> filteredData.subList(i * batchSize, Math.min((i + 1) * batchSize, filteredData.size())))
                .forEach(batch -> {
                    tmp.set(fetchDataForProvider(batch, userAndCorp, corpCifNo, provider));
                    tempList.addAll((tmp.get().getDatas().getData()));
                });
        SearchCollectionResponse<DepositFDAcctSummarySpDTO> dataESB = new SearchCollectionResponse<>();
        dataESB.setData(tempList);
        dataESB.setCurrentPage(t1Data.getDatas().getCurrentPage());
        dataESB.setNextPage(t1Data.getDatas().getNextPage());
        dataESB.setTotalRecordCount(t1Data.getDatas().getTotalRecordCount());
        dataESB.setTotalPages(t1Data.getDatas().getTotalPages());

        tData.setDatas(dataESB);
        tData.setEsbMetadata(tmp.get().getEsbMetadata());
        return tData;
    }

    private DepositFDAcctSummaryDTO fetchDataForProvider(List<DepositFDAcctSummarySpDTO> filteredData, UserAndCorp userAndCorp, String corpCifNo, String provider) {
        DepositFDAcctSummaryDTO tData = new DepositFDAcctSummaryDTO();
        SearchCollectionResponse<DepositFDAcctSummarySpDTO> dataESB = new SearchCollectionResponse<>();
        ESBMetadataDTO esbMetadataDTO = new ESBMetadataDTO();
        dataESB.setData(filteredData);
        tData.setDatas(dataESB);
        tData.setEsbMetadata(esbMetadataDTO);
        tData.setCorpCifNo(corpCifNo);
        log.info("tData FD before call ESB: {}", tData);
        ApiResponse<DepositFDAcctSummaryDTO> esbResult = new ApiResponse<>();

        ESBDepositFDAcctListReqDTO esbDepositFDAcctListReqDTO = new ESBDepositFDAcctListReqDTO();
        esbDepositFDAcctListReqDTO.setDatas(dataESB);
        esbDepositFDAcctListReqDTO.setCorpCifNo(corpCifNo);
        esbDepositFDAcctListReqDTO.setCorpProvider(provider);
        if (ProviderEnum.MY.name().equals(provider)) {
            esbResult = depositFDESBService.getDepositFDAccountListESB_MY(esbDepositFDAcctListReqDTO);
        } else {
            esbResult = depositFDESBService.getDepositFDAccountListESB_SG(esbDepositFDAcctListReqDTO);
        }
        log.info("after hitting ESB CA: {}", esbResult);
        if (esbResult != null && esbResult.getMsgBody() != null && esbResult.getMsgBody().getDatas() != null) {
            tData = esbResult.getMsgBody();
        }
        log.info("tData FD after calling ESB: {}", tData);
        return tData;
    }

    private ApiResponse<DepositFDAcctSummaryDTO> convertApiResponse(
            ApiResponse<DepositFDAcctSummaryDTO> apiResponse,
            String corpCifNo, ESBMetadataDTO esbMetadataDTO) {

        ApiResponse<DepositFDAcctSummaryDTO> apiResponseNew = new ApiResponse<>();

        apiResponseNew.setMsgHeader(apiResponse.getMsgHeader());

        DepositFDAcctSummaryDTO summaryResp = new DepositFDAcctSummaryDTO();
        summaryResp.setCorpCifNo(corpCifNo);
        summaryResp.setDatas(apiResponse.getMsgBody().getDatas());
        summaryResp.setEsbMetadata(esbMetadataDTO);

        apiResponseNew.setMsgBody(summaryResp);

        return apiResponseNew;
    }

    private MoreActionsEntitlement getAcctSpecificMoreActions(DepositFDAcctSummarySpDTO dto, UserAndCorp userAndCorp) {
        String makePlacementRedisKey = redisHelper.buildCacheKey(userAndCorp.getUserId(), userAndCorp.getCorpId(), USER_ACCT_PRODUCT_LIST_REDIS_KEY, MY_FD_PLACEMENT_PRODUCT_CODE, dto.getAcctNumber(), dto.getCurrency());
        log.debug("getAcctSpecificMoreActions() makePlacement Redis key: {}", makePlacementRedisKey);
        MoreActionsEntitlement moreActionsEntitlement = new MoreActionsEntitlement();
        moreActionsEntitlement.setMakePlacement(new MoreActionsRedirection(redisHelper.isPresent(makePlacementRedisKey), RedirectModuleEnum.PAYMENT_ACTION_REDIRECT.name(), Application.CASHLITE.getValue(), MY_FD_PLACEMENT_PRODUCT_CODE));
        return moreActionsEntitlement;
    }

    private MoreActionsEntitlement getCertSpecificMoreActions(
            String acctNumber, DepositFDCertificateDetailDTO.FullData dto, UserAndCorp userAndCorp) {
        String upliftRedisKey = redisHelper.buildCacheKey(
                userAndCorp.getUserId(),
                userAndCorp.getCorpId(),
                USER_ACCT_PRODUCT_LIST_REDIS_KEY,
                MY_FD_UPLIFTMENT_PRODUCT_CODE,
                acctNumber,
                dto.getCurrency());
        String manageRedisKey = redisHelper.buildCacheKey(userAndCorp.getUserId(),
                userAndCorp.getCorpId(),
                USER_ACCT_PRODUCT_LIST_REDIS_KEY,
                MY_FD_MANAGE_PRODUCT_CODE,
                acctNumber,
                dto.getCurrency());
        log.info("getCertSpecificMoreActions() uplift Redis key: {}", upliftRedisKey);
        log.info("getCertSpecificMoreActions() manage Redis key: {}", manageRedisKey);
        boolean isEntitledToUplift = false;
        boolean isEntitledToManage = false;
        boolean isMatured = false;

        try {
            // Cannot uplift/manage fixed deposits that are already matured
            LocalDate maturityDate = LocalDate.parse(
                    dto.getRawData().getMaturityDate(),
                    DateTimeFormatPatternEnum.yyyyMMdd.getFormatter()
            );
            isMatured = LocalDate.now().isAfter(maturityDate);
        } catch (Exception e) {
            log.warn("Failed to parse maturity date, fall back to not yet matured");
        }

        if (CertificateStatus.ACTIVE.name().equalsIgnoreCase(dto.getCertStatusCode()) && !isMatured) {
            isEntitledToUplift = true;
            isEntitledToManage = true;
        } else if (CertificateStatus.UPLIFTMENT_IN_31_DAYS.name().equalsIgnoreCase(dto.getCertStatusCode())) {
            isEntitledToUplift = true; // Can cancel uplift
        }

        isEntitledToUplift = isEntitledToUplift && redisHelper.isPresent(upliftRedisKey);
        isEntitledToManage = isEntitledToManage && redisHelper.isPresent(manageRedisKey);

        return MoreActionsEntitlement.builder()
                .uplift(new MoreActionsRedirection(
                        isEntitledToUplift,
                        RedirectModuleEnum.PAYMENT_ACTION_REDIRECT.name(),
                        Application.CASHLITE.getValue(),
                        MY_FD_UPLIFTMENT_PRODUCT_CODE))
                .manage(new MoreActionsRedirection(
                        isEntitledToManage,
                        RedirectModuleEnum.PAYMENT_ACTION_REDIRECT.name(),
                        Application.CASHLITE.getValue(),
                        MY_FD_MANAGE_PRODUCT_CODE))
                .build();
    }

    public ApiResponse<DepositFDAccountDetailDTO> getDepositFDAcctDtl(DepositFDAcctDetailReqDTO request, UserAndCorp userAndCorp) {
        DepositFDAccountDetailDTO depositFDAccountDetailDTO = new DepositFDAccountDetailDTO();
        //get account currency and provider to pass to ESB
        Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetails(request.getAcctNumber(), request.getCurrency(), userAndCorp.getUserId(), userAndCorp.getCorpId());
        //check if account exists or not
        if (accountData.isEmpty()) {
            log.error("Account Not Found for: " + request.getAcctNumber());
            return new ApiResponse<>(new ErrorResponse(), depositFDAccountDetailDTO);
        }

        //get corp provider to pass to ESB
        Optional<CorpDetailProjection> corpData = cifRepository.findCorpDetailByCifNo(request.getCorpCifNo());
        //check if corp data exists or not
        if (corpData.isEmpty()) {
            log.error("Corp Detail Not Found for Corp Cif No: " + request.getCorpCifNo());
            return new ApiResponse<>(new ErrorResponse(), depositFDAccountDetailDTO);
        }

        ESBDepositFDAcctDetailReqDTO depositFDAcctDetailESBReqDTO = new ESBDepositFDAcctDetailReqDTO();
        depositFDAcctDetailESBReqDTO.setAccountCurrency(request.getCurrency());
        depositFDAcctDetailESBReqDTO.setAccountRegion(accountData.get().getProvider());
        depositFDAcctDetailESBReqDTO.setAccountNo(request.getAcctNumber());

        switch (accountData.get().getProvider()) {
            case "MY" -> {
                depositFDAccountDetailDTO = depositFDESBService.getDepositFDAcctDetailESB_MY(depositFDAcctDetailESBReqDTO).getMsgBody();
            }
            case "SG" -> {
                depositFDAccountDetailDTO = depositFDESBService.getDepositFDAcctDetailESB_SG(depositFDAcctDetailESBReqDTO).getMsgBody();
            }
        }
        //append corp info to acct details response
        depositFDAccountDetailDTO.setCompanyName(corpData.get().getCorpLongName());
        depositFDAccountDetailDTO.setAcctName(accountData.get().getAccountPrefName() != null ? accountData.get().getAccountPrefName() : accountData.get().getProductName());
        depositFDAccountDetailDTO.setAcctNumber(request.getAcctNumber());
        depositFDAccountDetailDTO.setCurrency(request.getCurrency());
        String productName = Optional.ofNullable(accountData.get().getAccountPrefName())
                .filter(name -> !name.trim().isEmpty())
                .orElse(accountData.get().getProductName());
        depositFDAccountDetailDTO.setProductName(productName);
        depositFDAccountDetailDTO.setAcctType(accountData.get().getAccountType());
        depositFDAccountDetailDTO.setProductCode(accountData.get().getProductCode());
        depositFDAccountDetailDTO.setAcctStatusDesc(accountData.get().getAccountStatusDesc());
        //get formatted date time
        depositFDAccountDetailDTO.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());

        ApiResponse<DepositFDAccountDetailDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(depositFDAccountDetailDTO);
        return apiResponse;
    }

    public ApiResponse<DepositFDCertificateListDTO> getDepositFDAcctCertDtlList(DepositFDAcctCertificatesReqDTO request, UserAndCorp userAndCorp) {
        ApiResponse<DepositFDCertificateListDTO> apiResponse = new ApiResponse<>();

        Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetails(request.getAcctNumber(), request.getCurrency(), userAndCorp.getUserId(), userAndCorp.getCorpId());
        //check if account exists or not
        if (accountData.isEmpty()) {
            log.error("Account Not Found for: " + request.getAcctNumber());
            return new ApiResponse<>(new ErrorResponse(), new DepositFDCertificateListDTO());
        }
        ESBDepositFDCertsReqDTO esbDepositFDCertsReqDTO = new ESBDepositFDCertsReqDTO();
        esbDepositFDCertsReqDTO.setAccountNo(request.getAcctNumber());
        esbDepositFDCertsReqDTO.setProvider(request.getProvider());
        esbDepositFDCertsReqDTO.setSearchParam(request.getSearchParam());
        esbDepositFDCertsReqDTO.setCurrency(request.getCurrency());
        esbDepositFDCertsReqDTO.setAccountType(accountData.get().getAccountType());
        esbDepositFDCertsReqDTO.setPage(request.getPage());
        esbDepositFDCertsReqDTO.setPageSize(request.getPageSize());

        switch (request.getProvider()) {
            case MY -> {
                apiResponse = depositFDESBService.getDepositFDCertDtlESB_MY(esbDepositFDCertsReqDTO);
                apiResponse.getMsgBody().getDatas().getData().forEach(cert -> {
                    if (StringUtils.hasText(cert.getPrincipalCreditNumber())
                        && !cert.getPrincipalCreditNumber().equals(BAD_ACCOUNT_NUMBER)) {
                        Optional<DepositAcctDetailPageProjection> principalCreditAccount =
                                accountRepository.findAcctDetails(
                                        cert.getPrincipalCreditNumber(), request.getCurrency());
                        if (principalCreditAccount.isPresent()) {
                            cert.setPrincipalCreditAccountName(principalCreditAccount.get().getDisplayName());
                        } else {
                            cert.setPrincipalCreditAccountName(accountData.get().getAccountName());
                        }
                    }
                    if (StringUtils.hasText(cert.getInterestCreditNumber())
                        && !cert.getInterestCreditNumber().equals(BAD_ACCOUNT_NUMBER)) {
                        Optional<DepositAcctDetailPageProjection> interestCreditAccount =
                                accountRepository.findAcctDetails(
                                        cert.getInterestCreditNumber(), request.getCurrency());
                        if (interestCreditAccount.isPresent()) {
                            cert.setInterestCreditAccountName(interestCreditAccount.get().getDisplayName());
                        } else {
                            cert.setInterestCreditAccountName(accountData.get().getAccountName());
                        }
                    }
                    cert.setMoreActions(getCertSpecificMoreActions(request.getAcctNumber(), cert, userAndCorp));
                });
            }
            case SG -> {
                apiResponse = depositFDESBService.getDepositFDCertDtlESB_SG(esbDepositFDCertsReqDTO);
            }
        }
        //set account islamic flag
        Boolean isIslamic = accountData.get().getIsIslamic().equals("Y");
        apiResponse.getMsgBody().setIsIslamic(isIslamic);
        apiResponse.getMsgBody().setAcctNumber(request.getAcctNumber());
        apiResponse.getMsgBody().setCurrency(request.getCurrency());
        apiResponse.getMsgBody().setAcctName(accountData.get().getAccountName());
        apiResponse.getMsgBody().setAcctProductCode(accountData.get().getProductCode());
        return apiResponse;
    }

    public ApiResponse<SearchCollectionResponse<DepositFDTrxHistoryDTO>> getDepositFDTrxHistory(DepositFDTrxHistoryReqDTO request, UserAndCorp userAndCorp) {
        SearchCollectionResponse<DepositFDTrxHistoryDTO> result = Optional.ofNullable(depositFDSPService.getDepositFDTrxHistorySP(userAndCorp, request))
                .orElse(new SearchCollectionResponse<>());

        ApiResponse<SearchCollectionResponse<DepositFDTrxHistoryDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(result);
        return apiResponse;
    }

    public DownloadDTO downloadFdList(DownloadReq request, UserAndCorp userAndCorp) {
        DownloadDTO result = new DownloadDTO();
        switch (request.getReportType()) {
            case DEPOSIT_FD_ACCLISTING_RPT_TYPE -> {
                DepositFDAccountListingDownloadReqDTO requestData = (DepositFDAccountListingDownloadReqDTO) request;
                result = downloadFdAccountList(requestData, userAndCorp);
            }
            case DEPOSIT_FD_CERTLISTING_RPT_TYPE -> {
                DepositFDCertificateListingDownloadReqDTO requestData = (DepositFDCertificateListingDownloadReqDTO) request;
                result = downloadFdCerticifateList(requestData, userAndCorp);
            }
            case DEPOSIT_FD_TRXHISTORIES_RPT_TYPE -> {
                DepositFDTrxHistoryDownloadReqDTO requestData = (DepositFDTrxHistoryDownloadReqDTO) request;
                result = downloadFdTrxHistoryList(requestData, userAndCorp);
            }
            default -> {throw new IllegalArgumentException("Report type is undefined");}
        }
        return result;
    }

    public DownloadDTO downloadFdAccountList(DepositFDAccountListingDownloadReqDTO request, UserAndCorp userAndCorp) {
        DownloadDTO response = new DownloadDTO();
        DepositFDAcctSummaryDTO depositFDAcctSummaryDTO = new DepositFDAcctSummaryDTO();
        List<DepositFDAcctSummarySpDTO> dataList = new ArrayList<>();
        String provider;
        //get corp provider to pass to ESB
        if (request.getCorpCifNo().isEmpty()) {
            Optional<String> pvd = corpRepository.findProviderByCorpId(userAndCorp.getCorpId());

            if (pvd.isEmpty()) {
                log.error("Corp Detail Not Found for Corp Cif No: " + request.getCorpCifNo());
                return response;
            }

            provider = pvd.get();
        } else {
            Optional<CorpDetailProjection> corpData = cifRepository.findCorpDetailByCifNo(request.getCorpCifNo());
            //check if corp data exists or not
            if (corpData.isEmpty()) {
                log.error("Corp Detail Not Found for Corp Cif No: " + request.getCorpCifNo());
                return response;
            }

            provider = corpData.get().getCorpProvider();
        }
        AccountListReq accountListReq = new AccountListReq();
        accountListReq.setPage(1);
        accountListReq.setPageSize(Integer.MAX_VALUE);
        accountListReq.setCorpCifNo(request.getCorpCifNo());
        accountListReq.setSearchParam(request.getSearchParam());
        DepositFDAcctSummaryDTO spResult = depositFDSPService.getDepositFDAccountListingSP(userAndCorp, accountListReq);

        if (request.getRealTimeIndicator() && !spResult.getDatas().getData().isEmpty()) {
            List<DepositFDAcctSummarySpDTO> t1Data = getInitialData(spResult.getDatas());
            log.info("Deposit FD inquiry t1Data: {}", t1Data);

            List<String> providedProviders = getProvidedProviders(t1Data);
            if (!providedProviders.isEmpty()) {
                spResult = processProviders(providedProviders, spResult, userAndCorp);
                spResult.getDatas().getData().forEach(dto->{
                    boolean isClosed = dto.getAcctStatusDesc() != null && (dto.getAcctStatusDesc().equalsIgnoreCase(AcctStatusEnum.CLOSED.name()) || dto.getAcctStatusDesc().equalsIgnoreCase(AcctStatusEnum.INACTIVE.name()));
                    dto.setTotalPrincipalAmt(isClosed ? null : dto.getTotalPrincipalAmt());
                    dto.setNoOfCertificates(isClosed ? null : dto.getNoOfCertificates());
                    dto.setMoreActions(getAcctSpecificMoreActions(dto, userAndCorp));
                });
            } else {
                log.info("No providers or sub-products available to process for Corporate CIF No {}", request.getCorpCifNo());
            }
        } else {
            spResult.getDatas().getData().forEach(dto->{
                boolean isClosed = dto.getAcctStatusDesc() != null && (dto.getAcctStatusDesc().equalsIgnoreCase(AcctStatusEnum.CLOSED.name()) || dto.getAcctStatusDesc().equalsIgnoreCase(AcctStatusEnum.INACTIVE.name()));
                dto.setTotalPrincipalAmt(isClosed ? null : dto.getTotalPrincipalAmt());
                dto.setNoOfCertificates(isClosed ? null : dto.getNoOfCertificates());
                dto.setMoreActions(getAcctSpecificMoreActions(dto, userAndCorp));
            });
        }
        dataList = spResult.getDatas().getData();

        //set the file name based on corpProvider
        if ("SG".equals(provider)) {
            response.setFileName(DownloadEnum.DEPOSIT_TD_ACCLISTING_FILENAME.getDescription()
                    + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
        } else {
            response.setFileName(DownloadEnum.DEPOSIT_FD_ACCLISTING_FILENAME.getDescription()
                    + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
        }

        switch (request.getFormat()) {
            case CSV:
                response.setData(accountFileService.generateCsv(dataList));
                break;
            case PDF:
                response.setData(accountFileService.generatePdf(dataList, provider));
                break;
            case TXT:
                response.setData(accountFileService.generateTxt(dataList));
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return response;
    }

    public DownloadDTO downloadFdCerticifateList(DownloadReq downloadReq, UserAndCorp userAndCorp){
        DownloadDTO response = new DownloadDTO();
        DepositFDCertificateListingDownloadReqDTO req = (DepositFDCertificateListingDownloadReqDTO) downloadReq;
        ESBDepositFDCertsReqDTO esbDepositFDCertsReqDTO = new ESBDepositFDCertsReqDTO();
        esbDepositFDCertsReqDTO.setAccountNo(req.getAcctNumber());
        esbDepositFDCertsReqDTO.setProvider(req.getProvider());
        esbDepositFDCertsReqDTO.setSearchParam(req.getSearchParam());
        esbDepositFDCertsReqDTO.setCurrency(req.getCurrency());
        esbDepositFDCertsReqDTO.setPage(1);
        esbDepositFDCertsReqDTO.setPageSize(Integer.MAX_VALUE);

        ApiResponse<DepositFDCertificateListDTO> apiResponse = new ApiResponse<>();
        switch (req.getProvider()) {
            case MY -> {
                apiResponse = depositFDESBService.getDepositFDCertDtlESB_MY(esbDepositFDCertsReqDTO);
            }
            case SG -> {
                apiResponse = depositFDESBService.getDepositFDCertDtlESB_SG(esbDepositFDCertsReqDTO);
            }
        }
        List<DepositFDCertificateDetailDTO.FullData> result = apiResponse.getMsgBody().getDatas().getData();
        //set the file name based on corpProvider
        if (ProviderEnum.SG.equals(req.getProvider())) {
            response.setFileName(DownloadEnum.DEPOSIT_TD_CERTLISTING_FILENAME.getDescription()
                    + System.currentTimeMillis() + "." + req.getFormat().name().toLowerCase());
        } else {
            response.setFileName(DownloadEnum.DEPOSIT_FD_CERTLISTING_FILENAME.getDescription()
                    + System.currentTimeMillis() + "." + req.getFormat().name().toLowerCase());
        }
        switch (req.getFormat()) {
            case CSV:
                response.setData(certificateFileService.generateCsv(result));
                break;
            case PDF:
                response.setData(certificateFileService.generatePdf(result));
                break;
            case TXT:
                response.setData(certificateFileService.generateTxt(result));
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return response;
    }

    public DownloadDTO downloadFdTrxHistoryList(DepositFDTrxHistoryDownloadReqDTO request, UserAndCorp userAndCorp){
        DownloadDTO response = new DownloadDTO();
        String provider;
        //get corp provider
        Optional<String> pvd = corpRepository.findProviderByCorpId(userAndCorp.getCorpId());
        if (pvd.isEmpty()) {
            log.error("Download Fd Trx Hist Corp Detail Not Found for Corp Corp Id: " + userAndCorp.getCorpId());
            return response;
        }
        provider = pvd.get();

        DepositFDTrxHistoryReqDTO requestSearch = new DepositFDTrxHistoryReqDTO();

        requestSearch.setAcctNumber(request.getAcctNumber());
        requestSearch.setSearchParam(request.getSearchParam());
        requestSearch.setFilters(request.getFilters());
        requestSearch.setPage(1);
        requestSearch.setPageSize(Integer.MAX_VALUE);
        SearchCollectionResponse<DepositFDTrxHistoryDTO> result = Optional.ofNullable(depositFDSPService.getDepositFDTrxHistorySP(userAndCorp, requestSearch))
                .orElse(new SearchCollectionResponse<>());
        List<DepositFDTrxHistoryDTO> trxHistories = Optional.of(result).map(SearchCollectionResponse::getData).orElse(new ArrayList<>());
        //set the file name based on corpProvider
        if ("SG".equals(provider)) {
            response.setFileName(DownloadEnum.DEPOSIT_TD_TRXHISTORIES_FILENAME.getDescription()
                    + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
        } else {
            response.setFileName(DownloadEnum.DEPOSIT_FD_TRXHISTORIES_FILENAME.getDescription()
                    + System.currentTimeMillis() + "." + request.getFormat().name().toLowerCase());
        }
        switch (request.getFormat()) {
            case CSV:
                response.setData(trxHistoryFileService.generateCsv(trxHistories, request.getHeaders()));
                break;
            case PDF:
                response.setData(trxHistoryFileService.generatePdf(trxHistories, request.getHeaders()));
                break;
            case TXT:
                response.setData(trxHistoryFileService.generateTxt(trxHistories, request.getHeaders()));
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return response;
    }

    public ApiResponse<DepositFDMakeAPlacementDTO> getDepositFDMakeAPlacement(UserAndCorp userAndCorp) {
        DepositFDMakeAPlacementDTO dto = new DepositFDMakeAPlacementDTO();
        dto.setMoreActions(getGeneralMoreActions(userAndCorp));
        ApiResponse<DepositFDMakeAPlacementDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgBody(dto);
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        return apiResponse;
    }

    private MoreActionsEntitlement getGeneralMoreActions(UserAndCorp userAndCorp) {
        String makePlacementRedisKey = redisHelper.buildCacheKey(userAndCorp.getUserId(), userAndCorp.getCorpId(), USER_PRODUCT_LIST_REDIS_KEY, MY_FD_PLACEMENT_PRODUCT_CODE);
        log.debug("getGeneralMoreActions() makePlacement Redis key: {}", makePlacementRedisKey);
        MoreActionsEntitlement moreActionsEntitlement = new MoreActionsEntitlement();
        moreActionsEntitlement.setMakePlacement(new MoreActionsRedirection(redisHelper.isPresent(makePlacementRedisKey), RedirectModuleEnum.PAYMENT_ACTION_REDIRECT.name(), Application.CASHLITE.getValue(), MY_FD_PLACEMENT_PRODUCT_CODE));
        return moreActionsEntitlement;
    }

    public void removeAllFromCache(UserAndCorp userAndCorp){
        String redisKey = redisHelper.createCacheKey(BD_AM_DP_FD_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_*");
        redisHelper.deleteCachesWithPattern(redisKey);
        log.info("removed cache for {}", redisKey);
    }

    private ApiResponse<DepositFDAcctSummaryDTO> getAcctFromCache(UserAndCorp userAndCorp, AccountListReq reqDTO) {
        return (ApiResponse<DepositFDAcctSummaryDTO>) redisHelper.getFromCache(getRedisKey(userAndCorp, reqDTO), ApiResponse.class);
    }

    private String getRedisKey(UserAndCorp userAndCorp, AccountListReq reqDTO) {
        String redisKey = null;
        if (reqDTO.getRealTimeIndicator())
            redisKey = redisHelper.createCacheKey(BD_AM_DP_FD_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "ONL" + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
        else
            redisKey = redisHelper.createCacheKey(BD_AM_DP_FD_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());

        return redisKey;
    }

}
