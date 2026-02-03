package com.m2e.dashboard.service.einvoice.jms.validatetin;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceValidateTINReqDTO;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.jms.einvoice.request.validateTIN.ESBValidateTINMsgBodyReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBRequestHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.m2e.dashboard.enums.ESBServiceType.EINVOICE_VALIDATETIN_MY;

@Service
@RequiredArgsConstructor
public class EInvoiceValidateTINRequestHandler implements ESBRequestHandler<EInvoiceValidateTINReqDTO, ESBReqDTO> {

    private static final Logger log = LoggerFactory.getLogger(EInvoiceValidateTINRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Override
    public ESBReqDTO prepareRequest(EInvoiceValidateTINReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.EINVOICE_VALIDATETIN_MY.getValue());
        log.info("Properties info for validateTIN ESB: {}", configProperties);

        ESBReqDTO req = ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBValidateTINMsgBodyReqDTO.builder()
                                .countryOfOnboarding("MYS")
                                .entityOfOnboarding("110")
                                .partyClass("O") // TODO: make sure the mapping
                                .tin(request.getTin())
                                .idType("BRN") // TODO: make sure the mapping
                                .idValue(request.getBrn())
                                .requestor("RCMS")
                                .prefferedDateFormat(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                .transactionType("VALIDATETIN")
                                .build())
                        .build())
                .build();
        log.info("ESB request for service type -> {} with payload -> {} ", getServiceType(), req);
        return req;
    }

    @Override
    public String getServiceType() {
        return EINVOICE_VALIDATETIN_MY.name();
    }
}
