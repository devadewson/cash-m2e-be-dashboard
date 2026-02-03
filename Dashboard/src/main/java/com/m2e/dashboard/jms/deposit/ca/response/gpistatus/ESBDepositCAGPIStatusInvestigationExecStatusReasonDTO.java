package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositCAGPIStatusInvestigationExecStatusReasonDTO implements Serializable {
    @JsonProperty("rejected")
    private String rejected;

    @JsonProperty("pending")
    private String pending;
}
