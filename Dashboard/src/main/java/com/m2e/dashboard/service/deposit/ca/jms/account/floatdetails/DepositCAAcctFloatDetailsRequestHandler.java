package com.m2e.dashboard.service.deposit.ca.jms.account.floatdetails;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.details.ESBDepositCAAcctFloatDetailsReqDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.details.ESBDepositCAFloatReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.m2e.dashboard.enums.ESBServiceType.DEPOSIT_CA_ACCT_FLOAT_DETAILS;

@Service
public class DepositCAAcctFloatDetailsRequestHandler implements ESBRequestHandler<ESBDepositCAFloatReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(DepositCAAcctFloatDetailsRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public DepositCAAcctFloatDetailsRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }
    @Override
    public ESBReqDTO prepareRequest(ESBDepositCAFloatReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_CA_GETFLOATDETAILS.getValue());
        log.info("Properties info for getAcctFloatDetails ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBDepositCAAcctFloatDetailsReqDTO.builder()
                                .accountNo(request.getAccountNo())
                                .floatExpiryDate(convertDateFormat(request.getExpiryDate()))
                                .mbbLastKey("0")
                                .build())
                        .build())
                .build();
    }

    @Override
    public String getServiceType() {
        return DEPOSIT_CA_ACCT_FLOAT_DETAILS.name();
    }

    private String convertDateFormat(String expiryDate) {
        try {
            //format the date to hit ESB float
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate parsedDate = LocalDate.parse(expiryDate, inputFormatter);
            return parsedDate.format(outputFormatter);
        } catch (DateTimeParseException e) {
            return "Invalid date format";
        }
    }
}
