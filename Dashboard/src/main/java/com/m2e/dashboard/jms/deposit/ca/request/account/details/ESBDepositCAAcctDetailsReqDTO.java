package com.m2e.dashboard.jms.deposit.ca.request.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ESBDepositCAAcctDetailsReqDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNo;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("AccountType")
    private String accountType;
    @JsonProperty("Region")
    private String region;
}
