//package com.m2e.dashboard.service.deposit.fd.jms.account.acctstats.my;
//
//import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
//import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBReqDTO;
//import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctBalanceInquiryMsgBodyReqDTO;
//import com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry.ESBDepositFDAcctListReqDTO;
//import com.m2e.dashboard.service.esb.client.ESBClient;
//import com.m2e.dashboard.service.esb.ESBRequestHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static com.m2e.dashboard.enums.ESBServiceType.DEPOSIT_FD_ACCT_STATS_MY;
//
//@Slf4j
//@Service
//public class DepositFDAcctStatsInquiryMYRequestHandler implements ESBRequestHandler<ESBDepositFDAcctListReqDTO, ESBReqDTO> {
//    private final ESBIntegrationProperties esbIntegrationProperties;
//    private final ESBClient esbClient;
//
//    @Autowired
//    public DepositFDAcctStatsInquiryMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
//        this.esbIntegrationProperties = esbIntegrationProperties;
//        this.esbClient = esbClient;
//    }
//    @Override
//    public ESBReqDTO prepareRequest(ESBDepositFDAcctListReqDTO request) {
//        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_FD_GETACCTBALANCEINQUIRY_MY.getValue());
//        log.info("Properties info for FD getAcctStatsInquiry ESB for MY: {}", configProperties);
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
//    private List<ESBAcctListingReqDTO> convertToESBAcctListingReqDTO(ESBDepositFDAcctListReqDTO summaryDTO) {
//        return summaryDTO.getDatas().getData().stream().map(spDTO ->
//                ESBAcctListingReqDTO.builder()
//                        .accountNumber(spDTO.getAcctNumber())
//                        .accountType("T")
//                        .currency(spDTO.getCurrency())
//                        .region(spDTO.getProvider())
//                        .build()
//        ).collect(Collectors.toList());
//    }
//
//    @Override
//    public String getServiceType() {
//        return DEPOSIT_FD_ACCT_STATS_MY.name();
//    }
//}
