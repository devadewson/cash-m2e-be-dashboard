package com.m2e.dashboard.jms.deposit.ca.response.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositCAAccountFloatDetailTrnRecDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("FloatExpiryDate")
    private String floatExpiryDate;
    @JsonProperty("TransactionCode")
    private String transactionCode;
    @JsonProperty("TransactionDesc")
    private String transactionDesc;
    @JsonProperty("FloatAmount")
    private String floatAmount;
    @JsonProperty("StatusCode")
    private String statusCode;
}
