//package com.m2e.dashboard.service.deposit.ca.jms.gpistatus;
//
//import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
//import com.m2e.dashboard.dto.deposit.ca.request.DepositGPIStatusReqDTO;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
//import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBReqDTO;
//import com.m2e.dashboard.jms.deposit.ca.request.gpistatus.ESBDepositCAGPIStatusMsgBodyReqDTO;
//import com.m2e.dashboard.service.esb.ESBRequestHandler;
//import com.m2e.dashboard.service.esb.client.ESBClient;
//import com.m2e.dashboard.util.DateTimeUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//import static com.m2e.dashboard.enums.ESBServiceType.DEPOSIT_CA_GPI_STATUS;
//
//@Service
//public class DepositCAGPIStatusRequestHandler implements ESBRequestHandler<DepositGPIStatusReqDTO, ESBReqDTO> {
//    private static final Logger log = LoggerFactory.getLogger(DepositCAGPIStatusRequestHandler.class);
//
//    private final ESBIntegrationProperties esbIntegrationProperties;
//    private final ESBClient esbClient;
//
//    @Autowired
//    public DepositCAGPIStatusRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
//        this.esbIntegrationProperties = esbIntegrationProperties;
//        this.esbClient = esbClient;
//    }
//
//    @Override
//    public ESBReqDTO prepareRequest(DepositGPIStatusReqDTO request) {
//        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_CA_GETSWIFTGPI.getValue());
//        log.info("Properties info for getGPIStatus ESB: {}", configProperties);
//
//        return ESBReqDTO.builder()
//                .message(ESBMsgReqDTO.builder()
//                        .messageHeader(esbClient.constructMsgHeader(configProperties))
//                        .messageBody(ESBDepositCAGPIStatusMsgBodyReqDTO.builder()
//                                .uniqueID(UUID.randomUUID())
//                                .transactionDateTime(DateTimeUtil.getCompactDateTime())
//                                .serviceID(configProperties.getEsbMsgId())
//                                .serviceType("")
//                                .uetr(request.getUetr())
//                                .build())
//                        .build())
//                .build();
//    }
//
//    @Override
//    public String getServiceType() {
//        return DEPOSIT_CA_GPI_STATUS.name();
//    }
//}
