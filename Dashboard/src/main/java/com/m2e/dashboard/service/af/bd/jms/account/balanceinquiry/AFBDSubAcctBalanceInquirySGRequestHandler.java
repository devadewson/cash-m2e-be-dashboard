package com.m2e.dashboard.service.af.bd.jms.account.balanceinquiry;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry.ESBAFBDSubAcctBalanceInquirySGMsgBodyReqDTO;
import com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry.ESBAFBDSubAcctListSGReqDTO;
import com.m2e.dashboard.jms.af.bd.request.account.details.ESBAFBDSubAcctDetailsSGReqDTO;
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

import static com.m2e.dashboard.enums.ESBServiceType.AF_BD_SUB_ACCT_BALANCE_INQUIRY_SG;

@Service
public class AFBDSubAcctBalanceInquirySGRequestHandler implements ESBRequestHandler<ESBAFBDSubAcctListSGReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(AFBDSubAcctBalanceInquirySGRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public AFBDSubAcctBalanceInquirySGRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }

    @Override
    public ESBReqDTO prepareRequest(ESBAFBDSubAcctListSGReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_BD_GETSUBACCTDETAILS_SG.getValue());
        log.info("Properties info for BD Sub account getAcctBalanceInquiry ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBAFBDSubAcctBalanceInquirySGMsgBodyReqDTO.builder()
                                .accountListing(convertToESBAcctListingReqDTO(request)).build())
                        .build())
                .build();
    }

    private List<ESBAFBDSubAcctDetailsSGReqDTO> convertToESBAcctListingReqDTO(ESBAFBDSubAcctListSGReqDTO summaryDTO) {
        return summaryDTO.getDatas().stream().map(spDTO ->
                ESBAFBDSubAcctDetailsSGReqDTO.builder()
                        .accountNumber(summaryDTO.getMasterAccountNumber())
                        .subAccount(spDTO.getBlockNumber())
                        .accountType(summaryDTO.getAcctType())
                        .currency(spDTO.getCurrency())
                        .region(spDTO.getProvider())
                        .build()
        ).toList();
    }

    @Override
    public String getServiceType() {
        return AF_BD_SUB_ACCT_BALANCE_INQUIRY_SG.name();
    }
}
