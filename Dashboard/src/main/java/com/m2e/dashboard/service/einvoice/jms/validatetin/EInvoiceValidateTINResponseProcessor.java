//package com.m2e.dashboard.service.einvoice.jms.validatetin;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.jms.ESBServiceMapping;
//import com.m2e.dashboard.dto.einvoice.response.EInvoiceValidateTINDTO;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
//import com.m2e.dashboard.jms.einvoice.response.validateTIN.ESBValidateTINMsgBodyDTO;
//import com.m2e.dashboard.service.esb.ESBMetadataService;
//import com.m2e.dashboard.service.esb.ESBResponseProcessor;
//import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//@Service
//@ESBServiceMapping(ESBServiceType.EINVOICE_VALIDATETIN_MY)
//@RequiredArgsConstructor
//public class EInvoiceValidateTINResponseProcessor implements ESBResponseProcessor<EInvoiceValidateTINDTO> {
//
//    private static final Logger log = LoggerFactory.getLogger(EInvoiceValidateTINResponseProcessor.class);
//
//    private final ObjectMapper objectMapper;
//    private final ModelMapper modelMapper;
//    private final ESBMetadataService esbMetadataService;
//    @Override
//    public EInvoiceValidateTINDTO processResponse(ESBDTO result, Object request) {
//        log.info("executing EInvoiceValidateTINResponseProcessor with result -> {}", result);
//        ESBValidateTINMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBValidateTINMsgBodyDTO.class);
//        EInvoiceValidateTINDTO data = new EInvoiceValidateTINDTO();
//        if(respMsgBody != null){
//            data.setResponseStatus(respMsgBody.getResponseStatus());
//        }
//        esbMetadataService.attachMetadata(result, data);
//        return data;
//    }
//}
