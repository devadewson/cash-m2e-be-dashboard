package com.m2e.dashboard.service.af.hp.jms.account.balanceinquiry;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.hp.request.account.balanceinquiry.ESBAFHPAcctBalanceInquiryMsgBodyReqDTO;
import com.m2e.dashboard.jms.af.hp.request.account.balanceinquiry.ESBAFHPAcctListReqDTO;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.m2e.dashboard.enums.ESBServiceType.AF_HP_ACCT_BALANCE_INQUIRY_MY;

@Service
public class AFHPAcctBalanceInquiryMYRequestHandler implements ESBRequestHandler<ESBAFHPAcctListReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(AFHPAcctBalanceInquiryMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public AFHPAcctBalanceInquiryMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }

    @Override
    public ESBReqDTO prepareRequest(ESBAFHPAcctListReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_HP_GETACCTBALANCEINQUIRY_MY.getValue());
        log.info("Properties info for getActivityInquiry ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBAFHPAcctBalanceInquiryMsgBodyReqDTO.builder()
                                .accountListing(convertToESBAcctListingReqDTO(request)).build())
                        .build())
                .build();
    }

    private List<ESBAcctListingReqDTO> convertToESBAcctListingReqDTO(ESBAFHPAcctListReqDTO summaryDTO) {
        return summaryDTO.getDatas().stream().map(spDTO ->
                ESBAcctListingReqDTO.builder()
                        .accountNumber(spDTO.getAcctNumber())
                        .accountType(summaryDTO.getAcctType())
                        .currency(spDTO.getCurrency())
                        .region(spDTO.getProvider())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public String getServiceType() {
        return AF_HP_ACCT_BALANCE_INQUIRY_MY.name();
    }
}
