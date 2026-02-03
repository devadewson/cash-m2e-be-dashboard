package com.m2e.dashboard.service.af.pbr.jms.account.balanceinquiry;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRUnitSummaryDTO;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.enums.ProviderEnum;
import com.m2e.dashboard.jms.af.pbr.request.account.balanceinquiry.ESBAFPBRAcctListReqDTO;
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

import java.util.List;
import java.util.stream.Collectors;

import static com.m2e.dashboard.enums.ESBServiceType.AF_PBR_UNIT_BALANCE_INQUIRY_MY;

@Service
public class AFPBRUnitBalanceInquiryMYRequestHandler implements ESBRequestHandler<ESBAFPBRAcctListReqDTO<AFPBRUnitSummaryDTO>, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(AFPBRUnitBalanceInquiryMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public AFPBRUnitBalanceInquiryMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }

    @Override
    public ESBReqDTO prepareRequest(ESBAFPBRAcctListReqDTO<AFPBRUnitSummaryDTO> request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_PBR_GETUNITDETAILS_MY.getValue());
        log.info("Properties info for getActivityInquiry ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBAFPBRUnitBalanceInquiryMsgBodyReqDTO.builder()
                                .accountListing(convertToESBAcctListingReqDTO(request)).build())
                        .build())
                .build();
    }

    private List<ESBPBRUnitListingReqDTO> convertToESBAcctListingReqDTO(ESBAFPBRAcctListReqDTO<AFPBRUnitSummaryDTO> summaryDTO) {
        List<AFPBRUnitSummaryDTO> unitSummaryDTOS = summaryDTO.getDatas();
        return unitSummaryDTOS.stream().map(spDTO ->
                ESBPBRUnitListingReqDTO.builder()
                        .accountNumber(spDTO.getMasterAcctNumber())
                        .unitNumber(spDTO.getUnitNumber())
                        .accountType(summaryDTO.getAcctType())
                        .currency(spDTO.getAccountCurrency())
                        .region(ProviderEnum.MY.name())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public String getServiceType() {
        return AF_PBR_UNIT_BALANCE_INQUIRY_MY.name();
    }
}
