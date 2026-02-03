package com.m2e.dashboard.service.deposit.ca.sp;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.deposit.ca.request.*;
import com.m2e.dashboard.dto.deposit.ca.response.*;
import com.m2e.dashboard.sp.deposit.*;
import com.m2e.dashboard.sp.deposit.ca.request.*;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import com.m2e.dashboard.util.DateTimeUtil;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
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
public class DepositCASPService {
    private static final Logger log = LoggerFactory.getLogger(DepositCASPService.class);

    @Autowired
    BaseSpService baseSpService;

    public DepositAcctStatsDTO getDepositCAAccountStatsSp(UserAndCorp userAndCorp) {
        DepositCAStatsSpParam depositCAStatsSpParam = new DepositCAStatsSpParam();
        depositCAStatsSpParam.setCorpId(userAndCorp.getCorpId());
        depositCAStatsSpParam.setUserId(userAndCorp.getUserId());

        BaseSpDto<DepositCAStatsSpParam, DepositAcctStatsDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_DP_CA_ACCT_STATS.getValue());
        baseSpDto.setParameter(depositCAStatsSpParam);
        baseSpDto.setResultClass(DepositAcctStatsDTO.class);
        List<DepositAcctStatsDTO> results = Optional.ofNullable(baseSpService.execute(baseSpDto))
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        DepositAcctStatsDTO depositAcctStatsDTO = results.stream().findFirst().orElse(null);
        return depositAcctStatsDTO;
    }

    public List<DepositCAAcctListSpDTO> getAllDepositCAAccountListSp(UserAndCorp userAndCorp) {
        DepositCAAccountListSpParam depositCAAccountListSpParam = new DepositCAAccountListSpParam();
        depositCAAccountListSpParam.setUserId(userAndCorp.getUserId());
        depositCAAccountListSpParam.setCorpId(userAndCorp.getCorpId());
        depositCAAccountListSpParam.setPageSize(Integer.MAX_VALUE);
        depositCAAccountListSpParam.setPage(1);

        BaseSpDto<DepositCAAccountListSpParam, DepositCAAcctListSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_DP_CA_ACCT_LIST.getValue());
        baseSpDto.setParameter(depositCAAccountListSpParam);
        baseSpDto.setPageSize(depositCAAccountListSpParam.getPageSize());
        baseSpDto.setPageNumber(depositCAAccountListSpParam.getPage());
        baseSpDto.setResultClass(DepositCAAcctListSpDTO.class);
        BaseSpDto<DepositCAAccountListSpParam, DepositCAAcctListSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<DepositCAAcctListSpDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        return spResultList;
    }

    public DepositAcctListingDTO getDepositCAAccountListSp(UserAndCorp userAndCorp, AccountListReq req) {
        DepositCAAccountListSpParam depositCAAccountListSpParam = new DepositCAAccountListSpParam();
        depositCAAccountListSpParam.setUserId(userAndCorp.getUserId());
        depositCAAccountListSpParam.setCorpId(userAndCorp.getCorpId());
        depositCAAccountListSpParam.setSearchParam(StringUtils.hasText(req.getSearchParam()) ? req.getSearchParam() : null);
        depositCAAccountListSpParam.setCorpCifNo(StringUtils.hasText(req.getCorpCifNo()) ? req.getCorpCifNo() : null);
        depositCAAccountListSpParam.setPageSize(req.getPageSize());
        depositCAAccountListSpParam.setPage(req.getPage());

        BaseSpDto<DepositCAAccountListSpParam, DepositAcctSummarySpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_DP_CA_ACCT_LIST.getValue());
        baseSpDto.setParameter(depositCAAccountListSpParam);
        baseSpDto.setPageSize(depositCAAccountListSpParam.getPageSize());
        baseSpDto.setPageNumber(depositCAAccountListSpParam.getPage());
        baseSpDto.setResultClass(DepositAcctSummarySpDTO.class);
        BaseSpDto<DepositCAAccountListSpParam, DepositAcctSummarySpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<DepositAcctSummarySpDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<DepositAcctSummarySpDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(depositCAAccountListSpParam.getPage() >= totalPage ? totalPage : depositCAAccountListSpParam.getPage() + 1);
        result.setCurrentPage(depositCAAccountListSpParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);

        DepositAcctListingDTO depositAcctSummaryResp = new DepositAcctListingDTO();
        depositAcctSummaryResp.setCorpCifNo(req.getCorpCifNo());
        depositAcctSummaryResp.setDatas(result);

        return depositAcctSummaryResp;
    }

    public SearchCollectionResponse<DepositTrxHistoryDTO> getDepositCATrxHistorySp(UserAndCorp userAndCorp, DepositTrxHistoryReqDTO request) {
        DepositCATrxHistoryListSpParam depositTrxHistoryListSpParam = new DepositCATrxHistoryListSpParam();
        depositTrxHistoryListSpParam.setAcctNumber(request.getAcctNumber());
        depositTrxHistoryListSpParam.setAcctCurrency(request.getCurrency());
        depositTrxHistoryListSpParam.setTrxCode(StringUtils.hasText(request.getTrxCode()) ? request.getTrxCode() : null);
        depositTrxHistoryListSpParam.setSearchParam(StringUtils.hasText(request.getSearchParam()) ? request.getSearchParam() : null);
        depositTrxHistoryListSpParam.setUserId(userAndCorp.getUserId());
        depositTrxHistoryListSpParam.setCorpId(userAndCorp.getCorpId());
        depositTrxHistoryListSpParam.setPage(1);
        depositTrxHistoryListSpParam.setPageSize(Integer.MAX_VALUE);
        if (request.getFilters() != null) {
            depositTrxHistoryListSpParam.setStartDateTime(StringUtils.hasText(request.getFilters().getStartDate()) ? DateTimeUtil.convert12HourDateTime(request.getFilters().getStartDate(), request.getFilters().getStartTime(), true) : null);
            depositTrxHistoryListSpParam.setEndDateTime(StringUtils.hasText(request.getFilters().getEndDate()) ? DateTimeUtil.convert12HourDateTime(request.getFilters().getEndDate(), request.getFilters().getEndTime(), false) : null);
            depositTrxHistoryListSpParam.setAmtFrom(StringUtils.hasText(request.getFilters().getAmtFrom()) ? Double.valueOf(request.getFilters().getAmtFrom()) : null);
            depositTrxHistoryListSpParam.setAmtTo(StringUtils.hasText(request.getFilters().getAmtTo()) ? Double.valueOf(request.getFilters().getAmtTo()) : null);
        }

        BaseSpDto<DepositCATrxHistoryListSpParam, DepositTrxHistoryDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_DP_CA_TRXN_HIST.getValue());
        baseSpDto.setParameter(depositTrxHistoryListSpParam);
        baseSpDto.setPageSize(depositTrxHistoryListSpParam.getPageSize());
        baseSpDto.setPageNumber(depositTrxHistoryListSpParam.getPage());
        baseSpDto.setResultClass(DepositTrxHistoryDTO.class);
        BaseSpDto<DepositCATrxHistoryListSpParam, DepositTrxHistoryDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<DepositTrxHistoryDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());

        SearchCollectionResponse<DepositTrxHistoryDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(depositTrxHistoryListSpParam.getPage() >= totalPage ? totalPage : depositTrxHistoryListSpParam.getPage() + 1);
        result.setCurrentPage(depositTrxHistoryListSpParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);
        return result;
    }

    public SearchCollectionResponse<DepositPaymentHistoryDTO> getDepositCAPaymentHistoryListSp(UserAndCorp userAndCorp, DepositPaymentHistoryReqDTO request) {
        DepositCAPaymentHistorySpParam depositCAPaymentHistorySpParam = new DepositCAPaymentHistorySpParam();
        depositCAPaymentHistorySpParam.setAcctNumber(request.getAcctNumber());
        depositCAPaymentHistorySpParam.setAcctCurrency(request.getCurrency());
        depositCAPaymentHistorySpParam.setPaymentType(StringUtils.hasText(request.getPaymentType()) ? request.getPaymentType() : null);
        depositCAPaymentHistorySpParam.setTrxType(StringUtils.hasText(request.getTrxType()) ? request.getTrxType() : null);
        depositCAPaymentHistorySpParam.setStartDate(StringUtils.hasText(request.getStartDate()) ? request.getStartDate() : null);
        depositCAPaymentHistorySpParam.setEndDate(StringUtils.hasText(request.getEndDate()) ? request.getEndDate() : null);
        depositCAPaymentHistorySpParam.setSearchParam(StringUtils.hasText(request.getSearchParam()) ? request.getSearchParam() : null);
        depositCAPaymentHistorySpParam.setUserId(userAndCorp.getUserId());
        depositCAPaymentHistorySpParam.setCorpId(userAndCorp.getCorpId());
        depositCAPaymentHistorySpParam.setPage(request.getPage());
        depositCAPaymentHistorySpParam.setPageSize(request.getPageSize());

        BaseSpDto<DepositCAPaymentHistorySpParam, DepositPaymentHistoryDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_DP_CA_PYMT_HIST.getValue());
        baseSpDto.setParameter(depositCAPaymentHistorySpParam);
        baseSpDto.setPageSize(depositCAPaymentHistorySpParam.getPageSize());
        baseSpDto.setPageNumber(depositCAPaymentHistorySpParam.getPage());
        baseSpDto.setResultClass(DepositPaymentHistoryDTO.class);
        BaseSpDto<DepositCAPaymentHistorySpParam, DepositPaymentHistoryDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<DepositPaymentHistoryDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<DepositPaymentHistoryDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(depositCAPaymentHistorySpParam.getPage() >= totalPage ? totalPage : depositCAPaymentHistorySpParam.getPage() + 1);
        result.setCurrentPage(depositCAPaymentHistorySpParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);
        return result;
    }

    public SearchCollectionResponse<DepositCAEODBalanceDTO> getDepositAccountEODBalanceSP(UserAndCorp userAndCorp, DepositCAEODBalanceReqDTO request) {
        DepositCAEODBalanceSpParam depositCAEODBalanceSpParam = new DepositCAEODBalanceSpParam();
        depositCAEODBalanceSpParam.setAcctNumber(request.getAcctNumber());
        depositCAEODBalanceSpParam.setAcctCurrency(request.getCurrency());
        depositCAEODBalanceSpParam.setYear(request.getYear());
        depositCAEODBalanceSpParam.setMonth(request.getMonth());
        depositCAEODBalanceSpParam.setUserId(userAndCorp.getUserId());
        depositCAEODBalanceSpParam.setCorpId(userAndCorp.getCorpId());
        depositCAEODBalanceSpParam.setPage(request.getPage());
        depositCAEODBalanceSpParam.setPageSize(request.getPageSize());

        BaseSpDto<DepositCAEODBalanceSpParam, DepositCAEODBalanceDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_DP_CA_EOD_BALANCE.getValue());
        baseSpDto.setParameter(depositCAEODBalanceSpParam);
        baseSpDto.setPageSize(depositCAEODBalanceSpParam.getPageSize());
        baseSpDto.setPageNumber(depositCAEODBalanceSpParam.getPage());
        baseSpDto.setResultClass(DepositCAEODBalanceDTO.class);
        BaseSpDto<DepositCAEODBalanceSpParam, DepositCAEODBalanceDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<DepositCAEODBalanceDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<DepositCAEODBalanceDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(depositCAEODBalanceSpParam.getPage() >= totalPage ? totalPage : depositCAEODBalanceSpParam.getPage() + 1);
        result.setCurrentPage(depositCAEODBalanceSpParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);
        return result;
    }

    public DepositTrxReferenceDTO getDepositCAPaymentDetailTrxRefSP(UserAndCorp userAndCorp, DepositTrxReferenceReqDTO request) {
        DepositCATrxRefSpParam depositCATrxRefSpParam = new DepositCATrxRefSpParam();
        depositCATrxRefSpParam.setBankRefNo(request.getBankRefNo());
        depositCATrxRefSpParam.setAcctCurrency(request.getCurrency());
        depositCATrxRefSpParam.setUserId(userAndCorp.getUserId());
        depositCATrxRefSpParam.setCorpId(userAndCorp.getCorpId());

        BaseSpDto<DepositCATrxRefSpParam, DepositTrxReferenceDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_DP_CA_PYMT_DTL_TRXN_REF.getValue());
        baseSpDto.setParameter(depositCATrxRefSpParam);
        baseSpDto.setResultClass(DepositTrxReferenceDTO.class);
        List<DepositTrxReferenceDTO> results = Optional.ofNullable(baseSpService.execute(baseSpDto))
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        DepositTrxReferenceDTO depositTrxReferenceDTO = results.stream().findFirst().orElse(null);
        return depositTrxReferenceDTO;
    }

    public SearchCollectionResponse<DepositCADebitCardTrxDTO> getDepositCADebitCardHistoryListSp(UserAndCorp userAndCorp, DepositCADebitCardTrxReqDTO request) {
        DepositCADebitCardTrxSpParam depositCADebitCardTrxSpParam = new DepositCADebitCardTrxSpParam();
        depositCADebitCardTrxSpParam.setAcctNumber(request.getAcctNumber());
        depositCADebitCardTrxSpParam.setCardNumber(request.getCardNumber());
        depositCADebitCardTrxSpParam.setAcctCurrency(request.getCurrency());
        if (request.getFilters() != null) {
            depositCADebitCardTrxSpParam.setStartDate(StringUtils.hasText(request.getFilters().getStartDate()) ? request.getFilters().getStartDate() : null);
            depositCADebitCardTrxSpParam.setStartTime(StringUtils.hasText(request.getFilters().getStartTime()) ? request.getFilters().getStartTime() : null);
            depositCADebitCardTrxSpParam.setEndDate(StringUtils.hasText(request.getFilters().getEndDate()) ? request.getFilters().getEndDate() : null);
            depositCADebitCardTrxSpParam.setEndTime(StringUtils.hasText(request.getFilters().getEndTime()) ? request.getFilters().getEndTime() : null);
            depositCADebitCardTrxSpParam.setAmtFrom(StringUtils.hasText(request.getFilters().getAmtFrom()) ? Double.valueOf(request.getFilters().getAmtFrom()) : null);
            depositCADebitCardTrxSpParam.setAmtTo(StringUtils.hasText(request.getFilters().getAmtTo()) ? Double.valueOf(request.getFilters().getAmtTo()) : null);
        }
        depositCADebitCardTrxSpParam.setUserId(userAndCorp.getUserId());
        depositCADebitCardTrxSpParam.setCorpId(userAndCorp.getCorpId());
        depositCADebitCardTrxSpParam.setPage(request.getPage());
        depositCADebitCardTrxSpParam.setPageSize(request.getPageSize());

        BaseSpDto<DepositCADebitCardTrxSpParam, DepositCADebitCardTrxDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_DP_CA_DEBIT_HIST.getValue());
        baseSpDto.setParameter(depositCADebitCardTrxSpParam);
        baseSpDto.setPageSize(depositCADebitCardTrxSpParam.getPageSize());
        baseSpDto.setPageNumber(depositCADebitCardTrxSpParam.getPage());
        baseSpDto.setResultClass(DepositCADebitCardTrxDTO.class);
        BaseSpDto<DepositCADebitCardTrxSpParam, DepositCADebitCardTrxDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<DepositCADebitCardTrxDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<DepositCADebitCardTrxDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(depositCADebitCardTrxSpParam.getPage() >= totalPage ? totalPage : depositCADebitCardTrxSpParam.getPage() + 1);
        result.setCurrentPage(depositCADebitCardTrxSpParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);
        return result;
    }

}
