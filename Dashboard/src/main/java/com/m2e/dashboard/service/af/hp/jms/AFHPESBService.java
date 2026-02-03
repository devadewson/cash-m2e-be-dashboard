package com.m2e.dashboard.service.af.hp.jms;

import com.m2e.dashboard.dto.af.response.hp.AFHPAcctDetailDTO;
import com.m2e.dashboard.dto.af.response.hp.AFHPMasterAcctDetailDTO;
import com.m2e.dashboard.dto.af.response.hp.AFHPSGAcctDetailDTO;
import com.m2e.dashboard.dto.af.response.hp.AFHPTrxHistoryResp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.jms.af.common.ESBAFAcctActivityReqDTO;
import com.m2e.dashboard.jms.af.hp.request.account.balanceinquiry.ESBAFHPAcctListReqDTO;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import com.m2e.dashboard.service.esb.ESBClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.m2e.dashboard.enums.ESBServiceType.*;

@Service
public class AFHPESBService {

    private static final Logger logger = LoggerFactory.getLogger(AFHPESBService.class);

    private final ESBClientService esbClientService;

    @Autowired
    public AFHPESBService(ESBClientService esbClientService) {
        this.esbClientService = esbClientService;
    }

    public ApiResponse<AFAcctListESBDTO> getAFHPAccountBalanceInquiry(ESBAFHPAcctListReqDTO request) {
        logger.info("getAFHPAccountBalanceInquiry");
        return esbClientService.execute(request, AF_HP_ACCT_BALANCE_INQUIRY_MY, AFAcctListESBDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFAcctListESBDTO> getAFHPMasterAccountBalanceInquiry(ESBAFHPAcctListReqDTO request) {
        logger.info("getAFHPMasterAccountBalanceInquiry");
        return esbClientService.execute(request, AF_BHP_ACCT_BALANCE_INQUIRY_MY, AFAcctListESBDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFHPTrxHistoryResp> getAFHPTrxHistory(ESBAFAcctActivityReqDTO request) {
        logger.info("getAFHPTrxHistory");
        return esbClientService.execute(request, AF_HP_ACCT_ACTIVITY_INQUIRY_MY, AFHPTrxHistoryResp::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFHPAcctDetailDTO> getAFHPAcctDetails(ESBAcctListingReqDTO request) {
        logger.info("getAFHPAcctDetails");
        return esbClientService.execute(request, AF_HP_ACCT_DETAILS_MY, AFHPAcctDetailDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFHPMasterAcctDetailDTO> getAFHPMasterAcctDetails(ESBAcctListingReqDTO request) {
        logger.info("getAFHPMasterAcctDetails");
        return esbClientService.execute(request, AF_HP_MASTER_ACCT_DETAILS_MY, AFHPMasterAcctDetailDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFHPSGAcctDetailDTO> getAFHPSGAcctDetails(ESBAcctListingReqDTO request) {
        logger.info("getAFHPAcctDetails for SG");
        return esbClientService.execute(request, AF_HP_ACCT_DETAILS_SG, AFHPSGAcctDetailDTO::new, PropertyServiceEnum.ESBSG);
    }

    public ApiResponse<AFAcctListESBDTO> getAFHPSGAccountBalanceInquiry(ESBAFHPAcctListReqDTO request) {
        logger.info("getAFHPSGAccountBalanceInquiry");
        return esbClientService.execute(request, AF_HP_ACCT_BALANCE_INQUIRY_SG, AFAcctListESBDTO::new, PropertyServiceEnum.ESBSG);
    }
}
