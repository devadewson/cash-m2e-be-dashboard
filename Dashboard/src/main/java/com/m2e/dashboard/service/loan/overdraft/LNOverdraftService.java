package com.m2e.dashboard.service.loan.overdraft;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.base.loan.LNReq;
import com.m2e.dashboard.dto.base.loan.LNAcctListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.loan.overdraft.request.LNOverdraftAcctListingDownloadReqDTO;
import com.m2e.dashboard.dto.loan.overdraft.request.LNOverdraftDetailReqDTO;
import com.m2e.dashboard.dto.loan.overdraft.request.LNOverdraftOmnibusDetailReqDTO;
import com.m2e.dashboard.dto.loan.overdraft.response.*;
import com.m2e.dashboard.enums.DownloadEnum;
import com.m2e.dashboard.sp.loan.od.request.LNODAcctDetailSpParam;
import com.m2e.dashboard.sp.loan.od.request.LNODAcctListingSpParam;
import com.m2e.dashboard.sp.loan.od.request.LNODAcctStatsSpParam;
import com.m2e.dashboard.util.DateTimeUtil;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_LN_OD_ACCTLIST;
import static com.m2e.dashboard.enums.ReportType.LOAN_OD_ACCOUNTLISTING_RPT_TYPE;

@Service
@Slf4j
public class LNOverdraftService {
    @Autowired private JwtUtil jwtUtil;
    @Autowired private BaseSpService baseSpService;
    @Autowired private LNOverdraftSPService lnOverdraftSPService;
    @Autowired private LNOverdraftFileService fileService;

    @Autowired
    private RedisHelper redisHelper;

    public ApiResponse<LNOverdraftAccountStatisticDTO> getLoanOverdraftAccountStatistic(String auth, LNReq request){
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        removeAllFromCache(userAndCorp);
        LNODAcctStatsSpParam lnodAcctStatsSpParam = new LNODAcctStatsSpParam();
        lnodAcctStatsSpParam.setCorpId(userAndCorp.getCorpId());
        lnodAcctStatsSpParam.setUserId(userAndCorp.getUserId());
        LNOverdraftAccountStatisticDTO lnOverdraftAccountStatisticDTO = lnOverdraftSPService.accountStatisticsOD(lnodAcctStatsSpParam);

        if (request.isRealTimeIndicator())
            lnOverdraftAccountStatisticDTO.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());

        ApiResponse<LNOverdraftAccountStatisticDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(lnOverdraftAccountStatisticDTO);

        return apiResponse;
    }

    public ApiResponse<LNOverdraftAcctListingDTO> getLoanOverdraftAccountListing(String auth, LNAcctListReq request){
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        ApiResponse<LNOverdraftAcctListingDTO> apiResponse = new ApiResponse<>();
        //get acct balances from cache
        ApiResponse<LNOverdraftAcctListingDTO> finalResp = getAcctFromCache(userAndCorp, request);

        if (finalResp == null) {
            LNODAcctListingSpParam lnodAcctListingSpParam = new LNODAcctListingSpParam();
            lnodAcctListingSpParam.setCorpId(userAndCorp.getCorpId());
            lnodAcctListingSpParam.setUserId(userAndCorp.getUserId());
            lnodAcctListingSpParam.setCorpCifNo(StringUtils.hasText(request.getCorpCifNo()) ? request.getCorpCifNo() : null);
            lnodAcctListingSpParam.setSearchParam(StringUtils.hasText(request.getSearchParam()) ? request.getSearchParam() : null);
            lnodAcctListingSpParam.setPage(request.getPage());
            lnodAcctListingSpParam.setPageSize(request.getPageSize());

            BaseSpDto<LNODAcctListingSpParam, LNOverdraftDTO> resultBaseSpDto = lnOverdraftSPService.accountListingOD(lnodAcctListingSpParam);
            List<LNOverdraftDTO> spResultList = resultBaseSpDto.getResults().stream().toList();

            SearchCollectionResponse<LNOverdraftDTO> result = new SearchCollectionResponse<>();
            result.setNextPage(
                    lnodAcctListingSpParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : lnodAcctListingSpParam.getPage() + 1
            );
            result.setCurrentPage(lnodAcctListingSpParam.getPage());
            result.setTotalPages(resultBaseSpDto.getTotalPage());
            result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
            result.setData(spResultList);

            LNOverdraftAcctListingDTO lnodAcctListingDTO = new LNOverdraftAcctListingDTO();
            lnodAcctListingDTO.setCorpCifNo(request.getCorpCifNo());
            lnodAcctListingDTO.setDatas(result);
            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(lnodAcctListingDTO);

            finalResp = apiResponse;
            redisHelper.save(getRedisKey(userAndCorp, request), finalResp);
        }

        return finalResp;
    }

    public ApiResponse<LNOverdraftDetailDTO> getLoanOverdraftDetail(String auth, LNOverdraftDetailReqDTO request){
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        LNODAcctDetailSpParam lnodAcctDetailSpParam = new LNODAcctDetailSpParam();
        lnodAcctDetailSpParam.setCorpId(userAndCorp.getCorpId());
        lnodAcctDetailSpParam.setUserId(userAndCorp.getUserId());
        lnodAcctDetailSpParam.setAcctNumber(request.getAcctNumber());
        lnodAcctDetailSpParam.setAcctCurrency(request.getCurrency());

        boolean isOmnibus = false; // TODO: Differentiate OD type // NOSONAR
        if (!isOmnibus) { // NOSONAR
            LNOverdraftDetailDTO lnOverdraftDetailDTO = lnOverdraftSPService.accountDetailIntradayOD(lnodAcctDetailSpParam);
            return new ApiResponse(new ErrorResponse("0"), lnOverdraftDetailDTO);
        } else {
            // TODO: Handle omnibus OD type
        }

        return new ApiResponse(new ErrorResponse("0"), null);
    }
    
    public DownloadDTO downloadLoanOverdraft(String auth, DownloadReq downloadRequest){
        LNOverdraftAcctListingDownloadReqDTO request = (LNOverdraftAcctListingDownloadReqDTO) downloadRequest;
        DownloadDTO result = new DownloadDTO();
        if (request.getReportType()==LOAN_OD_ACCOUNTLISTING_RPT_TYPE){
            UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
            LNODAcctListingSpParam lnodAcctListingSpParam = new LNODAcctListingSpParam();
            lnodAcctListingSpParam.setUserId(userAndCorp.getUserId());
            lnodAcctListingSpParam.setCorpId(userAndCorp.getCorpId());
            lnodAcctListingSpParam.setCorpCifNo(StringUtils.hasText(request.getCorpCifNo()) ? request.getCorpCifNo() : null);
            lnodAcctListingSpParam.setSearchParam(StringUtils.hasText(request.getSearchParam()) ? request.getSearchParam() : null);
            lnodAcctListingSpParam.setPage(1);
            lnodAcctListingSpParam.setPageSize(Integer.MAX_VALUE);
            BaseSpDto<LNODAcctListingSpParam, LNOverdraftDTO> resultBaseSpDto = lnOverdraftSPService.accountListingOD(lnodAcctListingSpParam);
            List<LNOverdraftDTO> data = resultBaseSpDto.getResults().stream().toList();
            switch (request.getFormat()) {
                case CSV:
                    result.setData(fileService.generateCsv(data));
                    result.setFileName(DownloadEnum.LOAN_OD_ACCLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
                    break;
                case PDF:
                    result.setData(fileService.generatePdf(data));
                    result.setFileName(DownloadEnum.LOAN_OD_ACCLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
                    break;
                case TXT:
                    result.setData(fileService.generateTxt(data));
                    result.setFileName(DownloadEnum.LOAN_OD_ACCLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
                    break;
                default:
                    throw new IllegalArgumentException("Report format is undefined");
            }
        }
        return result;
    }

    public ApiResponse<SearchCollectionResponse<LNOverdraftOmnibusItemDTO>> getLoanOverdraftOmnibusAccounts(String auth, LNOverdraftOmnibusDetailReqDTO request){
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        List<LNOverdraftOmnibusItemDTO> list = lnOverdraftSPService.detailLoanOverdraftOmnibusAccounts(pageable);
        Page<LNOverdraftOmnibusItemDTO> page = new PageImpl<>(list, pageable, list.size());
        return new ApiResponse(new ErrorResponse(), new SearchCollectionResponse<>(request.getPage(), page));
    }

    public void removeAllFromCache(UserAndCorp userAndCorp){
        String redisKey = redisHelper.createCacheKey(BD_AM_LN_OD_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_*");
        redisHelper.deleteCachesWithPattern(redisKey);
        log.info("removed cache for {}", redisKey);
    }
    private ApiResponse<LNOverdraftAcctListingDTO> getAcctFromCache(UserAndCorp userAndCorp, LNAcctListReq reqDTO) {
        return (ApiResponse<LNOverdraftAcctListingDTO>) redisHelper.getFromCache(getRedisKey(userAndCorp, reqDTO), ApiResponse.class);
    }

    private String getRedisKey(UserAndCorp userAndCorp, LNAcctListReq reqDTO) {
        String redisKey = redisHelper.createCacheKey(BD_AM_LN_OD_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
        return redisKey;
    }
}
