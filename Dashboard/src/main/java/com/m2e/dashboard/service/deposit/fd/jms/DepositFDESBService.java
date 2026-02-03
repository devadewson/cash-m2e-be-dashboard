package com.m2e.dashboard.service.deposit.fd.jms;

import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.deposit.fd.response.DepositFDAcctStatsDTO;
import com.m2e.dashboard.dto.deposit.fd.response.DepositFDAcctSummaryDTO;
import com.m2e.dashboard.dto.deposit.fd.response.DepositFDCertificateListDTO;
import com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry.ESBDepositFDAcctDetailReqDTO;
import com.m2e.dashboard.dto.deposit.fd.response.DepositFDAccountDetailDTO;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry.ESBDepositFDAcctListReqDTO;
import com.m2e.dashboard.jms.deposit.fd.request.account.certdetails.ESBDepositFDCertsReqDTO;
import com.m2e.dashboard.service.esb.ESBClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.m2e.dashboard.enums.ESBServiceType.*;

@Service
public class DepositFDESBService {
    private static final Logger log = LoggerFactory.getLogger(DepositFDESBService.class);
    private final ESBClientService esbClientService;

    @Autowired
    public DepositFDESBService(ESBClientService esbClientService) {
        this.esbClientService = esbClientService;
    }

    public ApiResponse<DepositFDAcctStatsDTO> getAccountStats_MY(ESBDepositFDAcctListReqDTO request) {
        ApiResponse<DepositFDAcctStatsDTO> resp = esbClientService.execute(request, DEPOSIT_FD_ACCT_STATS_MY, DepositFDAcctStatsDTO::new, PropertyServiceEnum.ESBMY);
        return resp;
    }

    public ApiResponse<DepositFDAcctStatsDTO> getAccountStats_SG(ESBDepositFDAcctListReqDTO request) {
        ApiResponse<DepositFDAcctStatsDTO> resp = esbClientService.execute(request, DEPOSIT_FD_ACCT_STATS_SG, DepositFDAcctStatsDTO::new, PropertyServiceEnum.ESBSG);
        return resp;
    }

    public ApiResponse<DepositFDAcctSummaryDTO> getDepositFDAccountListESB_MY(ESBDepositFDAcctListReqDTO request) {
        ApiResponse<DepositFDAcctSummaryDTO> resp = esbClientService.execute(request, DEPOSIT_FD_ACCT_BALANCE_INQUIRY_MY, DepositFDAcctSummaryDTO::new, PropertyServiceEnum.ESBMY);
        return resp;
    }

    public ApiResponse<DepositFDAcctSummaryDTO> getDepositFDAccountListESB_SG(ESBDepositFDAcctListReqDTO request) {
        ApiResponse<DepositFDAcctSummaryDTO> resp = esbClientService.execute(request, DEPOSIT_FD_ACCT_BALANCE_INQUIRY_SG, DepositFDAcctSummaryDTO::new, PropertyServiceEnum.ESBSG);
        return resp;
    }

    public ApiResponse<DepositFDCertificateListDTO> getDepositFDCertDtlESB_MY(ESBDepositFDCertsReqDTO request) {
        ApiResponse<DepositFDCertificateListDTO> resp = esbClientService.execute(request, DEPOSIT_FD_ACCT_CERT_DETAIL_INQUIRY_MY, DepositFDCertificateListDTO::new, PropertyServiceEnum.ESBMY);
        return resp;
    }

    public ApiResponse<DepositFDCertificateListDTO> getDepositFDCertDtlESB_SG(ESBDepositFDCertsReqDTO request) {
        ApiResponse<DepositFDCertificateListDTO> resp = esbClientService.execute(request, DEPOSIT_FD_ACCT_CERT_DETAIL_INQUIRY_SG, DepositFDCertificateListDTO::new, PropertyServiceEnum.ESBSG);
        return resp;
    }

    public ApiResponse<DepositFDAccountDetailDTO> getDepositFDAcctDetailESB_MY(ESBDepositFDAcctDetailReqDTO request) {
        ApiResponse<DepositFDAccountDetailDTO> resp = esbClientService.execute(request, DEPOSIT_FD_ACCT_DETAILS_MY, DepositFDAccountDetailDTO::new, PropertyServiceEnum.ESBMY);
        return resp;
    }

    public ApiResponse<DepositFDAccountDetailDTO> getDepositFDAcctDetailESB_SG(ESBDepositFDAcctDetailReqDTO request) {
        ApiResponse<DepositFDAccountDetailDTO> resp = esbClientService.execute(request, DEPOSIT_FD_ACCT_DETAILS_SG, DepositFDAccountDetailDTO::new, PropertyServiceEnum.ESBSG);
        return resp;
    }
}
