package com.m2e.dashboard.service.otherbank.loan;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.SearchResultCountDTO;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.otherbank.request.loan.OtherBankLoanAcctDetailReqDTO;
import com.m2e.dashboard.dto.otherbank.request.loan.OtherBankLoanAcctListReqDTO;
import com.m2e.dashboard.dto.otherbank.request.loan.OtherBankLoanTrxHistReqDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailNormalDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailSpecialDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailSpecialListDTO;
import com.m2e.dashboard.dto.otherbank.response.loan.OtherBankLoanAcctStatsDTO;
import com.m2e.dashboard.dto.otherbank.response.loan.OtherBankLoanAcctStatsHistDTO;
import com.m2e.dashboard.dto.otherbank.response.loan.OtherBankLoanTrxHistDTO;
import com.m2e.dashboard.sp.otherbank.request.OtherBankAcctListSpParam;
import com.m2e.dashboard.sp.otherbank.request.OtherBankDetailSpParam;
import com.m2e.dashboard.sp.otherbank.request.OtherBankStatSpParam;
import com.m2e.dashboard.sp.otherbank.request.OtherBankTrxHistListSpParam;
import com.m2e.dashboard.sp.otherbank.response.OtherBankLoanAcctListSpDTO;
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
public class OtherBankLoanSpService {
    private static final Logger logger = LoggerFactory.getLogger(OtherBankLoanSpService.class);

    @Autowired
    private BaseSpService baseSpService;

    public OtherBankLoanAcctStatsHistDTO getOtherBankLoanAccountStatSp(UserAndCorp userAndCorp) {
        OtherBankStatSpParam statSpParam = new OtherBankStatSpParam();
        statSpParam.setCorpId(userAndCorp.getCorpId());
        statSpParam.setUserId(userAndCorp.getUserId());

        BaseSpDto<OtherBankStatSpParam, OtherBankLoanAcctStatsHistDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_OB_LN_ACCT_STATS.getValue());
        baseSpDto.setParameter(statSpParam);
        baseSpDto.setResultClass(OtherBankLoanAcctStatsHistDTO.class);

        logger.debug("baseSpDto " + baseSpDto);

        return baseSpService.execute(baseSpDto).getResults().get(0);
    }

    public OtherBankLoanAcctStatsDTO getOtherBankLoanAccountStatIntradaySp(UserAndCorp userAndCorp) {
        OtherBankStatSpParam statSpParam = new OtherBankStatSpParam();
        statSpParam.setCorpId(userAndCorp.getCorpId());
        statSpParam.setUserId(userAndCorp.getUserId());

        BaseSpDto<OtherBankStatSpParam, OtherBankLoanAcctStatsDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_OB_LN_ACCT_STATS_INTRADAY.getValue());
        baseSpDto.setParameter(statSpParam);
        baseSpDto.setResultClass(OtherBankLoanAcctStatsDTO.class);

        logger.debug("baseSpDto " + baseSpDto);

        return baseSpService.execute(baseSpDto).getResults().get(0);
    }

    public BaseSpDto<OtherBankAcctListSpParam, OtherBankLoanAcctListSpDTO> getOtherBankLoanAccountListSp(UserAndCorp userAndCorp, String searchParam, int page, int pageSize) {
        logger.info("Enter getOtherBankLoanAccountListSp");

        OtherBankAcctListSpParam spParam = new OtherBankAcctListSpParam();
        spParam.setUserId(userAndCorp.getUserId());
        spParam.setCorpId(userAndCorp.getCorpId());
        spParam.setSearchParam(searchParam);

        BaseSpDto<OtherBankAcctListSpParam, OtherBankLoanAcctListSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_OB_LN_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageNumber(page);
        baseSpDto.setPageSize(pageSize);
        baseSpDto.setResultClass(OtherBankLoanAcctListSpDTO.class);
        logger.debug("baseSpDto " + baseSpDto);
        return baseSpService.execute(baseSpDto);
    }

    public BaseSpDto<OtherBankAcctListSpParam, OtherBankLoanAcctListSpDTO> getOtherBankLoanAccountListIntradaySp(UserAndCorp userAndCorp, String searchParam, int page, int pageSize) {
        logger.info("Enter getOtherBankLoanAccountListSp");

        OtherBankAcctListSpParam spParam = new OtherBankAcctListSpParam();
        spParam.setUserId(userAndCorp.getUserId());
        spParam.setCorpId(userAndCorp.getCorpId());
        spParam.setSearchParam(searchParam);

        BaseSpDto<OtherBankAcctListSpParam, OtherBankLoanAcctListSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_OB_LN_ACCT_LIST_INTRADAY.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageNumber(page);
        baseSpDto.setPageSize(pageSize);
        baseSpDto.setResultClass(OtherBankLoanAcctListSpDTO.class);
        logger.debug("baseSpDto " + baseSpDto);
        return baseSpService.execute(baseSpDto);
    }

    public OtherBankAcctDetailNormalDTO getOtherBankLoanAccountDetailSp(UserAndCorp userAndCorp, OtherBankLoanAcctDetailReqDTO req) {
        logger.info("Enter getOtherBankDepositAccountDetailSp");

        OtherBankDetailSpParam detailSpParam = new OtherBankDetailSpParam();
        detailSpParam.setAcctNumber(req.getAcctNumber());
        detailSpParam.setCorpId(userAndCorp.getCorpId());
        detailSpParam.setUserId(userAndCorp.getUserId());
        detailSpParam.setMessageType(req.getMessageType());

        BaseSpDto<OtherBankDetailSpParam, OtherBankAcctDetailNormalDTO> baseSpDto = new BaseSpDto<>();

        baseSpDto.setSpId(SP_OB_ACCT_DTL.getValue());
        baseSpDto.setParameter(detailSpParam);
        baseSpDto.setResultClass(OtherBankAcctDetailNormalDTO.class);

        logger.debug("baseSpDto " + baseSpDto);

        return !baseSpService.execute(baseSpDto).getResults().isEmpty() ? baseSpService.execute(baseSpDto).getResults().get(0) : new OtherBankAcctDetailNormalDTO();
    }

    public OtherBankAcctDetailSpecialListDTO getOtherBankLoanAccountDetailSpecialSp(UserAndCorp userAndCorp, OtherBankLoanAcctDetailReqDTO req) {
        logger.info("Enter getOtherBankDepositAccountDetailSpecialSp");
        OtherBankDetailSpParam detailSpParam = new OtherBankDetailSpParam();
        detailSpParam.setAcctNumber(req.getAcctNumber());
        detailSpParam.setCorpId(userAndCorp.getCorpId());
        detailSpParam.setUserId(userAndCorp.getUserId());
        detailSpParam.setMessageType(req.getMessageType());

        BaseSpDto<OtherBankDetailSpParam, OtherBankAcctDetailSpecialListDTO> baseSpDto = new BaseSpDto<>();

        baseSpDto.setSpId(SP_OB_ACCT_DTL.getValue());
        baseSpDto.setParameter(detailSpParam);
        baseSpDto.setResultClass(OtherBankAcctDetailSpecialListDTO.class);

        logger.info("baseSpDto " + baseSpDto);

        return !baseSpService.execute(baseSpDto).getResults().isEmpty() ? baseSpService.execute(baseSpDto).getResults().get(0) : new OtherBankAcctDetailSpecialListDTO();
    }

    public BaseSpDto<OtherBankDetailSpParam, OtherBankAcctDetailSpecialDTO> getOtherBankLoanStatementDetailSp(UserAndCorp userAndCorp, OtherBankLoanAcctDetailReqDTO req) {
        logger.info("Enter getOtherBankDepositStatementDetailSp");
        OtherBankDetailSpParam detailSpParam = new OtherBankDetailSpParam();
        detailSpParam.setAcctNumber(req.getAcctNumber());
        detailSpParam.setCorpId(userAndCorp.getCorpId());
        detailSpParam.setUserId(userAndCorp.getUserId());
        detailSpParam.setMessageType(req.getMessageType());

        BaseSpDto<OtherBankDetailSpParam, OtherBankAcctDetailSpecialDTO> baseSpDto = new BaseSpDto<>();

        baseSpDto.setSpId(SP_OB_STMT_DTL.getValue());
        baseSpDto.setParameter(detailSpParam);
        baseSpDto.setResultClass(OtherBankAcctDetailSpecialDTO.class);

        logger.info("baseSpDto " + baseSpDto);

        return baseSpService.execute(baseSpDto);
    }

    public SearchCollectionResponse<OtherBankLoanTrxHistDTO> getOtherBankLoanTrxHistListSp(UserAndCorp userAndCorp, OtherBankLoanTrxHistReqDTO req) {
        logger.info("Enter getOtherBankDepositTrxHistListSp");

        OtherBankTrxHistListSpParam spParam = new OtherBankTrxHistListSpParam();
        spParam.setUserId(userAndCorp.getUserId());
        spParam.setCorpId(userAndCorp.getCorpId());
        spParam.setAcctNumber(req.getAcctNumber());
        spParam.setSearchParam(StringUtils.hasText(req.getSearchParam()) ? req.getSearchParam() : null);
        spParam.setTrxCode(StringUtils.hasText(req.getTrxCode()) ? req.getTrxCode() : null);
        spParam.setMsgType(StringUtils.hasText(req.getMsgType()) ? req.getMsgType() : null);

        if (req.getFilters() != null) {
            spParam.setStartDateTime(StringUtils.hasText(req.getFilters().getStartDate()) ? DateTimeUtil.convert12HourDateTime(req.getFilters().getStartDate(), req.getFilters().getStartTime(), true) : null);
            spParam.setEndDateTime(StringUtils.hasText(req.getFilters().getEndDate()) ? DateTimeUtil.convert12HourDateTime(req.getFilters().getEndDate(), req.getFilters().getEndTime(), false) : null);
            spParam.setAmtFrom(StringUtils.hasText(req.getFilters().getAmtFrom()) ? Double.valueOf(req.getFilters().getAmtFrom()) : null);
            spParam.setAmtTo(StringUtils.hasText(req.getFilters().getAmtTo()) ? Double.valueOf(req.getFilters().getAmtTo()) : null);
        }

        BaseSpDto<OtherBankTrxHistListSpParam, OtherBankLoanTrxHistDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_OB_LN_TRXN_HIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageNumber(req.getPage());
        baseSpDto.setPageSize(req.getPageSize());
        baseSpDto.setResultClass(OtherBankLoanTrxHistDTO.class);

        logger.debug("baseSpDto " + baseSpDto);

        List<OtherBankLoanTrxHistDTO> spResultList = Optional.ofNullable(baseSpService.execute(baseSpDto))
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<OtherBankLoanTrxHistDTO> result = new SearchCollectionResponse<>();

        logger.debug("spResultList " + spResultList);

        int totalPage = Optional.ofNullable(baseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(baseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(req.getPage() >= totalPage ? totalPage : req.getPage() + 1);
        result.setCurrentPage(req.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);

        return result;
    }
}
