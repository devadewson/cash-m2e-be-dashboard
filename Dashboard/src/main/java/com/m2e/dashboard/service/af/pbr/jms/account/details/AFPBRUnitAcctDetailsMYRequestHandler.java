package com.m2e.dashboard.service.af.pbr.jms.account.details;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.pbr.request.account.balanceinquiry.ESBAFPBRUnitBalanceInquiryMsgBodyReqDTO;
import com.m2e.dashboard.jms.af.pbr.request.account.details.ESBPBRUnitListingReqDTO;
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

import static com.m2e.dashboard.enums.ESBServiceType.AF_PBR_UNIT_DETAILS_MY;


@Service
public class AFPBRUnitAcctDetailsMYRequestHandler implements ESBRequestHandler<ESBPBRUnitListingReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(AFPBRUnitAcctDetailsMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public AFPBRUnitAcctDetailsMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }

    @Override
    public ESBReqDTO prepareRequest(ESBPBRUnitListingReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_PBR_GETUNITDETAILS_MY.getValue());
        log.info("Properties info for getAcctDetails ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBAFPBRUnitBalanceInquiryMsgBodyReqDTO.builder()
                                .accountListing(convertToESBAcctListingReqDTO(request)).build())
                        .build())
                .build();
    }

    private List<ESBPBRUnitListingReqDTO> convertToESBAcctListingReqDTO(ESBPBRUnitListingReqDTO summaryDTO) {
        List<ESBPBRUnitListingReqDTO> list = new ArrayList<>();
        list.add(ESBPBRUnitListingReqDTO.builder()
                .accountNumber(summaryDTO.getAccountNumber())
                .unitNumber(summaryDTO.getUnitNumber())
                .accountType(summaryDTO.getAccountType())
                .currency(summaryDTO.getCurrency())
                .region(summaryDTO.getRegion())
                .build());
        return list;
    }

    @Override
    public String getServiceType() {
        return AF_PBR_UNIT_DETAILS_MY.name();
    }
}
