package com.m2e.dashboard.jms.deposit.fd.request.account.certdetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ESBDepositFDCertDetailsReqDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNo;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("AccountType")
    private String accountType;
    @JsonProperty("MbbLastKey")
    private String mbbLastKey;
}
