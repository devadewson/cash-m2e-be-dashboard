package com.m2e.dashboard.service.af.hp.sp;

import com.m2e.dashboard.dto.af.response.bd.AFBDMasterAcctDetailDTO;
import com.m2e.dashboard.dto.af.response.hp.*;
import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.service.af.bd.sp.AFBlockDiscountingSPService;
import com.m2e.dashboard.sp.af.bd.request.AFBDDetailSpParam;
import com.m2e.dashboard.sp.af.hp.request.AFHPAcctDetailSpParam;
import com.m2e.dashboard.sp.af.hp.request.AFHPAcctListSpParam;
import com.m2e.dashboard.sp.af.hp.request.AFHPAcctStatsSpParam;
import com.m2e.dashboard.sp.af.hp.request.AFHPTrxHistSpParam;
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
public class AFHirePurchaseSPService {
    private static final Logger logger = LoggerFactory.getLogger(AFHirePurchaseSPService.class);

    @Autowired
    BaseSpService baseSpService;

    public AFHPAcctStatsDTO afHPAcctStatsSp(AFHPAcctStatsSpParam request) {
        BaseSpDto<AFHPAcctStatsSpParam, AFHPAcctStatsDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_HP_ACCT_STATS.getValue());
        baseSpDto.setParameter(request);
        baseSpDto.setResultClass(AFHPAcctStatsDTO.class);

        BaseSpDto<AFHPAcctStatsSpParam, AFHPAcctStatsDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);

        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error("Empty SP result for Hire Purchase afHPAcctStatsSp");
            return new AFHPAcctStatsDTO();
        }

        return resultBaseSpDto.getResults().get(0);
    }

    public SearchCollectionResponse<AFHPAcctListDTO> afHPAccountListingSP(AFHPAcctListSpParam request, BasePageRequest pageRequest) {
        BaseSpDto<AFHPAcctListSpParam, AFHPAcctListDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_HP_ACCT_LIST.getValue());
        baseSpDto.setParameter(request);
        baseSpDto.setPageSize(pageRequest.getPageSize());
        baseSpDto.setPageNumber(pageRequest.getPage());
        baseSpDto.setResultClass(AFHPAcctListDTO.class);

        BaseSpDto<AFHPAcctListSpParam, AFHPAcctListDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFHPAcctListDTO> result = new SearchCollectionResponse<>();

        if (resultBaseSpDto.getResults() == null) {
            logger.error("Empty SP result for Hire Purchase afHPAccountListingSP");
            return result;
        }

        List<AFHPAcctListDTO> spResultList = resultBaseSpDto.getResults().stream().toList();

        result.setNextPage(pageRequest.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : pageRequest.getPage() + 1);
        result.setCurrentPage(pageRequest.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public AFHPAcctDetailDTO afHPAccountDetailMYSP(AFHPAcctDetailSpParam request) {

        BaseSpDto<AFHPAcctDetailSpParam, AFHPAcctDetailDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_HP_ACCT_DTL_MY.getValue());
        baseSpDto.setParameter(request);
        baseSpDto.setResultClass(AFHPAcctDetailDTO.class);

        BaseSpDto<AFHPAcctDetailSpParam, AFHPAcctDetailDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);

        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error("Empty SP result for Hire Purchase afHPAccountDetailMYSP");
            return new AFHPAcctDetailDTO();
        }

        return resultBaseSpDto.getResults().get(0);
    }

    public AFHPMasterAcctDetailDTO afHPFacilityDetailMYSP(AFHPAcctDetailSpParam request) {

        BaseSpDto<AFHPAcctDetailSpParam, AFHPMasterAcctDetailDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_HP_ACCT_DTL_MY.getValue());
        baseSpDto.setParameter(request);
        baseSpDto.setResultClass(AFHPMasterAcctDetailDTO.class);

        BaseSpDto<AFHPAcctDetailSpParam, AFHPMasterAcctDetailDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);

        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error("Empty SP result for Hire Purchase afHPAccountDetailMYSP");
            return new AFHPMasterAcctDetailDTO();
        }

        return resultBaseSpDto.getResults().get(0);
    }

    public AFHPSGAcctDetailDTO afHPAccountDetailSGSP(AFHPAcctDetailSpParam request, Class<AFHPSGAcctDetailDTO> dtoClass) {

        BaseSpDto<AFHPAcctDetailSpParam, AFHPSGAcctDetailDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_HP_ACCT_DTL_SG.getValue());
        baseSpDto.setParameter(request);
        baseSpDto.setResultClass(dtoClass);

        BaseSpDto<AFHPAcctDetailSpParam, AFHPSGAcctDetailDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);

        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error("Empty SP result for Hire Purchase afHPAccountDetailSGSP");
            return new AFHPSGAcctDetailDTO();
        }

        return resultBaseSpDto.getResults().get(0);
    }

    public <T> SearchCollectionResponse<T> afHPTrxHistorySP(AFHPTrxHistSpParam request, BasePageRequest pageRequest, Class<T> dtoClass) {

        // Create BaseSpDto for the stored procedure
        BaseSpDto<AFHPTrxHistSpParam, T> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_HP_TRXN_HIST.getValue());
        baseSpDto.setParameter(request);
        baseSpDto.setPageSize(pageRequest.getPageSize());
        baseSpDto.setPageNumber(pageRequest.getPage());
        baseSpDto.setResultClass(dtoClass);

        // Execute stored procedure and get results
        BaseSpDto<AFHPTrxHistSpParam, T> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<T> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults() == null) {
            logger.error("Empty SP result for Hire Purchase afHPTrxHistorySP");
            return result;
        }

        List<T> spResultList = resultBaseSpDto.getResults().stream().toList();

        result.setNextPage(
                pageRequest.getPage() >= resultBaseSpDto.getTotalPage()
                        ? -1
                        : pageRequest.getPage() + 1
        );
        result.setCurrentPage(pageRequest.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }


}
