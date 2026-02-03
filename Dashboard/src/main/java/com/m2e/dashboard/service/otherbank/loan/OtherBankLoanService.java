package com.m2e.dashboard.service.otherbank.loan;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.otherbank.request.loan.OtherBankLoanAcctDetailReqDTO;
import com.m2e.dashboard.dto.otherbank.request.loan.OtherBankLoanAcctListReqDTO;
import com.m2e.dashboard.dto.otherbank.request.loan.OtherBankLoanAcctStatsReqDTO;
import com.m2e.dashboard.dto.otherbank.request.loan.OtherBankLoanTrxHistReqDTO;
import com.m2e.dashboard.dto.otherbank.request.loan.download.OtherBankLoanAcctListDownloadReqDTO;
import com.m2e.dashboard.dto.otherbank.request.loan.download.OtherBankLoanTrxHistoryDownloadReqDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailNormalDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailSpecialDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailSpecialListDTO;
import com.m2e.dashboard.dto.otherbank.response.loan.OtherBankLoanAcctListDTO;
import com.m2e.dashboard.dto.otherbank.response.loan.OtherBankLoanTrxHistDTO;
import com.m2e.dashboard.enums.DownloadEnum;
import com.m2e.dashboard.enums.OtherBankViewType;
import com.m2e.dashboard.sp.otherbank.request.OtherBankAcctListSpParam;
import com.m2e.dashboard.sp.otherbank.request.OtherBankDetailSpParam;
import com.m2e.dashboard.sp.otherbank.response.OtherBankLoanAcctListSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_OB_LN_ACCTLIST;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtherBankLoanService {
    private static final Logger logger = LoggerFactory.getLogger(OtherBankLoanService.class);

    private final OtherBankLoanAcctListFileService otherBankLoanAcctListFileService;
    private final OtherBankLoanTrxHistFileService otherBankLoanTrxHistFileService;
    private final OtherBankLoanSpService spService;
    private final RedisHelper redisHelper;

    @Value("${cache.ttl}")
    private Duration cacheDuration;

    public <T> ApiResponse<T> otherBankLoanAccountStats(OtherBankLoanAcctStatsReqDTO request, UserAndCorp userAndCorp) {
        logger.info("Enter otherBankLoanAccountStats: {} ", request);
        T data;
        switch (request.getViewType()) {
            case HISTORICAL_RECORD -> data = (T) spService.getOtherBankLoanAccountStatSp(userAndCorp);
            case INTRADAY -> data = (T) spService.getOtherBankLoanAccountStatIntradaySp(userAndCorp);
            default -> throw new IllegalArgumentException("View By Indicator is unrecognized");
        }
        logger.info("data: {} ", data);
        return new ApiResponse<>(new ErrorResponse(), data);
    }

    public ApiResponse<OtherBankLoanAcctListDTO> otherBankLoanAcctList(UserAndCorp userAndCorp, OtherBankLoanAcctListReqDTO request) {
        logger.info("Enter otherBankLoanAcctList: {} ", request);
        ApiResponse<OtherBankLoanAcctListDTO> apiResponse;
        SearchCollectionResponse<OtherBankLoanAcctListSpDTO> accountListResp = new SearchCollectionResponse<>();
        BaseSpDto<OtherBankAcctListSpParam, OtherBankLoanAcctListSpDTO> resultBaseSpDto;

        apiResponse = getAcctFromCache(userAndCorp, request);

        if (apiResponse == null) {
            apiResponse = new ApiResponse<>();

            switch (request.getViewType()) {
                case HISTORICAL_RECORD -> {
                    resultBaseSpDto = spService.getOtherBankLoanAccountListSp(userAndCorp,
                            request.getSearchParam(),
                            request.getPage(),
                            request.getPageSize());

                    logger.debug("HISTORICAL_RECORD " + resultBaseSpDto);
                }
                case INTRADAY -> {
                    resultBaseSpDto = spService.getOtherBankLoanAccountListIntradaySp(userAndCorp,
                            request.getSearchParam(),
                            request.getPage(),
                            request.getPageSize());

                    logger.debug("INTRADAY " + resultBaseSpDto);
                }
                default -> throw new IllegalArgumentException("View By Indicator is unrecognized");
            }

            List<OtherBankLoanAcctListSpDTO> spResultList = formatMessageType(Optional.ofNullable(resultBaseSpDto)
                    .map(BaseSpDto::getResults)
                    .orElse(Collections.emptyList()), request.getViewType());

            int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
            int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

            accountListResp.setNextPage(request.getPage() >= totalPage ? totalPage : request.getPage() + 1);
            accountListResp.setCurrentPage(request.getPage());
            accountListResp.setTotalPages(totalPage);
            accountListResp.setTotalRecordCount(totalRecordCount);
            accountListResp.setData(spResultList);

            OtherBankLoanAcctListDTO otherBankLoanAcctListDTO = new OtherBankLoanAcctListDTO();
            otherBankLoanAcctListDTO.setDatas(accountListResp);

            apiResponse.setMsgHeader(new ErrorResponse());
            apiResponse.setMsgBody(otherBankLoanAcctListDTO);

            redisHelper.save(getRedisKey(userAndCorp, request), apiResponse, cacheDuration.toMinutes(), TimeUnit.MINUTES);
        }

        return apiResponse;
    }

    public <T> ApiResponse<T> otherBankLoanAcctDetail(UserAndCorp userAndCorp, OtherBankLoanAcctDetailReqDTO request) {
        logger.info("Enter otherBankLoanAcctDetail: {} ", request);
        T result = null;

        switch(request.getMessageType()) {
            case MT940, MT950: {
                OtherBankAcctDetailNormalDTO detailNormalDTO = spService.getOtherBankLoanAccountDetailSp(userAndCorp, request);

                logger.info("data " + detailNormalDTO);
                result = (T) detailNormalDTO;
                break;
            }
            case MT941, MT942, MT941MT942: {
                OtherBankAcctDetailSpecialListDTO specialListDTO = spService.getOtherBankLoanAccountDetailSpecialSp(userAndCorp, request);
                BaseSpDto<OtherBankDetailSpParam, OtherBankAcctDetailSpecialDTO> resultBaseSpDto = spService.getOtherBankLoanStatementDetailSp(userAndCorp, request);
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

    public ApiResponse<SearchCollectionResponse<OtherBankLoanTrxHistDTO>> otherBankLoanTrxHistory(UserAndCorp userAndCorp, OtherBankLoanTrxHistReqDTO request) {
        logger.info("Enter otherBankLoanTrxHistory: {} ", request);
        ApiResponse<SearchCollectionResponse<OtherBankLoanTrxHistDTO>> apiResponse = new ApiResponse<>();
        SearchCollectionResponse<OtherBankLoanTrxHistDTO> data = spService.getOtherBankLoanTrxHistListSp(userAndCorp, request);
        apiResponse.setMsgHeader(new ErrorResponse());
        apiResponse.setMsgBody(data);
        return apiResponse;
    }

    public DownloadDTO downloadFile(UserAndCorp userAndCorp, DownloadReq request) {
        logger.info("Enter downloadFile: {} ", request);
        return switch (request.getReportType()) {
            case OTHER_BANK_LOAN_ACCOUNT_LISTING_RPT_TYPE -> downloadAcctListing(request, userAndCorp);
            case OTHER_BANK_LOAN_TRXHISTORIES_RPT_TYPE -> downloadTrxHistory(request, userAndCorp);
            default -> throw new IllegalArgumentException("Report type is undefined");
        };
    }

    public DownloadDTO downloadAcctListing(DownloadReq request, UserAndCorp userAndCorp) {
        logger.info("Enter downloadAcctListing: {} ", request);
        OtherBankLoanAcctListDownloadReqDTO req = (OtherBankLoanAcctListDownloadReqDTO) request;
        DownloadDTO result = new DownloadDTO();
        BaseSpDto<OtherBankAcctListSpParam, OtherBankLoanAcctListSpDTO> resultBaseSpDto;

        OtherBankLoanAcctListReqDTO requestPage = new OtherBankLoanAcctListReqDTO();
        requestPage.setPage(1);
        requestPage.setPageSize(Integer.MAX_VALUE);

        switch (req.getViewType()) {
            case HISTORICAL_RECORD -> resultBaseSpDto = spService.getOtherBankLoanAccountListSp(userAndCorp,
                    req.getSearchParam(),
                    1,
                    Integer.MAX_VALUE);
            case INTRADAY -> resultBaseSpDto = spService.getOtherBankLoanAccountListIntradaySp(userAndCorp,
                    req.getSearchParam(),
                    1,
                    Integer.MAX_VALUE);
            default -> throw new IllegalArgumentException("View By Indicator is unrecognized");
        }

        List<OtherBankLoanAcctListSpDTO> formattedSpResultList = formatMessageType(Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList()), req.getViewType());

        if (formattedSpResultList.isEmpty()) {
            result.setData(new byte[0]);
            return result;
        }

        switch (request.getFormat()) {
            case CSV:
                result.setData(otherBankLoanAcctListFileService.generateCsv(formattedSpResultList));
                result.setFileName(DownloadEnum.OTHER_BANK_LOAN_ACCTLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                result.setData(otherBankLoanAcctListFileService.generatePdf(formattedSpResultList));
                result.setFileName(DownloadEnum.OTHER_BANK_LOAN_ACCTLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                result.setData(otherBankLoanAcctListFileService.generateTxt(formattedSpResultList));
                result.setFileName(DownloadEnum.OTHER_BANK_LOAN_ACCTLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;

    }

    public DownloadDTO downloadTrxHistory(DownloadReq request, UserAndCorp userAndCorp) {
        logger.info("Enter downloadTrxHistory: {} ", request);
        DownloadDTO response = new DownloadDTO();
        OtherBankLoanTrxHistoryDownloadReqDTO downloadReqDTO = (OtherBankLoanTrxHistoryDownloadReqDTO) request;

        OtherBankLoanTrxHistReqDTO otherBankLoanTrxHistReqDTO = new OtherBankLoanTrxHistReqDTO();
        otherBankLoanTrxHistReqDTO.setPage(1);
        otherBankLoanTrxHistReqDTO.setPageSize(Integer.MAX_VALUE);
        otherBankLoanTrxHistReqDTO.setAcctNumber(downloadReqDTO.getAcctNumber());
        otherBankLoanTrxHistReqDTO.setTrxCode(downloadReqDTO.getTrxCode());
        otherBankLoanTrxHistReqDTO.setSearchParam(downloadReqDTO.getSearchParam());
        otherBankLoanTrxHistReqDTO.setFilters(downloadReqDTO.getFilters());
        otherBankLoanTrxHistReqDTO.setMsgType(downloadReqDTO.getMsgType());

        SearchCollectionResponse<OtherBankLoanTrxHistDTO> data = spService.getOtherBankLoanTrxHistListSp(userAndCorp, otherBankLoanTrxHistReqDTO);
        List<OtherBankLoanTrxHistDTO> spResultList = data.getData().stream().toList();

        if (spResultList.isEmpty()) {
            response.setData(new byte[0]);
            return response;
        }

        switch (request.getFormat()) {
            case CSV:
                response.setData(otherBankLoanTrxHistFileService.generateCsv(spResultList, downloadReqDTO.getHeaders()));
                response.setFileName(DownloadEnum.OTHER_BANK_LOAN_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                response.setData(otherBankLoanTrxHistFileService.generatePdf(spResultList, downloadReqDTO.getHeaders()));
                response.setFileName(DownloadEnum.OTHER_BANK_LOAN_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                response.setData(otherBankLoanTrxHistFileService.generateTxt(spResultList, downloadReqDTO.getHeaders()));
                response.setFileName(DownloadEnum.OTHER_BANK_LOAN_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return response;
    }

    public List<OtherBankLoanAcctListSpDTO> formatMessageType(List<OtherBankLoanAcctListSpDTO> accountList, OtherBankViewType viewType) {
        accountList.forEach(account -> {
            if (viewType.equals(OtherBankViewType.INTRADAY)) {
                String formattedMessageType = extractMT941AndMT942(account.getMsgType());
                account.setMsgType(formattedMessageType);
            } else {
                String formattedMessageType = extractMT940AndMT950(account.getMsgType());
                account.setMsgType(formattedMessageType);
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

    private String getRedisKey(UserAndCorp userAndCorp, OtherBankLoanAcctListReqDTO reqDTO) {
        String redisKey = null;
        switch (reqDTO.getViewType()) {
            case INTRADAY -> redisKey = redisHelper.createCacheKey(BD_AM_OB_LN_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
            case HISTORICAL_RECORD -> redisKey = redisHelper.createCacheKey(BD_AM_OB_LN_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "INTRA" + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
        }
        return redisKey;
    }

    private ApiResponse<OtherBankLoanAcctListDTO> getAcctFromCache(UserAndCorp userAndCorp, OtherBankLoanAcctListReqDTO reqDTO) {
        return (ApiResponse<OtherBankLoanAcctListDTO>) redisHelper.getFromCache(getRedisKey(userAndCorp, reqDTO), ApiResponse.class);
    }
}
