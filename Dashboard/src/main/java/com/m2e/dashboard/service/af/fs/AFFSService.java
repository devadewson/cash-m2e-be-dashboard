package com.m2e.dashboard.service.af.fs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.dto.MoreActionsRedirection;
import com.m2e.dashboard.dto.af.request.fs.AFFSAcctDetailReqDTO;
import com.m2e.dashboard.dto.af.request.fs.AFFSAcctStatsReqDTO;
import com.m2e.dashboard.dto.af.request.fs.AFFSCertListReqDTO;
import com.m2e.dashboard.dto.af.request.fs.AFFSTrxHistReqDTO;
import com.m2e.dashboard.dto.af.request.fs.download.AFFSAcctListDownloadReqDTO;
import com.m2e.dashboard.dto.af.request.fs.download.AFFSSubAcctListDownloadReqDTO;
import com.m2e.dashboard.dto.af.request.fs.download.AFFSTrxHistDownloadReqDTO;
import com.m2e.dashboard.dto.af.response.fs.*;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.base.af.AFAccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.*;
import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.jms.af.fs.request.account.activityinquiry.ESBAFFSAcctActivityInquiryReqDTO;
import com.m2e.dashboard.jms.af.fs.request.account.details.ESBAFFSCertDetailsReqDTO;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import com.m2e.dashboard.projection.AcctStatusProjection;
import com.m2e.dashboard.projection.DepositAcctDetailPageProjection;
import com.m2e.dashboard.repository.*;
import com.m2e.dashboard.service.AsyncPaginationService;
import com.m2e.dashboard.service.FileRepoService;
import com.m2e.dashboard.service.af.fs.jms.AFFSESBService;
import com.m2e.dashboard.service.af.fs.sp.AFFloorStockingSPService;
import com.m2e.dashboard.sp.af.fs.request.*;
import com.m2e.dashboard.sp.af.fs.response.AFFSAcctListSpDTO;
import com.m2e.dashboard.sp.af.fs.response.AFFSAcctStatsViewByAcctSpDTO;
import com.m2e.dashboard.sp.af.fs.response.AFFSAcctStatsViewByFacilitySpDTO;
import com.m2e.dashboard.sp.af.fs.response.AFFSCertListSpDTO;
import com.m2e.dashboard.util.DateTimeUtil;
import com.m2e.model.sso.base.AccountProductTypeMatrix;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static com.m2e.dashboard.enums.AcctProductEntitlementConst.LOAN_AND_CARD_PAYMENT_PRODUCT_CODE;
import static com.m2e.dashboard.enums.AcctProductEntitlementConst.USER_ACCT_PRODUCT_LIST_REDIS_KEY;
import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_AF_FS_ACCTLIST;
import static com.m2e.dashboard.enums.FilterAutoFinanceAccountListingEnum.ACCOUNT;
import static com.m2e.dashboard.enums.FilterAutoFinanceAccountListingEnum.FACILITY;
import static com.m2e.dashboard.enums.ReportCommonConstant.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AFFSService {
    private static final Logger logger = LoggerFactory.getLogger(AFFSService.class);
    private static final String ACCT_TYP_FOUND = "Account Type Not Found";
    private final int batchSize = 10;

    @Value("${cache.ttl}")
    private Duration cacheDuration;

    private final JwtUtil jwtUtil;
    private final AFFSAcctListViewAcctFileService afFSAcctListViewAcctFileService;
    private final AFFSAcctListViewFacilityFileService afFSAcctListViewFacilityFileService;
    private final AFFSSubAcctListFileService afFSSubAcctListFileService;
    private final AFFSTrxHistFileService afFSTrxHistFileService;
    private final AFFloorStockingSPService afFloorStockingSPService;
    private final AFFSESBService affsesbService;
    private final AccountRepository accountRepository;
    private final FloorStockRepository floorStockRepository;
    private final HostMatrixProductRepository hostProductMatrixRepository;
    private final AsyncPaginationService asyncPaginationService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final RedisHelper redisHelper;
    private final AccountProductTypeMatrixRepository matrixRepository;
    private final AccountStatusRepository accountStatusRepository;
    private final FileRepoService fileRepoService;

    public ApiResponse<AFFSAcctStatsDTO> floorStockingAccountStats(AFFSAcctStatsReqDTO request, UserAndCorp userAndCorp) {
        logger.trace("begin floorStockingAccountStats()");
        removeAllFromCache(userAndCorp);
        AFFSAcctStatsDTO data;

        switch (request.getViewByIndicator()) {
            case ACCOUNT -> {
                AFFSAcctStatsViewByAcctSpDTO statsSpDto = retreiveFSStatsViewByAcct(userAndCorp);

//                if (Boolean.TRUE.equals(request.isRealTimeIndicator())) {
//                    List<AFFSCertListSpDTO> account = acctStatAcctRealTime(userAndCorp);
//                    statsSpDto.setConsolidatedFinancedAmt(getFinancedAmountAccount(account));
//                    statsSpDto.setConsolidatedOutstandingBalance(getOutstandingAccount(account));
//                }
                data = modelMapper.map(statsSpDto, AFFSAcctStatViewAcctDTO.class);
            }
            case FACILITY -> {
                AFFSAcctStatsViewByFacilitySpDTO statsSpDto = retreiveFSStatsViewByFacility(userAndCorp);

//                if (Boolean.TRUE.equals(request.isRealTimeIndicator())) {
//                    List<AFFSMasterAcctDetailDTO> facility = acctStatFacilityRealTime(userAndCorp);
//                    statsSpDto.setConsolidatedFacilityLimit(getFacilityLimitFacility(facility));
//                }

                data = modelMapper.map(statsSpDto, AFFSAcctStatsViewFacilityDTO.class);
            }
            default -> throw new IllegalArgumentException("View By Indicator is undefined");
        }

        if (Boolean.TRUE.equals(request.isRealTimeIndicator())) {
            data.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
        }

        data.setDisplayShowViewBy(afFloorStockingSPService.getDisplayShowViewBy(new AFFSDisplayViewBySpParam(userAndCorp.getUserId(), userAndCorp.getCorpId())));

        logger.trace("end floorStockingAccountStats()");
        return new ApiResponse<>(new ErrorResponse(), data);
    }

    public ApiResponse<AFFSAcctListViewSummaryResp<Object>> floorStockingAccountListingByAccount(UserAndCorp userAndCorp, AFAccountListReq request) {
        logger.trace("begin floorStockingAccountListingByAccount()");
        AFFSAcctListViewSummaryResp<Object> acctListSummary = new AFFSAcctListViewSummaryResp<>();
        ApiResponse<AFFSAcctListViewSummaryResp<Object>> certListResponse;

        certListResponse = getAcctFromCache(userAndCorp, request);

        if (certListResponse == null) {
            SearchCollectionResponse<AFFSCertListSpDTO> spResult = retreiveFSAccountList(userAndCorp, request);
            SearchCollectionResponse<Object> viewByAcctListMYResp = new SearchCollectionResponse<>(spResult.getTotalPages(), spResult.getCurrentPage(), spResult.getNextPage(), spResult.getTotalRecordCount());
            List<AFFSCertListSpDTO> certList = spResult.getData();
            if (certList == null) {
                logger.info("No cert found in database");
                return certListResponse;
            }
            List<Object> fsViewByAcctList;
            if (request.isRealTimeIndicator()) {
                logger.info("T real time");
                // Categorize the list retrieved from SP by their provider to cater for ESB calls
                Map<String, List<AFFSCertListSpDTO>> certMapCaterogizedByProvider = categorizeAcctListByProvider(certList);
                // Call ESB to get acct list T case details
                fsViewByAcctList = callESBByProvider(acctListSummary, certMapCaterogizedByProvider); // stores the full acct list from all providers
            } else {
                logger.info("T-1 non real time");
                fsViewByAcctList = new ArrayList<>(certList);
            }

            setMoreActionsToListing(fsViewByAcctList, userAndCorp);
            viewByAcctListMYResp.setData(fsViewByAcctList);
            acctListSummary.setDatas(viewByAcctListMYResp);
            acctListSummary.setCorpCifNo(request.getCorpCifNo());
            certListResponse = new ApiResponse<>(new ErrorResponse("0"), acctListSummary);

            redisHelper.save(getRedisKey(userAndCorp, request), certListResponse, cacheDuration.toMinutes(), TimeUnit.MINUTES);
        }

        logger.trace("end floorStockingAccountListingByAccount()");
        return certListResponse;
    }

    private MoreActionsEntitlement getMoreActions(String accountNo, String currency, UserAndCorp userAndCorp) {
        String makePaymentRedisKey = redisHelper.buildCacheKey(userAndCorp.getUserId(), userAndCorp.getCorpId(), USER_ACCT_PRODUCT_LIST_REDIS_KEY, LOAN_AND_CARD_PAYMENT_PRODUCT_CODE, accountNo, currency);
        logger.debug("getMoreActions() makePaymentRedisKey Redis key: {}", makePaymentRedisKey);
        MoreActionsEntitlement moreActionsEntitlement = new MoreActionsEntitlement();
        moreActionsEntitlement.setMakePayment(new MoreActionsRedirection(redisHelper.isPresent(makePaymentRedisKey), RedirectModuleEnum.PAYMENT_ACTION_REDIRECT.name(), Application.CASHLITE.getValue(), LOAN_AND_CARD_PAYMENT_PRODUCT_CODE));
        return moreActionsEntitlement;
    }

    public ApiResponse<AFFSAcctListViewSummaryResp<Object>> floorStockingAccountListingByFacility(UserAndCorp userAndCorp, AFAccountListReq request) {
        logger.trace("begin floorStockingAccountListingByFacility()");
        AFFSAcctListViewSummaryResp<Object> acctListSummary = new AFFSAcctListViewSummaryResp<>();
        ApiResponse<AFFSAcctListViewSummaryResp<Object>> accountListResponse;

        accountListResponse = getAcctFromCache(userAndCorp, request);

        if (accountListResponse == null) {
            SearchCollectionResponse<AFFSAcctListSpDTO> spResult = retreiveFSMasterAccountList(userAndCorp, request);
            SearchCollectionResponse<Object> viewByFacilityListResp = new SearchCollectionResponse<>(spResult.getTotalPages(), spResult.getCurrentPage(), spResult.getNextPage(), spResult.getTotalRecordCount());

            List<AFFSAcctListSpDTO> masterAcctList = spResult.getData();
            List<Object> fsMainAccountList;
            if (masterAcctList == null) {
                logger.error("No Floor Stock account found in database");
                return accountListResponse;
            }

            if (request.isRealTimeIndicator()) {
                logger.info("T real time");
                AFAcctListESBDTO afAcctListESBDTO = getFacilityDataFromESB(masterAcctList);
                fsMainAccountList = combineFacilityDetailsMY(masterAcctList, afAcctListESBDTO);
                acctListSummary.setEsbMetadata(afAcctListESBDTO.getEsbMetadata());
            } else {
                logger.info("T-1 non real time");
                fsMainAccountList = new ArrayList<>(masterAcctList);
            }

            setMoreActionsToListing(fsMainAccountList, userAndCorp);
            viewByFacilityListResp.setData(fsMainAccountList);
            acctListSummary.setDatas(viewByFacilityListResp);
            acctListSummary.setCorpCifNo(request.getCorpCifNo());
            accountListResponse = new ApiResponse<>(new ErrorResponse("0"), acctListSummary);

            redisHelper.save(getRedisKey(userAndCorp, request), accountListResponse);
        }

        logger.trace("end floorStockingAccountListingByFacility()");
        return accountListResponse;
    }

    private void setMoreActionsToListing(List<Object> list, UserAndCorp userAndCorp) {
        list.forEach(dto -> {
            if (dto instanceof AFFSAcctListSpDTO acct) {
                acct.setMoreActions(getMoreActions(acct.getMasterAcctNumber(), acct.getCurrency(), userAndCorp));
            } else if (dto instanceof AFFSCertListSpDTO cert) {
                if (cert.getProvider().equals(ProviderEnum.SG.getProvider())) {
                    cert.setMoreActions(getMoreActions(cert.getCertNo(), cert.getCurrency(), userAndCorp));
                } else {
                    cert.setMoreActions(getMoreActions(cert.getMasterAcctNumber(), cert.getCurrency(), userAndCorp));
                }
            }
        });
    }

    public ApiResponse<AFFSSubAcctListViewSummaryResp> floorStockingSubAcctListing(UserAndCorp userAndCorp, AFFSCertListReqDTO request) {
        AFFSSubAcctListViewSummaryResp affsSubAcctListViewSummaryResp = new AFFSSubAcctListViewSummaryResp();
        List<AFFSSubAcctListDTO> fsCertlist = new ArrayList<>();
        SearchCollectionResponse<AFFSSubAcctListDTO> spResult = retreiveFSSubAccountList(request);
        List<AFFSSubAcctListDTO> certList = spResult.getData();
        SearchCollectionResponse<AFFSSubAcctListDTO> certListResp = new SearchCollectionResponse<>(
                spResult.getTotalPages(),
                spResult.getCurrentPage(),
                spResult.getNextPage(),
                spResult.getTotalRecordCount()
        );

        AtomicReference<AFAcctListESBDTO> afAcctListESBDTO = new AtomicReference<>(new AFAcctListESBDTO());
        List<AFFSCertDetailDTO> certDetailDTOS = new ArrayList<>();
        if (!certList.isEmpty()) {
            IntStream.range(0, (certList.size() + batchSize - 1) / batchSize)
                    .forEach(i -> {
                        int start = i * batchSize;
                        int end = Math.min(start + batchSize, certList.size());
                        List<AFFSSubAcctListDTO> batch = certList.subList(start, end);

                        List<ESBAFFSCertDetailsReqDTO> certDetailsRequestList =
                                batch.stream().map(cert ->
                                        ESBAFFSCertDetailsReqDTO.builder().accountNo(request.getAcctNumber())
                                                .certNo(cert.getCertNo())
                                                .currency(cert.getCurrency())
                                                .accountType(getAcctTypeCode(request.getProvider()))
                                                .region(request.getProvider()).build()).toList();
                        afAcctListESBDTO.set(affsesbService.getAFFSCertDetailsMY(certDetailsRequestList).getMsgBody());
                        if (afAcctListESBDTO.get().getAcctListing() != null && !afAcctListESBDTO.get().getAcctListing().isEmpty())
                            certDetailDTOS.addAll((List<AFFSCertDetailDTO>) afAcctListESBDTO.get().getAcctListing());
                    });

            log.info("certListResult: {}", certDetailDTOS);

            certList.forEach(cert -> {
                        log.info("cert.getCertNo(): {}", cert.getCertNo());
                        AFFSSubAcctListDTO affsSubAcctListDTO = new AFFSSubAcctListDTO();
                        affsSubAcctListDTO.setMasterAcctName(cert.getMasterAcctName());
                        affsSubAcctListDTO.setCertNo(cert.getCertNo());
                        affsSubAcctListDTO.setCurrency(cert.getCurrency());
                        affsSubAcctListDTO.setMoreActions(getMoreActions(request.getAcctNumber(), cert.getCurrency(), userAndCorp));
                        affsSubAcctListDTO.setAcctStatusCode(cert.getAcctStatusCode());
                        affsSubAcctListDTO.setAcctStatusDesc(cert.getAcctStatusDesc());

                        certDetailDTOS.forEach(esbCertDetail -> {
                            log.info("esbCertDetail.getCertNo(): {}", esbCertDetail.getCertNo());

                            // If acct no from DB and ESB matches, combine the data
                            if (cert.getCertNo().equalsIgnoreCase(esbCertDetail.getCertNo())) {
                                log.info("financed: {}", esbCertDetail.getFinancedAmt());
                                log.info("outstanding: {}", esbCertDetail.getOutstandingBalance());

                                affsSubAcctListDTO.setFinancedAmount(esbCertDetail.getFinancedAmt());
                                affsSubAcctListDTO.setOutstandingBalance(esbCertDetail.getOutstandingBalance());
                                affsSubAcctListDTO.setSettlementAmt(esbCertDetail.getSettlementAmt());
                            }
                        });
                        fsCertlist.add(affsSubAcctListDTO);
                    }
            );
            affsSubAcctListViewSummaryResp.setEsbMetadata(afAcctListESBDTO.get().getEsbMetadata());
        }
        certListResp.setData(fsCertlist);
        affsSubAcctListViewSummaryResp.setDatas(certListResp);
        return new ApiResponse<>(new ErrorResponse("0"), affsSubAcctListViewSummaryResp);
    }

    public ApiResponse<?> floorStockingAcctDetail(UserAndCorp userAndCorp, AFFSAcctDetailReqDTO request) {
        Object data;
        switch (request.getProvider()) {
            case MY:
                switch (request.getAccountLevel()) {
                    case FACILITY -> data = floorStockingMYMasterAcctDetail(request);
                    case ACCOUNT -> data = floorStockingMYCertDetail(request, userAndCorp);
                    default -> throw new IllegalArgumentException("Invalid account level indicator");
                }
                break;
            case SG:
                data = floorStockingSGAcctDetail(request, userAndCorp);
                break;
            default:
                log.error("Provider is unrecognized");
                return new ApiResponse<>(new ErrorResponse("Provider is unrecognized"), null);
        }

        return new ApiResponse<>(new ErrorResponse(), data);
    }

    public ApiResponse<AFFSTrxHistoryResp> floorStockingTrxHistory(AFFSTrxHistReqDTO request, String auth) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        ApiResponse<AFFSTrxHistoryResp> apiResponse = new ApiResponse<>();
        apiResponse.setMsgBody(getTrxHistoryData(request, userAndCorp));
        return apiResponse;
    }

    private AFFSTrxHistoryResp getTrxHistoryData(AFFSTrxHistReqDTO request, UserAndCorp userAndCorp) {
        AFFSTrxHistoryResp affsTrxHistoryResp = new AFFSTrxHistoryResp();

        AFFSTrxHistorySpParam affsTrxHistorySpParam = getTrxHistorySpParam(request, userAndCorp);
        SearchCollectionResponse<AFFSTrxHistDTO> spTrxHistory = getTrxHistoryWithSp(affsTrxHistorySpParam);

        switch (request.getProvider()) {
            case "MY":
                ESBAFFSAcctActivityInquiryReqDTO esbTrxHistoryRequest = modelMapper.map(request, ESBAFFSAcctActivityInquiryReqDTO.class);
                affsTrxHistoryResp = affsesbService.getAFFSTrxHistoryMY(esbTrxHistoryRequest).getMsgBody();

                if (affsTrxHistoryResp.getDatas() != null && affsTrxHistoryResp.getDatas().getData() != null) {
                    if (affsTrxHistoryResp.getDatas().getData().size() < request.getPageSize()) {
                        logger.info("ESB Data size < request page size");
                        affsTrxHistoryResp.setDatas(combineEsbAndSpTrxHistory(affsTrxHistoryResp.getDatas(), spTrxHistory, request));
                    }
                } else {
                    logger.info("ESB result is null or empty");
                    affsTrxHistoryResp.setDatas(getTrxHistoryWithSp(affsTrxHistorySpParam));
                }
                break;
            case "SG":
                affsTrxHistoryResp.setDatas(getTrxHistoryWithSp(affsTrxHistorySpParam));
                break;
            default:
                log.error("Provider is unrecognized");
        }

        return affsTrxHistoryResp;
    }

    private AFFSTrxHistorySpParam getTrxHistorySpParam(AFFSTrxHistReqDTO request, UserAndCorp userAndCorp) {
        AFFSTrxHistorySpParam affsTrxHistorySpParam = new AFFSTrxHistorySpParam(request.getAcctNumber(), userAndCorp.getCorpId(), userAndCorp.getUserId());
        affsTrxHistorySpParam.setCurrency(request.getCurrency());
        affsTrxHistorySpParam.setSearchParam(request.getSearchParam());
        affsTrxHistorySpParam.setTrxCode(request.getFilters().getTrxCode());
        if (!request.getFilters().getAmtFrom().isEmpty() && !request.getFilters().getAmtTo().isEmpty()) {
            affsTrxHistorySpParam.setAmtFrom(new BigDecimal(request.getFilters().getAmtFrom()));
            affsTrxHistorySpParam.setAmtTo(new BigDecimal(request.getFilters().getAmtTo()));
        }
        if (StringUtils.hasText(request.getFilters().getStartDate()) && StringUtils.hasText(request.getFilters().getEndDate()) && StringUtils.hasText(request.getFilters().getStartTime()) && StringUtils.hasText(request.getFilters().getEndTime())) {
            affsTrxHistorySpParam.setStartDateTime(DateTimeUtil.convert12HourDateTime(request.getFilters().getStartDate(), request.getFilters().getStartTime(), true));
            affsTrxHistorySpParam.setEndDateTime(DateTimeUtil.convert12HourDateTime(request.getFilters().getEndDate(), request.getFilters().getEndTime(), false));
        }
        affsTrxHistorySpParam.setPage(request.getPage());
        affsTrxHistorySpParam.setPageSize(request.getPageSize());
        return affsTrxHistorySpParam;
    }

    private SearchCollectionResponse<AFFSTrxHistDTO> getTrxHistoryWithSp(AFFSTrxHistorySpParam affsTrxHistorySpParam) {
        logger.info("Retrieving trx history with stored procedure");
        SearchCollectionResponse<AFFSTrxHistDTO> result = new SearchCollectionResponse<>();

        BaseSpDto<AFFSTrxHistorySpParam, AFFSTrxHistDTO> resultBaseSpDto = afFloorStockingSPService.trxHistories(affsTrxHistorySpParam);
        if (resultBaseSpDto.getResults() == null) {
            logger.error("Empty SP result for Floor Stock trxHistory");
            result.setData(new ArrayList<>());
            result.setTotalRecordCount(0);
            return result;
        }

        result.setNextPage(
                affsTrxHistorySpParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : affsTrxHistorySpParam.getPage() + 1
        );
        result.setCurrentPage(affsTrxHistorySpParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(resultBaseSpDto.getResults());
        return result;
    }

    private SearchCollectionResponse<AFFSTrxHistDTO> combineEsbAndSpTrxHistory(SearchCollectionResponse<AFFSTrxHistDTO> esbData, SearchCollectionResponse<AFFSTrxHistDTO> spData, AFFSTrxHistReqDTO request) {
        List<AFFSTrxHistDTO> esbDataList = esbData.getData();
        List<AFFSTrxHistDTO> spDataList = spData.getData();
        int remainingSize = request.getPageSize() - esbDataList.size();

        List<AFFSTrxHistDTO> fusedFloorStockTrxHistoryResList = new ArrayList<>(esbDataList);
        if (spDataList != null)
            fusedFloorStockTrxHistoryResList.addAll(spDataList.subList(0, Math.min(remainingSize, spDataList.size())));

        // total record count of both esb and sp data
        int esbTotalRecordCount = Optional.of(esbData)
                .map(SearchCollectionResponse::getTotalRecordCount)
                .orElse(0);
        int spTotalRecordCount = Optional.of(spData)
                .map(SearchCollectionResponse::getTotalRecordCount)
                .orElse(0);
        int totalRecordCount = esbTotalRecordCount + spTotalRecordCount;

        // fuse both data from both ESB and sp
        return asyncPaginationService.constructSearchCollectionResponse(fusedFloorStockTrxHistoryResList, request.getPageSize(), request.getPage(), totalRecordCount);
    }

    public DownloadDTO downloadFloorStocking(UserAndCorp userAndCorp, DownloadReq request) {
        return switch (request.getReportType()) {
            case AF_FS_ACCOUNT_LISTING_RPT_TYPE -> downloadAcctListing(userAndCorp, request);
            case AF_FS_SUB_ACCOUNT_LISTING_RPT_TYPE -> downloadSubAcctListing(userAndCorp, request);
            case AF_FS_TRX_HISTORY_RPT_TYPE -> downloadTrxHistory(userAndCorp, request);
            default -> throw new IllegalArgumentException("Report type is undefined");
        };
    }

    public DownloadDTO downloadAcctListing(UserAndCorp userAndCorp, DownloadReq downloadReq) {
        DownloadDTO result;
        AFFSAcctListDownloadReqDTO req = (AFFSAcctListDownloadReqDTO) downloadReq;
        switch (req.getViewByIndicator()) {
            case FACILITY -> result = downloadFacilityViewList(userAndCorp, req);
            case ACCOUNT -> result = downloadAccountViewList(userAndCorp, req);
            default -> result = new DownloadDTO();
        }
        return result;
    }

    public DownloadDTO downloadSubAcctListing(UserAndCorp userAndCorp, DownloadReq request) {
        AFFSSubAcctListDownloadReqDTO downloadRequest = (AFFSSubAcctListDownloadReqDTO) request;
        List<AFFSSubAcctListDTO> fsSubAcctDataList = getDownloadSubAcctList(userAndCorp, downloadRequest);

        if (fsSubAcctDataList.isEmpty()) return new DownloadDTO();
        DownloadDTO response = new DownloadDTO();
        afFSSubAcctListFileService.generateSubAcctListFile(response, fsSubAcctDataList, downloadRequest);
        return response;
    }

    public DownloadDTO downloadTrxHistory(UserAndCorp userAndCorp, DownloadReq request) {
        DownloadDTO response = new DownloadDTO();
        AFFSTrxHistDownloadReqDTO downloadRequest = (AFFSTrxHistDownloadReqDTO) request;

        Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetailByAcctNumberAndProvider(downloadRequest.getAcctNumber(), downloadRequest.getProvider());

        if (accountData.isEmpty()) {
            logger.error("Account Not Found for: " + downloadRequest.getAcctNumber());
            return response;
        }

        fileRepoService.insertFileRepo(request, userAndCorp, response, accountData.get(), downloadRequest.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_1);

        AFFSTrxHistReqDTO trxHistoryReq = modelMapper.map(request, AFFSTrxHistReqDTO.class);
        trxHistoryReq.setPage(1);
        trxHistoryReq.setPageSize(Integer.MAX_VALUE);

        fileRepoService.insertFileRepo(request, userAndCorp, response, accountData.get(), downloadRequest.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_2);

        AFFSTrxHistoryResp trxHistoryData = getTrxHistoryData(trxHistoryReq, userAndCorp);
        fileRepoService.insertFileRepo(request, userAndCorp, response, accountData.get(), downloadRequest.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_3);

        afFSTrxHistFileService.generateTrxHistoryFile(response, trxHistoryData.getDatas().getData(), downloadRequest);

        fileRepoService.insertFileRepo(request, userAndCorp, response, accountData.get(), downloadRequest.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_4);

        return response;
    }

    private List<Object> callESBByProvider(AFFSAcctListViewSummaryResp<Object> acctListSummary, Map<String, List<AFFSCertListSpDTO>> certMapCaterogizedByProvider) {
        List<Object> fsViewByAcctList = new ArrayList<>();

        // MY
        List<AFFSCertListSpDTO> certList = getDataFromCategorizedList(certMapCaterogizedByProvider, ProviderEnum.MY.name());
        if (!certList.isEmpty()) {
            AFAcctListESBDTO afAcctListESBDTO = getESBAcctListDataMY(certList, ProviderEnum.MY.name());
            List<Object> acctListMY = combineCertDetailsMY(certList, afAcctListESBDTO);
            fsViewByAcctList.addAll(acctListMY);
            acctListSummary.setEsbMetadata(afAcctListESBDTO.getEsbMetadata());
        }
        // SG
        AFAcctListESBDTO afAcctListESBDTOSG = getESBAcctListDataSG(getDataFromCategorizedList(certMapCaterogizedByProvider, ProviderEnum.SG.name()), ProviderEnum.SG.name());
        fsViewByAcctList.addAll(afAcctListESBDTOSG.getAcctListing());
        acctListSummary.setEsbMetadata(afAcctListESBDTOSG.getEsbMetadata());

        return fsViewByAcctList;
    }

    private AFAcctListESBDTO getESBAcctListDataMY(List<AFFSCertListSpDTO> certList, String provider) {
        AtomicReference<AFAcctListESBDTO> afAcctListESBDTO = new AtomicReference<>(new AFAcctListESBDTO());
        List<AFFSCertDetailDTO> result = new ArrayList<>();

        IntStream.range(0, (certList.size() + batchSize - 1) / batchSize)
                .forEach(i -> {
                    int start = i * batchSize;
                    int end = Math.min(start + batchSize, certList.size());
                    List<AFFSCertListSpDTO> batch = certList.subList(start, end);

                    List<ESBAFFSCertDetailsReqDTO> acctDetailsReqList = batch.stream().map(cert ->
                            ESBAFFSCertDetailsReqDTO.builder().accountNo(cert.getMasterAcctNumber())
                                    .certNo(cert.getCertNo())
                                    .currency(cert.getCurrency())
                                    .accountType(getAcctTypeCode(provider))
                                    .region(provider).build()
                    ).toList();
                    afAcctListESBDTO.set(affsesbService.getAFFSCertDetailsMY(acctDetailsReqList).getMsgBody());
                    if (afAcctListESBDTO.get().getAcctListing() != null && !afAcctListESBDTO.get().getAcctListing().isEmpty())
                        result.addAll((List<AFFSCertDetailDTO>) afAcctListESBDTO.get().getAcctListing());
                });

        afAcctListESBDTO.get().setAcctListing(result);

        return afAcctListESBDTO.get();
    }

    private AFAcctListESBDTO getESBAcctListDataSG(List<AFFSCertListSpDTO> certList, String provider) {
        AtomicReference<AFAcctListESBDTO> afAcctListESBDTO = new AtomicReference<>(new AFAcctListESBDTO());
        List<AFFSSGAcctDetailDTO> result = new ArrayList<>();
        IntStream.range(0, (certList.size() + batchSize - 1) / batchSize)
                .forEach(i -> {
                    int start = i * batchSize;
                    int end = Math.min(start + batchSize, certList.size());
                    List<AFFSCertListSpDTO> batch = certList.subList(start, end);

                    List<ESBAcctListingReqDTO> acctDetailsReqList = batch.stream().map(account ->
                            ESBAcctListingReqDTO.builder().accountNumber(account.getCertNo())
                                    .currency(account.getCurrency())
                                    .accountType(getAcctTypeCode(provider))
                                    .region(provider).build()
                    ).toList();
                    afAcctListESBDTO.set(affsesbService.getAFFSAcctDetailsSG(acctDetailsReqList).getMsgBody());
                    if (afAcctListESBDTO.get().getAcctListing() != null && !afAcctListESBDTO.get().getAcctListing().isEmpty())
                        result.addAll((List<AFFSSGAcctDetailDTO>) afAcctListESBDTO.get().getAcctListing());
                    logger.info("loop acctDetailDTOS: {}, acctDetailDTOS size: {}", result, result.size());
                });

        logger.info("acct: {}, acct size: {}", result, result.size());

        afAcctListESBDTO.get().setAcctListing(combineAcctDetailsSG(certList, result));
        return afAcctListESBDTO.get();
    }

    private List<AFFSCertListSpDTO> getDataFromCategorizedList(Map<String, List<AFFSCertListSpDTO>> certMapCaterogizedByProvider, String provider) {
        List<AFFSCertListSpDTO> list = certMapCaterogizedByProvider.get(provider);
        if (list != null)
            return list;
        else
            return new ArrayList<>();
    }

    private Map<String, List<AFFSCertListSpDTO>> categorizeAcctListByProvider(List<AFFSCertListSpDTO> certList) {
        Map<String, List<AFFSCertListSpDTO>> certMapCaterogizedByProvider = new HashMap();
        List<AFFSCertListSpDTO> certListMY = new ArrayList<>();
        List<AFFSCertListSpDTO> certListSG = new ArrayList<>();
        certList.forEach(cert -> {
            switch (cert.getProvider()) {
                case "MY" -> certListMY.add(cert);
                case "SG" -> certListSG.add(cert);
                default -> log.error("Invalid provider for cert list");
            }
        });
        certMapCaterogizedByProvider.put(ProviderEnum.MY.name(), certListMY);
        certMapCaterogizedByProvider.put(ProviderEnum.SG.name(), certListSG);
        return certMapCaterogizedByProvider;
    }

    private List<Object> combineFacilityDetailsMY(List<AFFSAcctListSpDTO> masterAcctList, AFAcctListESBDTO esbMasterAcctDetails) {
        List<Object> fsMainAccountList = new ArrayList<>();
        List<AFFSMasterAcctDetailDTO> esbTemp = (List<AFFSMasterAcctDetailDTO>) esbMasterAcctDetails.getAcctListing();
        logger.info("tData: {}", esbTemp);
        logger.info("t1Data: {}", masterAcctList);

        masterAcctList.forEach(masterAcct -> {
            AFFSAcctListSpDTO affsSAcctList = new AFFSAcctListSpDTO();
            affsSAcctList.setMasterAcctName(masterAcct.getMasterAcctName());
            affsSAcctList.setMasterAcctNumber(masterAcct.getMasterAcctNumber());
            affsSAcctList.setCurrency(masterAcct.getCurrency());
            affsSAcctList.setNumberOfCert(masterAcct.getNumberOfCert());
            affsSAcctList.setProvider(masterAcct.getProvider());
            affsSAcctList.setAcctStatusCode(masterAcct.getAcctStatusCode());
            affsSAcctList.setAcctStatusDesc(masterAcct.getAcctStatusDesc());
            if (esbTemp == null) return;
            esbTemp.forEach(esbMasterAcctDetail -> {
                // If acct no from DB and ESB matches, combine the data
                if (masterAcct.getMasterAcctNumber().equals(esbMasterAcctDetail.getAcctNumber().replaceFirst("^0+(?!$)", ""))) {
                    logger.info("account: {}, facilitylimit: {}, status: {}",
                            masterAcct.getMasterAcctNumber(), esbMasterAcctDetail.getFacilityLimit(), masterAcct.getAcctStatusDesc());
                    boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(masterAcct.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(masterAcct.getAcctStatusDesc());

                    affsSAcctList.setTotalFacilityLimit(isClosed ? null : esbMasterAcctDetail.getFacilityLimit());
                }
            });
            fsMainAccountList.add(affsSAcctList);
        });
        return fsMainAccountList;
    }

    private List<Object> combineCertDetailsMY(List<AFFSCertListSpDTO> certList, AFAcctListESBDTO esbCertAcctDetailsMY) {
        List<Object> fsViewByAcctList = new ArrayList<>();
        List<AFFSCertDetailDTO> certDetailDTOS = (List<AFFSCertDetailDTO>) esbCertAcctDetailsMY.getAcctListing();

        if (esbCertAcctDetailsMY.getAcctListing() == null) {
            certList.forEach(cert -> {
                AFFSCertListSpDTO affsAcctListViewAcctMYDTO = new AFFSCertListSpDTO();
                affsAcctListViewAcctMYDTO.setMasterAcctName(cert.getMasterAcctName());
                affsAcctListViewAcctMYDTO.setMasterAcctNumber(cert.getMasterAcctNumber());
                affsAcctListViewAcctMYDTO.setCertNo(cert.getCertNo());
                affsAcctListViewAcctMYDTO.setProvider(cert.getProvider());
                affsAcctListViewAcctMYDTO.setCurrency(cert.getCurrency());
                affsAcctListViewAcctMYDTO.setAcctStatusCode(cert.getAcctStatusCode());
                affsAcctListViewAcctMYDTO.setAcctStatusDesc(cert.getAcctStatusDesc());
                fsViewByAcctList.add(affsAcctListViewAcctMYDTO);
            });
        } else {
            certList.forEach(cert -> {
                AFFSCertListSpDTO affsAcctListViewAcctMYDTO = new AFFSCertListSpDTO();
                affsAcctListViewAcctMYDTO.setMasterAcctName(cert.getMasterAcctName());
                affsAcctListViewAcctMYDTO.setMasterAcctNumber(cert.getMasterAcctNumber());
                affsAcctListViewAcctMYDTO.setCertNo(cert.getCertNo());
                affsAcctListViewAcctMYDTO.setProvider(cert.getProvider());
                affsAcctListViewAcctMYDTO.setCurrency(cert.getCurrency());
                affsAcctListViewAcctMYDTO.setAcctStatusCode(cert.getAcctStatusCode());
                affsAcctListViewAcctMYDTO.setAcctStatusDesc(cert.getAcctStatusDesc());
                certDetailDTOS.forEach(esbCertDetail -> {
                    // If cert no from DB and ESB matches, combine the data
                    if (cert.getCertNo().equals(esbCertDetail.getCertNo())) {
                        logger.info("cert detail: {}, outstanding bal: {}", esbCertDetail, esbCertDetail.getOutstandingBalance());
                        boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(cert.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(cert.getAcctStatusDesc());

                        if (!isClosed) {
                            affsAcctListViewAcctMYDTO.setFinancedAmount(esbCertDetail.getFinancedAmt());
                            affsAcctListViewAcctMYDTO.setOutstandingBalance(esbCertDetail.getOutstandingBalance());
                            affsAcctListViewAcctMYDTO.setSettlementAmt(esbCertDetail.getSettlementAmt());
                        }
                    }
                });
                fsViewByAcctList.add(affsAcctListViewAcctMYDTO);
            });
        }
        return fsViewByAcctList;
    }

    private List<Object> combineAcctDetailsSG(List<AFFSCertListSpDTO> accountList, List<AFFSSGAcctDetailDTO> esbAcctDetailsSG) {
        List<Object> fsViewByAcctList = new ArrayList<>();
        if (esbAcctDetailsSG == null) {
            accountList.forEach(acct -> {
                AFFSCertListSpDTO affsAcctListViewAcctSGDTO = new AFFSCertListSpDTO();
                affsAcctListViewAcctSGDTO.setMasterAcctName(acct.getMasterAcctName());
                affsAcctListViewAcctSGDTO.setMasterAcctNumber(null);
                affsAcctListViewAcctSGDTO.setCertNo(acct.getCertNo());
                affsAcctListViewAcctSGDTO.setProvider(acct.getProvider());
                affsAcctListViewAcctSGDTO.setCurrency(acct.getCurrency());
                affsAcctListViewAcctSGDTO.setTlastIndicator(acct.getTlastIndicator());
                affsAcctListViewAcctSGDTO.setAcctStatusCode(acct.getAcctStatusCode());
                affsAcctListViewAcctSGDTO.setAcctStatusDesc(acct.getAcctStatusDesc());
                fsViewByAcctList.add(affsAcctListViewAcctSGDTO);
            });
        } else {
            accountList.forEach(acct -> {
                AFFSCertListSpDTO affsAcctListViewAcctSGDTO = new AFFSCertListSpDTO();
                affsAcctListViewAcctSGDTO.setMasterAcctName(acct.getMasterAcctName());
                affsAcctListViewAcctSGDTO.setMasterAcctNumber(null);
                affsAcctListViewAcctSGDTO.setCertNo(acct.getCertNo());
                affsAcctListViewAcctSGDTO.setProvider(acct.getProvider());
                affsAcctListViewAcctSGDTO.setCurrency(acct.getCurrency());
                affsAcctListViewAcctSGDTO.setAcctStatusCode(acct.getAcctStatusCode());
                affsAcctListViewAcctSGDTO.setAcctStatusDesc(acct.getAcctStatusDesc());
                affsAcctListViewAcctSGDTO.setChasisNo(acct.getChasisNo());
                esbAcctDetailsSG.forEach(esbAcctDetailSG -> {
                    // If acct no from DB and ESB matches, combine the data
                    if (acct.getCertNo().equals(esbAcctDetailSG.getAcctNumber().replaceFirst("^0+(?!$)", ""))) {
                        boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(acct.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(acct.getAcctStatusDesc());

                        if (!isClosed) {
                            affsAcctListViewAcctSGDTO.setFinancedAmount(esbAcctDetailSG.getFinancedAmt());
                            affsAcctListViewAcctSGDTO.setOutstandingBalance(esbAcctDetailSG.getOutstandingBalance());
                            affsAcctListViewAcctSGDTO.setSettlementAmt(esbAcctDetailSG.getSettlementAmt());
                        }
                        affsAcctListViewAcctSGDTO.setTlastIndicator(esbAcctDetailSG.getTlastIndicator());
                    }
                });
                fsViewByAcctList.add(affsAcctListViewAcctSGDTO);
            });
        }
        return fsViewByAcctList;
    }

    private AFAcctListESBDTO getFacilityDataFromESB(List<AFFSAcctListSpDTO> masterAcctList) {
        AtomicReference<AFAcctListESBDTO> afAcctListESBDTO = new AtomicReference<>(new AFAcctListESBDTO());
        List<AFFSMasterAcctDetailDTO> acctDetailDTOS = new ArrayList<>();

        IntStream.range(0, (masterAcctList.size() + batchSize - 1) / batchSize)
                .forEach(i -> {
                    int start = i * batchSize;
                    int end = Math.min(start + batchSize, masterAcctList.size());
                    List<AFFSAcctListSpDTO> batch = masterAcctList.subList(start, end);

                    List<ESBAcctListingReqDTO> acctDetailsReqList = batch.stream().map(masterAcct ->
                            new ESBAcctListingReqDTO(masterAcct.getMasterAcctNumber(), masterAcct.getCurrency(), getAcctTypeCode(masterAcct.getProvider()), masterAcct.getProvider()
                            )).toList();

                    afAcctListESBDTO.set(affsesbService.getAFFSMasterAcctDetails(acctDetailsReqList).getMsgBody());
                    if (afAcctListESBDTO.get().getAcctListing() != null && !afAcctListESBDTO.get().getAcctListing().isEmpty())
                        acctDetailDTOS.addAll((List<AFFSMasterAcctDetailDTO>) afAcctListESBDTO.get().getAcctListing());

                    logger.info("loop acctDetailDTOS: {}, acctDetailDTOS size: {}", acctDetailDTOS, acctDetailDTOS.size());
                });

        logger.info("acctDetailDTOS: {}, acctDetailDTOS size: {}", acctDetailDTOS, acctDetailDTOS.size());

        if (acctDetailDTOS.isEmpty()) {
            logger.error("Null message body received from ESB");
            return new AFAcctListESBDTO();
        }

        acctDetailDTOS.forEach(masterAcctDetail -> {
            Optional<String> accountStatusResult = accountStatusRepository.findAcctStatusDescriptionByAcctNo(masterAcctDetail.getAcctNumber());
            if (accountStatusResult.isPresent()) {
                masterAcctDetail.setAcctStatus(accountStatusResult.get());
            } else {
                logger.error("Cannot find account status from DB for acct: {}", masterAcctDetail.getAcctNumber());
            }
        });
        afAcctListESBDTO.get().setAcctListing(acctDetailDTOS);
        return afAcctListESBDTO.get();
    }

    private SearchCollectionResponse<AFFSAcctListSpDTO> retreiveFSMasterAccountList(UserAndCorp userAndCorp, AFAccountListReq request) {
        AFFSAcctListSpParam afFSAcctListSpParam = new AFFSAcctListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), request.getCorpCifNo(), request.getSearchParam());
        afFSAcctListSpParam.setPage(request.getPage());
        afFSAcctListSpParam.setPageSize(request.getPageSize());
        return afFloorStockingSPService.masterAcctListing(afFSAcctListSpParam);
    }

    private AFFSAcctStatsViewByFacilitySpDTO retreiveFSStatsViewByFacility(UserAndCorp userAndCorp) {
        AFFSAcctStatsSpParam afFSAcctStatsSpParam = new AFFSAcctStatsSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId());
        return afFloorStockingSPService.statsViewByFacility(afFSAcctStatsSpParam);
    }

    private AFFSAcctStatsViewByAcctSpDTO retreiveFSStatsViewByAcct(UserAndCorp userAndCorp) {
        AFFSAcctStatsSpParam afFSAcctStatsSpParam = new AFFSAcctStatsSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId());
        return afFloorStockingSPService.statsViewByAcct(afFSAcctStatsSpParam);
    }

    private SearchCollectionResponse<AFFSCertListSpDTO> retreiveFSAccountList(UserAndCorp userAndCorp, AFAccountListReq request) {
        AFFSCertListSpParam afFSCertListSpParam = new AFFSCertListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), request.getCorpCifNo(), request.getSearchParam());
        afFSCertListSpParam.setPage(request.getPage());
        afFSCertListSpParam.setPageSize(request.getPageSize());
        return afFloorStockingSPService.accountListing(afFSCertListSpParam);
    }

    private SearchCollectionResponse<AFFSSubAcctListDTO> retreiveFSSubAccountList(AFFSCertListReqDTO request) {
        AFFSSubAcctListSpParam afFSSubAcctListSpParam = new AFFSSubAcctListSpParam(request.getAcctNumber(), request.getSearchParam());
        afFSSubAcctListSpParam.setPage(request.getPage());
        afFSSubAcctListSpParam.setPageSize(request.getPageSize());
        return afFloorStockingSPService.subAcctListing(afFSSubAcctListSpParam);
    }

    private AFFSSGAcctDetailDTO floorStockingSGAcctDetail(AFFSAcctDetailReqDTO request, UserAndCorp userAndCorp) {
        AFFSSGAcctDetailDTO fsSGAcctDetail = new AFFSSGAcctDetailDTO();
        ESBAcctListingReqDTO fsAcctDetailReq = new ESBAcctListingReqDTO(request.getAcctNumber(), request.getCurrency(), getAcctTypeCode(request.getProvider().name()), request.getProvider().name());
        ApiResponse<AFAcctListESBDTO> esbResponse = affsesbService.getAFFSAcctDetailsSG(List.of(fsAcctDetailReq));

        if (esbResponse.getMsgBody() == null) {
            logger.error("Null message body received from ESB");
            return fsSGAcctDetail;
        }
        Optional<DepositAcctDetailPageProjection> accountDataResult = accountRepository.findAcctDetails(request.getAcctNumber(), request.getCurrency());
        DepositAcctDetailPageProjection accountData = null;
        if (accountDataResult.isPresent()) accountData = accountDataResult.get();
        else {
            logger.error("Account not found for {}", request.getAcctNumber());
            return fsSGAcctDetail;
        }
        log.info("account data: {}", accountData);
        fsSGAcctDetail.setMoreActions(getMoreActions(request.getAcctNumber(), request.getCurrency(), userAndCorp));
        return combineSGAcctDetail(fsSGAcctDetail, esbResponse, accountData, request);
    }

    private AFFSMasterAcctDetailDTO floorStockingMYMasterAcctDetail(AFFSAcctDetailReqDTO request) {
        AFFSMasterAcctDetailDTO fsMainAcctDetail = new AFFSMasterAcctDetailDTO();
        ESBAcctListingReqDTO fsAcctDetailReq = new ESBAcctListingReqDTO(request.getAcctNumber(), request.getCurrency(), getAcctTypeCode(request.getProvider().name()), request.getProvider().name());
        ApiResponse<AFAcctListESBDTO> esbMasterAcctDetails = affsesbService.getAFFSMasterAcctDetails(List.of(fsAcctDetailReq));

        if (esbMasterAcctDetails.getMsgBody() == null) {
            logger.error("Null message body received from ESB");
            return fsMainAcctDetail;
        }

        Optional<DepositAcctDetailPageProjection> accountDataResult = accountRepository.findAcctDetails(request.getAcctNumber(), request.getCurrency());
        DepositAcctDetailPageProjection accountData;
        if (accountDataResult.isPresent()) accountData = accountDataResult.get();
        else {
            logger.error("Account not found for {}", request.getAcctNumber());
            return fsMainAcctDetail;
        }
        log.info("account data: {}", accountData);
        return combineMasterAcctDetail(fsMainAcctDetail, esbMasterAcctDetails.getMsgBody(), accountData, request);
    }

    private AFFSSGAcctDetailDTO combineSGAcctDetail(AFFSSGAcctDetailDTO fsDetail, ApiResponse<AFAcctListESBDTO> esbResponse, DepositAcctDetailPageProjection accountData, AFFSAcctDetailReqDTO request) {
        List<AFFSSGAcctDetailDTO> esbAcctDetails = (List<AFFSSGAcctDetailDTO>) esbResponse.getMsgBody().getAcctListing();
        if (esbAcctDetails != null && !esbAcctDetails.isEmpty() && esbAcctDetails.get(0) != null)
            fsDetail = esbAcctDetails.get(0);
        Optional<AcctStatusProjection> acctStatus = accountRepository.findAcctSts(request.getAcctNumber());
        if (acctStatus.isPresent()) {
            fsDetail.setAcctStatusCode(acctStatus.get().getAcctStatusCode());
            fsDetail.setAcctStatusDesc(acctStatus.get().getAcctStatusDesc());
        } else {
            log.info("cannot find account status for account: {}", request.getAcctNumber());
        }
        fsDetail.setAcctName(accountData.getProductName() != null && !accountData.getProductName().isEmpty() ? accountData.getProductName() : accountData.getAccountName());
        fsDetail.setAcctNumber(request.getAcctNumber());
        fsDetail.setCurrency(request.getCurrency());
        fsDetail.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
        fsDetail.setIslamic("Y".equalsIgnoreCase(accountData.getIsIslamic()));
        fsDetail.setEsbMetadata(esbResponse.getMsgBody().getEsbMetadata());

        boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(fsDetail.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(fsDetail.getAcctStatusDesc());

        if (isClosed) {
            fsDetail.setInterestAmt(null);
            fsDetail.setOutstandingBalance(null);
            fsDetail.setInterestRate(null);
            fsDetail.setPaymentType(null);
            fsDetail.setNextPaymentDate(null);
            fsDetail.setLateInterestOverdue(null);
            fsDetail.setMisc(null);
            fsDetail.setOtherCharges(null);
            fsDetail.setAccruedInterest(null);
            fsDetail.setAccruedLateCharges(null);
            fsDetail.setMaturityRate(null);
            fsDetail.setLoanTerm(null);
            fsDetail.setLateChargeInterestRate(null);
            fsDetail.setOriginalLoanAmt(null);
        }

        if ("Y".equalsIgnoreCase(fsDetail.getTlastIndicator()))
            fsDetail.setOutstandingBalance(null);

        return fsDetail;
    }

    private AFFSMasterAcctDetailDTO combineMasterAcctDetail(AFFSMasterAcctDetailDTO fsDetail, AFAcctListESBDTO esbResponse, DepositAcctDetailPageProjection accountData, AFFSAcctDetailReqDTO request) {
        List<AFFSMasterAcctDetailDTO> esbMasterAcctDetails = (List<AFFSMasterAcctDetailDTO>) (esbResponse.getAcctListing());
        String accountType = null;

        if ("Y".equalsIgnoreCase(accountData.getIsIslamic())) accountType = "Islamic";
        else accountType = "Conventional";

        Optional<AcctStatusProjection> acctStatus = accountRepository.findAcctSts(request.getAcctNumber());

        long numberOfAccts = floorStockRepository.countFloorStockCertsByAccountNo(request.getAcctNumber());
        if (!esbMasterAcctDetails.isEmpty() && esbMasterAcctDetails.get(0) != null)
            fsDetail = esbMasterAcctDetails.get(0);

        if (acctStatus.isPresent()) {
            fsDetail.setAcctStatusCode(acctStatus.get().getAcctStatusCode());
            fsDetail.setAcctStatusDesc(acctStatus.get().getAcctStatusDesc());
        } else {
            log.info("cannot find account status for account: {}", request.getAcctNumber());
        }

        if (fsDetail.getAcctStatus() != null) {
            Optional<String> acctStatusDesc = accountStatusRepository.findDescription01ByCodeAndProvider(Integer.valueOf(fsDetail.getAcctStatus()), accountData.getProvider());
            fsDetail.setAcctStatus(Optional.ofNullable(acctStatusDesc)
                    .flatMap(opt -> opt)
                    .orElse(null));
        }

        boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(fsDetail.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(fsDetail.getAcctStatusDesc());

        if (isClosed) {
            fsDetail.setFacilityLimit(null);
            fsDetail.setFinancedAmt(null);
            fsDetail.setOutstandingBalance(null);
        }

        fsDetail.setProductName(accountData.getProductName());
        fsDetail.setAcctNumber(request.getAcctNumber());
        fsDetail.setAcctName(accountData.getAccountName());
        fsDetail.setCurrency(request.getCurrency());
        fsDetail.setNumberOfSubAcct(String.valueOf(numberOfAccts));
        fsDetail.setProductType(accountType);
        fsDetail.setIslamic("Y".equalsIgnoreCase(accountData.getIsIslamic()));
        fsDetail.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
        fsDetail.setEsbMetadata(esbResponse.getEsbMetadata());

        return fsDetail;
    }

    private AFFSCertDetailDTO floorStockingMYCertDetail(AFFSAcctDetailReqDTO request, UserAndCorp userAndCorp) {
        logger.trace("Retrieving Floor Stock account list...{}", request);
        AFFSCertDetailDTO fsCertDetail = new AFFSCertDetailDTO();

        ESBAFFSCertDetailsReqDTO fsCertDetailReq =
                ESBAFFSCertDetailsReqDTO.builder().accountNo(request.getAcctNumber())
                        .certNo(request.getCertNo())
                        .currency(request.getCurrency())
                        .accountType(getAcctTypeCode(request.getProvider().name()))
                        .region(request.getProvider().name()).build();

        ApiResponse<AFAcctListESBDTO> esbResponse = affsesbService.getAFFSCertDetailsMY(List.of(fsCertDetailReq));
        log.info("esbResponse AFFSCertDetailsMY {}", esbResponse);

        if (esbResponse.getMsgBody() == null) {
            logger.error("Null message body received from ESB");
            return fsCertDetail;
        }

        DepositAcctDetailPageProjection accountData = null;
        accountData = Optional.ofNullable(accountRepository.findAcctDetails(request.getAcctNumber(), request.getCurrency()))
                .flatMap(opt -> opt)
                .orElse(null);

        if(accountData == null) {
            logger.error("Account not found for {}", request.getAcctNumber());
            return fsCertDetail;
        }

        Optional<AcctStatusProjection> acctStatus = accountRepository.findAcctSts(request.getAcctNumber());
        if (acctStatus.isPresent()) {
            fsCertDetail.setAcctStatusCode(acctStatus.get().getAcctStatusCode());
            fsCertDetail.setAcctStatusDesc(acctStatus.get().getAcctStatusDesc());
        } else {
            log.info("cannot find account status for account: {}", request.getAcctNumber());
        }
        fsCertDetail.setCertNo(request.getCertNo());
        fsCertDetail.setMoreActions(getMoreActions(request.getAcctNumber(), request.getCurrency(), userAndCorp));
        return combineCertDetail(fsCertDetail, esbResponse.getMsgBody(), accountData);
    }

    private AFFSCertDetailDTO combineCertDetail(AFFSCertDetailDTO fsCertDetail, AFAcctListESBDTO esbResponse, DepositAcctDetailPageProjection accountData) {
        List<AFFSCertDetailDTO> esbMasterAcctDetails = (List<AFFSCertDetailDTO>) esbResponse.getAcctListing();
        log.info("esbMasterAcctDetails datas {} ", esbMasterAcctDetails);
        Optional<AFFSCertDetailDTO> matchedCert = esbMasterAcctDetails.stream()
                .filter(detail -> fsCertDetail.getCertNo().equals(detail.getCertNo()))
                .findFirst();
        fsCertDetail.setSolaStatus(matchedCert.map(AFFSCertDetailDTO::getSolaStatus).orElse(null));
        log.info("solaStatus --> {} ", fsCertDetail.getSolaStatus());
        if (fsCertDetail.getSolaStatus() != null) {
            Optional<String> acctStatusDesc = accountStatusRepository.findDescription01ByCodeAndProvider(Integer.valueOf(fsCertDetail.getSolaStatus()), accountData.getProvider());
            fsCertDetail.setSolaStatus(Optional.ofNullable(acctStatusDesc)
                    .flatMap(opt -> opt)
                    .orElse(null));
        }
        fsCertDetail.setAcctName(accountData.getAccountName());
        fsCertDetail.setProductName(accountData.getProductName());
        fsCertDetail.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
        fsCertDetail.setCurrency(accountData.getCurrency());
        fsCertDetail.setIslamic("Y".equalsIgnoreCase(accountData.getIsIslamic()));

        if (esbMasterAcctDetails != null && !esbMasterAcctDetails.isEmpty()) {
            AFFSCertDetailDTO master = esbMasterAcctDetails.get(0);
            boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(fsCertDetail.getSolaStatus()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(fsCertDetail.getSolaStatus());

            fsCertDetail.setAmtDue(master.getAmtDue());
            fsCertDetail.setFinancedAmt(isClosed ? null : master.getFinancedAmt());
            fsCertDetail.setInterestCharged(isClosed ? null : master.getInterestCharged());
            fsCertDetail.setSettlementAmt(master.getSettlementAmt());
            fsCertDetail.setOutstandingBalance(isClosed ? null : master.getOutstandingBalance());
            fsCertDetail.setExpiryDate(DateTimeUtil.convertDateFormat(master.getExpiryDate()));
        } else {
            log.info("No account details found in ESB response for account: {}", accountData.getAccountName());
        }

        fsCertDetail.setEsbMetadata(esbResponse.getEsbMetadata());
        return fsCertDetail;
    }

    private List<Object> getDownloadFacilityViewList(UserAndCorp userAndCorp, AFFSAcctListDownloadReqDTO request) {
        AFAccountListReq acctListReq = new AFAccountListReq(true, "", request.getCorpCifNo(), FACILITY);
        acctListReq.setPage(1);
        acctListReq.setPageSize(Integer.MAX_VALUE);

        // Get list of floor stock master accounts
        SearchCollectionResponse<AFFSAcctListSpDTO> spResult = retreiveFSMasterAccountList(userAndCorp, acctListReq);
        List<AFFSAcctListSpDTO> masterAcctList = spResult.getData();
        if (masterAcctList == null) {
            logger.error("No Floor Stock account found in database");
            return new ArrayList<>();
        }

        if (request.getRealTimeIndicator().equals(Boolean.TRUE)) {
            logger.info("T real time");
            return combineFacilityDetailsMY(masterAcctList, getFacilityDataFromESB(masterAcctList));
        } else {
            logger.info("T-1 non real time");
            return new ArrayList<>(masterAcctList);
        }
    }

    private List<Object> getDownloadAcctViewList(UserAndCorp userAndCorp, AFFSAcctListDownloadReqDTO request) {
        AFAccountListReq acctListReq = new AFAccountListReq(true, "", request.getCorpCifNo(), ACCOUNT);
        acctListReq.setPage(1);
        acctListReq.setPageSize(Integer.MAX_VALUE);

        SearchCollectionResponse<AFFSCertListSpDTO> spResult = retreiveFSAccountList(userAndCorp, acctListReq);
        List<AFFSCertListSpDTO> certList = spResult.getData();
        if (certList == null) {
            logger.info("No cert found in database");
            return new ArrayList<>();
        }

        if (request.getRealTimeIndicator().equals(Boolean.TRUE)) {
            logger.info("T real time");
            // Categorize the list retreived from SP by their provider to cater for ESB calls
            Map<String, List<AFFSCertListSpDTO>> certMapCaterogizedByProvider = categorizeAcctListByProvider(certList);

            // Call ESB to get acct list T case details
            return callESBByProvider(new AFFSAcctListViewSummaryResp<>(), certMapCaterogizedByProvider); // stores the full acct list from all providers
        } else {
            logger.info("T-1 non real time");
            return new ArrayList<>(certList);
        }
    }

    private List<AFFSSubAcctListDTO> getDownloadSubAcctList(UserAndCorp userAndCorp, AFFSSubAcctListDownloadReqDTO request) {
        AFFSCertListReqDTO affsCertListReqDTO = new AFFSCertListReqDTO();
        affsCertListReqDTO.setAcctNumber(request.getAcctNumber());
        affsCertListReqDTO.setProvider(request.getProvider());
        affsCertListReqDTO.setSearchParam(request.getSearchParam());
        affsCertListReqDTO.setPage(1);
        affsCertListReqDTO.setPageSize(Integer.MAX_VALUE);

        return floorStockingSubAcctListing(userAndCorp, affsCertListReqDTO).getMsgBody().getDatas().getData();
    }

    private DownloadDTO downloadFacilityViewList(UserAndCorp userAndCorp, AFFSAcctListDownloadReqDTO request) {
        AFAccountListReq acctListReq = new AFAccountListReq(request.getRealTimeIndicator(), request.getSearchParam(), request.getCorpCifNo(), request.getViewByIndicator());
        acctListReq.setPage(1);
        acctListReq.setPageSize(Integer.MAX_VALUE);
        ApiResponse<AFFSAcctListViewSummaryResp> data = objectMapper.convertValue(floorStockingAccountListingByFacility(userAndCorp, acctListReq), new TypeReference<ApiResponse<AFFSAcctListViewSummaryResp>>() {});

        List<AFFSAcctListSpDTO> fsFacilityViewDataList = Optional.ofNullable(data)
                .map(ApiResponse::getMsgBody)
                .map(AFFSAcctListViewSummaryResp::getDatas)
                .map(SearchCollectionResponse::getData)
                .map(obj -> {
                    if (obj instanceof List<?>) {
                        List<?> list = (List<?>) obj;
                        if (list.stream().allMatch(AFFSAcctListSpDTO.class::isInstance)) {
                            @SuppressWarnings("unchecked")
                            List<AFFSAcctListSpDTO> typed = (List<AFFSAcctListSpDTO>) list;
                            return typed;
                        }
                    }
                    try {
                        return objectMapper.convertValue(obj, new com.fasterxml.jackson.core.type.TypeReference<List<AFFSAcctListSpDTO>>() {});
                    } catch (IllegalArgumentException e) {
                        logger.warn("Failed to convert data to List<AFFSAcctListSpDTO>: {}", e.getMessage(), e);
                        return java.util.Collections.<AFFSAcctListSpDTO>emptyList();
                    }
                })
                .orElseGet(Collections::emptyList);
        logger.info("download fsFacilityViewDataList: {}", fsFacilityViewDataList);

        DownloadDTO result = new DownloadDTO();
        switch (request.getFormat()) {
            case CSV:
                result.setData(afFSAcctListViewFacilityFileService.generateCsv(fsFacilityViewDataList));
                result.setFileName(DownloadEnum.AF_FS_ACCT_LISTING_FACILITY_VIEW_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
                break;
            case PDF:
                result.setData(afFSAcctListViewFacilityFileService.generatePdf(fsFacilityViewDataList));
                result.setFileName(DownloadEnum.AF_FS_ACCT_LISTING_FACILITY_VIEW_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
                break;
            case TXT:
                result.setData(afFSAcctListViewFacilityFileService.generateTxt(fsFacilityViewDataList));
                result.setFileName(DownloadEnum.AF_FS_ACCT_LISTING_FACILITY_VIEW_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    private DownloadDTO downloadAccountViewList(UserAndCorp userAndCorp, AFFSAcctListDownloadReqDTO request) {
        AFAccountListReq acctListReq = new AFAccountListReq(request.getRealTimeIndicator(), request.getSearchParam(), request.getCorpCifNo(), request.getViewByIndicator());
        acctListReq.setPage(1);
        acctListReq.setPageSize(Integer.MAX_VALUE);

        ApiResponse<AFFSAcctListViewSummaryResp> data = objectMapper.convertValue(floorStockingAccountListingByAccount(userAndCorp, acctListReq), new TypeReference<ApiResponse<AFFSAcctListViewSummaryResp>>() {});

        List<AFFSCertListSpDTO> fsAcctViewDataList = Optional.ofNullable(data)
                .map(ApiResponse::getMsgBody)
                .map(AFFSAcctListViewSummaryResp::getDatas)
                .map(SearchCollectionResponse::getData)
                .map(obj -> {
                    if (obj instanceof List<?>) {
                        List<?> list = (List<?>) obj;
                        if (list.stream().allMatch(AFFSCertListSpDTO.class::isInstance)) {
                            @SuppressWarnings("unchecked")
                            List<AFFSCertListSpDTO> typed = (List<AFFSCertListSpDTO>) list;
                            return typed;
                        }
                    }
                    try {
                        return objectMapper.convertValue(obj, new com.fasterxml.jackson.core.type.TypeReference<List<AFFSCertListSpDTO>>() {});
                    } catch (IllegalArgumentException e) {
                        logger.warn("Failed to convert data to List<AFFSCertListSpDTO>: {}", e.getMessage(), e);
                        return java.util.Collections.<AFFSCertListSpDTO>emptyList();
                    }
                })
                .orElseGet(Collections::emptyList);

        DownloadDTO result = new DownloadDTO();
        switch (request.getFormat()) {
            case CSV:
                result.setData(afFSAcctListViewAcctFileService.generateCsv(fsAcctViewDataList));
                result.setFileName(DownloadEnum.AF_FS_ACCT_LISTING_ACCT_VIEW_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
                break;
            case PDF:
                result.setData(afFSAcctListViewAcctFileService.generatePdf(fsAcctViewDataList));
                result.setFileName(DownloadEnum.AF_FS_ACCT_LISTING_ACCT_VIEW_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
                break;
            case TXT:
                result.setData(afFSAcctListViewAcctFileService.generateTxt(fsAcctViewDataList));
                result.setFileName(DownloadEnum.AF_FS_ACCT_LISTING_ACCT_VIEW_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    private String getAcctTypeCode(String provider) {
        Optional<AccountProductTypeMatrix> matrix = matrixRepository.findByAcctTypeAndProv(AcctTypEnum.FS.name(), provider);
        if (matrix.isEmpty()) {
            logger.error(ACCT_TYP_FOUND);
            return null;
        }

        return matrix.get().getEsbAccountTypeCode();
    }

    private String getFacilityLimitFacility(List<AFFSMasterAcctDetailDTO> accountList) {
        if (accountList == null) {
            return null; // Return zero if DTO or data is null
        }
        BigDecimal totalBalance = accountList.stream()
                .map(account -> account.getFacilityLimit() != null
                        ? new BigDecimal(account.getFacilityLimit().replaceAll(",", ""))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalBalance.toPlainString();
    }

    private String getFinancedAmountAccount(List<AFFSCertListSpDTO> accountList) {
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

    private String getOutstandingAccount(List<AFFSCertListSpDTO> accountList) {
        if (accountList == null) {
            return null; // Return zero if DTO or data is null
        }
        BigDecimal totalBalance = accountList.stream()
                .map(account -> account.getOutstandingBalance() != null
                        ? new BigDecimal(account.getOutstandingBalance().replaceAll(",", ""))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalBalance.toPlainString();
    }

    private List<AFFSMasterAcctDetailDTO> acctStatFacilityRealTime (UserAndCorp userAndCorp) {
        AFAccountListReq accountListReq = new AFAccountListReq();
        accountListReq.setPage(1);
        accountListReq.setPageSize(Integer.MAX_VALUE);

        SearchCollectionResponse<AFFSAcctListSpDTO> spResult = retreiveFSMasterAccountList(userAndCorp, accountListReq);
        AFAcctListESBDTO afAcctListESBDTO = getFacilityDataFromESB(spResult.getData());

        return Optional.ofNullable(afAcctListESBDTO.getAcctListing())
                .orElse(Collections.emptyList())
                .stream()
                .map(AFFSMasterAcctDetailDTO.class::cast)
                .toList();
    }

    private List<AFFSCertListSpDTO> acctStatAcctRealTime (UserAndCorp userAndCorp) {
        AFAccountListReq accountListReq = new AFAccountListReq();
        accountListReq.setPage(1);
        accountListReq.setPageSize(Integer.MAX_VALUE);

        SearchCollectionResponse<AFFSCertListSpDTO> spResult = retreiveFSAccountList(userAndCorp, accountListReq);
        Map<String, List<AFFSCertListSpDTO>> certMapCaterogizedByProvider = categorizeAcctListByProvider(spResult.getData());
        List<Object> fsViewByAcctList = callESBByProvider(new AFFSAcctListViewSummaryResp<>(), certMapCaterogizedByProvider);

        return Optional.ofNullable(fsViewByAcctList)
                .orElse(Collections.emptyList())
                .stream()
                .map(AFFSCertListSpDTO.class::cast)
                .toList();
    }

    private String getRedisKey(UserAndCorp userAndCorp, AFAccountListReq reqDTO) {
        String redisKey = null;
        if (reqDTO.isRealTimeIndicator())
            redisKey = redisHelper.createCacheKey(BD_AM_AF_FS_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "ONL" + reqDTO.getViewByIndicator().name() + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
        else
            redisKey = redisHelper.createCacheKey(BD_AM_AF_FS_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + reqDTO.getViewByIndicator().name() + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());

        return redisKey;
    }

    private ApiResponse<AFFSAcctListViewSummaryResp<Object>> getAcctFromCache(UserAndCorp userAndCorp, AFAccountListReq reqDTO) {
        return (ApiResponse<AFFSAcctListViewSummaryResp<Object>>) redisHelper.getFromCache(getRedisKey(userAndCorp, reqDTO), ApiResponse.class);
    }

    public void removeAllFromCache(UserAndCorp userAndCorp){
        String redisKey = redisHelper.createCacheKey(BD_AM_AF_FS_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_*");
        redisHelper.deleteCachesWithPattern(redisKey);
        log.info("removed cache for {}", redisKey);
    }
}