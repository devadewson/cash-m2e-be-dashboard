package com.m2e.dashboard.service.maintenance;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.maintenance.request.AcctNameListReqDTO;
import com.m2e.dashboard.dto.maintenance.response.MTAFSearchDTO;
import com.m2e.dashboard.sp.maintenance.request.MTAFAcctListSpParam;
import com.m2e.dashboard.sp.maintenance.response.MTAFSearchSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.m2e.dashboard.enums.SPEnum.SP_ADM_MT_AF_ACCT_LIST;

@Service
@Slf4j
public class MTAutoFinanceService {

    private final BaseSpService baseSpService;

    @Autowired
    public MTAutoFinanceService(BaseSpService baseSpService) {
        this.baseSpService = baseSpService;
    }

    public ApiResponse<MTAFSearchDTO> getAutoFinanceList(UserAndCorp userAndCorp, AcctNameListReqDTO req) {
        log.info("getAutoFinanceList request :: " + req);
        MTAFAcctListSpParam spParam = new MTAFAcctListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), req.getCorpCifNo(), req.getSearchParam(), req.getSubProduct());
        spParam.setPageSize(req.getPageSize());
        spParam.setPage(req.getPage());

        log.info("getAutoFinanceList param :: " + spParam);

        BaseSpDto<MTAFAcctListSpParam, MTAFSearchSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_ADM_MT_AF_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(MTAFSearchSpDTO.class);

        BaseSpDto<MTAFAcctListSpParam, MTAFSearchSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);

        List<MTAFSearchSpDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<MTAFSearchSpDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(spParam.getPage() >= totalPage ? totalPage : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);

        MTAFSearchDTO mtSearchDTO = new MTAFSearchDTO();
        mtSearchDTO.setCorpCifNo(req.getCorpCifNo());
        mtSearchDTO.setDatas(result);

        ApiResponse<MTAFSearchDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(mtSearchDTO);

        return apiResponse;
    }

}