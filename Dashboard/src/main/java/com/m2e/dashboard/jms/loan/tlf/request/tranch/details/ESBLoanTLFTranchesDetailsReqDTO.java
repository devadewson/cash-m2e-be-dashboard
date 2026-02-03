package com.m2e.dashboard.jms.loan.tlf.request.tranch.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ESBLoanTLFTranchesDetailsReqDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNo;
    @JsonProperty("AccountType")
    private String accountType;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("MbbLastKey")
    private String mbbLastKey;
}
