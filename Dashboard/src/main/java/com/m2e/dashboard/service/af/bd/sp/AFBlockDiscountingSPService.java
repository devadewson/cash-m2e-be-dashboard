package com.m2e.dashboard.service.af.bd.sp;

import com.m2e.dashboard.dto.af.response.bd.*;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.af.bd.request.*;
import com.m2e.dashboard.sp.af.bd.response.AFBDDebitNoteListSpDTO;
import com.m2e.dashboard.sp.af.bd.response.AFBDDisplayViewBySpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.m2e.dashboard.enums.SPEnum.*;

@Service
@Slf4j
public class AFBlockDiscountingSPService {
    private static final Logger logger = LoggerFactory.getLogger(AFBlockDiscountingSPService.class);
    private static final String ACCT_DETATIL_ERR_LOG = "Empty SP result for Block Discounting accountDetail";

    @Autowired
    BaseSpService baseSpService;

    public SearchCollectionResponse<AFBDAcctListViewFacilityDTO> masterAcctListing(AFBDAcctListSpParam spParam) {
        BaseSpDto<AFBDAcctListSpParam, AFBDAcctListViewFacilityDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_MASTER_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(AFBDAcctListViewFacilityDTO.class);

        BaseSpDto<AFBDAcctListSpParam, AFBDAcctListViewFacilityDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFBDAcctListViewFacilityDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults() == null) {
            result.setData(new ArrayList<>());
            logger.error("Empty SP result for Block Discounting masterAcctListing");
            return result;
        }
        List<AFBDAcctListViewFacilityDTO> spResultList = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        logger.info("masterAcctListing spResultList: {}", spResultList);

        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public SearchCollectionResponse<AFBDAcctListViewAcctDTO> acctListing(AFBDAcctListSpParam spParam) {
        BaseSpDto<AFBDAcctListSpParam, AFBDAcctListViewAcctDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(AFBDAcctListViewAcctDTO.class);

        BaseSpDto<AFBDAcctListSpParam, AFBDAcctListViewAcctDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFBDAcctListViewAcctDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults() == null) {
            result.setData(new ArrayList<>());
            logger.error("Empty SP result for Block Discounting acctListing");
            return result;
        }
        List<AFBDAcctListViewAcctDTO> spResultList = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        logger.info("acctListing spResultList: {}", spResultList);

        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public SearchCollectionResponse<AFBDSubMasterAcctListDTO> subMasterListing(AFBDSubAcctListSpParam spParam) {
        BaseSpDto<AFBDSubAcctListSpParam, AFBDSubMasterAcctListDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_SUB_MASTER_ACCT_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(AFBDSubMasterAcctListDTO.class);

        BaseSpDto<AFBDSubAcctListSpParam, AFBDSubMasterAcctListDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFBDSubMasterAcctListDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults() == null) {
            result.setData(new ArrayList<>());
            logger.error("Empty SP result for Block Discounting sub master listing");
            return result;
        }
        List<AFBDSubMasterAcctListDTO> spResultList = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        logger.info("subMasterListing spResultList: {}", spResultList);

        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public SearchCollectionResponse<AFBDSubChildAcctListDTO> subAcctListing(AFBDSubAcctListSpParam spParam) {
        logger.info("subAcctListing ");
        BaseSpDto<AFBDSubAcctListSpParam, AFBDSubChildAcctListDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_SUB_ACCT_LIST_MY.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(AFBDSubChildAcctListDTO.class);

        BaseSpDto<AFBDSubAcctListSpParam, AFBDSubChildAcctListDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFBDSubChildAcctListDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults() == null) {
            result.setData(new ArrayList<>());
            logger.error("Empty SP result for Block Discounting subAcctListing");
            return result;
        }
        List<AFBDSubChildAcctListDTO> spResultList = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        logger.info("subAcctListing spResultList: {}", spResultList);
        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public SearchCollectionResponse<AFBDSubAcctListSGDTO> subAcctListingSG(AFBDSubAcctListSpParam spParam) {
        BaseSpDto<AFBDSubAcctListSpParam, AFBDSubAcctListSGDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_SUB_ACCT_LIST_SG.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(AFBDSubAcctListSGDTO.class);

        BaseSpDto<AFBDSubAcctListSpParam, AFBDSubAcctListSGDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFBDSubAcctListSGDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults() == null) {
            result.setData(new ArrayList<>());
            logger.error("Empty SP result for Block Discounting acctListing");
            return result;
        }
        List<AFBDSubAcctListSGDTO> spResultList = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        logger.info("acctListing spResultList: {}", spResultList);

        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public SearchCollectionResponse<AFBDDebitNoteListSpDTO> debitNoteListing(AFBDDebitNoteListSpParam spParam) {
        BaseSpDto<AFBDDebitNoteListSpParam, AFBDDebitNoteListSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_DEBIT_NOTE_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(AFBDDebitNoteListSpDTO.class);

        BaseSpDto<AFBDDebitNoteListSpParam, AFBDDebitNoteListSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFBDDebitNoteListSpDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults() == null) {
            result.setData(new ArrayList<>());
            logger.error("Empty SP result for Block Discounting subAcctListing");
            return result;
        }
        List<AFBDDebitNoteListSpDTO> spResultList = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        logger.info("debitNoteListing spResultList: {}", spResultList);
        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public AFBDAcctStatsViewFacilityDTO statsViewByFacility(AFBDAcctStatsSpParam spParam) {
        BaseSpDto<AFBDAcctStatsSpParam, AFBDAcctStatsViewFacilityDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_ACCT_STATS_VIEW_BY_FACILITY.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFBDAcctStatsViewFacilityDTO.class);
        BaseSpDto<AFBDAcctStatsSpParam, AFBDAcctStatsViewFacilityDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error("Empty SP result for Block Discounting statsViewByFacility");
            AFBDAcctStatsViewFacilityDTO afbdAcctStatsViewFacilityDTO = new AFBDAcctStatsViewFacilityDTO();
            afbdAcctStatsViewFacilityDTO.setNumberOfMasterAcct("0");
            return afbdAcctStatsViewFacilityDTO;
        }
        return resultBaseSpDto.getResults().get(0);
    }

    public AFBDAcctStatsViewAcctDTO statsViewByAcct(AFBDAcctStatsSpParam spParam) {
        BaseSpDto<AFBDAcctStatsSpParam, AFBDAcctStatsViewAcctDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_ACCT_STATS_VIEW_BY_ACCOUNT.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFBDAcctStatsViewAcctDTO.class);
        BaseSpDto<AFBDAcctStatsSpParam, AFBDAcctStatsViewAcctDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error("Empty SP result for Block Discounting statsViewByAcct");
            AFBDAcctStatsViewAcctDTO afbdAcctStatsViewAcctDTO = new AFBDAcctStatsViewAcctDTO();
            afbdAcctStatsViewAcctDTO.setNumberOfAcct("0");
            return afbdAcctStatsViewAcctDTO;
        }
        return resultBaseSpDto.getResults().get(0);
    }

    public AFBDMasterAcctDetailDTO masterAcctDetail(AFBDDetailSpParam spParam) {
        BaseSpDto<AFBDDetailSpParam, AFBDMasterAcctDetailDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_MASTER_ACCT_DTL.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFBDMasterAcctDetailDTO.class);
        BaseSpDto<AFBDDetailSpParam, AFBDMasterAcctDetailDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error("Empty SP result for Block Discounting masterAcctDetail");
            return new AFBDMasterAcctDetailDTO();
        }
        return resultBaseSpDto.getResults().get(0);
    }

    public AFBDAcctDetailDTO accountDetail(AFBDDetailSpParam spParam) {
        BaseSpDto<AFBDDetailSpParam, AFBDAcctDetailDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_ACCT_DTL_MY.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFBDAcctDetailDTO.class);
        BaseSpDto<AFBDDetailSpParam, AFBDAcctDetailDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error(ACCT_DETATIL_ERR_LOG);
            return new AFBDAcctDetailDTO();
        }
        return resultBaseSpDto.getResults().get(0);
    }

    public AFBDSubAcctDetailDTO agreementDetail(AFBDAgreementDetailSpParam spParam) {
        BaseSpDto<AFBDAgreementDetailSpParam, AFBDSubAcctDetailDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_AGREEMENT_DTL.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFBDSubAcctDetailDTO.class);
        BaseSpDto<AFBDAgreementDetailSpParam, AFBDSubAcctDetailDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error("Empty SP result for Block Discounting agreementDetail");
            return new AFBDSubAcctDetailDTO();
        }
        return resultBaseSpDto.getResults().get(0);
    }

    public AFBDDebitNoteDetailDTO debitDetail(AFBDDebitDetailSpParam spParam) {
        BaseSpDto<AFBDDebitDetailSpParam, AFBDDebitNoteDetailDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_DEBIT_DTL.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFBDDebitNoteDetailDTO.class);
        BaseSpDto<AFBDDebitDetailSpParam, AFBDDebitNoteDetailDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error("Empty SP result for Block Discounting debitDetail");
            return new AFBDDebitNoteDetailDTO();
        }
        return resultBaseSpDto.getResults().get(0);
    }

    public AFBDSGAcctDetailDTO accountDetailSG(AFBDDetailSpParam spParam) {
        BaseSpDto<AFBDDetailSpParam, AFBDSGAcctDetailDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_ACCT_DTL_SG.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFBDSGAcctDetailDTO.class);
        BaseSpDto<AFBDDetailSpParam, AFBDSGAcctDetailDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error(ACCT_DETATIL_ERR_LOG);
            return new AFBDSGAcctDetailDTO();
        }
        return resultBaseSpDto.getResults().get(0);
    }

    public AFBDSGSubAcctDetailDTO subAccountDetailSG(AFBDDetailSpParam spParam) {
        BaseSpDto<AFBDDetailSpParam, AFBDSGSubAcctDetailDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_SUB_ACCT_DTL_SG.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFBDSGSubAcctDetailDTO.class);
        BaseSpDto<AFBDDetailSpParam, AFBDSGSubAcctDetailDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            logger.error("Empty SP result for Block Discounting sub accountDetail");
            return new AFBDSGSubAcctDetailDTO();
        }
        return resultBaseSpDto.getResults().get(0);
    }

    public SearchCollectionResponse<AFBDTrxHistoryDTO> trxHistory(AFBDTrxHistorySpParam spParam) {
        BaseSpDto<AFBDTrxHistorySpParam, AFBDTrxHistoryDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_TRXN_HIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setResultClass(AFBDTrxHistoryDTO.class);
        BaseSpDto<AFBDTrxHistorySpParam, AFBDTrxHistoryDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        SearchCollectionResponse<AFBDTrxHistoryDTO> result = new SearchCollectionResponse<>();
        if (resultBaseSpDto.getResults() == null) {
            logger.error("Empty SP result for Block Discounting trxHistory");
            result.setTotalRecordCount(0);
            return result;
        }
        List<AFBDTrxHistoryDTO> spResultList = resultBaseSpDto.getResults().stream().toList();
        logger.info("trxHistory spResultList: {}", spResultList);
        result.setNextPage(spParam.getPage() >= resultBaseSpDto.getTotalPage() ? resultBaseSpDto.getTotalPage() : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(resultBaseSpDto.getTotalPage());
        result.setTotalRecordCount(resultBaseSpDto.getTotalRecord());
        result.setData(spResultList);

        return result;
    }

    public boolean getDisplayShowViewBy(AFBDAcctListSpParam spParam) {
        BaseSpDto<AFBDAcctListSpParam, AFBDDisplayViewBySpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_AF_BD_VIEW_BY.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(AFBDDisplayViewBySpDTO.class);

        BaseSpDto<AFBDAcctListSpParam, AFBDDisplayViewBySpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null) {
            logger.error("Empty SP result for AF BD getDisplayShowViewBy");
            return true;
        }
        return "Y".equals(resultBaseSpDto.getResults().get(0).getDisplayViewBy());
    }
}
