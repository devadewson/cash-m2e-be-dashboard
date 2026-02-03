package com.m2e.dashboard.jms.loan.tlf.response.tranch.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBLoanTLFTranchesMsgBodyDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("CurrencyCode")
    private String currency;
    @JsonProperty("DbKey")
    private String mbbLastKey;
    @JsonProperty("TranchDetails")
    private List<ESBLoanTLFTranchesTrnRecDTO> tranchDetails;
}
