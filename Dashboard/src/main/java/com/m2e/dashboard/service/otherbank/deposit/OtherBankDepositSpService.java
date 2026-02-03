package com.m2e.dashboard.service.otherbank.deposit;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.SearchResultCountDTO;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.otherbank.request.deposit.OtherBankDepositAcctDetailReqDTO;
import com.m2e.dashboard.dto.otherbank.request.deposit.OtherBankDepositAcctListingReqDTO;
import com.m2e.dashboard.dto.otherbank.request.deposit.OtherBankDepositTrxHistoryReqDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailNormalDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailSpecialDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailSpecialListDTO;
import com.m2e.dashboard.dto.otherbank.response.deposit.OtherBankDepositAcctStatsDTO;
import com.m2e.dashboard.dto.otherbank.response.deposit.OtherBankDepositAcctStatsHistDTO;
import com.m2e.dashboard.dto.otherbank.response.deposit.OtherBankDepositTrxHistoryDTO;
import com.m2e.dashboard.sp.otherbank.request.OtherBankAcctListSpParam;
import com.m2e.dashboard.sp.otherbank.request.OtherBankDetailSpParam;
import com.m2e.dashboard.sp.otherbank.request.OtherBankStatSpParam;
import com.m2e.dashboard.sp.otherbank.request.OtherBankTrxHistListSpParam;
import com.m2e.dashboard.sp.otherbank.response.OtherBankDepositAcctListingSpDTO;
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
public class OtherBankDepositSpService {
    private static final Logger logger = LoggerFactory.getLogger(OtherBankDepositSpService.class);

    @Autowired
    private BaseSpService baseSpService;

    public OtherBankDepositAcctStatsHistDTO getOtherBankDepositAccountStatSp(UserAndCorp userAndCorp) {
        logger.info("Enter getOtherBankDepositAccountStatSp");
        OtherBankStatSpParam statSpParam = new OtherBankStatSpParam();
        statSpParam.setCorpId(userAndCorp.getCorpId());
        statSpParam.setUserId(userAndCorp.getUserId());

        BaseSpDto<OtherBankStatSpParam, OtherBankDepositAcctStatsHistDTO> baseSpDto = new BaseSpDto<>();

        baseSpDto.setSpId(SP_OB_DP_ACCT_STATS.getValue());
        baseSpDto.setParameter(statSpParam);
        baseSpDto.setResultClass(OtherBankDepositAcctStatsHistDTO.class);

        return baseSpService.execute(baseSpDto).getResults().get(0);
    }

    public OtherBankDepositAcctStatsDTO getOtherBankDepositAccountStatIntradaySp(UserAndCorp userAndCorp) {
        logger.info("Enter getOtherBankDepositAccountStatSp");
        OtherBankStatSpParam statSpParam = new OtherBankStatSpParam();
        statSpParam.setCorpId(userAndCorp.getCorpId());
        statSpParam.setUserId(userAndCorp.getUserId());

        BaseSpDto<OtherBankStatSpParam, OtherBankDepositAcctStatsDTO> baseSpDto = new BaseSpDto<>();

        baseSpDto.setSpId(SP_OB_DP_ACCT_STATS_INTRADAY.getValue());
        baseSpDto.setParameter(statSpParam);
        baseSpDto.setResultClass(OtherBankDepositAcctStatsDTO.class);

        return baseSpService.execute(baseSpDto).getResults().get(0);
    }


    public BaseSpDto<OtherBankAcctListSpParam, OtherBankDepositAcctListingSpDTO> getOtherBankDepositAccountListSp(UserAndCorp userAndCorp, String searchParam, int page, int pageSize) {
        logger.info("Enter getOtherBankDepositAccountListSp");
        OtherBankAcctListSpParam spParam = new OtherBankAcctListSpParam();
        spParam.setUserId(userAndCorp.getUserId());
        spParam.setCorpId(userAndCorp.getCorpId());
        spParam.setSearchParam(searchParam);

        BaseSpDto<OtherBankAcctListSpParam, OtherBankDepositAcctListingSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_OB_DP_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageNumber(page);
        baseSpDto.setPageSize(pageSize);
        baseSpDto.setResultClass(OtherBankDepositAcctListingSpDTO.class);

        logger.info("baseSpDto " + baseSpDto);

        return baseSpService.execute(baseSpDto);
    }

    public BaseSpDto<OtherBankAcctListSpParam, OtherBankDepositAcctListingSpDTO> getOtherBankDepositAccountListIntradaySp(UserAndCorp userAndCorp, String searchParam, int page, int pageSize) {
        logger.info("Enter getOtherBankDepositAccountListSp");
        OtherBankAcctListSpParam spParam = new OtherBankAcctListSpParam();
        spParam.setUserId(userAndCorp.getUserId());
        spParam.setCorpId(userAndCorp.getCorpId());
        spParam.setSearchParam(searchParam);

        BaseSpDto<OtherBankAcctListSpParam, OtherBankDepositAcctListingSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_OB_DP_ACCT_LIST_INTRADAY.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageNumber(page);
        baseSpDto.setPageSize(pageSize);
        baseSpDto.setResultClass(OtherBankDepositAcctListingSpDTO.class);

        logger.info("baseSpDto " + baseSpDto);

        return baseSpService.execute(baseSpDto);
    }

    public OtherBankAcctDetailNormalDTO getOtherBankDepositAccountDetailSp(UserAndCorp userAndCorp, OtherBankDepositAcctDetailReqDTO req) {
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

        logger.info("baseSpDto " + baseSpDto);

        return !baseSpService.execute(baseSpDto).getResults().isEmpty() ? baseSpService.execute(baseSpDto).getResults().get(0) : new OtherBankAcctDetailNormalDTO();
    }

    public OtherBankAcctDetailSpecialListDTO getOtherBankDepositAccountDetailSpecialSp(UserAndCorp userAndCorp, OtherBankDepositAcctDetailReqDTO req) {
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

    public BaseSpDto<OtherBankDetailSpParam, OtherBankAcctDetailSpecialDTO> getOtherBankDepositStatementDetailSp(UserAndCorp userAndCorp, OtherBankDepositAcctDetailReqDTO req) {
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

    public SearchCollectionResponse<OtherBankDepositTrxHistoryDTO> getOtherBankDepositTrxHistListSp(UserAndCorp userAndCorp, OtherBankDepositTrxHistoryReqDTO req) {
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

        BaseSpDto<OtherBankTrxHistListSpParam, OtherBankDepositTrxHistoryDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_OB_DP_TRXN_HIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageNumber(req.getPage());
        baseSpDto.setPageSize(req.getPageSize());
        baseSpDto.setResultClass(OtherBankDepositTrxHistoryDTO.class);


        List<OtherBankDepositTrxHistoryDTO> spResultList = Optional.ofNullable(baseSpService.execute(baseSpDto))
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<OtherBankDepositTrxHistoryDTO> result = new SearchCollectionResponse<>();

        logger.info("spResultList " + spResultList);

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
