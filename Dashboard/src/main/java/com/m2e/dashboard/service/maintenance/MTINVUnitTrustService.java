package com.m2e.dashboard.service.maintenance;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.maintenance.request.AcctNameListReqDTO;
import com.m2e.dashboard.dto.maintenance.response.MTINVUnitTrustSearchDTO;
import com.m2e.dashboard.sp.investment.request.INVUnitTrustSpParam;
import com.m2e.dashboard.sp.maintenance.response.MTINVUnitTrustSearchSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.m2e.dashboard.enums.SPEnum.SP_ADM_MT_IV_UT_ACCT_LIST;

@Service
@Slf4j
public class MTINVUnitTrustService {

    private final BaseSpService baseSpService;
    @Autowired
    public MTINVUnitTrustService(BaseSpService baseSpService) {
        this.baseSpService = baseSpService;
    }

    public ApiResponse<MTINVUnitTrustSearchDTO> getINVUTAcctLists(UserAndCorp userAndCorp, AcctNameListReqDTO unitTrustReqDto) {
        log.info("unitTrustReqDto :: "+ unitTrustReqDto);
        INVUnitTrustSpParam spParam = new INVUnitTrustSpParam();
        spParam.setUserId(userAndCorp.getUserId());
        spParam.setCorpId(userAndCorp.getCorpId());
        spParam.setCorpCifNo(unitTrustReqDto.getCorpCifNo());
        spParam.setSearchParam(unitTrustReqDto.getSearchParam());
        spParam.setPageSize(unitTrustReqDto.getPageSize());
        spParam.setPage(unitTrustReqDto.getPage());

        BaseSpDto<INVUnitTrustSpParam, MTINVUnitTrustSearchSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_ADM_MT_IV_UT_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(MTINVUnitTrustSearchSpDTO.class);

        BaseSpDto<INVUnitTrustSpParam, MTINVUnitTrustSearchSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);

        List<MTINVUnitTrustSearchSpDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<MTINVUnitTrustSearchSpDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(spParam.getPage() >= totalPage ? totalPage : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);

        MTINVUnitTrustSearchDTO invUnitTrustSearchDTO = new MTINVUnitTrustSearchDTO();
        invUnitTrustSearchDTO.setCorpCifNo(unitTrustReqDto.getCorpCifNo());
        invUnitTrustSearchDTO.setDatas(result);

        ApiResponse<MTINVUnitTrustSearchDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(invUnitTrustSearchDTO);

        return apiResponse;
    }

}