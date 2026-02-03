package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositCAGPIStatusCreditorAccountDTO implements Serializable {
    @JsonProperty("IBAN")
    private String iban;

    @JsonProperty("Identification")
    private String identification;

    @JsonProperty("UnderlyingCustomerCreditTransfer")
    private ESBDepositCAGPIStatusUnderlyingCustCreditTrfDTO underlyingCustomerCreditTransfer;
}
