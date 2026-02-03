package com.m2e.dashboard.service.deposit.fd.sp;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.deposit.fd.request.DepositFDTrxHistoryReqDTO;
import com.m2e.dashboard.dto.deposit.fd.response.*;
import com.m2e.dashboard.sp.deposit.fd.request.*;
import com.m2e.dashboard.sp.deposit.fd.response.DepositFDAcctSummarySpDTO;
import com.m2e.dashboard.util.DateTimeUtil;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.m2e.dashboard.enums.SPEnum.*;

@Service
public class DepositFDSPService {
    private static final Logger log = LoggerFactory.getLogger(DepositFDSPService.class);

    @Autowired
    private BaseSpService baseSpService;
    @Autowired
    private ModelMapper modelMapper;

    public DepositFDAcctStatsDTO getDepositFDAccountStatsSP(DepositFDAcctStatsSpParam request) {
        BaseSpDto<DepositFDAcctStatsSpParam, DepositFDAcctStatsDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_DP_FD_ACCT_STATS.getValue());
        baseSpDto.setParameter(request);
        baseSpDto.setResultClass(DepositFDAcctStatsDTO.class);

        DepositFDAcctStatsDTO depositFDAcctStatsDTO = baseSpService.execute(baseSpDto).getResults().get(0);
        return depositFDAcctStatsDTO;
    }

    public DepositFDAcctSummaryDTO getDepositFDAccountListingSP(UserAndCorp userAndCorp, AccountListReq request) {
        DepositFDAcctListSpParam depositFDAcctListSpParam = new DepositFDAcctListSpParam();
        depositFDAcctListSpParam.setUserId(userAndCorp.getUserId());
        depositFDAcctListSpParam.setCorpId(userAndCorp.getCorpId());
        depositFDAcctListSpParam.setSearchParam(StringUtils.hasText(request.getSearchParam()) ? request.getSearchParam() : null);
        depositFDAcctListSpParam.setCorpCifNo(StringUtils.hasText(request.getCorpCifNo()) ? request.getCorpCifNo() : null);

        BaseSpDto<DepositFDAcctListSpParam, DepositFDAcctSummarySpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_DP_FD_ACCT_LIST.getValue());
        baseSpDto.setParameter(depositFDAcctListSpParam);
        baseSpDto.setPageSize(request.getPageSize());
        baseSpDto.setPageNumber(request.getPage());
        baseSpDto.setResultClass(DepositFDAcctSummarySpDTO.class);

        BaseSpDto<DepositFDAcctListSpParam, DepositFDAcctSummarySpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<DepositFDAcctSummarySpDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<DepositFDAcctSummarySpDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(request.getPage() >= totalPage ? totalPage : request.getPage() + 1);
        result.setCurrentPage(request.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);

        DepositFDAcctSummaryDTO depositFDAcctSummaryDTO = new DepositFDAcctSummaryDTO();
        depositFDAcctSummaryDTO.setCorpCifNo(request.getCorpCifNo());
        depositFDAcctSummaryDTO.setDatas(result);

        return depositFDAcctSummaryDTO;
    }

    public SearchCollectionResponse<DepositFDTrxHistoryDTO> getDepositFDTrxHistorySP(UserAndCorp userAndCorp, DepositFDTrxHistoryReqDTO request) {
        DepositFDTrxHistoryListSpParam depositFDTrxHistoryListSpParam = new DepositFDTrxHistoryListSpParam();
        depositFDTrxHistoryListSpParam.setAcctNumber(request.getAcctNumber());
        depositFDTrxHistoryListSpParam.setSearchParam(StringUtils.hasText(request.getSearchParam()) ? request.getSearchParam() : null);
        depositFDTrxHistoryListSpParam.setUserId(userAndCorp.getUserId());
        depositFDTrxHistoryListSpParam.setCorpId(userAndCorp.getCorpId());
        depositFDTrxHistoryListSpParam.setPageSize(request.getPageSize());
        depositFDTrxHistoryListSpParam.setPage(request.getPage());
        if (request.getFilters() != null) {
            depositFDTrxHistoryListSpParam.setStartDateTime(StringUtils.hasText(request.getFilters().getStartDate()) ? DateTimeUtil.convert12HourDateTime(request.getFilters().getStartDate(), request.getFilters().getStartTime(), true) : null);
            depositFDTrxHistoryListSpParam.setEndDateTime(StringUtils.hasText(request.getFilters().getEndDate()) ? DateTimeUtil.convert12HourDateTime(request.getFilters().getEndDate(), request.getFilters().getEndTime(), false) : null);
            depositFDTrxHistoryListSpParam.setAmtFrom(StringUtils.hasText(request.getFilters().getAmtFrom()) ? Double.valueOf(request.getFilters().getAmtFrom()) : null);
            depositFDTrxHistoryListSpParam.setAmtTo(StringUtils.hasText(request.getFilters().getAmtTo()) ? Double.valueOf(request.getFilters().getAmtTo()) : null);
        }

        BaseSpDto<DepositFDTrxHistoryListSpParam, DepositFDTrxHistoryDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_DP_FD_TRXN_HIST.getValue());
        baseSpDto.setParameter(depositFDTrxHistoryListSpParam);
        baseSpDto.setPageSize(depositFDTrxHistoryListSpParam.getPageSize());
        baseSpDto.setPageNumber(depositFDTrxHistoryListSpParam.getPage());
        baseSpDto.setResultClass(DepositFDTrxHistoryDTO.class);
        BaseSpDto<DepositFDTrxHistoryListSpParam, DepositFDTrxHistoryDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<DepositFDTrxHistoryDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());

        SearchCollectionResponse<DepositFDTrxHistoryDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(depositFDTrxHistoryListSpParam.getPage() >= totalPage ? totalPage : depositFDTrxHistoryListSpParam.getPage() + 1);
        result.setCurrentPage(depositFDTrxHistoryListSpParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);
        return result;
    }
}
