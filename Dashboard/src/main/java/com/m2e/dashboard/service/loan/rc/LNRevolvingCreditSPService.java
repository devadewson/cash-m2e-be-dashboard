package com.m2e.dashboard.service.loan.rc;

import com.m2e.dashboard.dto.loan.rc.response.LNRevolvingCreditAccountStatisticDTO;
import com.m2e.dashboard.dto.loan.rc.response.LNRevolvingCreditTrxHistoryDTO;
import com.m2e.dashboard.sp.loan.rc.request.*;
import com.m2e.dashboard.sp.loan.rc.response.LNRCAcctDetailMYSpDTO;
import com.m2e.dashboard.sp.loan.rc.response.LNRCAcctDetailSGSpDTO;
import com.m2e.dashboard.sp.loan.rc.response.LNRCAcctListSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.m2e.dashboard.enums.SPEnum.*;

@Component
public class LNRevolvingCreditSPService {
    private static final Logger logger = LoggerFactory.getLogger(LNRevolvingCreditSPService.class);
    @Autowired private BaseSpService baseSpService;

    public BaseSpDto<LNRCAcctListingSpParam, LNRCAcctListSpDTO> accountListingRC(LNRCAcctListingSpParam lnrcAcctListingSpParam){
        BaseSpDto<LNRCAcctListingSpParam, LNRCAcctListSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_LN_RC_ACCT_LIST.getValue());
        baseSpDto.setParameter(lnrcAcctListingSpParam);
        baseSpDto.setPageSize(lnrcAcctListingSpParam.getPageSize());
        baseSpDto.setPageNumber(lnrcAcctListingSpParam.getPage());
        baseSpDto.setResultClass(LNRCAcctListSpDTO.class);
        return baseSpService.execute(baseSpDto);
    }

    public LNRevolvingCreditAccountStatisticDTO accountStatisticsRCMY(LNRCAcctStatsSpParam lnrcAcctStatsSpParam){
        BaseSpDto<LNRCAcctStatsSpParam, LNRevolvingCreditAccountStatisticDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_LN_RC_ACCT_STATS.getValue());
        baseSpDto.setParameter(lnrcAcctStatsSpParam);
        baseSpDto.setResultClass(LNRevolvingCreditAccountStatisticDTO.class);
        return baseSpService.execute(baseSpDto).getResults().get(0);
    }

    public LNRCAcctDetailMYSpDTO accountDetailRCMY(LNRCAcctDetailSpParam lnrcAcctDetailSpParam){
        LNRCAcctDetailMYSpDTO accountDetailDTO;
        BaseSpDto<LNRCAcctDetailSpParam, LNRCAcctDetailMYSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_LN_RC_ACCT_DTL_MY.getValue());
        baseSpDto.setParameter(lnrcAcctDetailSpParam);
        baseSpDto.setResultClass(LNRCAcctDetailMYSpDTO.class);

        try{
            accountDetailDTO = baseSpService.execute(baseSpDto).getResults().get(0);
        } catch (IndexOutOfBoundsException  e) {
            accountDetailDTO = new LNRCAcctDetailMYSpDTO();
            logger.error("No result found for SP: {}", baseSpDto.getSpId());
        }

        return accountDetailDTO;
    }

    public LNRCAcctDetailSGSpDTO accountDetailRCSG(LNRCAcctDetailSpParam lnrcAcctDetailSpParam){
        LNRCAcctDetailSGSpDTO accountDetailDTO;
        BaseSpDto<LNRCAcctDetailSpParam, LNRCAcctDetailSGSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_LN_RC_ACCT_DTL_SG.getValue());
        baseSpDto.setParameter(lnrcAcctDetailSpParam);
        baseSpDto.setResultClass(LNRCAcctDetailSGSpDTO.class);

        try{
            accountDetailDTO = baseSpService.execute(baseSpDto).getResults().get(0);
        } catch (IndexOutOfBoundsException  e) {
            accountDetailDTO = new LNRCAcctDetailSGSpDTO();
            logger.error("No result found for SP: {}", baseSpDto.getSpId());
        }

        return accountDetailDTO;
    }

    public BaseSpDto<LNRCTrxHistorySpParam, LNRevolvingCreditTrxHistoryDTO> trxHistorySG(LNRCTrxHistorySpParam lnrcTrxHistorySpParam){
        BaseSpDto<LNRCTrxHistorySpParam, LNRevolvingCreditTrxHistoryDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_LN_RC_TRXN_HIST_SG.getValue());
        baseSpDto.setParameter(lnrcTrxHistorySpParam);
        baseSpDto.setPageSize(lnrcTrxHistorySpParam.getPageSize());
        baseSpDto.setPageNumber(lnrcTrxHistorySpParam.getPage());
        baseSpDto.setResultClass(LNRevolvingCreditTrxHistoryDTO.class);
        return baseSpService.execute(baseSpDto);
    }
}
