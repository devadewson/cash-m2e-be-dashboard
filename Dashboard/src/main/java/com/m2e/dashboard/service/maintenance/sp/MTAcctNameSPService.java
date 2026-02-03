package com.m2e.dashboard.service.maintenance.sp;

import com.m2e.dashboard.sp.maintenance.request.MTResetAcctNameSpParam;
import com.m2e.dashboard.sp.maintenance.response.MTResetAcctNameSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.m2e.dashboard.enums.SPEnum.*;

@Component
public class MTAcctNameSPService {
    private static final Logger logger = LoggerFactory.getLogger(MTAcctNameSPService.class);
    private final BaseSpService baseSpService;

    public MTAcctNameSPService(BaseSpService baseSpService) {
        this.baseSpService = baseSpService;
    }

    public boolean resetToDefaultAcctName(MTResetAcctNameSpParam mtResetAcctNameSpParam){
        MTResetAcctNameSpDTO spResult;
        BaseSpDto<MTResetAcctNameSpParam, MTResetAcctNameSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_ADM_MT_RESET_ACCT_NAME.getValue());
        baseSpDto.setParameter(mtResetAcctNameSpParam);
        baseSpDto.setResultClass(MTResetAcctNameSpDTO.class);

        try{
            spResult = baseSpService.execute(baseSpDto).getResults().get(0);
        } catch (IndexOutOfBoundsException  e) {
            spResult = new MTResetAcctNameSpDTO();
            logger.error("No result found for SP: {}", baseSpDto.getSpId());
        }

        if (spResult.getRowsUpdated()==null || spResult.getRowsUpdated()<0) {
            logger.error("Stored procedure execution error");
            return false;
        } else {
            logger.info("Updated {} rows", spResult.getRowsUpdated());
            return true;
        }
    }

    public boolean resetToDefaultAcctNameCifAsAcctNo(MTResetAcctNameSpParam mtResetAcctNameSpParam){
        MTResetAcctNameSpDTO spResult;
        BaseSpDto<MTResetAcctNameSpParam, MTResetAcctNameSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_ADM_MT_RESET_ACCT_NAME_CIF_AS_ACCT.getValue());
        baseSpDto.setParameter(mtResetAcctNameSpParam);
        baseSpDto.setResultClass(MTResetAcctNameSpDTO.class);

        try{
            spResult = baseSpService.execute(baseSpDto).getResults().get(0);
        } catch (IndexOutOfBoundsException  e) {
            spResult = new MTResetAcctNameSpDTO();
            logger.error("No result found for SP: {}", baseSpDto.getSpId());
        }

        if (spResult.getRowsUpdated()==null || spResult.getRowsUpdated()<0) {
            logger.error("Stored procedure execution error");
            return false;
        } else {
            logger.info("Updated {} rows", spResult.getRowsUpdated());
            return true;
        }
    }

    public boolean resetToDefaultAcctNameLoanOverdraft(MTResetAcctNameSpParam mtResetAcctNameSpParam){
        MTResetAcctNameSpDTO spResult;
        BaseSpDto<MTResetAcctNameSpParam, MTResetAcctNameSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_ADM_MT_RESET_ACCT_NAME_LN_OD.getValue());
        baseSpDto.setParameter(mtResetAcctNameSpParam);
        baseSpDto.setResultClass(MTResetAcctNameSpDTO.class);

        try{
            spResult = baseSpService.execute(baseSpDto).getResults().get(0);
        } catch (IndexOutOfBoundsException  e) {
            spResult = new MTResetAcctNameSpDTO();
            logger.error("No result found for SP: {}", baseSpDto.getSpId());
        }

        if (spResult.getRowsUpdated()==null || spResult.getRowsUpdated()<0) {
            logger.error("Stored procedure execution error");
            return false;
        } else {
            logger.info("Updated {} rows", spResult.getRowsUpdated());
            return true;
        }
    }
}