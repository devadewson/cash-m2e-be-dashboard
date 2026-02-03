package com.m2e.dashboard.service.maintenance;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.maintenance.response.MTTradeSearchDTO;
import com.m2e.dashboard.dto.trade.TradeMaintAccountListReqDTO;
import com.m2e.dashboard.sp.maintenance.request.MTTradeAcctListSpParam;
import com.m2e.dashboard.sp.maintenance.response.MTTradeSearchSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.m2e.dashboard.enums.SPEnum.SP_ADM_MT_TD_ACCT_LIST;

@Service
@Slf4j
public class MTTradeService {

    private final BaseSpService baseSpService;

    @Autowired
    public MTTradeService(BaseSpService baseSpService) {
        this.baseSpService = baseSpService;
    }

    public ApiResponse<MTTradeSearchDTO> getTradeAccountList(UserAndCorp userAndCorp, TradeMaintAccountListReqDTO req) {
        log.info("request :: " + req);
        MTTradeAcctListSpParam spParam = new MTTradeAcctListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), req.getCorpCifNo(), req.getSearchParam(), StringUtils.hasText(req.getSubProduct()) ? req.getSubProduct() : null);
        spParam.setPageSize(req.getPageSize());
        spParam.setPage(req.getPage());

        BaseSpDto<MTTradeAcctListSpParam, MTTradeSearchSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_ADM_MT_TD_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(MTTradeSearchSpDTO.class);

        BaseSpDto<MTTradeAcctListSpParam, MTTradeSearchSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);

        List<MTTradeSearchSpDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<MTTradeSearchSpDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(spParam.getPage() >= totalPage ? totalPage : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);

        MTTradeSearchDTO mtSearchDTO = new MTTradeSearchDTO();
        mtSearchDTO.setCorpCifNo(req.getCorpCifNo());
        mtSearchDTO.setDatas(result);

        ApiResponse<MTTradeSearchDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(mtSearchDTO);

        return apiResponse;
    }
}
