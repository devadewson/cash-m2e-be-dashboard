package com.m2e.dashboard.service.af.bd.jms.debitnote.balanceinquiry;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry.ESBAFBDDebitNoteInquiryMsgBodyReqDTO;
import com.m2e.dashboard.jms.af.bd.request.account.details.ESBAFBDDebitNoteDetailsReqDTO;
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

import static com.m2e.dashboard.enums.ESBServiceType.AF_BD_DEBIT_NOTE_BALANCE_INQUIRY_MY;


@Service
public class AFBDDebitNoteInquiryMYRequestHandler implements ESBRequestHandler<List<ESBAFBDDebitNoteDetailsReqDTO>, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(AFBDDebitNoteInquiryMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public AFBDDebitNoteInquiryMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }

    @Override
    public ESBReqDTO prepareRequest(List<ESBAFBDDebitNoteDetailsReqDTO> request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_BD_GETDEBITNOTEDETAILS_MY.getValue());
        log.info("Properties info for getActivityInquiry ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBAFBDDebitNoteInquiryMsgBodyReqDTO.builder()
                                .accountListing(request).build())
                        .build())
                .build();
    }

    @Override
    public String getServiceType() {
        return AF_BD_DEBIT_NOTE_BALANCE_INQUIRY_MY.name();
    }
}
