package com.m2e.dashboard.service.af.hp.jms.account.details;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.hp.request.account.balanceinquiry.ESBAFHPAcctBalanceInquiryMsgBodyReqDTO;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBRequestHandler;
import com.m2e.dashboard.service.af.hp.MockAFHPDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.m2e.dashboard.enums.ESBServiceType.AF_HP_MASTER_ACCT_DETAILS_MY;


@Service
public class AFHPMasterAcctDetailsMYRequestHandler implements ESBRequestHandler<ESBAcctListingReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(AFHPMasterAcctDetailsMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;
    private final MockAFHPDataService mockAFHPDataService;

    @Autowired
    public AFHPMasterAcctDetailsMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient, MockAFHPDataService mockAFHPDataService) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
        this.mockAFHPDataService = mockAFHPDataService;
    }

    @Override
    public ESBReqDTO prepareRequest(ESBAcctListingReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_HP_GETMASTERACCTBALANCEINQUIRY_MY.getValue());
        log.info("Properties info for getAcctDetails ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBAFHPAcctBalanceInquiryMsgBodyReqDTO.builder()
                                .accountListing(createESBAcctBalanceInquiryReqDTO(request)).build())
                        .build())
                .build();
    }

    public List<ESBAcctListingReqDTO> createESBAcctBalanceInquiryReqDTO(ESBAcctListingReqDTO request){
        return new ArrayList<>(Arrays.asList(
                ESBAcctListingReqDTO.builder()
                        .accountNumber(request.getAccountNumber())
                        .accountType(request.getAccountType())
                        .currency(request.getCurrency())
                        .region(request.getRegion()).build()));
    }

    @Override
    public String getServiceType() {
        return AF_HP_MASTER_ACCT_DETAILS_MY.name();
    }
}
