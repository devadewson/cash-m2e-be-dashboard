package com.m2e.dashboard.jms.deposit.ca.request.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ESBDepositCAAcctFloatDetailsReqDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNo;
    @JsonProperty("FloatExpiryDate")
    private String floatExpiryDate;
    @JsonProperty("MbbLastKey")
    private String mbbLastKey;
}
