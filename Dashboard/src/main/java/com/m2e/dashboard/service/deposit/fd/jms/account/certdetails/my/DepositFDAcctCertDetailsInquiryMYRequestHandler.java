package com.m2e.dashboard.service.deposit.fd.jms.account.certdetails.my;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.jms.deposit.fd.request.account.certdetails.ESBDepositFDCertDetailsReqDTO;
import com.m2e.dashboard.jms.deposit.fd.request.account.certdetails.ESBDepositFDCertsReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.m2e.dashboard.enums.ESBServiceType.DEPOSIT_FD_ACCT_CERT_DETAIL_INQUIRY_MY;

@Service
public class DepositFDAcctCertDetailsInquiryMYRequestHandler implements ESBRequestHandler<ESBDepositFDCertsReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(DepositFDAcctCertDetailsInquiryMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public DepositFDAcctCertDetailsInquiryMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }
    @Override
    public ESBReqDTO prepareRequest(ESBDepositFDCertsReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_FD_GETCERTDETAILS_MY.getValue());
        log.info("Properties info for FD getCertDetailsInquiry MY ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(createESBAcctBalanceInquiryReqDTO(request)).build())
                .build();
    }

    public ESBDepositFDCertDetailsReqDTO createESBAcctBalanceInquiryReqDTO(ESBDepositFDCertsReqDTO request) {
        return ESBDepositFDCertDetailsReqDTO.builder()
                .accountNo(request.getAccountNo())
                .accountType("T")
                .currency(request.getCurrency())
                .mbbLastKey("0")
                .build();
    }

    @Override
    public String getServiceType() {
        return DEPOSIT_FD_ACCT_CERT_DETAIL_INQUIRY_MY.name();
    }
}
