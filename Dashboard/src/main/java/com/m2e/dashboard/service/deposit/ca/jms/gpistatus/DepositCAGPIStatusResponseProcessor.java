//package com.m2e.dashboard.service.deposit.ca.jms.gpistatus;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.jms.ESBServiceMapping;
//import com.m2e.dashboard.dto.deposit.ca.response.DepositCAGPIStatusDTO;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
//import com.m2e.dashboard.jms.deposit.ca.response.gpistatus.ESBDepositCAGPIStatusMsgBodyDTO;
//import com.m2e.dashboard.jms.deposit.ca.response.gpistatus.ESBDepositCAGPIStatusPaymentTrxnDetailsDTO;
//import com.m2e.dashboard.mapper.deposit.ca.ESBDepositCAGPIMapper;
//import com.m2e.dashboard.service.esb.ESBMetadataService;
//import com.m2e.dashboard.service.esb.ESBResponseProcessor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//@ESBServiceMapping(ESBServiceType.DEPOSIT_CA_GPI_STATUS)
//public class DepositCAGPIStatusResponseProcessor implements ESBResponseProcessor<DepositCAGPIStatusDTO> {
//
//    private final ESBDepositCAGPIMapper esbDepositCAGPIMapper;
//    private final ObjectMapper objectMapper;
//    private final ESBMetadataService metadataService;
//
//    @Autowired
//    public DepositCAGPIStatusResponseProcessor(ESBDepositCAGPIMapper esbDepositCAGPIMapper, ObjectMapper objectMapper, ESBMetadataService metadataService) {
//        this.esbDepositCAGPIMapper = esbDepositCAGPIMapper;
//        this.objectMapper = objectMapper;
//        this.metadataService = metadataService;
//    }
//
//    @Override
//    public DepositCAGPIStatusDTO processResponse(ESBDTO result, Object request) {
//        DepositCAGPIStatusDTO depositCAGPIStatusDTO = new DepositCAGPIStatusDTO();
//        ESBDepositCAGPIStatusMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBDepositCAGPIStatusMsgBodyDTO.class);
//        ESBDepositCAGPIStatusPaymentTrxnDetailsDTO paymentTransactionDetailResponse = respMsgBody.getPaymentTransactionDetail();
//        if (paymentTransactionDetailResponse != null) {
//            depositCAGPIStatusDTO = esbDepositCAGPIMapper.mapToDepositCAGPIStatusRes(paymentTransactionDetailResponse);
//        }
//        // esb metadata injection
//        metadataService.attachMetadata(result, depositCAGPIStatusDTO);
//        return depositCAGPIStatusDTO;
//    }
//}