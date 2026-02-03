package com.m2e.dashboard.service.af.bd;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.AccountConditions;
import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.dto.MoreActionsRedirection;
import com.m2e.dashboard.dto.af.request.bd.*;
import com.m2e.dashboard.dto.af.request.bd.download.AFBDAcctListDownloadReqDTO;
import com.m2e.dashboard.dto.af.request.bd.download.AFBDSubAcctListDownloadReqDTO;
import com.m2e.dashboard.dto.af.request.bd.download.AFBDTrxHistoryDownloadReqDTO;
import com.m2e.dashboard.dto.af.response.bd.*;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.*;
import com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry.*;
import com.m2e.dashboard.jms.af.bd.request.account.details.*;
import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.jms.af.common.ESBAFAcctActivityReqDTO;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.mapper.af.bd.AFBDSpParamMapper;
import com.m2e.dashboard.mapper.af.bd.ESBAFBDAcctDetailsMergeMapper;
import com.m2e.dashboard.projection.DepositAcctDetailPageProjection;
import com.m2e.dashboard.repository.AccountLinkBlockDebitRepository;
import com.m2e.dashboard.repository.AccountProductTypeMatrixRepository;
import com.m2e.dashboard.repository.AccountRepository;
import com.m2e.dashboard.repository.AccountStatusRepository;
import com.m2e.dashboard.service.AsyncPaginationService;
import com.m2e.dashboard.service.FileRepoService;
import com.m2e.dashboard.service.af.bd.jms.AFBDESBService;
import com.m2e.dashboard.service.af.bd.sp.AFBlockDiscountingSPService;
import com.m2e.dashboard.sp.af.bd.request.*;
import com.m2e.dashboard.sp.af.bd.response.AFBDDebitNoteListSpDTO;
import com.m2e.dashboard.util.DateTimeUtil;
import com.m2e.model.sso.base.AccountLinkBdDb;
import com.m2e.model.sso.base.AccountProductTypeMatrix;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static com.m2e.dashboard.enums.AcctProductEntitlementConst.LOAN_AND_CARD_PAYMENT_PRODUCT_CODE;
import static com.m2e.dashboard.enums.AcctProductEntitlementConst.USER_ACCT_PRODUCT_LIST_REDIS_KEY;
import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_AF_BD_ACCTLIST;
import static com.m2e.dashboard.enums.ReportCommonConstant.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AFBlockDiscountingService {
    private static final Logger logger = LoggerFactory.getLogger(AFBlockDiscountingService.class);
    private static final String ACCT_NOT_FOUND = "Account Not Found";
    private static final String ACCT_TYP_FOUND = "Account Type Not Found";
    private final int batchSize = 10;

    @Value("${cache.ttl}")
    private Duration cacheDuration;


    private final AFBDAcctListViewAcctFileService afBDAcctListViewAcctFileService;
    private final AFBDAcctListViewFacilityFileService afBDAcctListViewFacilityFileService;
    private final AFBDSubAcctMasterListFileService afBDSubAcctMasterListFileService;
    private final AFBDMYTrxHistFileService afBDMYTrxHistFileService;
    private final AFBDSGTrxHistFileService afBDSGTrxHistFileService;
    private final AFBDSubAcctChildListFileService afBDSubAcctChildListFileService;
    private final AFBDSubAcctChildListFileSGService afbdSubAcctChildListFileSGService;
    private final AFBDESBService afbdesbService;
    private final AccountRepository accountRepository;
    private final AFBlockDiscountingSPService afBlockDiscountingSPService;
    private final JwtUtil jwtUtil;
    private final AsyncPaginationService asyncPaginationService;
    private final ObjectMapper objectMapper;
    private final AccountLinkBlockDebitRepository accountLinkBlockDebitRepository;
    private final RedisHelper redisHelper;
    private final AccountStatusRepository accountStatusRepository;
    private final FileRepoService fileRepoService;
    private final ModelMapper modelMapper;
    private final AccountProductTypeMatrixRepository matrixRepository;
    private final ESBAFBDAcctDetailsMergeMapper mergeMapper;
    private final AFBDSpParamMapper spParamMapper;

    public <T> ApiResponse<T> bloorDiscountingAccountStats(AFBDAcctStatsReqDTO request, UserAndCorp userAndCorp) {
        logger.info("Enter bloorDiscountingAccountStats: {}", request);
        removeAllFromCache(userAndCorp);

        AFBDAcctStatsViewFacilityDTO afbdAcctStatsViewFacilityDTO = new AFBDAcctStatsViewFacilityDTO();
        AFBDAcctStatsViewAcctDTO afbdAcctStatsViewAcctDTO = new AFBDAcctStatsViewAcctDTO();
        T data;

        AFBDAcctListSpParam afbdAcctListSpParam = new AFBDAcctListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), null, null);
        afbdAcctListSpParam.setPage(1);
        afbdAcctListSpParam.setPageSize(Integer.MAX_VALUE);

        AFBDAcctStatsSpParam afbdAcctStatsSpParam = new AFBDAcctStatsSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId());

        switch (request.getViewByIndicator()) {
            case ACCOUNT -> {
                afbdAcctStatsViewAcctDTO = afBlockDiscountingSPService.statsViewByAcct(afbdAcctStatsSpParam);

                if (Boolean.TRUE.equals(request.getRealTimeIndicator())) {
//                    SearchCollectionResponse<AFBDAcctListViewAcctDTO> spResult = afBlockDiscountingSPService.acctListing(afbdAcctListSpParam);
//                    List<AFBDAcctListViewAcctDTO> overall = new ArrayList<>();
//
//                    AFAcctListESBDTO afAcctListESBDTOMY = getAccountListESBMY(spResult.getData(), null);
//                    if (afAcctListESBDTOMY != null && afAcctListESBDTOMY.getAcctListing() != null)
//                        overall.addAll((List<AFBDAcctListViewAcctDTO>) afAcctListESBDTOMY.getAcctListing());
//
//
//                    AFAcctListESBDTO afAcctListESBDTOSG = getAccountListESBSG(spResult.getData(), null);
//                    if (afAcctListESBDTOSG != null && afAcctListESBDTOSG.getAcctListing() != null)
//                        overall.addAll((List<AFBDAcctListViewAcctDTO>) afAcctListESBDTOSG.getAcctListing());
//
//                    afbdAcctStatsViewAcctDTO.setOutstandingBalance(getOutstandingBalance(overall));
//                    afbdAcctStatsViewAcctDTO.setFinancedAmt(getFinancedAmount(overall));
                    afbdAcctStatsViewAcctDTO.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
                }

                afbdAcctStatsViewAcctDTO.setDisplayShowViewBy(afBlockDiscountingSPService.getDisplayShowViewBy(new AFBDAcctListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), null, null)));

                data = (T) afbdAcctStatsViewAcctDTO;

            }
            case FACILITY -> {
                afbdAcctStatsViewFacilityDTO = afBlockDiscountingSPService.statsViewByFacility(afbdAcctStatsSpParam);

                if (Boolean.TRUE.equals(request.getRealTimeIndicator())) {
//                    SearchCollectionResponse<AFBDAcctListViewFacilityDTO> spResult = afBlockDiscountingSPService.masterAcctListing(afbdAcctListSpParam);
//                    List<AFBDAcctListViewFacilityDTO> overall = new ArrayList<>();
//
//                    AFAcctListESBDTO afAcctListESBDTOMY = getFacilityListESBMY(spResult.getData(), null);
//
//                    if (afAcctListESBDTOMY != null && afAcctListESBDTOMY.getAcctListing() != null)
//                        overall.addAll((List<AFBDAcctListViewFacilityDTO>) afAcctListESBDTOMY.getAcctListing());
//
//                    afbdAcctStatsViewFacilityDTO.setOutstandingBalance(getTotalOutstandingBalance(overall));
//                    afbdAcctStatsViewFacilityDTO.setFacilityLimit(getTotalFacilityLimit(overall));

                    afbdAcctStatsViewFacilityDTO.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
                }

                if ("0".equalsIgnoreCase(afbdAcctStatsViewFacilityDTO.getNumberOfMasterAcct()))
                    afbdAcctStatsViewFacilityDTO.setDisplayShowViewBy(false);
                else
                    afbdAcctStatsViewFacilityDTO.setDisplayShowViewBy(afBlockDiscountingSPService.getDisplayShowViewBy(new AFBDAcctListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), null, null)));

                data = (T) afbdAcctStatsViewFacilityDTO;
            }
            default -> throw new IllegalArgumentException("View By Indicator is undefined");
        }

        return new ApiResponse<>(new ErrorResponse(), data);
    }

    public <T> ApiResponse<AFBDAcctSummaryDTO> bloorDiscountingAccountListing(UserAndCorp userAndCorp, AFBDAccountListReq request) {
        logger.info("Enter bloorDiscountingAccountListing: {}", request);
        ApiResponse<AFBDAcctSummaryDTO> apiResponse;

        apiResponse = getAcctFromCache(userAndCorp, request);

        if (apiResponse == null) {
            AFBDAcctSummaryDTO afbsAcctSummaryDTO = new AFBDAcctSummaryDTO();
            SearchCollectionResponse<T> searchCollectionResponse;

            List<AFBDAcctListViewAcctDTO> acctList;
            List<AFBDAcctListViewFacilityDTO> facilityList;

            AFBDAcctListSpParam afbdAcctListSpParam = new AFBDAcctListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), request.getCorpCifNo(), request.getSearchParam());
            afbdAcctListSpParam.setPage(request.getPage());
            afbdAcctListSpParam.setPageSize(request.getPageSize());

            if (request.isRealTimeIndicator()) {
                logger.info("T real time");

                switch (request.getViewByIndicator()) {
                    case ACCOUNT -> {
                        SearchCollectionResponse<AFBDAcctListViewAcctDTO> spResult = afBlockDiscountingSPService.acctListing(afbdAcctListSpParam);
                        acctList = spResult.getData();

                        List<AFBDAcctListViewAcctDTO> masterRealTimeDto = new ArrayList<>();
                        ESBMetadataDTO esbMetadataDTO = new ESBMetadataDTO();

                        if (!acctList.isEmpty()) {
                            //continue query from ESB if not empty
                            List<AFBDAcctListViewAcctDTO> acctDTOSMY;

                            AFAcctListESBDTO afAcctListESBDTOMY = getAccountListESBMY(acctList, request.getCorpCifNo());
                            acctDTOSMY = (List<AFBDAcctListViewAcctDTO>) afAcctListESBDTOMY.getAcctListing();
                            if (acctDTOSMY != null)
                                masterRealTimeDto.addAll(acctDTOSMY);
                            esbMetadataDTO = afAcctListESBDTOMY.getEsbMetadata();

                            List<AFBDAcctListViewAcctDTO> acctDTOSSG;
                            AFAcctListESBDTO afAcctListESBDTOSG = getAccountListESBSG(acctList, request.getCorpCifNo());
                            acctDTOSSG = (List<AFBDAcctListViewAcctDTO>) afAcctListESBDTOSG.getAcctListing();
                            if (acctDTOSSG != null)
                                masterRealTimeDto.addAll(acctDTOSSG);
                            esbMetadataDTO = afAcctListESBDTOSG.getEsbMetadata();

                        }

                        if (!masterRealTimeDto.isEmpty()) {
                            logger.info("acctList t1 data: {}", acctList);
                            logger.info("t data: {}", masterRealTimeDto);

                            //set amount value to null before mapping
                            acctList.forEach(dto -> {
                                dto.setOutstandingBalance(null);
                                dto.setFinancedAmt(null);
                                dto.setAmountDue(null);
                                dto.setSettlementAmount(null);
                                dto.setInstalmentAmt(null);
                                dto.setInstalmentAmtOverdue(null);
                                dto.setTlastIndicator(null);
                                dto.setMoreActions(getMoreActions(dto, userAndCorp));
                            });

                            // Set balance values from ESB Host for real-time data
                            masterRealTimeDto.forEach(account -> {
                                String accountNumber = account.getAcctNumber();
                                String outstandingBalance = account.getOutstandingBalance();
                                String financedAmt = account.getFinancedAmt();
                                String amountDue = account.getAmountDue();
                                String settlementAmount = account.getSettlementAmount();
                                String instalmentAmt = account.getInstalmentAmt();
                                String instalmentAmtOverdue = account.getInstalmentAmtOverdue();
                                String tlast = account.getTlastIndicator();
                                logger.info("accountNumber: {}, outstandingBalance: {}, financedAmt: {}", accountNumber, outstandingBalance, financedAmt);

                                acctList.stream()
                                        .filter(dto -> dto.getAcctNumber().equals(accountNumber))
                                        .forEach(dto -> {
                                            boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(dto.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(dto.getAcctStatusDesc());

                                            dto.setMoreActions(getMoreActions(dto, userAndCorp));
                                            dto.setOutstandingBalance(isClosed ? null : outstandingBalance);
                                            dto.setFinancedAmt(isClosed ? null : financedAmt);
                                            dto.setAmountDue(isClosed ? null : amountDue);
                                            dto.setSettlementAmount(isClosed ? null : settlementAmount);
                                            dto.setInstalmentAmt(isClosed ? null : instalmentAmt);
                                            dto.setInstalmentAmtOverdue(isClosed ? null : instalmentAmtOverdue);
                                            dto.setTlastIndicator(account.getTlastIndicator());
                                        });
                            });

                        } else {
                            acctList.forEach(dto -> {
                                dto.setOutstandingBalance(null);
                                dto.setFinancedAmt(null);
                                dto.setAmountDue(null);
                                dto.setSettlementAmount(null);
                                dto.setInstalmentAmt(null);
                                dto.setInstalmentAmtOverdue(null);
                                dto.setMoreActions(getMoreActions(dto, userAndCorp));
                            });
                        }

                        searchCollectionResponse = new SearchCollectionResponse<>(spResult.getTotalPages(), spResult.getCurrentPage(), spResult.getNextPage(), spResult.getTotalRecordCount());
                        searchCollectionResponse.setData((List<T>) acctList);
                        afbsAcctSummaryDTO.setDatas(searchCollectionResponse);
                        afbsAcctSummaryDTO.setEsbMetadata(esbMetadataDTO);
                    }
                    case FACILITY -> {
                        SearchCollectionResponse<AFBDAcctListViewFacilityDTO> spResult = afBlockDiscountingSPService.masterAcctListing(afbdAcctListSpParam);
                        facilityList = spResult.getData();

                        List<AFBDAcctListViewFacilityDTO> masterRealTimeDto = new ArrayList<>();
                        ESBMetadataDTO esbMetadataDTO = new ESBMetadataDTO();

                        if (!facilityList.isEmpty()) {
                            //continue query from ESB if not empty

                            AFAcctListESBDTO afAcctListESBDTOMY = getFacilityListESBMY(facilityList, request.getCorpCifNo());
                            List<AFBDAcctListViewFacilityDTO> facilityDTOSMY = (List<AFBDAcctListViewFacilityDTO>) afAcctListESBDTOMY.getAcctListing();
                            if (facilityDTOSMY != null)
                                masterRealTimeDto.addAll(facilityDTOSMY);
                            esbMetadataDTO = afAcctListESBDTOMY.getEsbMetadata();
                        }

                        logger.info("facilityList t1 data: {}", facilityList);
                        logger.info("t data: {}", masterRealTimeDto);

                        if (!masterRealTimeDto.isEmpty()) {

                            //set amount values to null before mapping
                            facilityList.forEach(dto -> {
                                dto.setTotalFacilityLimit(null);
                                dto.setTotalOutstandingBalance(null);
                                dto.setMoreActions(getMoreActions(new AFBDAcctListViewAcctDTO(dto.getMasterAcctNumber(), dto.getCurrency()), userAndCorp));
                            });

                            // Set balance values from ESB Host for real-time data
                            masterRealTimeDto.forEach(account -> {
                                String accountNumber = account.getMasterAcctNumber();
                                String outstandingBalance = account.getTotalOutstandingBalance();
                                String facilityLimit = account.getTotalFacilityLimit();

                                logger.info("accountNumber: {}, outstandingBalance: {}, facilityLimit: {}", accountNumber, outstandingBalance, facilityLimit);

                                facilityList.stream()
                                        .filter(dto -> dto.getMasterAcctNumber().equals(accountNumber))
                                        .forEach(dto -> {
                                            boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(dto.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(dto.getAcctStatusDesc());
                                            dto.setTotalOutstandingBalance(isClosed ? null : outstandingBalance);
                                            dto.setTotalFacilityLimit(isClosed ? null : facilityLimit);
                                        });
                            });

                        } else {
                            facilityList.forEach(dto -> {
                                dto.setTotalFacilityLimit(null);
                                dto.setTotalOutstandingBalance(null);
                                dto.setMoreActions(getMoreActions(new AFBDAcctListViewAcctDTO(dto.getMasterAcctNumber(), dto.getCurrency()), userAndCorp));
                            });
                        }

                        searchCollectionResponse = new SearchCollectionResponse<>(spResult.getTotalPages(), spResult.getCurrentPage(), spResult.getNextPage(), spResult.getTotalRecordCount());
                        searchCollectionResponse.setData((List<T>) facilityList);
                        afbsAcctSummaryDTO.setDatas(searchCollectionResponse);
                        afbsAcctSummaryDTO.setEsbMetadata(esbMetadataDTO);
                    }
                    default -> throw new IllegalArgumentException("View by indicator is unrecognized");
                }

            } else {
                //get list of account from T-1 case
                switch (request.getViewByIndicator()) {
                    case ACCOUNT -> {
                        SearchCollectionResponse<AFBDAcctListViewAcctDTO> spResult = afBlockDiscountingSPService.acctListing(afbdAcctListSpParam);
                        if (spResult.getData() != null) {
                            spResult.getData().forEach(dto -> dto.setMoreActions(getMoreActions(dto, userAndCorp)));
                        }
                        afbsAcctSummaryDTO.setDatas(spResult);
                    }
                    case FACILITY -> {
                        SearchCollectionResponse<AFBDAcctListViewFacilityDTO> spResult = afBlockDiscountingSPService.masterAcctListing(afbdAcctListSpParam);
                        if (spResult.getData() != null) {
                            spResult.getData().forEach(dto -> dto.setMoreActions(getMoreActions(new AFBDAcctListViewAcctDTO(dto.getMasterAcctNumber(), dto.getCurrency()), userAndCorp)));
                        }
                        afbsAcctSummaryDTO.setDatas(spResult);
                    }
                    default -> throw new IllegalArgumentException("View by indicator is unrecognized");
                }
            }

            afbsAcctSummaryDTO.setCorpCifNo(request.getCorpCifNo());
            logger.info("result: {}", afbsAcctSummaryDTO);
            apiResponse = new ApiResponse<>(new ErrorResponse("0"), afbsAcctSummaryDTO);

            redisHelper.save(getRedisKey(userAndCorp, request), apiResponse, cacheDuration.toMinutes(), TimeUnit.MINUTES);
        }

        return apiResponse;
    }

    private MoreActionsEntitlement getMoreActions(AFBDAcctListViewAcctDTO dto, UserAndCorp userAndCorp) {
        String acctNumber = dto.getMasterAcctNumber();
        if (dto.getProvider() != null && dto.getProvider().equals(ProviderEnum.SG.getProvider())) {
            acctNumber = dto.getAcctNumber();
        }
        String makePaymentRedisKey = redisHelper.buildCacheKey(userAndCorp.getUserId(), userAndCorp.getCorpId(), USER_ACCT_PRODUCT_LIST_REDIS_KEY, LOAN_AND_CARD_PAYMENT_PRODUCT_CODE, acctNumber, dto.getCurrency());
        logger.debug("getMoreActions() makePaymentRedisKey Redis key: {}", makePaymentRedisKey);
        MoreActionsEntitlement moreActionsEntitlement = new MoreActionsEntitlement();
        moreActionsEntitlement.setMakePayment(new MoreActionsRedirection(redisHelper.isPresent(makePaymentRedisKey), RedirectModuleEnum.PAYMENT_ACTION_REDIRECT.name(), Application.CASHLITE.getValue(), LOAN_AND_CARD_PAYMENT_PRODUCT_CODE));
        return moreActionsEntitlement;
    }

    public ApiResponse<?> blockDiscountingAcctDetail(UserAndCorp userAndCorp, AFBDAcctDetailReqDTO request) {
        logger.info("Enter blockDiscountingAcctDetail: {}", request);

        return switch (request.getProvider()) {
            case MY -> switch (request.getAccountLevel()) {
                case FACILITY -> getFacilityDetailForProviderMY(userAndCorp, request);
                case ACCOUNT -> getBlockDetailForProviderMY(userAndCorp, request);
                case SUBACCOUNT -> getAgreementDetailForProviderMY(userAndCorp, request);
            };
            case SG -> switch (request.getAccountLevel()) {
                case ACCOUNT -> getAccountDetailForProviderSG(userAndCorp, request);
                case SUBACCOUNT -> getSubAccountDetailForProviderSG(userAndCorp, request);
                default -> throw new IllegalArgumentException("Invalid account level indicator");
            };
        };
    }

    private ApiResponse<AFBDMasterAcctDetailDTO> getFacilityDetailForProviderMY(UserAndCorp userAndCorp, AFBDAcctDetailReqDTO request) {
        AFBDDetailSpParam spParam = spParamMapper.mapDetailSp(request, userAndCorp);
        AFBDMasterAcctDetailDTO t1Data = afBlockDiscountingSPService.masterAcctDetail(spParam);

        ESBAFBDAcctDetailsReqDTO esbafbdAcctDetailsReqDTO = ESBAFBDAcctDetailsReqDTO.builder()
                .accountNumber(request.getAcctNumber())
                .accountType(getAcctTypeCode(request.getProvider().name()))
                .region(ProviderEnum.MY.name())
                .currency(t1Data.getCurrency())
                .build();

        AFBDMasterAcctDetailDTO tData = afbdesbService.getAFBDMasterAcctDetails(esbafbdAcctDetailsReqDTO).getMsgBody();
        if (tData != null) {
            if (tData.getAcctStatus() != null) {
                Optional<String> status = accountStatusRepository.findDescription01ByCodeAndProvider(
                        Integer.parseInt(tData.getAcctStatus()), "MY");
                t1Data.setAcctStatus(status.orElse(t1Data.getAcctStatusDesc()));

            } else {
                t1Data.setAcctStatus(null);
            }

            mergeMapper.merge(t1Data, tData, new AccountConditions(t1Data::getAcctStatus));
            t1Data.setMoreActions(
                    getMoreActions(
                            new AFBDAcctListViewAcctDTO(request.getAcctNumber(), request.getCurrency()), userAndCorp));
        }
        logger.info("result: {}", t1Data);

        return new ApiResponse<>(new ErrorResponse("0"), t1Data);
    }

    private ApiResponse<AFBDAcctDetailDTO> getBlockDetailForProviderMY(UserAndCorp userAndCorp, AFBDAcctDetailReqDTO request) {
        AFBDDetailSpParam spParam = spParamMapper.mapDetailSp(request, userAndCorp);
        AFBDAcctDetailDTO t1Data = afBlockDiscountingSPService.accountDetail(spParam);

        Optional<AccountLinkBdDb> accountLinkBdDb = accountLinkBlockDebitRepository.findByBlockNo(
                request.getAcctNumber());
        ESBAFBDBlockDetailsReqDTO esbafbdBlockDetailsReqDTO;

        if (accountLinkBdDb.isPresent()) {
            AccountLinkBdDb accountLinkBdDbData = accountLinkBdDb.get();
            esbafbdBlockDetailsReqDTO = ESBAFBDBlockDetailsReqDTO.builder()
                    .accountNumber(accountLinkBdDbData.getBlockDiscountingAccountNo())
                    .blockNumber(request.getAcctNumber())
                    .accountType(getAcctTypeCode(request.getProvider().name()))
                    .currency(accountLinkBdDbData.getCurrency())
                    .region(ProviderEnum.MY.name())
                    .build();
        } else {
            esbafbdBlockDetailsReqDTO = ESBAFBDBlockDetailsReqDTO.builder().build();
        }

        AFBDAcctDetailDTO tData = afbdesbService.getAFBDBlockDetails(esbafbdBlockDetailsReqDTO).getMsgBody();
        if (tData != null && accountLinkBdDb.isPresent()) {
            if (tData.getBlockStatus() != null) {
                Optional<String> status = accountStatusRepository.findDescription01ByCodeAndProvider(
                        Integer.parseInt(tData.getBlockStatus()), "MY");
                t1Data.setBlockStatus(status.orElse(t1Data.getAcctStatusDesc()));
            }

            mergeMapper.merge(t1Data, tData, new AccountConditions(t1Data::getBlockStatus));
            t1Data.setMoreActions(
                    getMoreActions(
                            new AFBDAcctListViewAcctDTO(
                                    accountLinkBdDb.get().getBlockDiscountingAccountNo(), request.getCurrency()), userAndCorp));
        }

        logger.info("result: {}", t1Data);

        return new ApiResponse<>(new ErrorResponse("0"), t1Data);
    }

    private ApiResponse<AFBDSubAcctDetailDTO> getAgreementDetailForProviderMY(
            UserAndCorp userAndCorp, AFBDAcctDetailReqDTO request) {
        AFBDAgreementDetailSpParam spParam = spParamMapper.mapAgreementDetailSp(request, userAndCorp);
        AFBDSubAcctDetailDTO t1Data = afBlockDiscountingSPService.agreementDetail(spParam);

        ESBAFBDAgreementDetailsReqDTO esbReqDto = ESBAFBDAgreementDetailsReqDTO.builder()
                .accountNumber(request.getAcctNumber())
                .blockNumber(request.getBlockNumber())
                .agreementNumber(request.getAgreementNumber())
                .accountType(getAcctTypeCode(request.getProvider().name()))
                .currency(request.getCurrency())
                .region(ProviderEnum.MY.name())
                .build();

        AFBDSubAcctDetailDTO tData = afbdesbService.getAFBDAgreementDetails(esbReqDto).getMsgBody();

        if (tData != null) {
            if (tData.getAgreementStatus() != null) {
                Optional<String> status = accountStatusRepository.findDescription01ByCodeAndProvider(
                        Integer.parseInt(tData.getAgreementStatus()), "MY");
                t1Data.setAgreementStatus(status.orElse(t1Data.getAcctStatusDesc()));
            }

            mergeMapper.merge(t1Data, tData, new AccountConditions(t1Data::getAgreementStatus));
            t1Data.setMoreActions(
                    getMoreActions(
                            new AFBDAcctListViewAcctDTO(
                                    request.getAcctNumber(), request.getCurrency()), userAndCorp));
        }

        logger.info("result: {}", t1Data);
        return new ApiResponse<>(new ErrorResponse("0"), t1Data);
    }

    private ApiResponse<AFBDSGAcctDetailDTO> getAccountDetailForProviderSG(UserAndCorp userAndCorp, AFBDAcctDetailReqDTO request) {
        AFBDDetailSpParam spParam = spParamMapper.mapDetailSp(request, userAndCorp);
        AFBDSGAcctDetailDTO t1Data = afBlockDiscountingSPService.accountDetailSG(spParam);

        ESBAFBDAcctDetailsReqDTO subAcctListSGReqDTO = ESBAFBDAcctDetailsReqDTO.builder()
                .accountNumber(t1Data.getAcctNumber())
                .accountType(getAcctTypeCode(request.getProvider().name()))
                .region(ProviderEnum.SG.name())
                .currency(t1Data.getCurrency())
                .build();

        AFBDSGAcctDetailDTO tData = afbdesbService.getAFBDMasterAcctDetailsSG(subAcctListSGReqDTO).getMsgBody();
        if (tData != null) {
            if (tData.getBlockStatus() != null) {
                Optional<String> status = accountStatusRepository.findDescription01ByCodeAndProvider(
                        Integer.parseInt(tData.getBlockStatus()), "SG");
                t1Data.setBlockStatus(status.orElse(t1Data.getAcctStatusDesc()));
            }

            mergeMapper.merge(t1Data, tData, new AccountConditions(t1Data::getBlockStatus, tData::getTlastIndicator));
            t1Data.setMoreActions(
                    getMoreActions(
                            new AFBDAcctListViewAcctDTO(request.getAcctNumber(), request.getCurrency()), userAndCorp));
        }

        logger.info("result: {}", t1Data);

        return new ApiResponse<>(new ErrorResponse("0"), t1Data);
    }

    private ApiResponse<AFBDSGSubAcctDetailDTO> getSubAccountDetailForProviderSG(UserAndCorp userAndCorp, AFBDAcctDetailReqDTO request) {
        AFBDDetailSpParam spParam = spParamMapper.mapDetailSp(request, userAndCorp);
        AFBDSGSubAcctDetailDTO t1Data = afBlockDiscountingSPService.subAccountDetailSG(spParam);

        log.info("t1Data sub acct: {}", t1Data);

        ESBAFBDSubAcctDetailsSGReqDTO subAcctListSGReqDTO = ESBAFBDSubAcctDetailsSGReqDTO.builder()
                .accountNumber(t1Data.getAcctNumber())
                .subAccount(t1Data.getAgreementNumber())
                .accountType(getAcctTypeCode(request.getProvider().name()))
                .region(ProviderEnum.SG.name())
                .currency(t1Data.getCurrency())
                .build();

        AFBDSGSubAcctDetailDTO tData = afbdesbService.getAFBDAcctDetailsSG(subAcctListSGReqDTO).getMsgBody();
        if (tData != null) {
            mergeMapper.merge(t1Data, tData, new AccountConditions(t1Data::getAcctStatusDesc, tData::getTlastIndicator));
            t1Data.setMoreActions(
                    getMoreActions(
                            new AFBDAcctListViewAcctDTO(t1Data.getAcctNumber(), request.getCurrency()), userAndCorp));
        }
        logger.info("result: {}", t1Data);

        return new ApiResponse<>(new ErrorResponse("0"), t1Data);
    }

    public <T> ApiResponse<AFBDSubAcctSummaryDTO> blockDiscountingSubAcctListing(AFBDSubAcctListReqDTO request, UserAndCorp userAndCorp) {
        logger.info("Enter blockDiscountingSubAcctListing: {}", request);
        SearchCollectionResponse<T> overallResult;

        AFBDSubAcctListSpParam afbdSubAcctListSpParam = new AFBDSubAcctListSpParam(request.getAcctNumber(), request.getSearchValue(), userAndCorp.getUserId(), userAndCorp.getCorpId());
        afbdSubAcctListSpParam.setPage(request.getPage());
        afbdSubAcctListSpParam.setPageSize(request.getPageSize());

        AFBDSubAcctSummaryDTO afbdSubAcctSummaryDTO = new AFBDSubAcctSummaryDTO();
        AtomicReference<ESBMetadataDTO> esbMetadataDTO = new AtomicReference<>(new ESBMetadataDTO());
        switch (request.getAccountLevel()) {
            case FACILITY:
                Optional<String> provider = accountRepository.findProviderByAccountNo(request.getAcctNumber());

                if (provider.isEmpty()) {
                    logger.error(ACCT_NOT_FOUND + " {}", request.getAcctNumber());
                    return new ApiResponse<>(new ErrorResponse(ACCT_NOT_FOUND), new AFBDSubAcctSummaryDTO());
                }

                SearchCollectionResponse<AFBDSubMasterAcctListDTO> spResult = afBlockDiscountingSPService.subMasterListing(afbdSubAcctListSpParam);
                List<AFBDSubMasterAcctListDTO> acctT1 = spResult.getData();

                if (!acctT1.isEmpty()) {
                    //continue query from ESB if not empty

                    ESBAFBDBlockListReqDTO esbafbdBlockListReqDTO = new ESBAFBDBlockListReqDTO();
                    esbafbdBlockListReqDTO.setMasterAccountNumber(request.getAcctNumber());
                    esbafbdBlockListReqDTO.setProvider(provider.get());
                    esbafbdBlockListReqDTO.setAcctType(getAcctTypeCode(provider.get()));

                    AFAcctListESBDTO afAcctListESBDTO = new AFAcctListESBDTO();
                    List<AFBDSubMasterAcctListDTO> temp = new ArrayList<>();

                    IntStream.range(0, (acctT1.size() + batchSize - 1) / batchSize)
                            .forEach(i -> {
                                int start = i * batchSize;
                                int end = Math.min(start + batchSize, acctT1.size());
                                List<AFBDSubMasterAcctListDTO> batch = acctT1.subList(start, end);

                                esbafbdBlockListReqDTO.setDatas(batch);
                                AFAcctListESBDTO esbdto =
                                        afbdesbService.getAFBDMasterSubListInquiry(esbafbdBlockListReqDTO).getMsgBody();
                                esbMetadataDTO.set(esbdto.getEsbMetadata());
                                if (esbdto.getAcctListing() != null && !esbdto.getAcctListing().isEmpty())
                                    temp.addAll((List<AFBDSubMasterAcctListDTO>) esbdto.getAcctListing());
                            });

                    logger.info("t1 data: {}", acctT1);

                    if (!temp.isEmpty()) {
                        // Set balance values from ESB Host for real-time data
                        temp.forEach(account -> {
                            String accountNumber = account.getAcctNumber();
                            String outstandingBalance = account.getOutstandingBalance();
                            String financedAmt = account.getFinancedAmt();
                            String amountDue = account.getAmountDue();
                            logger.info("accountNumber: {}, outstandingBalance: {}, financedAmt: {}, amountDue: {}", accountNumber, outstandingBalance, financedAmt, amountDue);

                            acctT1.stream()
                                    .filter(dto -> dto.getAcctNumber() != null && dto.getAcctNumber().equals(accountNumber))
                                    .forEach(dto -> {
                                        boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(dto.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(dto.getAcctStatusDesc());

                                        dto.setOutstandingBalance(isClosed ? null : outstandingBalance);
                                        dto.setFinancedAmt(isClosed ? null : financedAmt);
                                        dto.setAmountDue(isClosed ? null : amountDue);
                                    });
                        });

                        acctT1.forEach(dto -> dto.setMoreActions(getMoreActions(new AFBDAcctListViewAcctDTO(request.getAcctNumber(), dto.getCurrency()), userAndCorp)));
                    } else {
                        acctT1.forEach(dto -> {
                            dto.setOutstandingBalance(null);
                            dto.setFinancedAmt(null);
                            dto.setMoreActions(getMoreActions(new AFBDAcctListViewAcctDTO(request.getAcctNumber(), dto.getCurrency()), userAndCorp));
                        });
                    }
                }

                overallResult = new SearchCollectionResponse<>(spResult.getTotalPages(), spResult.getCurrentPage(), spResult.getNextPage(), spResult.getTotalRecordCount());
                overallResult.setData((List<T>) acctT1);

                break;
            case ACCOUNT:
                Optional<AccountLinkBdDb> acctLinkProvider = accountLinkBlockDebitRepository.findByBlockNo(request.getAcctNumber());
                String validProvider = null;

                if (acctLinkProvider.isEmpty()) {
                    logger.error(ACCT_NOT_FOUND + " in acct link table {}", request.getAcctNumber());

                    Optional<String> acctProvider = accountRepository.findProviderByAccountNo(request.getAcctNumber());
                    if (acctProvider.isEmpty()) {
                        logger.error(ACCT_NOT_FOUND + " {}", request.getAcctNumber());
                        return new ApiResponse<>(new ErrorResponse(ACCT_NOT_FOUND), new AFBDSubAcctSummaryDTO());
                    } else {
                        validProvider = acctProvider.get();
                    }
                } else {
                    validProvider = acctLinkProvider.get().getSystemProvider();
                }

                if ("MY".equalsIgnoreCase(validProvider)) {
                    Optional<AccountLinkBdDb> linkBdDb = accountLinkBlockDebitRepository.findByBlockNo(request.getAcctNumber());

                    SearchCollectionResponse<AFBDSubChildAcctListDTO> facilitySpResult = afBlockDiscountingSPService.subAcctListing(afbdSubAcctListSpParam);
                    List<AFBDSubChildAcctListDTO> facilityT1 = facilitySpResult.getData();

                    if (!facilityT1.isEmpty()) {
                        //continue query from ESB if not empty
                        ESBAFBDAgreementListReqDTO esbafbdAgreementListReqDTO = new ESBAFBDAgreementListReqDTO();
                        esbafbdAgreementListReqDTO.setProvider("MY");
                        esbafbdAgreementListReqDTO.setMasterAccountNumber(linkBdDb.map(AccountLinkBdDb::getBlockDiscountingAccountNo).orElse(null));
                        esbafbdAgreementListReqDTO.setBlockNumber(request.getAcctNumber());
                        esbafbdAgreementListReqDTO.setAcctType(getAcctTypeCode("MY"));

                        AFAcctListESBDTO afAcctListESBDTO = new AFAcctListESBDTO();
                        List<AFBDSubChildAcctListDTO> facilityTemp = new ArrayList<>();

                        IntStream.range(0, (facilityT1.size() + batchSize - 1) / batchSize)
                                .forEach(i -> {
                                    int start = i * batchSize;
                                    int end = Math.min(start + batchSize, facilityT1.size());
                                    List<AFBDSubChildAcctListDTO> batch = facilityT1.subList(start, end);

                                    esbafbdAgreementListReqDTO.setDatas(batch);
                                    AFAcctListESBDTO esbdto =
                                            afbdesbService.getAFBDAccountSubListInquiry(esbafbdAgreementListReqDTO).getMsgBody();
                                    esbMetadataDTO.set(esbdto.getEsbMetadata());
                                    if (esbdto.getAcctListing() != null && !esbdto.getAcctListing().isEmpty())
                                        facilityTemp.addAll((List<AFBDSubChildAcctListDTO>) esbdto.getAcctListing());
                                });


                        logger.info("MY t1 data: {}", facilityT1);

                        if (!facilityTemp.isEmpty()) {
                            // Set balance values from ESB Host for real-time data
                            facilityTemp.forEach(account -> {
                                String accountNumber = account.getAgreementNumber();
                                String outstandingBalance = account.getOutstandingBalance();
                                String financedAmt = account.getFinancedAmt();
                                String amountDue = account.getAmountDue();
                                String settlementAmount = account.getSettlementAmount();
                                String installmentAmount = account.getInstallmentAmount();
                                logger.info("accountNumber: {}, outstandingBalance: {}, financedAmt: {}", accountNumber, outstandingBalance, financedAmt);

                                facilityT1.stream()
                                        .filter(dto -> dto.getAgreementNumber() != null && dto.getAgreementNumber().equals(accountNumber))
                                        .forEach(dto -> {
                                            boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(dto.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(dto.getAcctStatusDesc());

                                            dto.setOutstandingBalance(isClosed ? null : outstandingBalance);
                                            dto.setFinancedAmt(isClosed ? null : financedAmt);
                                            dto.setAmountDue(isClosed ? null : amountDue);
                                            dto.setSettlementAmount(isClosed ? null : settlementAmount);
                                            dto.setInstallmentAmount(isClosed ? null : installmentAmount);
                                        });
                            });

                            facilityT1
                                    .forEach(dto -> dto.setMoreActions(getMoreActions(new AFBDAcctListViewAcctDTO(linkBdDb.get().getBlockDiscountingAccountNo(), dto.getCurrency()), userAndCorp)));
                        } else {
                            facilityT1
                                    .forEach(dto -> {
                                        dto.setOutstandingBalance(null);
                                        dto.setFinancedAmt(null);
                                        dto.setAmountDue(null);
                                        dto.setSettlementAmount(null);
                                        dto.setInstallmentAmount(null);
                                        dto.setMoreActions(getMoreActions(new AFBDAcctListViewAcctDTO(linkBdDb.get().getBlockDiscountingAccountNo(), dto.getCurrency()), userAndCorp));
                                    });
                        }

                    }
                    overallResult = new SearchCollectionResponse<>(facilitySpResult.getTotalPages(), facilitySpResult.getCurrentPage(), facilitySpResult.getNextPage(), facilitySpResult.getTotalRecordCount());
                    overallResult.setData((List<T>) facilityT1);

                } else {
                    SearchCollectionResponse<AFBDSubAcctListSGDTO> spResultSG = afBlockDiscountingSPService.subAcctListingSG(afbdSubAcctListSpParam);
                    List<AFBDSubAcctListSGDTO> subAcctT1 = spResultSG.getData();

                    if (!subAcctT1.isEmpty()) {
                        //continue query from ESB if not empty

                        ESBAFBDSubAcctListSGReqDTO esbafbdSubAcctListSGReqDTO = new ESBAFBDSubAcctListSGReqDTO();
                        esbafbdSubAcctListSGReqDTO.setMasterAccountNumber(request.getAcctNumber());
                        esbafbdSubAcctListSGReqDTO.setProvider("SG");
                        esbafbdSubAcctListSGReqDTO.setAcctType(getAcctTypeCode("SG"));

                        AFAcctListESBDTO afAcctListESBDTO = new AFAcctListESBDTO();
                        List<AFBDSubAcctListSGDTO> subAcctTemp = new ArrayList<>();

                        IntStream.range(0, (subAcctT1.size() + batchSize - 1) / batchSize)
                                .forEach(i -> {
                                    int start = i * batchSize;
                                    int end = Math.min(start + batchSize, subAcctT1.size());
                                    List<AFBDSubAcctListSGDTO> batch = subAcctT1.subList(start, end);

                                    esbafbdSubAcctListSGReqDTO.setDatas(batch);
                                    AFAcctListESBDTO esbdto =
                                            afbdesbService.getAFBDSubAccountBalanceInquirySG(esbafbdSubAcctListSGReqDTO).getMsgBody();
                                    esbMetadataDTO.set(esbdto.getEsbMetadata());
                                    if (esbdto.getAcctListing() != null && !esbdto.getAcctListing().isEmpty())
                                        subAcctTemp.addAll((List<AFBDSubAcctListSGDTO>) esbdto.getAcctListing());
                                });

                        logger.info("SG t1 data: {}", subAcctTemp);

                        if (!subAcctTemp.isEmpty()) {
                            // Set balance values from ESB Host for real-time data
                            subAcctTemp.forEach(account -> {
                                String accountNumber = account.getBlockNumber();
                                String outstandingBalance = account.getOutstandingBalance();
                                String financedAmt = account.getFinancedAmt();
                                String settlementAmount = account.getSettlementAmount();
                                String instalmentAmt = account.getInstalmentAmt();
                                String instalmentAmtOverdue = account.getInstalmentAmtOverdue();
                                String collateralId = account.getCollateralId();
                                logger.info("accountNumber: {}, outstandingBalance: {}, financedAmt: {}", accountNumber, outstandingBalance, financedAmt);

                                subAcctT1.stream()
                                        .filter(dto -> dto.getBlockNumber() != null && dto.getBlockNumber().equals(accountNumber))
                                        .forEach(dto -> {
                                            boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(dto.getAcctStatusDesc()) || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(dto.getAcctStatusDesc());

                                            dto.setOutstandingBalance(isClosed ? null : outstandingBalance);
                                            dto.setFinancedAmt(isClosed ? null : financedAmt);
                                            dto.setSettlementAmount(isClosed ? null : settlementAmount);
                                            dto.setInstalmentAmt(isClosed ? null : instalmentAmt);
                                            dto.setInstalmentAmtOverdue(isClosed ? null : instalmentAmtOverdue);
                                            dto.setCollateralId(collateralId);
                                            dto.setTlastIndicator(account.getTlastIndicator());
                                            dto.setMoreActions(getMoreActions(new AFBDAcctListViewAcctDTO(request.getAcctNumber(), dto.getCurrency()), userAndCorp));
                                        });
                            });

                            subAcctT1.forEach(dto -> dto.setMoreActions(getMoreActions(new AFBDAcctListViewAcctDTO(request.getAcctNumber(), dto.getCurrency()), userAndCorp)));
                        } else {
                            subAcctT1
                                    .forEach(dto -> {
                                        dto.setOutstandingBalance(null);
                                        dto.setFinancedAmt(null);
                                        dto.setSettlementAmount(null);
                                        dto.setInstalmentAmt(null);
                                        dto.setInstalmentAmtOverdue(null);
                                        dto.setCollateralId(null);
                                        dto.setMoreActions(getMoreActions(new AFBDAcctListViewAcctDTO(request.getAcctNumber(), dto.getCurrency()), userAndCorp));
                                    });
                        }
                    }

                    overallResult = new SearchCollectionResponse<>(spResultSG.getTotalPages(), spResultSG.getCurrentPage(), spResultSG.getNextPage(), spResultSG.getTotalRecordCount());
                    overallResult.setData((List<T>) subAcctT1);
                }
                break;
            default:
                log.error("Account Level is unrecognized");
                return new ApiResponse<>(new ErrorResponse("Provider is unrecognized"), null);
        }
        logger.info("result: {}", overallResult);


        afbdSubAcctSummaryDTO.setDatas(overallResult);
        afbdSubAcctSummaryDTO.setEsbMetadata(esbMetadataDTO.get());
        return new ApiResponse<>(new ErrorResponse(), afbdSubAcctSummaryDTO);
    }

    public ApiResponse<AFBDTrxHistorySummaryDTO> blockDiscountingTrxHistory(AFBDTrxHistoryReqDTO request, UserAndCorp userAndCorp) {
        logger.info("Enter blockDiscountingTrxHistory: {}", request);
        ApiResponse<AFBDTrxHistorySummaryDTO> apiResponse = new ApiResponse<>();
        AFBDTrxHistorySummaryDTO afTrxHistoryDTO = new AFBDTrxHistorySummaryDTO();

        SearchCollectionResponse<AFBDTrxHistoryDTO> spResult = Optional.ofNullable(afBlockDiscountingSPService.trxHistory(getTrxHistorySpParam(request, userAndCorp)))
                .orElse(new SearchCollectionResponse<>());

        switch (request.getProvider().name()) {
            case "MY" -> {
                // get trx hist data from ESB
                ESBAFAcctActivityReqDTO esbafAcctActivityReqDTO = objectMapper.convertValue(request, ESBAFAcctActivityReqDTO.class);
                esbafAcctActivityReqDTO.setAcctType(getAcctTypeCode(request.getProvider().name()));
                esbafAcctActivityReqDTO.setPage(request.getPage());
                esbafAcctActivityReqDTO.setPageSize(request.getPageSize());

                List<AFBDTrxHistoryDTO> spDataList = Optional.of(spResult).map(SearchCollectionResponse::getData).orElse(new ArrayList<>());

                afTrxHistoryDTO = afbdesbService.getAFBDTrxHistory(esbafAcctActivityReqDTO).getMsgBody();
                List<AFBDTrxHistoryDTO> esbDataList = Optional.ofNullable(afTrxHistoryDTO.getDatas()).map(SearchCollectionResponse::getData).orElse(new ArrayList<>());
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

                    List<AFBDTrxHistoryDTO> fusedTrxHistoryDTOList = new ArrayList<>(esbDataList);
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
            case "SG" -> afTrxHistoryDTO.setDatas(spResult);
        }

        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(afTrxHistoryDTO);
        return apiResponse;
    }

    public DownloadDTO downloadFloorStocking(String auth, DownloadReq request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        return switch (request.getReportType()) {
            case AF_BD_ACCOUNT_LISTING_RPT_TYPE -> downloadAcctListing(userAndCorp, request);
            case AF_BD_SUB_ACCOUNT_LISTING_RPT_TYPE -> downloadSubAcctListing(request, userAndCorp);
            case AF_BD_TRX_HISTORY_RPT_TYPE -> downloadTrxHistory(request, userAndCorp);
            default -> throw new IllegalArgumentException("Report type is undefined");
        };
    }

    public DownloadDTO downloadAcctListing(UserAndCorp userAndCorp, DownloadReq request) {
        AFBDAcctListDownloadReqDTO req = (AFBDAcctListDownloadReqDTO) request;
        logger.info("download req: {}", req);

        byte[] fileData;
        String fileName;

        AFBDAcctListSpParam afbdAcctListSpParam = new AFBDAcctListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), req.getCorpCifNo(), req.getSearchParam());
        afbdAcctListSpParam.setPage(1);
        afbdAcctListSpParam.setPageSize(Integer.MAX_VALUE);

        AFBDAccountListReq afbdAccountListReq = new AFBDAccountListReq();
        afbdAccountListReq.setViewByIndicator(req.getViewByIndicator());
        afbdAccountListReq.setPage(1);
        afbdAccountListReq.setPageSize(Integer.MAX_VALUE);
        afbdAccountListReq.setSearchParam(req.getSearchParam());
        afbdAccountListReq.setCorpCifNo(req.getCorpCifNo());
        afbdAccountListReq.setRealTimeIndicator(req.getRealTimeIndicator());

        ApiResponse<AFBDAcctSummaryDTO> data = objectMapper.convertValue(bloorDiscountingAccountListing(userAndCorp, afbdAccountListReq), new TypeReference<ApiResponse<AFBDAcctSummaryDTO>>() {});
        switch (req.getViewByIndicator()) {
            case ACCOUNT -> {
                List<AFBDAcctListViewAcctDTO> acctList = Optional.ofNullable(data)
                        .map(ApiResponse::getMsgBody)
                        .map(body -> {
                            try {
                                return (body instanceof AFBDAcctSummaryDTO)
                                        ? (AFBDAcctSummaryDTO) body
                                        : objectMapper.convertValue(body, AFBDAcctSummaryDTO.class);
                            } catch (IllegalArgumentException e) {
                                logger.warn("Failed to convert msgBody to AFBDAcctSummaryDTO: {}", e.getMessage(), e);
                                return null;
                            }
                        })
                        .map(AFBDAcctSummaryDTO::getDatas)
                        .map(SearchCollectionResponse::getData)
                        .map(obj -> {
                            try {
                                return objectMapper.convertValue(obj, new com.fasterxml.jackson.core.type.TypeReference<List<AFBDAcctListViewAcctDTO>>() {});
                            } catch (IllegalArgumentException e) {
                                logger.warn("Failed to convert data to List<AFBDAcctListViewAcctDTO>: {}", e.getMessage(), e);
                                return java.util.Collections.<AFBDAcctListViewAcctDTO>emptyList();
                            }
                        })
                        .orElseGet(java.util.Collections::emptyList);

                logger.info("Result account list: {}", acctList);
                fileData = afBDAcctListViewAcctFileService.generateFile(acctList, req.getFormat());
                fileName = DownloadEnum.AF_BD_ACCT_LISTING_FILENAME.getDescription() + "ACCOUNT_" + System.currentTimeMillis() + "." + req.getFormat().name().toLowerCase();
            }
            case FACILITY -> {
                List<AFBDAcctListViewFacilityDTO> facilityList = Optional.ofNullable(data)
                        .map(ApiResponse::getMsgBody)
                        .map(body -> {
                            try {
                                return (body instanceof AFBDAcctSummaryDTO)
                                        ? (AFBDAcctSummaryDTO) body
                                        : objectMapper.convertValue(body, AFBDAcctSummaryDTO.class);
                            } catch (IllegalArgumentException e) {
                                logger.warn("Failed to convert msgBody to AFBDAcctSummaryDTO: {}", e.getMessage(), e);
                                return null;
                            }
                        })
                        .map(AFBDAcctSummaryDTO::getDatas)
                        .map(SearchCollectionResponse::getData)
                        .map(obj -> {
                            try {
                                return objectMapper.convertValue(obj, new com.fasterxml.jackson.core.type.TypeReference<List<AFBDAcctListViewFacilityDTO>>() {});
                            } catch (IllegalArgumentException e) {
                                logger.warn("Failed to convert data to List<AFBDAcctListViewFacilityDTO>: {}", e.getMessage(), e);
                                return java.util.Collections.<AFBDAcctListViewFacilityDTO>emptyList();
                            }
                        })
                        .orElseGet(java.util.Collections::emptyList);

                logger.info("result facilityList List: {}", facilityList);

                fileData = afBDAcctListViewFacilityFileService.generateFile(facilityList, req.getFormat());
                fileName = DownloadEnum.AF_BD_ACCT_LISTING_FILENAME.getDescription() + "FACILITY_" + System.currentTimeMillis() + "." + req.getFormat().name().toLowerCase();
            }
            default -> throw new IllegalArgumentException("Invalid view by indicator");
        }

        DownloadDTO result = new DownloadDTO();
        result.setData(fileData);
        result.setFileName(fileName);

        return result;
    }

    public <T> DownloadDTO downloadSubAcctListing(DownloadReq request, UserAndCorp userAndCorp) {
        logger.info("downloadSubAcctListing");
        AFBDSubAcctListDownloadReqDTO req = (AFBDSubAcctListDownloadReqDTO) request;

        AFBDSubAcctListReqDTO requestPage = new AFBDSubAcctListReqDTO();
        requestPage.setPage(1);
        requestPage.setPageSize(Integer.MAX_VALUE);
        requestPage.setAccountLevel(req.getAccountLevel());
        requestPage.setAcctNumber(req.getAcctNumber());
        requestPage.setSearchValue(req.getSearchParam());

        DownloadDTO result = new DownloadDTO();

        byte[] fileData;
        String fileName;

        List<T> listResult = blockDiscountingSubAcctListing(requestPage, userAndCorp).getMsgBody().getDatas().getData();
        logger.info("listResult: {}", listResult);

        switch (req.getAccountLevel()) {
            case FACILITY -> {
                List<AFBDSubMasterAcctListDTO> msPage = (List<AFBDSubMasterAcctListDTO>) listResult;
                logger.info("msPage: {}", msPage);
                fileData = afBDSubAcctMasterListFileService.generateFile(msPage, req.getFormat());
                fileName = DownloadEnum.AF_BD_SUB_ACCT_LISTING_FILENAME.getDescription() + "MY_" + System.currentTimeMillis() + "." + req.getFormat().name().toLowerCase();
            }
            case ACCOUNT -> {
                //check provider
                Optional<AccountLinkBdDb> acctLinkProvider = accountLinkBlockDebitRepository.findByBlockNo(req.getAcctNumber());
                String validProvider = null;

                if (acctLinkProvider.isEmpty()) {
                    logger.error(ACCT_NOT_FOUND + " in acct link table {}", req.getAcctNumber());

                    Optional<String> acctProvider = accountRepository.findProviderByAccountNo(req.getAcctNumber());
                    if (acctProvider.isEmpty()) {
                        logger.error(ACCT_NOT_FOUND + " {}", req.getAcctNumber());
                    } else {
                        validProvider = acctProvider.get();
                    }
                } else {
                    validProvider = acctLinkProvider.get().getSystemProvider();
                }

                if (validProvider == null) {
                    throw new IllegalArgumentException("Invalid provider indicator");
                }

                switch (validProvider) {
                    case "MY" -> {
                        List<AFBDSubChildAcctListDTO> myPage = (List<AFBDSubChildAcctListDTO>) listResult;
                        logger.info("myPage: {}", myPage);
                        fileData = afBDSubAcctChildListFileService.generateFile(myPage, req.getFormat());
                        fileName = DownloadEnum.AF_BD_SUB_ACCT_LISTING_FILENAME.getDescription() + "MY_" + System.currentTimeMillis() + "." + req.getFormat().name().toLowerCase();
                    }
                    case "SG" -> {
                        List<AFBDSubAcctListSGDTO> sgPage = (List<AFBDSubAcctListSGDTO>) listResult;
                        logger.info("sgPage: {}", sgPage);
                        fileData = afbdSubAcctChildListFileSGService.generateFile(sgPage, req.getFormat());
                        fileName = DownloadEnum.AF_BD_SUB_ACCT_LISTING_FILENAME.getDescription() + "SG_" + System.currentTimeMillis() + "." + req.getFormat().name().toLowerCase();
                    }
                    default -> throw new IllegalArgumentException("Invalid provider indicator");
                }

            }
            default -> throw new IllegalArgumentException("Invalid account level indicator");
        }

        result.setData(fileData);
        result.setFileName(fileName);

        return result;
    }

    public DownloadDTO downloadTrxHistory(DownloadReq request, UserAndCorp userAndCorp) {
        AFBDTrxHistoryDownloadReqDTO req = (AFBDTrxHistoryDownloadReqDTO) request;
        DownloadDTO result = new DownloadDTO();

        Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetailByAcctNumberAndProvider(req.getAcctNumber(), req.getProvider().name());

        if (accountData.isEmpty()) {
            logger.error("Account Not Found for: " + req.getAcctNumber());
            return result;
        }

        fileRepoService.insertFileRepo(request, userAndCorp, result, accountData.get(), req.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_1);

        AFBDTrxHistoryReqDTO requestPage = new AFBDTrxHistoryReqDTO();
        requestPage.setPage(1);
        requestPage.setPageSize(Integer.MAX_VALUE);
        requestPage.setAcctNumber(req.getAcctNumber());
        requestPage.setProvider(req.getProvider());
        requestPage.setFilters(req.getFilters());
        requestPage.setSearchParam(req.getSearchParam());
        requestPage.setCurrency(accountData.get().getCurrency());

        fileRepoService.insertFileRepo(request, userAndCorp, result, accountData.get(), req.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_2);

        byte[] fileData;
        String fileName;

        List<AFBDTrxHistoryDTO> trxHistories = blockDiscountingTrxHistory(requestPage, userAndCorp).getMsgBody().getDatas().getData();

        fileRepoService.insertFileRepo(request, userAndCorp, result, accountData.get(), req.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, REPORT_GENERATION_PROGRESS_STAGE_3);

        switch (req.getProvider()) {
            case MY -> {
                fileData = afBDMYTrxHistFileService.generateFile(trxHistories, req.getFormat(), req.getHeaders());
                fileName = DownloadEnum.AF_BD_TRX_HISTORY_FILENAME.getDescription() + "MY_" + System.currentTimeMillis() + "." + req.getFormat().name().toLowerCase();
            }
            case SG -> {
                fileData = afBDSGTrxHistFileService.generateFile(trxHistories, req.getFormat(), req.getHeaders());
                fileName = DownloadEnum.AF_BD_TRX_HISTORY_FILENAME.getDescription() + "SG_" + System.currentTimeMillis() + "." + req.getFormat().name().toLowerCase();
            }
            default -> throw new IllegalArgumentException("Invalid provider indicator");
        }

        result.setData(fileData);
        result.setFileName(fileName);

        fileRepoService.insertFileRepo(request, userAndCorp, result, accountData.get(), req.getAcctNumber(), accountData.get().getCurrency(),
                Product.AUTO_FINANCE, ReportTransactionTypeEnum.TRANSACTION_HISTORY, 100);

        return result;
    }

    public ApiResponse<AFBDDebitNoteDetailDTO> blockDiscountingDebitNoteDetail(UserAndCorp userAndCorp, AFBDDebitNoteDetailReqDTO request) {
        logger.info("Enter blockDiscountingDebitNoteDetail: {}", request);
        AFBDDebitDetailSpParam afbdDetailSpParam = new AFBDDebitDetailSpParam(request.getDebitNoteNumber(), request.getBlockNumber(), userAndCorp.getCorpId(), userAndCorp.getUserId());

        AFBDDebitNoteDetailDTO t1Data = afBlockDiscountingSPService.debitDetail(afbdDetailSpParam);

        ESBAFBDDebitNoteDetailsReqDTO esbRequest = ESBAFBDDebitNoteDetailsReqDTO.builder().
                accountNumber(t1Data.getMasterAcctNumber())
                .accountType(getAcctTypeCode(t1Data.getProvider()))
                .currency(t1Data.getCurrency())
                .debitNote(request.getDebitNoteNumber())
                .blockNumber(request.getBlockNumber())
                .region(t1Data.getProvider())
                .build();

        AFBDDebitNoteDetailDTO tData = afbdesbService.getAFBDDebitNoteDetails(esbRequest).getMsgBody();
        if (tData != null) {
            t1Data.setOutstandingBalance(tData.getOutstandingBalance());
        }

        logger.info("result: {}", t1Data);

        return new ApiResponse<>(new ErrorResponse("0"), t1Data);
    }

    public ApiResponse<AFBDDebitNoteSummaryDTO> blockDiscountingDebitNoteList(AFBDDebitNoteListReqDTO request, UserAndCorp userAndCorp) {
        logger.info("Enter blockDiscountingDebitNoteList: {}", request);

        AFBDDebitNoteListSpParam afbdDebitNoteListSpParam = new AFBDDebitNoteListSpParam(request.getDebitNoteNumber(), request.getBlockNumber(), userAndCorp.getUserId(), userAndCorp.getCorpId());
        afbdDebitNoteListSpParam.setPage(request.getPage());
        afbdDebitNoteListSpParam.setPageSize(request.getPageSize());

        List<AFBDDebitNoteListSpDTO> t1Data = afBlockDiscountingSPService.debitNoteListing(afbdDebitNoteListSpParam).getData();

        List<ESBAFBDDebitNoteDetailsReqDTO> esbafbdDebitListReqDTO = t1Data.stream().map(db -> ESBAFBDDebitNoteDetailsReqDTO.builder().
                accountNumber(db.getMasterAcctNumber())
                .accountType(getAcctTypeCode(db.getProvider()))
                .currency(db.getCurrency())
                .debitNote(request.getDebitNoteNumber())
                .blockNumber(db.getAcctNumber())
                .region(db.getProvider())
                .build()).toList();

        List<AFBDDebitNoteListDTO> populatedData = t1Data.stream().map(data -> modelMapper.map(data, AFBDDebitNoteListDTO.class)).toList();
        log.info("populatedData -> {}", populatedData);
        ESBMetadataDTO esbMetadataDTO = new ESBMetadataDTO();
        if (!esbafbdDebitListReqDTO.isEmpty()) {
            AFAcctListESBDTO afAcctListESBDTO = afbdesbService.getAFBDDebitNoteInquiry(esbafbdDebitListReqDTO).getMsgBody();
            List<AFBDDebitNoteListDTO> dataList = (List<AFBDDebitNoteListDTO>) afAcctListESBDTO.getAcctListing();
            log.info("dataList -> {}", dataList);
            esbMetadataDTO = afAcctListESBDTO.getEsbMetadata();

            if (dataList != null) {
                // Set balance values from ESB Host for real-time data
                dataList.forEach(account -> {
                    String accountNumber = account.getAcctNumber();
                    log.info("dataList accountNumber -> {}", accountNumber);
                    String blockAmt = account.getBlockAmt();
                    String date = account.getDisbursementDate();

                    populatedData.stream()
                            .filter(dto -> dto.getAcctNumber().equalsIgnoreCase(accountNumber))
                            .forEach(dto -> {
                                dto.setBlockAmt(blockAmt);
                                dto.setDisbursementDate(date);
                            });
                });
                log.info("populatedData after merging ESB fields -> {}", populatedData);
            } else {
                populatedData.stream()
                        .forEach(dto -> {
                            dto.setBlockAmt(null);
                            dto.setDisbursementDate(null);
                        });
            }
        }
        AFBDDebitNoteSummaryDTO afbdDebitNoteSummaryDTO = new AFBDDebitNoteSummaryDTO();
        SearchCollectionResponse<AFBDDebitNoteListDTO> searchCollectionResponse = new SearchCollectionResponse<>();
        int totalRecords = populatedData.size();
        searchCollectionResponse.setTotalRecordCount(totalRecords);
        Integer reqPageSize = request.getPageSize();
        int pageSize = (reqPageSize == null || reqPageSize <= 0) ? totalRecords == 0 ? 1 : totalRecords : reqPageSize;
        int totalPages = (totalRecords == 0) ? 0 : (totalRecords + pageSize - 1) / pageSize;
        searchCollectionResponse.setTotalPages(totalPages);
        int currentPage = Math.max(1, request.getPage());
        if (totalRecords > 0 && currentPage > totalPages) currentPage = totalPages;
        int nextPage = (currentPage < totalPages) ? currentPage + 1 : -1;
        searchCollectionResponse.setNextPage(nextPage);
        searchCollectionResponse.setCurrentPage(currentPage);
        searchCollectionResponse.setData(populatedData);
        logger.info("result: {}", searchCollectionResponse);

        afbdDebitNoteSummaryDTO.setDatas(searchCollectionResponse);
        afbdDebitNoteSummaryDTO.setEsbMetadata(esbMetadataDTO);

        return new ApiResponse<>(new ErrorResponse(), afbdDebitNoteSummaryDTO);
    }

    private AFBDTrxHistorySpParam getTrxHistorySpParam(AFBDTrxHistoryReqDTO request, UserAndCorp userAndCorp) {
        AFBDTrxHistorySpParam afbdTrxHistorySpParam = new AFBDTrxHistorySpParam(request.getAcctNumber(), userAndCorp.getCorpId(), userAndCorp.getUserId());
        afbdTrxHistorySpParam.setSearchParam(request.getSearchParam());
        afbdTrxHistorySpParam.setPage(request.getPage());
        afbdTrxHistorySpParam.setPageSize(request.getPageSize());
        afbdTrxHistorySpParam.setTrxCode(request.getFilters().getTrxCode());
        if (!request.getFilters().getAmtFrom().isEmpty() && !request.getFilters().getAmtTo().isEmpty()) {
            afbdTrxHistorySpParam.setAmtFrom(new BigDecimal(request.getFilters().getAmtFrom()));
            afbdTrxHistorySpParam.setAmtTo(new BigDecimal(request.getFilters().getAmtTo()));
        }
        if (!request.getFilters().getStartDate().isEmpty() && !request.getFilters().getStartTime().isEmpty() && !request.getFilters().getEndDate().isEmpty() && !request.getFilters().getEndTime().isEmpty()) {
            afbdTrxHistorySpParam.setStartDateTime(StringUtils.hasText(request.getFilters().getStartDate()) ? DateTimeUtil.convert12HourDateTime(request.getFilters().getStartDate(), request.getFilters().getStartTime(), true) : null);
            afbdTrxHistorySpParam.setEndDateTime(StringUtils.hasText(request.getFilters().getEndDate()) ? DateTimeUtil.convert12HourDateTime(request.getFilters().getEndDate(), request.getFilters().getEndTime(), false) : null);
        }
        afbdTrxHistorySpParam.setPage(request.getPage());
        afbdTrxHistorySpParam.setPageSize(request.getPageSize());
        return afbdTrxHistorySpParam;
    }


    private String getOutstandingBalance(List<AFBDAcctListViewAcctDTO> accountList) {
        if (accountList == null) {
            return null; // Return zero if DTO or data is null
        }
        BigDecimal totalBalance = accountList.stream()
                .map(account -> account.getOutstandingBalance() != null
                        ? new BigDecimal(account.getOutstandingBalance().replace(",", ""))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalBalance.toPlainString();
    }

    private String getFinancedAmount(List<AFBDAcctListViewAcctDTO> accountList) {
        if (accountList == null) {
            return null; // Return zero if DTO or data is null
        }
        BigDecimal totalBalance = accountList.stream()
                .map(account -> account.getFinancedAmt() != null
                        ? new BigDecimal(account.getFinancedAmt().replace(",", ""))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalBalance.toPlainString();
    }

    private String getTotalOutstandingBalance(List<AFBDAcctListViewFacilityDTO> accountList) {
        if (accountList == null) {
            return null; // Return zero if DTO or data is null
        }
        BigDecimal totalBalance = accountList.stream()
                .map(account -> account.getTotalOutstandingBalance() != null
                        ? new BigDecimal(account.getTotalOutstandingBalance().replace(",", ""))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalBalance.toPlainString();
    }

    private String getTotalFacilityLimit(List<AFBDAcctListViewFacilityDTO> accountList) {
        if (accountList == null) {
            return null; // Return zero if DTO or data is null
        }
        BigDecimal totalBalance = accountList.stream()
                .map(account -> account.getTotalFacilityLimit() != null
                        ? new BigDecimal(account.getTotalFacilityLimit().replace(",", ""))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalBalance.toPlainString();
    }

    private String getAcctTypeCode(String provider) {
        Optional<AccountProductTypeMatrix> matrix = matrixRepository.findByAcctTypeAndProv(AcctTypEnum.BD.name(), provider);
        if (matrix.isEmpty()) {
            logger.error(ACCT_TYP_FOUND);
            return null;
        }

        return matrix.get().getEsbAccountTypeCode();
    }

    private AFAcctListESBDTO getAccountListESBMY(List<AFBDAcctListViewAcctDTO> spResult, String cifNo) {
        AFAcctListESBDTO afAcctListESBDTO = new AFAcctListESBDTO();
        List<AFBDAcctListViewAcctDTO> acctTemp = new ArrayList<>();
        AtomicReference<ESBMetadataDTO> esbMetadataDTO = new AtomicReference<>(new ESBMetadataDTO());

        List<AFBDAcctListViewAcctDTO> t1MY = spResult.stream()
                .filter(dto -> ProviderEnum.MY.name().equalsIgnoreCase(dto.getProvider()))
                .toList();

        if (!t1MY.isEmpty()) {
            ESBAFBDAcctListReqDTO esbafbdAcctListReqDTOMY = new ESBAFBDAcctListReqDTO();
            esbafbdAcctListReqDTOMY.setCorpCifNo(cifNo);
            IntStream.range(0, (t1MY.size() + batchSize - 1) / batchSize)
                    .forEach(i -> {
                        int start = i * batchSize;
                        int end = Math.min(start + batchSize, t1MY.size());
                        List<AFBDAcctListViewAcctDTO> batch = t1MY.subList(start, end);

                        esbafbdAcctListReqDTOMY.setDatas(batch);
                        AFAcctListESBDTO esbdto =
                                afbdesbService.getAFBDAccountBalanceInquiry(esbafbdAcctListReqDTOMY).getMsgBody();
                        esbMetadataDTO.set(esbdto.getEsbMetadata());
                        if (esbdto.getAcctListing() != null && !esbdto.getAcctListing().isEmpty())
                            acctTemp.addAll((List<AFBDAcctListViewAcctDTO>) esbdto.getAcctListing());
                    });

            afAcctListESBDTO.setAcctListing(acctTemp);
            afAcctListESBDTO.setEsbMetadata(esbMetadataDTO.get());
            return afAcctListESBDTO;
        }

        return new AFAcctListESBDTO();
    }

    private AFAcctListESBDTO getAccountListESBSG(List<AFBDAcctListViewAcctDTO> spResult, String cifNo) {
        AFAcctListESBDTO afAcctListESBDTO = new AFAcctListESBDTO();
        List<AFBDAcctListViewAcctDTO> acctTemp = new ArrayList<>();
        List<AFBDAcctListViewAcctDTO> t1SG = spResult.stream()
                .filter(dto -> ProviderEnum.SG.name().equalsIgnoreCase(dto.getProvider()))
                .toList();

        if (!t1SG.isEmpty()) {
            ESBAFBDAcctListReqDTO esbafbdAcctListReqDTOSG = new ESBAFBDAcctListReqDTO();
            esbafbdAcctListReqDTOSG.setCorpCifNo(cifNo);

            AtomicReference<ESBMetadataDTO> esbMetadataDTO = new AtomicReference<>(new ESBMetadataDTO());
            IntStream.range(0, (t1SG.size() + batchSize - 1) / batchSize)
                    .forEach(i -> {
                        int start = i * batchSize;
                        int end = Math.min(start + batchSize, t1SG.size());
                        List<AFBDAcctListViewAcctDTO> batch = t1SG.subList(start, end);

                        esbafbdAcctListReqDTOSG.setDatas(batch);
                        AFAcctListESBDTO esbdto =
                                afbdesbService.getAFBDAccountBalanceInquirySG(esbafbdAcctListReqDTOSG).getMsgBody();
                        esbMetadataDTO.set(esbdto.getEsbMetadata());
                        if (esbdto.getAcctListing() != null && !esbdto.getAcctListing().isEmpty())
                            acctTemp.addAll((List<AFBDAcctListViewAcctDTO>) esbdto.getAcctListing());
                    });

            afAcctListESBDTO.setAcctListing(acctTemp);
            afAcctListESBDTO.setEsbMetadata(esbMetadataDTO.get());
            return afAcctListESBDTO;
        }

        return new AFAcctListESBDTO();
    }

    private AFAcctListESBDTO getFacilityListESBMY(List<AFBDAcctListViewFacilityDTO> spResult, String cifNo) {
        AFAcctListESBDTO afAcctListESBDTO = new AFAcctListESBDTO();
        List<AFBDAcctListViewFacilityDTO> acctTemp = new ArrayList<>();

        if (!spResult.isEmpty()) {
            ESBAFBDFacilityListReqDTO esbafbdFacilityListReqDTOMY = new ESBAFBDFacilityListReqDTO();
            esbafbdFacilityListReqDTOMY.setCorpCifNo(cifNo);

            AtomicReference<ESBMetadataDTO> esbMetadataDTO = new AtomicReference<>(new ESBMetadataDTO());

            IntStream.range(0, (spResult.size() + batchSize - 1) / batchSize)
                    .forEach(i -> {
                        int start = i * batchSize;
                        int end = Math.min(start + batchSize, spResult.size());
                        List<AFBDAcctListViewFacilityDTO> batch = spResult.subList(start, end);

                        esbafbdFacilityListReqDTOMY.setDatas(batch);
                        AFAcctListESBDTO esbdto =
                                afbdesbService.getAFBDMasterAccountBalanceInquiry(esbafbdFacilityListReqDTOMY).getMsgBody();
                        esbMetadataDTO.set(esbdto.getEsbMetadata());
                        if (esbdto.getAcctListing() != null && !esbdto.getAcctListing().isEmpty())
                            acctTemp.addAll((List<AFBDAcctListViewFacilityDTO>) esbdto.getAcctListing());
                        logger.info("getFacilityListESBMY: {}", acctTemp);
                    });

            afAcctListESBDTO.setAcctListing(acctTemp);
            afAcctListESBDTO.setEsbMetadata(esbMetadataDTO.get());
            return afAcctListESBDTO;
        }

        return new AFAcctListESBDTO();
    }

    private String getRedisKey(UserAndCorp userAndCorp, AFBDAccountListReq reqDTO) {
        String redisKey = null;
        if (reqDTO.isRealTimeIndicator())
            redisKey = redisHelper.createCacheKey(BD_AM_AF_BD_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "ONL" + reqDTO.getViewByIndicator().name() + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
        else
            redisKey = redisHelper.createCacheKey(BD_AM_AF_BD_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + reqDTO.getViewByIndicator().name() + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());

        return redisKey;
    }

    private ApiResponse<AFBDAcctSummaryDTO> getAcctFromCache(UserAndCorp userAndCorp, AFBDAccountListReq reqDTO) {
        return (ApiResponse<AFBDAcctSummaryDTO>) redisHelper.getFromCache(getRedisKey(userAndCorp, reqDTO), ApiResponse.class);
    }

    public void removeAllFromCache(UserAndCorp userAndCorp) {
        String redisKey = redisHelper.createCacheKey(BD_AM_AF_BD_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_*");
        redisHelper.deleteCachesWithPattern(redisKey);
        log.info("removed cache for {}", redisKey);
    }
}
