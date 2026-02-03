package com.m2e.dashboard.service.af.pbr.jms;

import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.dto.af.response.pbr.*;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.common.ESBAFAcctActivityReqDTO;
import com.m2e.dashboard.jms.af.pbr.request.account.ESBPBRAcctListingReqDTO;
import com.m2e.dashboard.jms.af.pbr.request.account.balanceinquiry.ESBAFPBRAcctListReqDTO;
import com.m2e.dashboard.jms.af.pbr.request.account.details.ESBPBRUnitListingReqDTO;
import com.m2e.dashboard.service.esb.ESBClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.m2e.dashboard.enums.ESBServiceType.*;

@Service
public class AFPBRESBService {

    private static final Logger logger = LoggerFactory.getLogger(AFPBRESBService.class);

    private final ESBClientService esbClientService;

    @Autowired
    public AFPBRESBService(ESBClientService esbClientService) {
        this.esbClientService = esbClientService;
    }

    public ApiResponse<AFAcctListESBDTO> getAFPBRAccountBalanceInquiry(ESBAFPBRAcctListReqDTO<AFPBRViewByAccountDTO> request) {
        logger.info("getAFPBRAccountBalanceInquiry");
        return esbClientService.execute(request, AF_PBR_ACCT_BALANCE_INQUIRY_MY, AFAcctListESBDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFAcctListESBDTO> getAFPBRMasterAccountBalanceInquiry(ESBAFPBRAcctListReqDTO<AFPBRViewByFacilityDTO> request) {
        logger.info("getAFPBRMasterAccountBalanceInquiry");
        return esbClientService.execute(request, AF_PBR_MASTER_BALANCE_INQUIRY_MY, AFAcctListESBDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFAcctListESBDTO> getAFPBRUnitBalanceInquiry(ESBAFPBRAcctListReqDTO<AFPBRUnitSummaryDTO> request) {
        logger.info("getAFPBRUnitBalanceInquiry");
        return esbClientService.execute(request, AF_PBR_UNIT_BALANCE_INQUIRY_MY, AFAcctListESBDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFPBRTrxHistorySummaryDTO> getAFPBRTrxHistory(ESBAFAcctActivityReqDTO request) {
        logger.info("getAFPBRTrxHistory");
        return esbClientService.execute(request, AF_PBR_ACCT_ACTIVITY_INQUIRY_MY, AFPBRTrxHistorySummaryDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFPBRMasterAccountDetailDTO> getAFPBRAcctDetails(ESBPBRAcctListingReqDTO request) {
        logger.info("getAFPBRAcctDetails");
        return esbClientService.execute(request, AF_PBR_ACCT_DETAILS_MY, AFPBRMasterAccountDetailDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFPBRSubaccountDetailDTO> getAFPBRUnitDetails(ESBPBRUnitListingReqDTO request) {
        logger.info("getAFPBRUnitDetails");
        return esbClientService.execute(request, AF_PBR_UNIT_DETAILS_MY, AFPBRSubaccountDetailDTO::new, PropertyServiceEnum.ESBMY);
    }
}
