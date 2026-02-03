package com.m2e.dashboard.service.af.pbr.sp;

import com.m2e.dashboard.dto.af.response.pbr.*;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.af.bd.request.AFBDTrxHistorySpParam;
import com.m2e.dashboard.sp.af.pbr.request.*;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.m2e.dashboard.enums.SPEnum.*;

@Service
@Slf4j
public class AFPBRSPService {
    private static final Logger logger = LoggerFactory.getLogger(AFPBRSPService.class);

    @Autowired
    BaseSpService baseSpService;

    public SearchCollectionResponse<AFPBRViewByFacilityDTO> masterAcctListing(AFPBRAcctListSpParam spParam) {
        BaseSpDto<AFPBRAcctListSpParam, AFPBRViewByFacilityDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_PBR_MASTER_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(AFPBRViewByFacilityDTO.class);

        BaseSpDto<AFPBRAcctListSpParam, AFPBRViewByFacilityDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFPBRViewByFacilityDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults() == null) {
            result.setData(new ArrayList<>());
            logger.error("Empty SP result for PBR masterAcctListing");
            return result;
        }
        List<AFPBRViewByFacilityDTO> spResultList = resultBaseSpDto.getResults().stream().toList();
        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public SearchCollectionResponse<AFPBRViewByAccountDTO> acctListing(AFPBRAcctListSpParam spParam) {
        BaseSpDto<AFPBRAcctListSpParam, AFPBRViewByAccountDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_PBR_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(AFPBRViewByAccountDTO.class);

        BaseSpDto<AFPBRAcctListSpParam, AFPBRViewByAccountDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFPBRViewByAccountDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults() == null) {
            result.setData(new ArrayList<>());
            logger.error("Empty SP result for PBR certListing");
            return result;
        }
        List<AFPBRViewByAccountDTO> spResultList = resultBaseSpDto.getResults().stream().toList();
        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public SearchCollectionResponse<AFPBRUnitSummaryDTO> subAcctListing(AFPBRSubAcctListSpParam spParam) {
        BaseSpDto<AFPBRSubAcctListSpParam, AFPBRUnitSummaryDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_PBR_SUB_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(AFPBRUnitSummaryDTO.class);

        BaseSpDto<AFPBRSubAcctListSpParam, AFPBRUnitSummaryDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFPBRUnitSummaryDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults() == null) {
            result.setData(new ArrayList<>());
            logger.error("Empty SP result for PBR subAcctListing");
            return result;
        }
        List<AFPBRUnitSummaryDTO> spResultList = resultBaseSpDto.getResults().stream().toList();
        logger.info("spResultList: {}", spResultList);
        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public AFPBRStatisticDTO statsViewByAcct(AFPBRAcctStatsSpParam spParam) {
        BaseSpDto<AFPBRAcctStatsSpParam, AFPBRStatisticDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_PBR_ACCT_STATS_VIEW_BY_ACCOUNT.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFPBRStatisticDTO.class);
        BaseSpDto<AFPBRAcctStatsSpParam, AFPBRStatisticDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error("Empty SP result for PBR statsViewByAcct");
            AFPBRStatisticDTO afpbrStatisticDTO = new AFPBRStatisticDTO();
            afpbrStatisticDTO.setNoOfAccounts("0");
            return afpbrStatisticDTO;
        }
        return resultBaseSpDto.getResults().get(0);
    }

    public AFPBRMasterAccountDetailDTO masterAcctDetail(AFPBRDetailSpParam spParam) {
        BaseSpDto<AFPBRDetailSpParam, AFPBRMasterAccountDetailDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_PBR_MASTER_ACCT_DTL.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFPBRMasterAccountDetailDTO.class);
        BaseSpDto<AFPBRDetailSpParam, AFPBRMasterAccountDetailDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error("Empty SP result for Block Discounting masterAcctDetail");
            return new AFPBRMasterAccountDetailDTO();
        }
        return resultBaseSpDto.getResults().get(0);
    }

    public AFPBRSubaccountDetailDTO accountDetail(AFPBRDetailSpParam spParam) {
        BaseSpDto<AFPBRDetailSpParam, AFPBRSubaccountDetailDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_PBR_ACCT_DTL.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFPBRSubaccountDetailDTO.class);
        BaseSpDto<AFPBRDetailSpParam, AFPBRSubaccountDetailDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error("Empty SP result for Block Discounting accountDetail");
            return new AFPBRSubaccountDetailDTO();
        }
        return resultBaseSpDto.getResults().get(0);
    }

    public SearchCollectionResponse<AFPBRTrxHistoryDTO> trxHistory(AFPBRTrxHistorySpParam spParam) {
        BaseSpDto<AFPBRTrxHistorySpParam, AFPBRTrxHistoryDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_PBR_TRXN_HIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setResultClass(AFPBRTrxHistoryDTO.class);
        BaseSpDto<AFPBRTrxHistorySpParam, AFPBRTrxHistoryDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFPBRTrxHistoryDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults() == null) {
            result.setData(new ArrayList<>());
            logger.error("Empty SP result for PBR trxHistory");
            result.setTotalRecordCount(0);
            return result;
        }
        List<AFPBRTrxHistoryDTO> spResultList = resultBaseSpDto.getResults().stream().toList();
        logger.info("trxHistory spResultList: {}", spResultList);
        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }
}
