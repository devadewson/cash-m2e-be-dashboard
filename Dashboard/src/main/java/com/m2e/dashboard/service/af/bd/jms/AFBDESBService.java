package com.m2e.dashboard.service.af.bd.jms;

import com.m2e.dashboard.dto.af.response.bd.*;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry.*;
import com.m2e.dashboard.jms.af.bd.request.account.details.*;
import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.jms.af.common.ESBAFAcctActivityReqDTO;
import com.m2e.dashboard.service.esb.ESBClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.m2e.dashboard.enums.ESBServiceType.*;

@Service
public class AFBDESBService {

    private static final Logger logger = LoggerFactory.getLogger(AFBDESBService.class);

    private final ESBClientService esbClientService;

    @Autowired
    public AFBDESBService(ESBClientService esbClientService) {
        this.esbClientService = esbClientService;
    }

    public ApiResponse<AFAcctListESBDTO> getAFBDAccountBalanceInquiry(ESBAFBDAcctListReqDTO request) {
        logger.info("getAFBDAccountBalanceInquiry");
        return esbClientService.execute(request, AF_BD_ACCT_BALANCE_INQUIRY_MY, AFAcctListESBDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFBDTrxHistorySummaryDTO> getAFBDTrxHistory(ESBAFAcctActivityReqDTO request) {
        logger.info("getAFBDTrxHistory");
        return esbClientService.execute(request, AF_BD_ACCT_ACTIVITY_INQUIRY_MY, AFBDTrxHistorySummaryDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFBDAcctDetailDTO> getAFBDBlockDetails(ESBAFBDBlockDetailsReqDTO request) {
        logger.info("getAFBDBlockDetails");
        return esbClientService.execute(request, AF_BD_BLOCK_DETAILS_MY, AFBDAcctDetailDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFAcctListESBDTO> getAFBDMasterAccountBalanceInquiry(ESBAFBDFacilityListReqDTO request) {
        logger.info("getAFBDFacilityBalanceInquiry");
        return esbClientService.execute(request, AF_BD_MASTER_ACCT_BALANCE_INQUIRY_MY, AFAcctListESBDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFAcctListESBDTO> getAFBDDebitNoteInquiry(List<ESBAFBDDebitNoteDetailsReqDTO> request) {
        logger.info("getAFBDDebitNoteInquiry");
        return esbClientService.execute(request, AF_BD_DEBIT_NOTE_BALANCE_INQUIRY_MY, AFAcctListESBDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFAcctListESBDTO> getAFBDAccountSubListInquiry(ESBAFBDAgreementListReqDTO request) {
        logger.info("getAFBDAccountSubListInquiry");
        return esbClientService.execute(request, AF_BD_AGREEMENT_BALANCE_INQUIRY_MY, AFAcctListESBDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFAcctListESBDTO> getAFBDMasterSubListInquiry(ESBAFBDBlockListReqDTO request) {
        logger.info("getAFBDMasterSubListInquiry");
        return esbClientService.execute(request, AF_BD_BLOCK_BALANCE_INQUIRY_MY, AFAcctListESBDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFBDMasterAcctDetailDTO> getAFBDMasterAcctDetails(ESBAFBDAcctDetailsReqDTO request) {
        logger.info("getAFBDMasterAcctDetails");
        return esbClientService.execute(request, AF_BD_ACCT_DETAILS_MY, AFBDMasterAcctDetailDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFBDDebitNoteDetailDTO> getAFBDDebitNoteDetails(ESBAFBDDebitNoteDetailsReqDTO request) {
        logger.info("getAFBDDebitNoteDetails");
        return esbClientService.execute(request, AF_BD_DEBIT_NOTE_DETAILS_MY, AFBDDebitNoteDetailDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFBDSubAcctDetailDTO> getAFBDAgreementDetails(ESBAFBDAgreementDetailsReqDTO request) {
        logger.info("getAFBDAgreementDetails");
        return esbClientService.execute(request, AF_BD_AGREEMENT_DETAILS_MY, AFBDSubAcctDetailDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFAcctListESBDTO> getAFBDSubAccountBalanceInquirySG(ESBAFBDSubAcctListSGReqDTO request) {
        logger.info("getAFBDAccountBalanceInquirySG");
        return esbClientService.execute(request, AF_BD_SUB_ACCT_BALANCE_INQUIRY_SG, AFAcctListESBDTO::new, PropertyServiceEnum.ESBSG);
    }

    public ApiResponse<AFAcctListESBDTO> getAFBDAccountBalanceInquirySG(ESBAFBDAcctListReqDTO request) {
        logger.info("getAFBDAccountBalanceInquirySG");
        return esbClientService.execute(request, AF_BD_ACCT_BALANCE_INQUIRY_SG, AFAcctListESBDTO::new, PropertyServiceEnum.ESBSG);
    }

    public ApiResponse<AFBDSGAcctDetailDTO> getAFBDMasterAcctDetailsSG(ESBAFBDAcctDetailsReqDTO request) {
        logger.info("getAFBDMasterAcctDetailsSG");
        return esbClientService.execute(request, AF_BD_MASTER_ACCT_DETAILS_SG, AFBDSGAcctDetailDTO::new, PropertyServiceEnum.ESBSG);
    }

    public ApiResponse<AFBDSGSubAcctDetailDTO> getAFBDAcctDetailsSG(ESBAFBDSubAcctDetailsSGReqDTO request) {
        logger.info("getAFBDAcctDetailsSG");
        return esbClientService.execute(request, AF_BD_ACCT_DETAILS_SG, AFBDSGSubAcctDetailDTO::new, PropertyServiceEnum.ESBSG);
    }
}
