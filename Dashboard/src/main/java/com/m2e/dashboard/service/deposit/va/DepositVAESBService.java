//package com.m2e.dashboard.service.deposit.va;
//
//import com.m2e.dashboard.dto.common.ApiResponse;
//import com.m2e.dashboard.dto.deposit.va.response.DepositVATrxHistoryDTO;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.deposit.ca.request.account.activityinquiry.ESBDepositCAAcctActivityReqDTO;
//import com.m2e.dashboard.service.esb.ESBClientService;
//import com.m2e.dashboard.service.deposit.ca.jms.DepositCAESBService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import static com.m2e.dashboard.enums.ESBServiceType.DEPOSIT_VA_ACCT_ACTIVITY_INQUIRY_MY;
//
//@Service
//public class DepositVAESBService {
//    private static final Logger log = LoggerFactory.getLogger(DepositCAESBService.class);
//
//    private final ESBClientService esbClientService;
//
//    @Autowired
//    public DepositVAESBService(ESBClientService esbClientService) {
//        this.esbClientService = esbClientService;
//    }
//
//    public ApiResponse<DepositVATrxHistoryDTO> getDepositVATrxHistory_MY(ESBDepositCAAcctActivityReqDTO request) {
//        return esbClientService.execute(request, DEPOSIT_VA_ACCT_ACTIVITY_INQUIRY_MY, DepositVATrxHistoryDTO::new, PropertyServiceEnum.ESBMY);
//    }
//}
