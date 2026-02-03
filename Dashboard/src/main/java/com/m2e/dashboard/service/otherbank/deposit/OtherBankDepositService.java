package com.m2e.dashboard.service.otherbank.deposit;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.otherbank.request.deposit.OtherBankDepositAcctDetailReqDTO;
import com.m2e.dashboard.dto.otherbank.request.deposit.OtherBankDepositAcctListingReqDTO;
import com.m2e.dashboard.dto.otherbank.request.deposit.OtherBankDepositAcctStatsReqDTO;
import com.m2e.dashboard.dto.otherbank.request.deposit.OtherBankDepositTrxHistoryReqDTO;
import com.m2e.dashboard.dto.otherbank.request.deposit.download.OtherBankDepositAcctListingDownloadReqDTO;
import com.m2e.dashboard.dto.otherbank.request.deposit.download.OtherBankDepositTrxHistoryDownloadReqDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailNormalDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailSpecialDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailSpecialListDTO;
import com.m2e.dashboard.dto.otherbank.response.deposit.OtherBankDepositAcctListingDTO;
import com.m2e.dashboard.dto.otherbank.response.deposit.OtherBankDepositTrxHistoryDTO;
import com.m2e.dashboard.enums.DownloadEnum;
import com.m2e.dashboard.enums.OtherBankViewType;
import com.m2e.dashboard.sp.otherbank.request.OtherBankAcctListSpParam;
import com.m2e.dashboard.sp.otherbank.request.OtherBankDetailSpParam;
import com.m2e.dashboard.sp.otherbank.response.OtherBankDepositAcctListingSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_OB_DP_ACCTLIST;

@Service
@RequiredArgsConstructor
public class OtherBankDepositService {

    private static final Logger logger = LoggerFactory.getLogger(OtherBankDepositService.class);

    private final OtherBankDepositSpService spService;
    private final OtherBankDepositFileService fileService;
    private final OtherBankDepositTrxHistoryFileService trxHistoryFileService;
    private final RedisHelper redisHelper;

    @Value("${cache.ttl}")
    private Duration cacheDuration;

    public <T> ApiResponse<T> getOtherBankDepositAccountStat(UserAndCorp userAndCorp, OtherBankDepositAcctStatsReqDTO req) {
        logger.info("Enter getOtherBankDepositAccountStatsSp: {} ", req);

        ApiResponse<T> apiResponse = new ApiResponse<>();
        T otherBanktAcctStatsResp;

        switch (req.getViewType()) {
            case INTRADAY ->
                    otherBanktAcctStatsResp = (T) spService.getOtherBankDepositAccountStatIntradaySp(userAndCorp);
            case HISTORICAL_RECORD ->
                    otherBanktAcctStatsResp = (T) spService.getOtherBankDepositAccountStatSp(userAndCorp);
            default -> throw new IllegalArgumentException("View By Indicator is unrecognized");
        }

        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(otherBanktAcctStatsResp);

        return apiResponse;
    }

    public ApiResponse<OtherBankDepositAcctListingDTO> getOtherBankDepositAccountList(UserAndCorp userAndCorp, OtherBankDepositAcctListingReqDTO req) {
        logger.info("Enter getOtherBankDepositAccountList: {} ", req);
        BaseSpDto<OtherBankAcctListSpParam, OtherBankDepositAcctListingSpDTO> resultBaseSpDto;
        ApiResponse<OtherBankDepositAcctListingDTO> apiResponse;

        apiResponse = getAcctFromCache(userAndCorp, req);

        if (apiResponse == null) {
            apiResponse = new ApiResponse<>();
            switch (req.getViewType()) {
                case INTRADAY -> {

                    resultBaseSpDto = spService.getOtherBankDepositAccountListIntradaySp(userAndCorp,
                            req.getSearchParam(),
                            req.getPage(),
                            req.getPageSize());

                    logger.info("INTRADAY " + resultBaseSpDto);

                }
                case HISTORICAL_RECORD -> {

                    resultBaseSpDto = spService.getOtherBankDepositAccountListSp(userAndCorp,
                            req.getSearchParam(),
                            req.getPage(),
                            req.getPageSize());

                    logger.info("HISTORICAL_RECORD " + resultBaseSpDto);

                }
                default -> throw new IllegalArgumentException("View By Indicator is unrecognized");
            }

            List<OtherBankDepositAcctListingSpDTO> spResultList = formatMessageType(Optional.ofNullable(resultBaseSpDto)
                    .map(BaseSpDto::getResults)
                    .orElse(Collections.emptyList()), req.getViewType());

            int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
            int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

            SearchCollectionResponse<OtherBankDepositAcctListingSpDTO> accountListResp = new SearchCollectionResponse<>();
            accountListResp.setNextPage(req.getPage() >= totalPage ? totalPage : req.getPage() + 1);
            accountListResp.setCurrentPage(req.getPage());
            accountListResp.setTotalPages(totalPage);
            accountListResp.setTotalRecordCount(totalRecordCount);
            accountListResp.setData(spResultList);

            OtherBankDepositAcctListingDTO otherBankDepositAcctListingDTO = new OtherBankDepositAcctListingDTO();
            otherBankDepositAcctListingDTO.setDatas(accountListResp);

            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(otherBankDepositAcctListingDTO);

            redisHelper.save(getRedisKey(userAndCorp, req), apiResponse, cacheDuration.toMinutes(), TimeUnit.MINUTES);
        }

        return apiResponse;
    }

    public DownloadDTO downloadOtherBankDeposit(UserAndCorp userAndCorp, DownloadReq request) {
        logger.info("Enter downloadOtherBankDeposit: {}", request);
        DownloadDTO result = new DownloadDTO();
        switch (request.getReportType()) {
            case OTHER_BANK_DEPOSIT_ACCOUNT_LISTING_RPT_TYPE -> {
                OtherBankDepositAcctListingDownloadReqDTO requestData = (OtherBankDepositAcctListingDownloadReqDTO) request;
                downloadOtherBankDepositAccountList(result, requestData, userAndCorp);
            }
            case OTHER_BANK_TRXHISTORIES_RPT_TYPE -> {
                OtherBankDepositTrxHistoryDownloadReqDTO requestData = (OtherBankDepositTrxHistoryDownloadReqDTO) request;
                downloadOtherBankDepositTrxHistoryList(result, requestData, userAndCorp);
            }
            default -> {
                throw new IllegalArgumentException("Report type is undefined");
            }
        }
        return result;
    }

    public DownloadDTO downloadOtherBankDepositAccountList(DownloadDTO response, OtherBankDepositAcctListingDownloadReqDTO request, UserAndCorp userAndCorp) {
        logger.info("Enter downloadOtherBankDepositAccountList: {} ", request);
        BaseSpDto<OtherBankAcctListSpParam, OtherBankDepositAcctListingSpDTO> resultBaseSpDto;

        switch (request.getViewType()) {
            case INTRADAY -> resultBaseSpDto = spService.getOtherBankDepositAccountListIntradaySp(userAndCorp,
                    request.getSearchParam(),
                    1,
                    Integer.MAX_VALUE);
            case HISTORICAL_RECORD -> resultBaseSpDto = spService.getOtherBankDepositAccountListSp(userAndCorp,
                    request.getSearchParam(),
                    1,
                    Integer.MAX_VALUE);
            default -> throw new IllegalArgumentException("View By Indicator is unrecognized");
        }

        List<OtherBankDepositAcctListingSpDTO> formattedSpResultList = formatMessageType(Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList()), request.getViewType());

        if (formattedSpResultList.isEmpty()) {
            response.setData(new byte[0]);
            return response;
        }

        switch (request.getFormat()) {
            case CSV:
                response.setData(fileService.generateCsv(formattedSpResultList));
                response.setFileName(DownloadEnum.OTHER_BANK_DEPOSIT_ACCTLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                response.setData(fileService.generatePdf(formattedSpResultList));
                response.setFileName(DownloadEnum.OTHER_BANK_DEPOSIT_ACCTLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                response.setData(fileService.generateTxt(formattedSpResultList));
                response.setFileName(DownloadEnum.OTHER_BANK_DEPOSIT_ACCTLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return response;
    }

    public DownloadDTO downloadOtherBankDepositTrxHistoryList(DownloadDTO response, OtherBankDepositTrxHistoryDownloadReqDTO request, UserAndCorp userAndCorp) {
        logger.info("Enter downloadOtherBankDepositTrxHistoryList: {} ", request);
        OtherBankDepositTrxHistoryReqDTO otherBankDepositTrxHistoryReqDTO = new OtherBankDepositTrxHistoryReqDTO();
        otherBankDepositTrxHistoryReqDTO.setPage(1);
        otherBankDepositTrxHistoryReqDTO.setPageSize(Integer.MAX_VALUE);
        otherBankDepositTrxHistoryReqDTO.setAcctNumber(request.getAcctNumber());
        otherBankDepositTrxHistoryReqDTO.setTrxCode(request.getTrxCode());
        otherBankDepositTrxHistoryReqDTO.setSearchParam(request.getSearchParam());
        otherBankDepositTrxHistoryReqDTO.setFilters(request.getFilters());
        otherBankDepositTrxHistoryReqDTO.setMsgType(request.getMsgType());

        SearchCollectionResponse<OtherBankDepositTrxHistoryDTO> data = spService.getOtherBankDepositTrxHistListSp(userAndCorp, otherBankDepositTrxHistoryReqDTO);
        List<OtherBankDepositTrxHistoryDTO> spResultList = data.getData().stream().toList();

        if (spResultList.isEmpty()) {
            response.setData(new byte[0]);
            return response;
        }

        switch (request.getFormat()) {
            case CSV:
                response.setData(trxHistoryFileService.generateCsv(spResultList, request.getHeaders()));
                response.setFileName(DownloadEnum.OTHER_BANK_DEPOSIT_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                response.setData(trxHistoryFileService.generatePdf(spResultList, request.getHeaders()));
                response.setFileName(DownloadEnum.OTHER_BANK_DEPOSIT_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                response.setData(trxHistoryFileService.generateTxt(spResultList, request.getHeaders()));
                response.setFileName(DownloadEnum.OTHER_BANK_DEPOSIT_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return response;
    }

    public <T> ApiResponse<T> otherBankDepositAccountDetail(UserAndCorp userAndCorp, OtherBankDepositAcctDetailReqDTO request) {
        logger.info("Enter otherBankDepositAccountDetail: {} ", request);
        T result = null;

        switch(request.getMessageType()) {
            case MT940, MT950: {
                OtherBankAcctDetailNormalDTO detailNormalDTO = spService.getOtherBankDepositAccountDetailSp(userAndCorp, request);

                logger.info("data " + detailNormalDTO);
                result = (T) detailNormalDTO;
                break;
            }
            case MT941, MT942, MT941MT942: {
                OtherBankAcctDetailSpecialListDTO specialListDTO = spService.getOtherBankDepositAccountDetailSpecialSp(userAndCorp, request);
                BaseSpDto<OtherBankDetailSpParam, OtherBankAcctDetailSpecialDTO> resultBaseSpDto = spService.getOtherBankDepositStatementDetailSp(userAndCorp, request);
                List<OtherBankAcctDetailSpecialDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                        .map(BaseSpDto::getResults)
                        .orElse(Collections.emptyList());

                if (specialListDTO != null)
                    specialListDTO.setStatementDetail(spResultList);

                logger.info("data " + specialListDTO);

                result = (T) specialListDTO;
                break;
            }
        }

        return new ApiResponse<>(new ErrorResponse(), result);
    }

    public ApiResponse<SearchCollectionResponse<OtherBankDepositTrxHistoryDTO>> otherBankDepositTrxHistory
            (UserAndCorp userAndCorp, OtherBankDepositTrxHistoryReqDTO request) {
        logger.info("Enter otherBankDepositTrxHistory: {} ", request);
        ApiResponse<SearchCollectionResponse<OtherBankDepositTrxHistoryDTO>> apiResponse = new ApiResponse<>();
        SearchCollectionResponse<OtherBankDepositTrxHistoryDTO> data = spService.getOtherBankDepositTrxHistListSp(userAndCorp, request);
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(data);
        return apiResponse;
    }

    public List<OtherBankDepositAcctListingSpDTO> formatMessageType(List<OtherBankDepositAcctListingSpDTO> accountList, OtherBankViewType viewType) {
        accountList.forEach(account -> {
            if (viewType.equals(OtherBankViewType.INTRADAY)) {
                String formattedMessageType = extractMT941AndMT942(account.getMessageType());
                account.setMessageType(formattedMessageType);
            } else {
                String formattedMessageType = extractMT940AndMT950(account.getMessageType());
                account.setMessageType(formattedMessageType);
            }
        });
        return accountList;
    }

    private String extractMT940AndMT950(String messageType) {
        if (messageType == null || messageType.isEmpty()) {
            return null;
        }
        //only extract MT940, MT950 value
        return List.of(messageType.split("[,|]"))
                .stream()
                .filter(type -> type.trim().equals("MT940") || type.trim().equals("MT950"))
                .collect(Collectors.joining(", "));
    }

    private String extractMT941AndMT942(String messageType) {
        if (messageType == null || messageType.isEmpty()) {
            return null;
        }
        //only extract MT941, MT942 value
        return List.of(messageType.split("[,|]"))
                .stream()
                .filter(type -> type.trim().equals("MT941") || type.trim().equals("MT942"))
                .collect(Collectors.joining(", "));
    }

    private String getRedisKey(UserAndCorp userAndCorp, OtherBankDepositAcctListingReqDTO reqDTO) {
        String redisKey = null;
        switch (reqDTO.getViewType()) {
            case INTRADAY -> redisKey = redisHelper.createCacheKey(BD_AM_OB_DP_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
            case HISTORICAL_RECORD -> redisKey = redisHelper.createCacheKey(BD_AM_OB_DP_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "INTRA" + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
        }
        return redisKey;
    }

    private ApiResponse<OtherBankDepositAcctListingDTO> getAcctFromCache(UserAndCorp userAndCorp, OtherBankDepositAcctListingReqDTO reqDTO) {
        return (ApiResponse<OtherBankDepositAcctListingDTO>) redisHelper.getFromCache(getRedisKey(userAndCorp, reqDTO), ApiResponse.class);
    }
}
