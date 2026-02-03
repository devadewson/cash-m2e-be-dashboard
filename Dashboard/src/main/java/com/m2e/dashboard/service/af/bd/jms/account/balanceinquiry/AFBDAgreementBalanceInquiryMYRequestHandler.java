package com.m2e.dashboard.service.af.bd.jms.account.balanceinquiry;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry.ESBAFBDAgreementInquiryMsgBodyReqDTO;
import com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry.ESBAFBDAgreementListReqDTO;
import com.m2e.dashboard.jms.af.bd.request.account.details.ESBAFBDAgreementDetailsReqDTO;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.m2e.dashboard.enums.ESBServiceType.AF_BD_AGREEMENT_BALANCE_INQUIRY_MY;

@Service
public class AFBDAgreementBalanceInquiryMYRequestHandler implements ESBRequestHandler<ESBAFBDAgreementListReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(AFBDAgreementBalanceInquiryMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public AFBDAgreementBalanceInquiryMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }

    @Override
    public ESBReqDTO prepareRequest(ESBAFBDAgreementListReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_BD_GETAGREEMENTDETAILS_MY.getValue());
        log.info("Properties info for getActivityInquiry ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBAFBDAgreementInquiryMsgBodyReqDTO.builder()
                                .accountListing(convertToESBAcctListingReqDTO(request)).build())
                        .build())
                .build();
    }

    private List<ESBAFBDAgreementDetailsReqDTO> convertToESBAcctListingReqDTO(ESBAFBDAgreementListReqDTO summaryDTO) {
        return summaryDTO.getDatas().stream().map(spDTO ->
                ESBAFBDAgreementDetailsReqDTO.builder()
                        .accountNumber(summaryDTO.getMasterAccountNumber())
                        .accountType(summaryDTO.getAcctType())
                        .currency(spDTO.getCurrency())
                        .region(spDTO.getProvider())
                        .agreementNumber(spDTO.getAgreementNumber())
                        .blockNumber(summaryDTO.getBlockNumber())
                        .build()
        ).toList();
    }

    @Override
    public String getServiceType() {
        return AF_BD_AGREEMENT_BALANCE_INQUIRY_MY.name();
    }
}
