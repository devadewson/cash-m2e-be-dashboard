package com.m2e.dashboard.service.deposit.ca.jms.account.omnibus;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctDetailReqDTO;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.details.ESBDepositCAAcctDetailsReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.m2e.dashboard.enums.ESBServiceType.DEPOSIT_CA_OMNIBUS_BALANCE_INQUIRY;

@Service
public class DepositCAAcctOmnibusRequestHandler implements ESBRequestHandler<ESBDepositCAAcctDetailReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(DepositCAAcctOmnibusRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public DepositCAAcctOmnibusRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }
    @Override
    public ESBReqDTO prepareRequest(ESBDepositCAAcctDetailReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_CA_GETACCTOMNIBUSINQUIRY.getValue());
        log.info("Properties info for getAcctOmnibusDetails ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBDepositCAAcctDetailsReqDTO.builder()
                                .accountNo(request.getAccountNo())
                                .accountType("D") //account Type D is Deposit CA for ESB
                                .currency(request.getAccountCurrency())
                                .region(request.getAccountRegion())
                                .build())
                        .build())
                .build();
    }

    @Override
    public String getServiceType() {
        return DEPOSIT_CA_OMNIBUS_BALANCE_INQUIRY.name();
    }

}
