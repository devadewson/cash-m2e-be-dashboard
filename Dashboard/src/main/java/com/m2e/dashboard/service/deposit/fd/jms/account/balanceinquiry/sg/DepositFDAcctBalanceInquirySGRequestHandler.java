//package com.m2e.dashboard.service.deposit.fd.jms.account.balanceinquiry.sg;
//
//import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
//import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBReqDTO;
//import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBSGDepositFDAcctBalanceInquiryMsgBodyReqDTO;
//import com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry.ESBDepositFDAcctListReqDTO;
//import com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry.ESBSGAcctListingReqDTO;
//import com.m2e.dashboard.service.esb.client.ESBClient;
//import com.m2e.dashboard.service.esb.ESBRequestHandler;
//import com.m2e.dashboard.service.deposit.fd.jms.account.balanceinquiry.my.DepositFDAcctBalanceInquiryMYRequestHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static com.m2e.dashboard.enums.ESBServiceType.DEPOSIT_FD_ACCT_BALANCE_INQUIRY_SG;
//
//@Service
//public class DepositFDAcctBalanceInquirySGRequestHandler implements ESBRequestHandler<ESBDepositFDAcctListReqDTO, ESBReqDTO> {
//    private static final Logger log = LoggerFactory.getLogger(DepositFDAcctBalanceInquiryMYRequestHandler.class);
//
//    private final ESBIntegrationProperties esbIntegrationProperties;
//    private final ESBClient esbClient;
//
//    @Autowired
//    public DepositFDAcctBalanceInquirySGRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
//        this.esbIntegrationProperties = esbIntegrationProperties;
//        this.esbClient = esbClient;
//    }
//    @Override
//    public ESBReqDTO prepareRequest(ESBDepositFDAcctListReqDTO request) {
//        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_FD_GETACCTBALANCEINQUIRY_SG.getValue());
//        log.info("Properties info for FD getBalanceInquiry ESB: {}", configProperties);
//
//        return ESBReqDTO.builder()
//                .message(ESBMsgReqDTO.builder()
//                        .messageHeader(esbClient.constructMsgHeader(configProperties))
//                        .messageBody(ESBSGDepositFDAcctBalanceInquiryMsgBodyReqDTO.builder()
//                                .accountListing(convertToESBAcctListingReqDTO(request)).build())
//                        .build())
//                .build();
//    }
//
//    private List<ESBSGAcctListingReqDTO> convertToESBAcctListingReqDTO(ESBDepositFDAcctListReqDTO summaryDTO) {
//        return summaryDTO.getDatas().getData().stream().map(spDTO ->
//                ESBSGAcctListingReqDTO.builder()
//                        .accountNumber(spDTO.getAcctNumber())
//                        .accountType("T")
//                        .currency(spDTO.getCurrency())
//                        .fdInfo("1")
//                        .region(spDTO.getProvider())
//                        .build()
//        ).collect(Collectors.toList());
//    }
//
//    @Override
//    public String getServiceType() {
//        return DEPOSIT_FD_ACCT_BALANCE_INQUIRY_SG.name();
//    }
//}
