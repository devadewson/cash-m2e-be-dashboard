//package com.m2e.dashboard.service.loan.tlf.jms.account.tranchdetails;
//
//import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
//import com.m2e.dashboard.dto.loan.tlf.request.LNTLFTranchesDetailReqDTO;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
//import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBReqDTO;
//import com.m2e.dashboard.jms.loan.tlf.request.tranch.details.ESBLoanTLFTranchesDetailsReqDTO;
//import com.m2e.dashboard.service.esb.client.ESBClient;
//import com.m2e.dashboard.service.esb.ESBRequestHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import static com.m2e.dashboard.enums.ESBServiceType.LOAN_TLF_TRANCH_DETAIL_INQUIRY_FOREIGN_ISLAMIC;
//
//@Service
//public class LoanTLFTranchDetailsIslamicRequestHandler implements ESBRequestHandler<LNTLFTranchesDetailReqDTO, ESBReqDTO> {
//    private static final Logger log = LoggerFactory.getLogger(LoanTLFTranchDetailsIslamicRequestHandler.class);
//
//    private final ESBIntegrationProperties esbIntegrationProperties;
//    private final ESBClient esbClient;
//
//    @Autowired
//    public LoanTLFTranchDetailsIslamicRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
//        this.esbIntegrationProperties = esbIntegrationProperties;
//        this.esbClient = esbClient;
//    }
//
//    @Override
//    public ESBReqDTO prepareRequest(LNTLFTranchesDetailReqDTO request) {
//        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.LOAN_TLF_GETTRANCHDETAILS_FOREIGN_ISLAMIC.getValue());
//        log.info("Properties info for getActivityInquiry ESB: {}", configProperties);
//
//        return ESBReqDTO.builder()
//                .message(ESBMsgReqDTO.builder()
//                        .messageHeader(esbClient.constructMsgHeader(configProperties))
//                        .messageBody(ESBLoanTLFTranchesDetailsReqDTO.builder()
//                                .accountNo(request.getAcctNumber())
//                                .accountType("L")
//                                .currency(request.getCurrency())
//                                .mbbLastKey("0")
//                                .build())
//                        .build())
//                .build();
//    }
//
//    @Override
//    public String getServiceType() {
//        return LOAN_TLF_TRANCH_DETAIL_INQUIRY_FOREIGN_ISLAMIC.name();
//    }
//}
