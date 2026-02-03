package com.m2e.dashboard.service.loan.rc;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.base.loan.LNAcctListReq;
import com.m2e.dashboard.dto.base.loan.LNReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.loan.tlf.request.LNTrxHistoryReqDTO;
import com.m2e.dashboard.dto.loan.rc.request.LNRevolvingCreditDetailReqDTO;
import com.m2e.dashboard.dto.loan.rc.request.download.LNRevolvingCreditAcctListingDownloadReqDTO;
import com.m2e.dashboard.dto.loan.rc.request.download.LNRevolvingCreditTrxHistoryDownloadReqDTO;
import com.m2e.dashboard.dto.loan.rc.response.*;
import com.m2e.dashboard.enums.DownloadEnum;
import com.m2e.dashboard.sp.loan.rc.request.LNRCAcctDetailSpParam;
import com.m2e.dashboard.sp.loan.rc.request.LNRCAcctListingSpParam;
import com.m2e.dashboard.sp.loan.rc.request.LNRCAcctStatsSpParam;
import com.m2e.dashboard.sp.loan.rc.request.LNRCTrxHistorySpParam;
import com.m2e.dashboard.sp.loan.rc.response.LNRCAcctDetailMYSpDTO;
import com.m2e.dashboard.sp.loan.rc.response.LNRCAcctDetailSGSpDTO;
import com.m2e.dashboard.sp.loan.rc.response.LNRCAcctListSpDTO;
import com.m2e.dashboard.util.DateTimeUtil;
import com.m2e.dashboard.util.UtilityFunctions;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.m2e.dashboard.enums.AcctRedisKeyEnum.*;

@Service
@Slf4j
public class LNRevolvingCreditService {
    private static final Logger logger = LoggerFactory.getLogger(LNRevolvingCreditService.class.getName());
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UtilityFunctions utilityFunctions;
    @Autowired private ModelMapper modelMapper;
    @Autowired private LNRevolvingCreditSPService lnRevolvingCreditSPService;
    @Autowired private LNRevolvingCreditFileService fileService;
    @Autowired private LNRevolvingCreditTrxHistoryFileService historyFileService;
    @Autowired private RedisHelper redisHelper;

    public ApiResponse<LNRevolvingCreditAccountStatisticDTO> getLoanRevolvingCreditAccountStatistic(String auth, LNReq request){
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        removeAllFromCache(userAndCorp);
        LNRCAcctStatsSpParam lntlfAcctStatsSpParam = new LNRCAcctStatsSpParam();
        lntlfAcctStatsSpParam.setCorpId(userAndCorp.getCorpId());
        lntlfAcctStatsSpParam.setUserId(userAndCorp.getUserId());
        LNRevolvingCreditAccountStatisticDTO lnRevolvingCreditAccountStatisticDTO = lnRevolvingCreditSPService.accountStatisticsRCMY(lntlfAcctStatsSpParam);

        if (request.isRealTimeIndicator())
            lnRevolvingCreditAccountStatisticDTO.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());

        ApiResponse<LNRevolvingCreditAccountStatisticDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(lnRevolvingCreditAccountStatisticDTO);

        return apiResponse;
    }

    public ApiResponse<LNRevolvingCreditAcctListingDTO> getLoanRevolvingCreditAccountListing(String auth, LNAcctListReq request){
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        ApiResponse<LNRevolvingCreditAcctListingDTO> apiResponse = new ApiResponse<>();
        apiResponse = getAcctFromCache(userAndCorp, request);

        if (apiResponse == null) {
            apiResponse = new ApiResponse<>();
            LNRCAcctListingSpParam lnrcAcctListingSpParam = new LNRCAcctListingSpParam();
            lnrcAcctListingSpParam.setCorpId(userAndCorp.getCorpId());
            lnrcAcctListingSpParam.setUserId(userAndCorp.getUserId());
            lnrcAcctListingSpParam.setCorpCifNo(request.getCorpCifNo());
            lnrcAcctListingSpParam.setSearchParam(request.getSearchParam());
            lnrcAcctListingSpParam.setPage(request.getPage());
            lnrcAcctListingSpParam.setPageSize(request.getPageSize());

            BaseSpDto<LNRCAcctListingSpParam, LNRCAcctListSpDTO> resultBaseSpDto = lnRevolvingCreditSPService.accountListingRC(lnrcAcctListingSpParam);
            List<LNRCAcctListSpDTO> dbResult = resultBaseSpDto.getResults();
            List<LNRevolvingCreditDTO> spResultList = dbResult.stream().map(acct -> {
                LNRevolvingCreditDTO result = modelMapper.map(acct, LNRevolvingCreditDTO.class);
                result.setIslamic("Y".equals(acct.getIsIslamic()));
                result.setFITAS("Y".equals(acct.getIsFITAS()));
                return result;
            }).toList();

            SearchCollectionResponse<LNRevolvingCreditDTO> result = new SearchCollectionResponse<>();
            result.setNextPage(
                    lnrcAcctListingSpParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : lnrcAcctListingSpParam.getPage() + 1
            );
            result.setCurrentPage(lnrcAcctListingSpParam.getPage());
            result.setTotalPages(resultBaseSpDto.getTotalPage());
            result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
            result.setData(spResultList);

            LNRevolvingCreditAcctListingDTO lnrcAcctListingDTO = new LNRevolvingCreditAcctListingDTO();
            lnrcAcctListingDTO.setCorpCifNo(request.getCorpCifNo());
            lnrcAcctListingDTO.setDatas(result);

            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(lnrcAcctListingDTO);

            redisHelper.save(getRedisKey(userAndCorp, request), apiResponse);
        }

        return apiResponse;
    }

    public DownloadDTO downloadLoanRevolvingCredit(String auth, DownloadReq request){
        DownloadDTO result = new DownloadDTO();
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        switch (request.getReportType()){
            case LOAN_RC_ACCOUNTLISTING_RPT_TYPE -> {
                downloadAccountListing(result, userAndCorp, request);
            }
            case LOAN_RC_TRXHISTORIES_RPT_TYPE -> {
                LNRevolvingCreditTrxHistoryDownloadReqDTO requestData = (LNRevolvingCreditTrxHistoryDownloadReqDTO) request;
                downloadTrxHistorySG(result, userAndCorp, requestData);
            }
            default -> {throw new IllegalArgumentException("Report type is undefined");}
        }
        return result;
    }

    private void downloadAccountListing(DownloadDTO response, UserAndCorp userAndCorp, DownloadReq downloadRequest){
        LNRevolvingCreditAcctListingDownloadReqDTO request = (LNRevolvingCreditAcctListingDownloadReqDTO) downloadRequest;
        LNRCAcctListingSpParam acctListingSpParam = new LNRCAcctListingSpParam();
        acctListingSpParam.setUserId(userAndCorp.getUserId());
        acctListingSpParam.setCorpId(userAndCorp.getCorpId());
        acctListingSpParam.setCorpCifNo(request.getCorpCifNo());
        acctListingSpParam.setSearchParam("");
        acctListingSpParam.setPage(1);
        acctListingSpParam.setPageSize(Integer.MAX_VALUE);
        BaseSpDto<LNRCAcctListingSpParam, LNRCAcctListSpDTO> resultBaseSpDto = lnRevolvingCreditSPService.accountListingRC(acctListingSpParam);
        List<LNRCAcctListSpDTO> data = resultBaseSpDto.getResults().stream().toList();
        switch (request.getFormat()) {
            case CSV:
                response.setData(fileService.generateCsv(data));
                response.setFileName(DownloadEnum.LOAN_RC_ACCLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
                break;
            case PDF:
                response.setData(fileService.generatePdf(data));
                response.setFileName(DownloadEnum.LOAN_RC_ACCLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
                break;
            case TXT:
                response.setData(fileService.generateTxt(data));
                response.setFileName(DownloadEnum.LOAN_RC_ACCLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
    }

    private void downloadTrxHistorySG(DownloadDTO response, UserAndCorp userAndCorp, LNRevolvingCreditTrxHistoryDownloadReqDTO request){
        LNRCTrxHistorySpParam lnrcTrxHistorySpParam = new LNRCTrxHistorySpParam(request.getAcctNumber(), request.getCurrency(), userAndCorp.getCorpId(), userAndCorp.getUserId());
        lnrcTrxHistorySpParam.setPage(1);
        lnrcTrxHistorySpParam.setPageSize(Integer.MAX_VALUE);

        BaseSpDto<LNRCTrxHistorySpParam, LNRevolvingCreditTrxHistoryDTO> resultBaseSpDto = lnRevolvingCreditSPService.trxHistorySG(lnrcTrxHistorySpParam);
        logger.info("generateTrxHistorySGFile request: {}", request);
        logger.info("generateTrxHistorySGFile lnrcTrxHistorySpParam: {}", lnrcTrxHistorySpParam);
        logger.info("generateTrxHistorySGFile resultBaseSpDto: {}", resultBaseSpDto);
        generateTrxHistorySGFile(response, resultBaseSpDto, request);
    }

    private <T> void generateTrxHistorySGFile(DownloadDTO response, BaseSpDto<T, LNRevolvingCreditTrxHistoryDTO> resultBaseSpDto, DownloadReq request) {
        List<LNRevolvingCreditTrxHistoryDTO> data = resultBaseSpDto.getResults();
        logger.info("generateTrxHistorySGFile data: {}", data);

        switch (request.getFormat()) {
            case CSV:
                response.setData(historyFileService.generateCsv(data));
                response.setFileName(DownloadEnum.LOAN_RC_TRXHISTORIES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
                break;
            case PDF:
                response.setData(historyFileService.generatePdf(data));
                response.setFileName(DownloadEnum.LOAN_RC_TRXHISTORIES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
                break;
            case TXT:
                response.setData(historyFileService.generateTxt(data));
                response.setFileName(DownloadEnum.LOAN_RC_TRXHISTORIES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
    }

    public ApiResponse<LNRevolvingCreditDetailDTO> getLoanRevolvingCreditDetail(String auth, LNRevolvingCreditDetailReqDTO request){
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        LNRCAcctDetailSpParam lnrcAcctDetailSpParam = new LNRCAcctDetailSpParam();
        lnrcAcctDetailSpParam.setCorpId(userAndCorp.getCorpId());
        lnrcAcctDetailSpParam.setUserId(userAndCorp.getUserId());
        lnrcAcctDetailSpParam.setAcctNumber(request.getAcctNumber());
        lnrcAcctDetailSpParam.setAcctCurrency(request.getCurrency());

        switch (request.getProvider()) {
            case "MY" -> {
                LNRCAcctDetailMYSpDTO dbResult = lnRevolvingCreditSPService.accountDetailRCMY(lnrcAcctDetailSpParam);
                LNRevolvingCreditDetailMYDTO acctDtlMY = modelMapper.map(dbResult, LNRevolvingCreditDetailMYDTO.class);
                return new ApiResponse<>(new ErrorResponse(), acctDtlMY);
            }
            case "SG" -> {
                LNRCAcctDetailSGSpDTO dbResult = lnRevolvingCreditSPService.accountDetailRCSG(lnrcAcctDetailSpParam);
                if ("Y".equals(dbResult.getIsFitas())) {
                    if ("Y".equals(dbResult.getIsIslamic())) {
                        LNRevolvingCreditDetailIslamicFITASSGDTO acctDtlIslamicFitasSG = modelMapper.map(dbResult, LNRevolvingCreditDetailIslamicFITASSGDTO.class);
                        acctDtlIslamicFitasSG.setIslamic("Y".equals(dbResult.getIsIslamic()));
                        acctDtlIslamicFitasSG.setFITAS("Y".equals(dbResult.getIsFitas()));
                        return new ApiResponse<>(new ErrorResponse(), acctDtlIslamicFitasSG);
                    } else {
                        LNRevolvingCreditDetailConventionalFITASSGDTO acctDtlConventionalFitasSG = modelMapper.map(dbResult, LNRevolvingCreditDetailConventionalFITASSGDTO.class);
                        acctDtlConventionalFitasSG.setIslamic("Y".equals(dbResult.getIsIslamic()));
                        acctDtlConventionalFitasSG.setFITAS("Y".equals(dbResult.getIsFitas()));
                        return new ApiResponse<>(new ErrorResponse(), acctDtlConventionalFitasSG);
                    }
                } else {
                    LNRevolvingCreditDetailSGDTO acctDtlSG = modelMapper.map(dbResult, LNRevolvingCreditDetailSGDTO.class);
                    acctDtlSG.setIslamic("Y".equals(dbResult.getIsIslamic()));
                    acctDtlSG.setFITAS("Y".equals(dbResult.getIsFitas()));
                    return new ApiResponse<>(new ErrorResponse(), acctDtlSG);
                }
            }
            default -> {
                return new ApiResponse<>(new ErrorResponse(), new LNRevolvingCreditDetailDTO());
            }
        }
    }

    public ApiResponse<SearchCollectionResponse<LNRevolvingCreditTrxHistoryDTO>> getTrxHistorySG(String auth, LNTrxHistoryReqDTO request){
        LNRCTrxHistorySpParam lnrcTrxHistorySpParam = getTrxHistorySpParam(request, jwtUtil.extractUserIdAndCorpId(auth));
        BaseSpDto<LNRCTrxHistorySpParam, LNRevolvingCreditTrxHistoryDTO> resultBaseSpDto = lnRevolvingCreditSPService.trxHistorySG(lnrcTrxHistorySpParam);
        
        ApiResponse<SearchCollectionResponse<LNRevolvingCreditTrxHistoryDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(new SearchCollectionResponse<>(
                resultBaseSpDto.getTotalPage(),
                lnrcTrxHistorySpParam.getPage(),
                lnrcTrxHistorySpParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : lnrcTrxHistorySpParam.getPage() + 1,
                resultBaseSpDto.getTotalRecord(),
                resultBaseSpDto.getResults()==null ? new ArrayList<>() : resultBaseSpDto.getResults()));
        return apiResponse;
    }

    private LNRCTrxHistorySpParam getTrxHistorySpParam(LNTrxHistoryReqDTO request, UserAndCorp userAndCorp) {
        LNRCTrxHistorySpParam lnrcTrxHistorySpParam = new LNRCTrxHistorySpParam(request.getAcctNumber(), request.getCurrency(), userAndCorp.getCorpId(), userAndCorp.getUserId());
        lnrcTrxHistorySpParam.setSearchParam(request.getSearchParam());
        lnrcTrxHistorySpParam.setCurrency(request.getCurrency());
        lnrcTrxHistorySpParam.setTrxCode(request.getFilters().getTrxCode());
        if (!request.getFilters().getAmtFrom().isEmpty() && !request.getFilters().getAmtTo().isEmpty()) {
            lnrcTrxHistorySpParam.setAmtFrom(new BigDecimal(request.getFilters().getAmtFrom()));
            lnrcTrxHistorySpParam.setAmtTo(new BigDecimal(request.getFilters().getAmtTo()));
        }
        if (StringUtils.hasText(request.getFilters().getStartDate()) && StringUtils.hasText(request.getFilters().getEndDate()) && StringUtils.hasText(request.getFilters().getStartTime()) && StringUtils.hasText(request.getFilters().getEndTime())) {
            lnrcTrxHistorySpParam.setStartDateTime(DateTimeUtil.convert12HourDateTime(request.getFilters().getStartDate(), request.getFilters().getStartTime(), true));
            lnrcTrxHistorySpParam.setEndDateTime(DateTimeUtil.convert12HourDateTime(request.getFilters().getEndDate(), request.getFilters().getEndTime(), false));
        }
        lnrcTrxHistorySpParam.setPage(request.getPage());
        lnrcTrxHistorySpParam.setPageSize(request.getPageSize());
        return lnrcTrxHistorySpParam;
    }

    public String lastUpdated(){
        OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);
        ZoneOffset gmtPlus8 = ZoneOffset.ofHours(8);
        OffsetDateTime adjustedTime = offsetDateTime.withOffsetSameInstant(gmtPlus8);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss '(GMT+8)'");
        return adjustedTime.format(formatter);
    }

    public void removeAllFromCache(UserAndCorp userAndCorp){
        String redisKey = redisHelper.createCacheKey(BD_AM_LN_RC_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_*");
        redisHelper.deleteCachesWithPattern(redisKey);
        log.info("removed cache for {}", redisKey);
    }
    private ApiResponse<LNRevolvingCreditAcctListingDTO> getAcctFromCache(UserAndCorp userAndCorp, LNAcctListReq reqDTO) {
        return (ApiResponse<LNRevolvingCreditAcctListingDTO>) redisHelper.getFromCache(getRedisKey(userAndCorp, reqDTO), ApiResponse.class);
    }
    private String getRedisKey(UserAndCorp userAndCorp, LNAcctListReq reqDTO) {
        return redisHelper.createCacheKey(BD_AM_LN_RC_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + "_" + reqDTO.getCorpCifNo() + "_" +reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
    }
}