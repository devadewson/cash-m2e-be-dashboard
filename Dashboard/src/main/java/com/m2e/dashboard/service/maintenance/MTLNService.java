package com.m2e.dashboard.service.maintenance;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.maintenance.request.AcctNameListReqDTO;
import com.m2e.dashboard.dto.maintenance.response.MTLNSearchDTO;
import com.m2e.dashboard.sp.maintenance.request.MTLNAcctListSpParam;
import com.m2e.dashboard.sp.maintenance.response.MTLNSearchSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.m2e.dashboard.enums.SPEnum.SP_ADM_MT_LN_ACCT_LIST;

@Service
@Slf4j
public class MTLNService {
    private final BaseSpService baseSpService;
    @Autowired
    public MTLNService(BaseSpService baseSpService) {
        this.baseSpService = baseSpService;
    }

    public ApiResponse<MTLNSearchDTO> getLoanTLFAcctList(UserAndCorp userAndCorp, AcctNameListReqDTO termLoanReqDto) {
        log.info("termLoanReqDto: {}", termLoanReqDto);
        MTLNAcctListSpParam spParam = MTLNAcctListSpParam.builder()
                .userId(userAndCorp.getUserId())
                .corpId(userAndCorp.getCorpId())
                .corpCifNo(termLoanReqDto.getCorpCifNo())
                .searchParam(termLoanReqDto.getSearchParam())
                .subProduct(termLoanReqDto.getSubProduct())
                .page(termLoanReqDto.getPage())
                .pageSize(termLoanReqDto.getPageSize())
                .build();

        BaseSpDto<MTLNAcctListSpParam, MTLNSearchSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_ADM_MT_LN_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(MTLNSearchSpDTO.class);

        BaseSpDto<MTLNAcctListSpParam, MTLNSearchSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);

        List<MTLNSearchSpDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<MTLNSearchSpDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(spParam.getPage() >= totalPage ? totalPage : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);

        MTLNSearchDTO loanTlfSearchDTO = new MTLNSearchDTO();
        loanTlfSearchDTO.setCorpCifNo(termLoanReqDto.getCorpCifNo());
        loanTlfSearchDTO.setDatas(result);

        ApiResponse<MTLNSearchDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(loanTlfSearchDTO);

        return apiResponse;
    }

}