package com.m2e.dashboard.service.af.fs.sp;

import com.m2e.dashboard.dto.af.response.fs.AFFSSubAcctListDTO;
import com.m2e.dashboard.dto.af.response.fs.AFFSTrxHistDTO;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.af.fs.request.*;
import com.m2e.dashboard.sp.af.fs.response.*;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.m2e.dashboard.enums.SPEnum.*;

@Service
@Slf4j
public class AFFloorStockingSPService {
    private static final Logger logger = LoggerFactory.getLogger(AFFloorStockingSPService.class);

    @Autowired
    BaseSpService baseSpService;

    public SearchCollectionResponse<AFFSAcctListSpDTO> masterAcctListing(AFFSAcctListSpParam spParam) {
        BaseSpDto<AFFSAcctListSpParam, AFFSAcctListSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_FS_MASTER_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(AFFSAcctListSpDTO.class);

        BaseSpDto<AFFSAcctListSpParam, AFFSAcctListSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFFSAcctListSpDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults()==null){
            logger.error("Empty SP result for Floor Stock masterAcctListing");
            return result;
        }
        List<AFFSAcctListSpDTO> spResultList = resultBaseSpDto.getResults();
        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public SearchCollectionResponse<AFFSCertListSpDTO> accountListing(AFFSCertListSpParam spParam) {
        BaseSpDto<AFFSCertListSpParam, AFFSCertListSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_FS_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(AFFSCertListSpDTO.class);

        BaseSpDto<AFFSCertListSpParam, AFFSCertListSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFFSCertListSpDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults()==null){
            logger.error("Empty SP result for Floor Stock certListing");
            return result;
        }
        List<AFFSCertListSpDTO> spResultList = resultBaseSpDto.getResults();
        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public SearchCollectionResponse<AFFSSubAcctListDTO> subAcctListing(AFFSSubAcctListSpParam spParam) {
        BaseSpDto<AFFSSubAcctListSpParam, AFFSSubAcctListDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_FS_SUB_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(AFFSSubAcctListDTO.class);

        BaseSpDto<AFFSSubAcctListSpParam, AFFSSubAcctListDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFFSSubAcctListDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults()==null){
            logger.error("Empty SP result for Floor Stock subAcctListing");
            return result;
        }
        List<AFFSSubAcctListDTO> spResultList = resultBaseSpDto.getResults().stream().toList();
        logger.info("spResultList: {}", spResultList);
        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public AFFSAcctStatsViewByFacilitySpDTO statsViewByFacility(AFFSAcctStatsSpParam spParam){
        BaseSpDto<AFFSAcctStatsSpParam, AFFSAcctStatsViewByFacilitySpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_FS_ACCT_STATS_VIEW_BY_FACILITY.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFFSAcctStatsViewByFacilitySpDTO.class);
        BaseSpDto<AFFSAcctStatsSpParam, AFFSAcctStatsViewByFacilitySpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults()==null){
            logger.error("Empty SP result for Floor Stock statsViewByFacility");
            return new AFFSAcctStatsViewByFacilitySpDTO();
        }
        return resultBaseSpDto.getResults().get(0);
    }

    public AFFSAcctStatsViewByAcctSpDTO statsViewByAcct(AFFSAcctStatsSpParam spParam){
        BaseSpDto<AFFSAcctStatsSpParam, AFFSAcctStatsViewByAcctSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_FS_ACCT_STATS_VIEW_BY_ACCOUNT.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFFSAcctStatsViewByAcctSpDTO.class);
        BaseSpDto<AFFSAcctStatsSpParam, AFFSAcctStatsViewByAcctSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults()==null){
            logger.error("Empty SP result for Floor Stock statsViewByAcct");
            return new AFFSAcctStatsViewByAcctSpDTO();
        }
        return resultBaseSpDto.getResults().get(0);
    }

    public BaseSpDto<AFFSTrxHistorySpParam, AFFSTrxHistDTO> trxHistories(AFFSTrxHistorySpParam spParam){
        BaseSpDto<AFFSTrxHistorySpParam, AFFSTrxHistDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_FS_TRXN_HIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFFSTrxHistDTO.class);
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setPageSize(spParam.getPageSize());
        return baseSpService.execute(baseSpDto);
    }

    public boolean getDisplayShowViewBy(AFFSDisplayViewBySpParam spParam) {
        BaseSpDto<AFFSDisplayViewBySpParam, AFFSDisplayViewBySpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_FS_VIEW_BY.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFFSDisplayViewBySpDTO.class);

        BaseSpDto<AFFSDisplayViewBySpParam, AFFSDisplayViewBySpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults()==null){
            logger.error("Empty SP result for Floor Stock certListing");
            return true;
        }
        return "Y".equals(resultBaseSpDto.getResults().get(0).getDisplayViewBy());
    }
}
