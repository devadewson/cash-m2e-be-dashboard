package com.m2e.dashboard.jms.deposit.fd.request.account.certdetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ESBSGDepositFDCertDetailsReqDTO {
    @JsonProperty("AccountNo")
    private String accountNo;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("AccountType")
    private String accountType;
    @JsonProperty("FDInfo")
    private String fdInfo;
    @JsonProperty("MbbLastKey")
    private String mbbLastKey;
}
