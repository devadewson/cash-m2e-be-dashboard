//package com.m2e.dashboard.service.loan.tlf.jms.account.tranch;
//
//import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
//import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBReqDTO;
//import com.m2e.dashboard.jms.loan.tlf.request.tranch.details.ESBLoanTLFTranchesDetailsReqDTO;
//import com.m2e.dashboard.jms.loan.tlf.request.tranch.details.ESBLoanTLFTranchesReqDTO;
//import com.m2e.dashboard.service.esb.client.ESBClient;
//import com.m2e.dashboard.service.esb.ESBRequestHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import static com.m2e.dashboard.enums.ESBServiceType.LOAN_TLF_TRANCHES_INQUIRY_FOREIGN_CONV;
//
//@Service
//public class LoanTLFTranchesForeignConvRequestHandler implements ESBRequestHandler<ESBLoanTLFTranchesReqDTO, ESBReqDTO> {
//    private static final Logger log = LoggerFactory.getLogger(LoanTLFTranchesForeignConvRequestHandler.class);
//
//    private final ESBIntegrationProperties esbIntegrationProperties;
//    private final ESBClient esbClient;
//
//    @Autowired
//    public LoanTLFTranchesForeignConvRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
//        this.esbIntegrationProperties = esbIntegrationProperties;
//        this.esbClient = esbClient;
//    }
//
//    @Override
//    public ESBReqDTO prepareRequest(ESBLoanTLFTranchesReqDTO request) {
//        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.LOAN_TLF_GETTRANCHDETAILS_FOREIGN_CONV.getValue());
//        log.info("Properties info for getTranchDetails ESB: {}", configProperties);
//
//        return ESBReqDTO.builder()
//                .message(ESBMsgReqDTO.builder()
//                        .messageHeader(esbClient.constructMsgHeader(configProperties))
//                        .messageBody(ESBLoanTLFTranchesDetailsReqDTO.builder()
//                                .accountNo(request.getAcctNumber())
//                                .accountType("L")
//                                .currency(request.getAccountCurrency())
//                                .mbbLastKey("0")
//                                .build())
//                        .build())
//                .build();
//    }
//
//    @Override
//    public String getServiceType() {
//        return LOAN_TLF_TRANCHES_INQUIRY_FOREIGN_CONV.name();
//    }
//}
