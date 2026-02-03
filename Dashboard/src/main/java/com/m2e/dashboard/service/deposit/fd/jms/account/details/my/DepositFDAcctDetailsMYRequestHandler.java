package com.m2e.dashboard.service.deposit.fd.jms.account.details.my;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctBalanceInquiryMsgBodyReqDTO;
import com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry.ESBDepositFDAcctDetailReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.m2e.dashboard.enums.ESBServiceType.DEPOSIT_FD_ACCT_DETAILS_MY;

@Service
public class DepositFDAcctDetailsMYRequestHandler implements ESBRequestHandler<ESBDepositFDAcctDetailReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(DepositFDAcctDetailsMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public DepositFDAcctDetailsMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }
    @Override
    public ESBReqDTO prepareRequest(ESBDepositFDAcctDetailReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_FD_GETACCTBALANCEINQUIRY_MY.getValue());
        log.info("Properties info for FD getAcctDetails ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBDepositCAAcctBalanceInquiryMsgBodyReqDTO.builder()
                                .accountListing(createESBAcctBalanceInquiryReqDTO(request)).build())
                        .build())
                .build();
    }

    public List<ESBAcctListingReqDTO> createESBAcctBalanceInquiryReqDTO(ESBDepositFDAcctDetailReqDTO request){
        return new ArrayList<>(Arrays.asList(
                ESBAcctListingReqDTO.builder()
                        .accountNumber(request.getAccountNo())
                        .accountType("T")
                        .currency(request.getAccountCurrency())
                        .region(request.getAccountRegion()).build()));
    }

    @Override
    public String getServiceType() {
        return DEPOSIT_FD_ACCT_DETAILS_MY.name();
    }
}
