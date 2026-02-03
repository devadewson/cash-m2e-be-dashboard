package com.m2e.dashboard.service.maintenance;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.maintenance.request.MTOBAccountListReqDTO;
import com.m2e.dashboard.dto.maintenance.response.MTOBSearchDTO;
import com.m2e.dashboard.sp.maintenance.request.MTOBAcctListSpParam;
import com.m2e.dashboard.sp.maintenance.response.MTOBSearchSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.m2e.dashboard.enums.SPEnum.SP_ADM_MT_OB_ACCT_LIST;


@Service
@Slf4j
public class MTOtherBankService {

    private final BaseSpService baseSpService;

    @Autowired
    public MTOtherBankService(BaseSpService baseSpService) {
        this.baseSpService = baseSpService;
    }

    public ApiResponse<MTOBSearchDTO> getOtherBankList(UserAndCorp userAndCorp, MTOBAccountListReqDTO req) {
        log.info("getOtherBankList request :: " + req);
        MTOBAcctListSpParam spParam = new MTOBAcctListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), req.getSearchParam(), req.getSubProduct());
        spParam.setPageSize(req.getPageSize());
        spParam.setPage(req.getPage());

        log.info("getOtherBankList param :: " + spParam);

        BaseSpDto<MTOBAcctListSpParam, MTOBSearchSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_ADM_MT_OB_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(MTOBSearchSpDTO.class);

        BaseSpDto<MTOBAcctListSpParam, MTOBSearchSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);

        List<MTOBSearchSpDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<MTOBSearchSpDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(spParam.getPage() >= totalPage ? totalPage : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);

        MTOBSearchDTO mtSearchDTO = new MTOBSearchDTO();
        mtSearchDTO.setDatas(result);

        ApiResponse<MTOBSearchDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(mtSearchDTO);

        return apiResponse;
    }

}