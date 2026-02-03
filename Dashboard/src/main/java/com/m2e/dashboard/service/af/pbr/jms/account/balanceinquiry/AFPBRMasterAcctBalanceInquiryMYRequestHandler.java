package com.m2e.dashboard.service.af.pbr.jms.account.balanceinquiry;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRViewByFacilityDTO;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.pbr.request.account.ESBPBRAcctListingReqDTO;
import com.m2e.dashboard.jms.af.pbr.request.account.balanceinquiry.ESBAFPBRAcctBalanceInquiryMsgBodyReqDTO;
import com.m2e.dashboard.jms.af.pbr.request.account.balanceinquiry.ESBAFPBRAcctListReqDTO;
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

import static com.m2e.dashboard.enums.ESBServiceType.AF_PBR_MASTER_BALANCE_INQUIRY_MY;

@Service
public class AFPBRMasterAcctBalanceInquiryMYRequestHandler implements ESBRequestHandler<ESBAFPBRAcctListReqDTO<AFPBRViewByFacilityDTO>, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(AFPBRMasterAcctBalanceInquiryMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public AFPBRMasterAcctBalanceInquiryMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }

    @Override
    public ESBReqDTO prepareRequest(ESBAFPBRAcctListReqDTO<AFPBRViewByFacilityDTO> request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_PBR_GETACCTDETAILS_MY.getValue());
        log.info("Properties info for getActivityInquiry ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBAFPBRAcctBalanceInquiryMsgBodyReqDTO.builder()
                                .accountListing(convertToESBAcctListingReqDTO(request)).build())
                        .build())
                .build();
    }

    private List<ESBPBRAcctListingReqDTO> convertToESBAcctListingReqDTO(ESBAFPBRAcctListReqDTO<AFPBRViewByFacilityDTO> summaryDTO) {
        List<AFPBRViewByFacilityDTO> facilityDTOS = summaryDTO.getDatas();
        return facilityDTOS.stream().map(spDTO ->
                ESBPBRAcctListingReqDTO.builder()
                        .accountNumber(spDTO.getMasterAcctNumber())
                        .accountType(summaryDTO.getAcctType())
                        .currency(spDTO.getAccountCurrency())
                        .region(spDTO.getProvider())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public String getServiceType() {
        return AF_PBR_MASTER_BALANCE_INQUIRY_MY.name();
    }
}
