//package com.m2e.dashboard.service.deposit.ca.jms.account.omnibus;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.jms.ESBServiceMapping;
//import com.m2e.dashboard.dto.deposit.ca.request.DepositAccountDetailReqDTO;
//import com.m2e.dashboard.dto.deposit.ca.response.DepositCAAcctOmnibusDetailDTO;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
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
//@ESBServiceMapping(ESBServiceType.DEPOSIT_CA_OMNIBUS_BALANCE_INQUIRY)
//public class DepositCAAcctOmnibusResponseProcessor implements ESBResponseProcessor<DepositCAAcctOmnibusDetailDTO> {
//    private final ObjectMapper objectMapper;
//    private final ESBDepositCAAcctDetailsMapper eSBDepositCAAcctDetailsMapper;
//    private final ESBMetadataService metadataService;
//
//    @Autowired
//    public DepositCAAcctOmnibusResponseProcessor(ObjectMapper objectMapper, ESBDepositCAAcctDetailsMapper eSBDepositCAAcctDetailsMapper, ESBMetadataService metadataService) {
//        this.objectMapper = objectMapper;
//        this.eSBDepositCAAcctDetailsMapper = eSBDepositCAAcctDetailsMapper;
//        this.metadataService = metadataService;
//    }
//
//    @Override
//    public DepositCAAcctOmnibusDetailDTO processResponse(ESBDTO result, Object request) {
//        DepositCAAcctOmnibusDetailDTO depositCAAcctOmnibusDetailDTO = new DepositCAAcctOmnibusDetailDTO();
//        DepositAccountDetailReqDTO receivedRequest = objectMapper.convertValue(request, DepositAccountDetailReqDTO.class);
//        ESBDepositCAAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBDepositCAAcctBalanceInquiryMsgBodyDTO.class);
//        if (respMsgBody != null) {
//            List<ESBDepositCAAccountDTO> data = respMsgBody.getAccounts();
//            if (data != null && !data.isEmpty()) {
//                ESBDepositCAAccountDTO ESBDepositCAAccountDTO = data.get(0);
//                depositCAAcctOmnibusDetailDTO = eSBDepositCAAcctDetailsMapper.mapToDepositAccountOmnibusDTO(ESBDepositCAAccountDTO);
//            }
//        }
//        // esb metadata injection
//        metadataService.attachMetadata(result, depositCAAcctOmnibusDetailDTO);
//        return depositCAAcctOmnibusDetailDTO;
//    }
//}
