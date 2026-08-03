//package com.m2e.dashboard.service.deposit.ca.jms.account.details.my;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.jms.ESBServiceMapping;
//import com.m2e.dashboard.dto.deposit.ca.response.DepositAccountDetailDTO;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
//import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctDetailReqDTO;
//import com.m2e.dashboard.jms.deposit.ca.response.account.balanceinquiry.ESBDepositCAAcctBalanceInquiryMsgBodyDTO;
//import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDTO;
//import com.m2e.dashboard.mapper.deposit.ca.ESBDepositCAAcctDetailsMapper;
//import com.m2e.dashboard.service.esb.ESBMetadataService;
//import com.m2e.dashboard.service.esb.ESBResponseProcessor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@ESBServiceMapping(ESBServiceType.DEPOSIT_CA_ACCT_DETAILS_MY)
//public class DepositCAAcctDetailsMYResponseProcessor implements ESBResponseProcessor<DepositAccountDetailDTO> {
//    private final ObjectMapper objectMapper;
//    private final ESBDepositCAAcctDetailsMapper eSBDepositCAAcctDetailsMapper;
//
//    private final ESBMetadataService metadataService;
//
//    @Autowired
//    public DepositCAAcctDetailsMYResponseProcessor(ObjectMapper objectMapper, ESBDepositCAAcctDetailsMapper eSBDepositCAAcctDetailsMapper, ESBMetadataService metadataService) {
//        this.objectMapper = objectMapper;
//        this.eSBDepositCAAcctDetailsMapper = eSBDepositCAAcctDetailsMapper;
//        this.metadataService = metadataService;
//    }
//
//    @Override
//    public DepositAccountDetailDTO processResponse(ESBDTO result, Object request) {
//        DepositAccountDetailDTO depositAccountDetailDTO = new DepositAccountDetailDTO();
//        ESBDepositCAAcctDetailReqDTO receivedRequest = objectMapper.convertValue(request, ESBDepositCAAcctDetailReqDTO.class);
//        ESBDepositCAAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBDepositCAAcctBalanceInquiryMsgBodyDTO.class);
//        if (respMsgBody != null) {
//            List<ESBDepositCAAccountDTO> data = respMsgBody.getAccounts();
//            if (data != null && !data.isEmpty()) {
//                ESBDepositCAAccountDTO esbDepositCAAccountDTO = data.get(0);
//                if (receivedRequest.getAccountType().equals("CA")) {
//                    depositAccountDetailDTO = eSBDepositCAAcctDetailsMapper.mapToDepositAccountDetail(esbDepositCAAccountDTO);
//                } else {
//                    depositAccountDetailDTO = eSBDepositCAAcctDetailsMapper.mapToMFCADepositAccountDetail(esbDepositCAAccountDTO);
//                }
//            }
//        }
//        // esb metadata injection
//        metadataService.attachMetadata(result, depositAccountDetailDTO);
//        return depositAccountDetailDTO;
//    }
//
//
//}
