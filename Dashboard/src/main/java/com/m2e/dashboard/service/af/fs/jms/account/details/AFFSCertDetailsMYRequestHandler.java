package com.m2e.dashboard.service.af.fs.jms.account.details;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.fs.request.account.details.ESBAFFSCertDetailsReqDTO;
import com.m2e.dashboard.jms.af.fs.request.account.details.ESBAFFSCertInquiryMsgBodyReqDTO;
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

import static com.m2e.dashboard.enums.ESBServiceType.AF_FS_CERT_DETAILS_MY;

@Service
public class AFFSCertDetailsMYRequestHandler implements ESBRequestHandler<List<ESBAFFSCertDetailsReqDTO>, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(AFFSCertDetailsMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public AFFSCertDetailsMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }

    @Override
    public ESBReqDTO prepareRequest(List<ESBAFFSCertDetailsReqDTO> listOfCertRequest) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_FS_GETCERTDETAILS_MY.getValue());
        log.info("Properties info for getAcctDetails ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                    .messageHeader(esbClient.constructMsgHeader(configProperties))
                    .messageBody(ESBAFFSCertInquiryMsgBodyReqDTO.builder()
                            .accountListing(listOfCertRequest).build())
                        .build())
                .build();
    }
    @Override
    public String getServiceType() {
        return AF_FS_CERT_DETAILS_MY.name();
    }
}
