package com.m2e.dashboard.service.af.bd.jms.account.details;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry.ESBAFBDAgreementInquiryMsgBodyReqDTO;
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

import java.util.ArrayList;
import java.util.List;

import static com.m2e.dashboard.enums.ESBServiceType.AF_BD_AGREEMENT_DETAILS_MY;


@Service
public class AFBDAgreementDetailsMYRequestHandler implements ESBRequestHandler<ESBAFBDAgreementDetailsReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(AFBDAgreementDetailsMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public AFBDAgreementDetailsMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }

    @Override
    public ESBReqDTO prepareRequest(ESBAFBDAgreementDetailsReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_BD_GETAGREEMENTDETAILS_MY.getValue());
        log.info("Properties info for getAcctDetails ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBAFBDAgreementInquiryMsgBodyReqDTO.builder()
                                .accountListing(convertToESBAgreementListingReqDTO(request)).build())
                        .build())
                .build();
    }

    private List<ESBAFBDAgreementDetailsReqDTO> convertToESBAgreementListingReqDTO(ESBAFBDAgreementDetailsReqDTO summaryDTO) {
        List<ESBAFBDAgreementDetailsReqDTO> list = new ArrayList<>();
        list.add(summaryDTO);
        return list;
    }

    @Override
    public String getServiceType() {
        return AF_BD_AGREEMENT_DETAILS_MY.name();
    }
}
