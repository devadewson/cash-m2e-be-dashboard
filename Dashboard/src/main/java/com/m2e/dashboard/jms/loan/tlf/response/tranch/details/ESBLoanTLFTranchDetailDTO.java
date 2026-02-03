package com.m2e.dashboard.jms.loan.tlf.response.tranch.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBLoanTLFTranchDetailDTO implements Serializable {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Value")
    private String value;
}
