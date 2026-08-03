//package com.m2e.dashboard.service.deposit.fd.jms.account.certdetails.sg;
//
//import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
//import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBReqDTO;
//import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBSGDepositFDCertBalanceInquiryMsgBodyReqDTO;
//import com.m2e.dashboard.jms.deposit.fd.request.account.certdetails.ESBDepositFDCertsReqDTO;
//import com.m2e.dashboard.jms.deposit.fd.request.account.certdetails.ESBSGDepositFDCertDetailsReqDTO;
//import com.m2e.dashboard.service.esb.client.ESBClient;
//import com.m2e.dashboard.service.esb.ESBRequestHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.List;
//
//import static com.m2e.dashboard.enums.ESBServiceType.DEPOSIT_FD_ACCT_CERT_DETAIL_INQUIRY_SG;
//
//@Service
//public class DepositFDAcctCertDetailsInquirySGRequestHandler implements ESBRequestHandler<ESBDepositFDCertsReqDTO, ESBReqDTO> {
//
//    private static final Logger log = LoggerFactory.getLogger(DepositFDAcctCertDetailsInquirySGRequestHandler.class);
//
//    private final ESBIntegrationProperties esbIntegrationProperties;
//    private final ESBClient esbClient;
//
//    @Autowired
//    public DepositFDAcctCertDetailsInquirySGRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
//        this.esbIntegrationProperties = esbIntegrationProperties;
//        this.esbClient = esbClient;
//    }
//    @Override
//    public ESBReqDTO prepareRequest(ESBDepositFDCertsReqDTO request) {
//        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_FD_GETCERTDETAILS_SG.getValue());
//        log.info("Properties info for FD getCertDetailsInquiry SG ESB: {}", configProperties);
//
//        return ESBReqDTO.builder()
//                .message(ESBMsgReqDTO.builder()
//                        .messageHeader(esbClient.constructMsgHeader(configProperties))
//                        .messageBody(ESBSGDepositFDCertBalanceInquiryMsgBodyReqDTO.builder()
//                                .accountListing(createESBAcctBalanceInquiryReqDTO(request)).build())
//                        .build())
//                .build();
//    }
//
//    public List<ESBSGDepositFDCertDetailsReqDTO> createESBAcctBalanceInquiryReqDTO(ESBDepositFDCertsReqDTO request) {
//        return Collections.singletonList(ESBSGDepositFDCertDetailsReqDTO.builder()
//                .accountNo(request.getAccountNo())
//                .accountType("T")
//                .currency(request.getCurrency())
//                .fdInfo("2")
//                .mbbLastKey("") //first-page key for ESB SG FD cert inquiry
//                .build());
//    }
//
//    @Override
//    public String getServiceType() {
//        return DEPOSIT_FD_ACCT_CERT_DETAIL_INQUIRY_SG.name();
//    }
//}
