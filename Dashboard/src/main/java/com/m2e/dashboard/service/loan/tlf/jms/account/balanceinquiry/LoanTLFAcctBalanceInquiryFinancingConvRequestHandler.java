//package com.m2e.dashboard.service.loan.tlf.jms.account.balanceinquiry;
//
//import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
//import com.m2e.dashboard.dto.loan.tlf.response.LNTLFDTO;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
//import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBReqDTO;
//import com.m2e.dashboard.jms.loan.tlf.request.account.balanceinquiry.ESBLoanTLFAcctBalanceInquiryMsgBodyReqDTO;
//import com.m2e.dashboard.service.esb.client.ESBClient;
//import com.m2e.dashboard.service.esb.ESBRequestHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//import static com.m2e.dashboard.enums.ESBServiceType.LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_FOREIGN_CONV;
//
//@Service
//public class LoanTLFAcctBalanceInquiryFinancingConvRequestHandler implements ESBRequestHandler<List<LNTLFDTO>, ESBReqDTO> {
//    private static final Logger log = LoggerFactory.getLogger(LoanTLFAcctBalanceInquiryFinancingConvRequestHandler.class);
//
//    private final ESBIntegrationProperties esbIntegrationProperties;
//    private final ESBClient esbClient;
//
//    @Autowired
//    public LoanTLFAcctBalanceInquiryFinancingConvRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
//        this.esbIntegrationProperties = esbIntegrationProperties;
//        this.esbClient = esbClient;
//    }
//
//    @Override
//    public ESBReqDTO prepareRequest(List<LNTLFDTO> request) {
//        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.LOAN_TLF_GETACCTDETAILS_FOREIGN_MY_CONV.getValue());
//        log.info("Properties info for getBalanceInquiry ESB: {}", configProperties);
//
//        return ESBReqDTO.builder()
//                .message(ESBMsgReqDTO.builder()
//                        .messageHeader(esbClient.constructMsgHeader(configProperties))
//                        .messageBody(ESBLoanTLFAcctBalanceInquiryMsgBodyReqDTO.builder()
//                                .accountListing(createESBAcctDetailReqDTO(request)).build())
//                        .build())
//                .build();
//    }
//
//    private List<ESBAcctListingReqDTO> createESBAcctDetailReqDTO(List<LNTLFDTO> request){
//        return request.stream().map(
//                acctReq-> ESBAcctListingReqDTO.builder()
//                        .accountNumber(acctReq.getAcctNumber())
//                        .accountType("L")
//                        .currency(acctReq.getAccountCurrency())
//                        .region(acctReq.getProvider()).build()
//        ).toList();
//    }
//
//    @Override
//    public String getServiceType() {
//        return LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_FOREIGN_CONV.name();
//    }
//}
