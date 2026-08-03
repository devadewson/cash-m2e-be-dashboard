//package com.m2e.dashboard.service.deposit.ca.jms.account.balanceinquiry.sg;
//
//import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
//import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBReqDTO;
//import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctBalanceInquiryMsgBodyReqDTO;
//import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctListReqDTO;
//import com.m2e.dashboard.service.esb.client.ESBClient;
//import com.m2e.dashboard.service.esb.ESBRequestHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static com.m2e.dashboard.enums.ESBServiceType.DEPOSIT_CA_ACCT_BALANCE_INQUIRY_SG;
//
//@Service
//public class DepositCAAcctBalanceInquirySGRequestHandler implements ESBRequestHandler<ESBDepositCAAcctListReqDTO, ESBReqDTO> {
//    private static final Logger log = LoggerFactory.getLogger(DepositCAAcctBalanceInquirySGRequestHandler.class);
//
//    private final ESBIntegrationProperties esbIntegrationProperties;
//    private final ESBClient esbClient;
//
//    @Autowired
//    public DepositCAAcctBalanceInquirySGRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
//        this.esbIntegrationProperties = esbIntegrationProperties;
//        this.esbClient = esbClient;
//    }
//    @Override
//    public ESBReqDTO prepareRequest(ESBDepositCAAcctListReqDTO request) {
//        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_CA_GETACCTBALANCEINQUIRY_SG.getValue());
//        log.info("Properties info for CA getAcctBalanceInquiry ESB: {}", configProperties);
//
//        return ESBReqDTO.builder()
//                .message(ESBMsgReqDTO.builder()
//                        .messageHeader(esbClient.constructMsgHeader(configProperties))
//                        .messageBody(ESBDepositCAAcctBalanceInquiryMsgBodyReqDTO.builder()
//                                .accountListing(convertToESBAcctListingReqDTO(request)).build())
//                        .build())
//                .build();
//    }
//
//    private List<ESBAcctListingReqDTO> convertToESBAcctListingReqDTO(ESBDepositCAAcctListReqDTO summaryDTO) {
//        return summaryDTO.getDatas().getData().stream().map(spDTO ->
//                ESBAcctListingReqDTO.builder()
//                        .accountNumber(spDTO.getAcctNumber())
//                        .accountType("D")
//                        .currency(spDTO.getCurrency())
//                        .region(spDTO.getProvider())
//                        .build()
//        ).collect(Collectors.toList());
//    }
//
//    @Override
//    public String getServiceType() {
//        return DEPOSIT_CA_ACCT_BALANCE_INQUIRY_SG.name();
//    }
//}
