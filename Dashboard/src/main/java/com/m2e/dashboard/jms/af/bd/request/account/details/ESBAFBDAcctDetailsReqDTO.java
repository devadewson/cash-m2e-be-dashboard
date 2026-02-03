package com.m2e.dashboard.jms.af.bd.request.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ESBAFBDAcctDetailsReqDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("AccountType")
    private String accountType;
    @JsonProperty("Region")
    private String region;
}
