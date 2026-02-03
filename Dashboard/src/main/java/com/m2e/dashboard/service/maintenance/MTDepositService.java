package com.m2e.dashboard.service.maintenance;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.maintenance.request.AcctNameListReqDTO;
import com.m2e.dashboard.dto.maintenance.response.MTDepositSearchDTO;
import com.m2e.dashboard.sp.maintenance.request.MTDepositAcctListSpParam;
import com.m2e.dashboard.sp.maintenance.response.MTDepositSearchSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.m2e.dashboard.enums.SPEnum.SP_ADM_MT_DP_ACCT_LIST;

@Service
@Slf4j
public class MTDepositService {

    private final BaseSpService baseSpService;

    @Autowired
    public MTDepositService(BaseSpService baseSpService) {
        this.baseSpService = baseSpService;
    }

    public ApiResponse<MTDepositSearchDTO> getDepositAccountList(UserAndCorp userAndCorp, AcctNameListReqDTO req) {
        log.info("request :: " + req);
        MTDepositAcctListSpParam spParam = new MTDepositAcctListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId(), req.getCorpCifNo(), req.getSearchParam(), req.getSubProduct());
        spParam.setPageSize(req.getPageSize());
        spParam.setPage(req.getPage());

        BaseSpDto<MTDepositAcctListSpParam, MTDepositSearchSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_ADM_MT_DP_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(MTDepositSearchSpDTO.class);

        BaseSpDto<MTDepositAcctListSpParam, MTDepositSearchSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);

        List<MTDepositSearchSpDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<MTDepositSearchSpDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(spParam.getPage() >= totalPage ? totalPage : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);

        MTDepositSearchDTO mtSearchDTO = new MTDepositSearchDTO();
        mtSearchDTO.setCorpCifNo(req.getCorpCifNo());
        mtSearchDTO.setDatas(result);

        ApiResponse<MTDepositSearchDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(mtSearchDTO);

        return apiResponse;
    }
}
