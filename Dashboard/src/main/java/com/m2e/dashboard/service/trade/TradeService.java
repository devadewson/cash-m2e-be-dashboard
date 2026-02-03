package com.m2e.dashboard.service.trade;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.*;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.trade.request.TradeAcctListingReqDTO;
import com.m2e.dashboard.dto.trade.request.TradeAcctStatsReqDTO;
import com.m2e.dashboard.dto.trade.request.download.TradeAccountListingDownloadReqDTO;
import com.m2e.dashboard.dto.trade.response.TradeAcctStatsDTO;
import com.m2e.dashboard.dto.trade.response.TradeAcctListingDTO;
import com.m2e.dashboard.enums.DownloadEnum;
import com.m2e.dashboard.enums.TradeEnum;
import com.m2e.dashboard.sp.trade.response.TradeAcctListingSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_TD_ACCTLIST;
import static com.m2e.dashboard.enums.SPEnum.*;

@Slf4j
@Service
public class TradeService {

    @Autowired
    private BaseSpService baseSpService;

    @Autowired
    private TradeFileService fileService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RedisHelper redisHelper;

    public ApiResponse<TradeAcctStatsDTO> getTradeAccountStatsSp(UserAndCorp userAndCorp, TradeEnum tradeEnum) {
        removeAllFromCache(userAndCorp);

        TradeAcctStatsReqDTO tradeAcctStatsReqDTO = new TradeAcctStatsReqDTO();
        tradeAcctStatsReqDTO.setCorpId(userAndCorp.getCorpId());
        tradeAcctStatsReqDTO.setUserId(userAndCorp.getUserId());
        tradeAcctStatsReqDTO.setTradeCategory(tradeEnum.getCategory());
        tradeAcctStatsReqDTO.setAcctType(tradeEnum.getAcctType());

        BaseSpDto<TradeAcctStatsReqDTO, TradeAcctStatsDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_TD_ACCT_STATS.getValue());
        baseSpDto.setParameter(tradeAcctStatsReqDTO);
        baseSpDto.setResultClass(TradeAcctStatsDTO.class);
        List<TradeAcctStatsDTO> results = Optional.ofNullable(baseSpService.execute(baseSpDto))
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        TradeAcctStatsDTO tradeAcctStatsDTO = results.stream().findFirst().orElse(null);

        ApiResponse<TradeAcctStatsDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgBody(tradeAcctStatsDTO);
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        return apiResponse;
    }

    public ApiResponse<TradeAcctListingDTO> getTradeAccountListingSp(UserAndCorp userAndCorp, AccountListReq req, TradeEnum tradeEnum) {
        ApiResponse<TradeAcctListingDTO> apiResponse = new ApiResponse<>();
        ApiResponse<TradeAcctListingDTO> finalResp = getAcctFromCache(userAndCorp, req);

        if (finalResp == null) {
            TradeAcctListingReqDTO tradeAcctListingReqDTO = new TradeAcctListingReqDTO();
            tradeAcctListingReqDTO.setUserId(userAndCorp.getUserId());
            //use open tag corpId if exists, else use corpId from token
            String corpId = StringUtils.hasText(req.getOpenTaggedCorpId()) ? req.getOpenTaggedCorpId() : userAndCorp.getCorpId();
            tradeAcctListingReqDTO.setCorpId(corpId);
            tradeAcctListingReqDTO.setCorpCifNo(req.getCorpCifNo());
            tradeAcctListingReqDTO.setSearchKey(StringUtils.hasText(req.getSearchParam()) ? req.getSearchParam() : null);
            tradeAcctListingReqDTO.setTradeCategory(tradeEnum.getCategory());
            tradeAcctListingReqDTO.setAcctType(tradeEnum.getAcctType());
            tradeAcctListingReqDTO.setPageSize(req.getPageSize());
            tradeAcctListingReqDTO.setPage(req.getPage());

            BaseSpDto<TradeAcctListingReqDTO, TradeAcctListingSpDTO> baseSpDto = new BaseSpDto<>();
            baseSpDto.setSpId(SP_TD_ACCT_LIST.getValue());
            baseSpDto.setParameter(tradeAcctListingReqDTO);
            baseSpDto.setPageSize(tradeAcctListingReqDTO.getPageSize());
            baseSpDto.setPageNumber(tradeAcctListingReqDTO.getPage());
            baseSpDto.setResultClass(TradeAcctListingSpDTO.class);
            BaseSpDto<TradeAcctListingReqDTO, TradeAcctListingSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
            List<TradeAcctListingSpDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                    .map(BaseSpDto::getResults)
                    .orElse(Collections.emptyList());
            SearchCollectionResponse<TradeAcctListingSpDTO> result = new SearchCollectionResponse<>();

            int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
            int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

            result.setNextPage(tradeAcctListingReqDTO.getPage() >= totalPage ? totalPage : tradeAcctListingReqDTO.getPage() + 1);
            result.setCurrentPage(tradeAcctListingReqDTO.getPage());
            result.setTotalPages(totalPage);
            result.setTotalRecordCount(totalRecordCount);
            result.setData(spResultList);

            TradeAcctListingDTO tradeAcctListingDTO = new TradeAcctListingDTO();
            tradeAcctListingDTO.setCorpCifNo(req.getCorpCifNo());
            tradeAcctListingDTO.setDatas(result);

            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(tradeAcctListingDTO);

            finalResp = apiResponse;
            redisHelper.save(getRedisKey(userAndCorp, req), finalResp);
        }

        return finalResp;
    }

    public DownloadDTO downloadTrade(UserAndCorp userAndCorp, TradeAccountListingDownloadReqDTO req, TradeEnum tradeEnum){
        DownloadDTO result = new DownloadDTO();
        TradeAcctListingReqDTO tradeAcctSummaryReq = new TradeAcctListingReqDTO();
        tradeAcctSummaryReq.setUserId(userAndCorp.getUserId());
        tradeAcctSummaryReq.setCorpId(userAndCorp.getCorpId());
        tradeAcctSummaryReq.setCorpCifNo(StringUtils.hasText(req.getCorpCifNo()) ? req.getCorpCifNo() : null);
        tradeAcctSummaryReq.setTradeCategory(tradeEnum.getCategory());
        tradeAcctSummaryReq.setAcctType(tradeEnum.getAcctType());
        tradeAcctSummaryReq.setPageSize(1);
        tradeAcctSummaryReq.setPage(Integer.MAX_VALUE);

        BaseSpDto<TradeAcctListingReqDTO, TradeAcctListingSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_TD_ACCT_LIST.getValue());
        baseSpDto.setParameter(tradeAcctSummaryReq);
        baseSpDto.setPageNumber(1);
        baseSpDto.setPageSize(Integer.MAX_VALUE);
        baseSpDto.setResultClass(TradeAcctListingSpDTO.class);
        BaseSpDto<TradeAcctListingReqDTO, TradeAcctListingSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<TradeAcctListingSpDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        switch (req.getFormat()) {
            case CSV:
                result.setData(fileService.generateCsv(spResultList));
                result.setFileName(DownloadEnum.TRADE_IMPORT_ACCLISTING_FILENAME.getDescription() + tradeEnum.getAcctType() + "_" + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                result.setData(fileService.generatePdf(spResultList));
                result.setFileName(DownloadEnum.TRADE_IMPORT_ACCLISTING_FILENAME.getDescription() + tradeEnum.getAcctType() + "_" + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                result.setData(fileService.generateTxt(spResultList));
                result.setFileName(DownloadEnum.TRADE_IMPORT_ACCLISTING_FILENAME.getDescription() + tradeEnum.getAcctType() + "_" + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    public void removeAllFromCache(UserAndCorp userAndCorp){
        String redisKey = redisHelper.createCacheKey(BD_AM_TD_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_*");
        redisHelper.deleteCachesWithPattern(redisKey);
        log.info("removed cache for {}", redisKey);
    }
    private ApiResponse<TradeAcctListingDTO> getAcctFromCache(UserAndCorp userAndCorp, AccountListReq reqDTO) {
        return (ApiResponse<TradeAcctListingDTO>) redisHelper.getFromCache(getRedisKey(userAndCorp, reqDTO), ApiResponse.class);
    }

    private String getRedisKey(UserAndCorp userAndCorp, AccountListReq reqDTO) {
        String redisKey = redisHelper.createCacheKey(BD_AM_TD_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + "_" + reqDTO.getCorpCifNo() + "_" + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
        return redisKey;
    }
}
