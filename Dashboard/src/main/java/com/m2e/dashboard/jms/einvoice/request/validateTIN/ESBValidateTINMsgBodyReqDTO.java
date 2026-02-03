package com.m2e.dashboard.jms.einvoice.request.validateTIN;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class ESBValidateTINMsgBodyReqDTO implements Serializable {
    private String countryOfOnboarding;
    private String entityOfOnboarding;
    private String partyClass;
    private String tin;
    private String idType;
    private String idValue;
    private String requestor;
    private String prefferedDateFormat;
    private String transactionType;
}
