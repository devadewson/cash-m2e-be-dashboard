package com.m2e.dashboard.service.loan.tlf;

import com.m2e.dashboard.dto.loan.tlf.response.LNTLFAccountStatisticDTO;
import com.m2e.dashboard.dto.loan.tlf.response.acctdetail.LNTLFDetailSGConventionalFITASDTO;
import com.m2e.dashboard.dto.loan.tlf.response.acctdetail.LNTLFDetailSGIslamicFITASDTO;
import com.m2e.dashboard.sp.loan.tlf.request.LNTLFAcctDetailSpParam;
import com.m2e.dashboard.sp.loan.tlf.request.LNTLFAcctListingSpParam;
import com.m2e.dashboard.sp.loan.tlf.request.LNTLFAcctStatsSpParam;
import com.m2e.dashboard.sp.loan.tlf.request.LNTLFTrxHistorySpParam;
import com.m2e.dashboard.sp.loan.tlf.response.LNTLFAcctListingSpDTO;
import com.m2e.dashboard.sp.loan.tlf.response.LNTLFTrxHistorySpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static com.m2e.dashboard.enums.SPEnum.*;

@Component
public class LNTLFSPService {
    private static final Logger logger = LoggerFactory.getLogger(LNTLFSPService.class);
    @Autowired private BaseSpService baseSpService;

    public BaseSpDto<LNTLFAcctListingSpParam, LNTLFAcctListingSpDTO> accountListingTLF(LNTLFAcctListingSpParam lntlfAcctListingSpParam, String spId){
        BaseSpDto<LNTLFAcctListingSpParam, LNTLFAcctListingSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(spId);
        baseSpDto.setParameter(lntlfAcctListingSpParam);
        baseSpDto.setPageSize(lntlfAcctListingSpParam.getPageSize());
        baseSpDto.setPageNumber(lntlfAcctListingSpParam.getPage());
        baseSpDto.setResultClass(LNTLFAcctListingSpDTO.class);
        return baseSpService.execute(baseSpDto);
    }

    public LNTLFAccountStatisticDTO accountStatisticsTLF(LNTLFAcctStatsSpParam lntlfAcctStatsSpParam, String spId){
        BaseSpDto<LNTLFAcctStatsSpParam, LNTLFAccountStatisticDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(spId);
        baseSpDto.setParameter(lntlfAcctStatsSpParam);
        baseSpDto.setResultClass(LNTLFAccountStatisticDTO.class);
        return baseSpService.execute(baseSpDto).getResults().get(0);
    }

    public BaseSpDto<LNTLFTrxHistorySpParam, LNTLFTrxHistorySpDTO> trxHistories(LNTLFTrxHistorySpParam lntlfTrxHistorySpParam){
        logger.info("Executing SP with ID: {}", SP_LN_TL_TRXN_HIST.getValue());
        BaseSpDto<LNTLFTrxHistorySpParam, LNTLFTrxHistorySpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_LN_TL_TRXN_HIST.getValue());
        baseSpDto.setParameter(lntlfTrxHistorySpParam);
        baseSpDto.setPageSize(lntlfTrxHistorySpParam.getPageSize());
        baseSpDto.setPageNumber(lntlfTrxHistorySpParam.getPage());
        baseSpDto.setResultClass(LNTLFTrxHistorySpDTO.class);
        return baseSpService.execute(baseSpDto);
    }

    public BaseSpDto<LNTLFAcctDetailSpParam, LNTLFDetailSGIslamicFITASDTO> detailSGFitasIslamic(LNTLFAcctDetailSpParam lntlfAcctDetailSpParam){
        logger.info("Executing SP with ID: {}", SP_LN_TL_ACCT_DTL_SG_FITAS_ISLAMIC.getValue());
        BaseSpDto<LNTLFAcctDetailSpParam, LNTLFDetailSGIslamicFITASDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_LN_TL_ACCT_DTL_SG_FITAS_ISLAMIC.getValue());
        baseSpDto.setParameter(lntlfAcctDetailSpParam);
        baseSpDto.setResultClass(LNTLFDetailSGIslamicFITASDTO.class);
        return baseSpService.execute(baseSpDto);
    }

    public BaseSpDto<LNTLFAcctDetailSpParam, LNTLFDetailSGConventionalFITASDTO> detailSGFitasConventional(LNTLFAcctDetailSpParam lntlfAcctDetailSpParam){
        logger.info("Executing SP with ID: {}", SP_LN_TL_ACCT_DTL_SG_FITAS_CONV.getValue());
        BaseSpDto<LNTLFAcctDetailSpParam, LNTLFDetailSGConventionalFITASDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_LN_TL_ACCT_DTL_SG_FITAS_CONV.getValue());
        baseSpDto.setParameter(lntlfAcctDetailSpParam);
        baseSpDto.setResultClass(LNTLFDetailSGConventionalFITASDTO.class);
        return baseSpService.execute(baseSpDto);
    }

    public String lastUpdated(){
        OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);
        ZoneOffset gmtPlus8 = ZoneOffset.ofHours(8);
        OffsetDateTime adjustedTime = offsetDateTime.withOffsetSameInstant(gmtPlus8);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy. HH:mm:ss '(GMT+8)'");
        return adjustedTime.format(formatter);
    }
}
