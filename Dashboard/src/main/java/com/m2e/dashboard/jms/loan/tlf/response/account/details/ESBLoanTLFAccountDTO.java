package com.m2e.dashboard.jms.loan.tlf.response.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBLoanTLFAccountDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("Region")
    private String region;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("Details")
    private List<ESBLoanTLFDetailDTO> details;
}
