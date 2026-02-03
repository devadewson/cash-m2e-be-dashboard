package com.m2e.dashboard.service.loan.tlf.jms.account.details;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.dto.loan.tlf.request.LNTLFDetailReqDTO;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.jms.loan.tlf.request.account.balanceinquiry.ESBLoanTLFAcctBalanceInquiryMsgBodyReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.m2e.dashboard.enums.ESBServiceType.LOAN_TLF_ACCT_DETAILS_SG_SIBS_ISLAMIC;


@Service
public class LoanTLFAcctDetailsSGSIBSIslamicRequestHandler implements ESBRequestHandler<LNTLFDetailReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(LoanTLFAcctDetailsSGSIBSIslamicRequestHandler.class);
    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public LoanTLFAcctDetailsSGSIBSIslamicRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }

    @Override
    public ESBReqDTO prepareRequest(LNTLFDetailReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.LOAN_TLF_GETACCTDETAILS_SG_SIBS_ISLAMIC.getValue());
        log.info("Properties info for getAcctDetails ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBLoanTLFAcctBalanceInquiryMsgBodyReqDTO.builder()
                                .accountListing(createESBAcctDetailReqDTO(request)).build())
                        .build())
                .build();
    }

    public List<ESBAcctListingReqDTO> createESBAcctDetailReqDTO(LNTLFDetailReqDTO request){
        return new ArrayList<>(Collections.singletonList(
                ESBAcctListingReqDTO.builder()
                        .accountNumber(request.getAcctNumber())
                        .accountType("L")
                        .currency(request.getCurrency())
                        .region(request.getProvider()).build()));
    }

    @Override
    public String getServiceType() {
        return LOAN_TLF_ACCT_DETAILS_SG_SIBS_ISLAMIC.name();
    }
}
