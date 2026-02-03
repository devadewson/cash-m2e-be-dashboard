package com.m2e.dashboard.service.deposit.ca.jms;

import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.deposit.ca.request.DepositGPIStatusReqDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.activityinquiry.ESBDepositCAAcctActivityReqDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctDetailReqDTO;
import com.m2e.dashboard.dto.deposit.ca.response.*;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctListReqDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.details.ESBDepositCAFloatReqDTO;
import com.m2e.dashboard.service.esb.ESBClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static com.m2e.dashboard.enums.ESBServiceType.*;

@Service
public class DepositCAESBService {

    private static final Logger log = LoggerFactory.getLogger(DepositCAESBService.class);

    private final ESBClientService esbClientService;

    @Autowired
    public DepositCAESBService(ESBClientService esbClientService) {
        this.esbClientService = esbClientService;
    }

    public ApiResponse<DepositCAGPIStatusDTO> getGPIStatus(DepositGPIStatusReqDTO request) {
        return esbClientService.execute(request, DEPOSIT_CA_GPI_STATUS, DepositCAGPIStatusDTO::new, PropertyServiceEnum.ESBMYV23);
    }

    public ApiResponse<DepositAcctStatsDTO> getAccountStats_MY(ESBDepositCAAcctListReqDTO request) {
        ApiResponse<DepositAcctStatsDTO> resp = esbClientService.execute(request, DEPOSIT_CA_ACCT_STATS_MY, DepositAcctStatsDTO::new, PropertyServiceEnum.ESBMY);
        return resp;
    }

    public ApiResponse<DepositAcctStatsDTO> getAccountStats_SG(ESBDepositCAAcctListReqDTO request) {
        ApiResponse<DepositAcctStatsDTO> resp = esbClientService.execute(request, DEPOSIT_CA_ACCT_STATS_SG, DepositAcctStatsDTO::new, PropertyServiceEnum.ESBSG);
        return resp;
    }

    public ApiResponse<DepositAcctListingDTO> getAccountBalanceInquiry_MY(ESBDepositCAAcctListReqDTO request) {
        ApiResponse<DepositAcctListingDTO> resp = esbClientService.execute(request, DEPOSIT_CA_ACCT_BALANCE_INQUIRY_MY, DepositAcctListingDTO::new, PropertyServiceEnum.ESBMY);
        return resp;
    }

    public ApiResponse<DepositAcctListingDTO> getAccountBalanceInquiry_SG(ESBDepositCAAcctListReqDTO request) {
        ApiResponse<DepositAcctListingDTO> resp = esbClientService.execute(request, DEPOSIT_CA_ACCT_BALANCE_INQUIRY_SG, DepositAcctListingDTO::new, PropertyServiceEnum.ESBSG);
        return resp;
    }

    public ApiResponse<DepositCATrxHistoryDTO> getDepositCATrxHistory_MY(ESBDepositCAAcctActivityReqDTO request) {
        return esbClientService.execute(request, DEPOSIT_CA_ACCT_ACTIVITY_INQUIRY_MY, DepositCATrxHistoryDTO::new, PropertyServiceEnum.ESBMY);
    }
    public ApiResponse<DepositCATrxHistoryDTO> getDepositCATrxHistory_SG(ESBDepositCAAcctActivityReqDTO request) {
        return esbClientService.execute(request, DEPOSIT_CA_ACCT_ACTIVITY_INQUIRY_SG, DepositCATrxHistoryDTO::new, PropertyServiceEnum.ESBSG);
    }

    public ApiResponse<DepositAccountDetailDTO> getDepositCAAcctDetails_MY(ESBDepositCAAcctDetailReqDTO request) {
        return esbClientService.execute(request, DEPOSIT_CA_ACCT_DETAILS_MY, DepositAccountDetailDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<DepositAccountDetailDTO> getDepositCAAcctDetails_SG(ESBDepositCAAcctDetailReqDTO request) {
        return esbClientService.execute(request, DEPOSIT_CA_ACCT_DETAILS_SG, DepositAccountDetailDTO::new, PropertyServiceEnum.ESBSG);
    }

    public ApiResponse<DepositCAAcctOmnibusDetailDTO> getDepositCAAcctOmnibusDetails(ESBDepositCAAcctDetailReqDTO request) {
        return esbClientService.execute(request, DEPOSIT_CA_OMNIBUS_BALANCE_INQUIRY, DepositCAAcctOmnibusDetailDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<DepositAcctFloatDTO> getDepositCAAcctFloatDetails(ESBDepositCAFloatReqDTO request) {
        return esbClientService.execute(request, DEPOSIT_CA_ACCT_FLOAT_DETAILS, DepositAcctFloatDTO::new, PropertyServiceEnum.ESBMY);
    }
}
