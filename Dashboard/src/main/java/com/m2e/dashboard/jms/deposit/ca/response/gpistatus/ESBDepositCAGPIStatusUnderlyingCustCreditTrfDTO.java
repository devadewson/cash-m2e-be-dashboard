package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositCAGPIStatusUnderlyingCustCreditTrfDTO implements Serializable {
    @JsonProperty("DebtorAgent")
    private String debtorAgent;

    @JsonProperty("PreviousInstructingAgent1")
    private String previousInstructingAgent1;

    @JsonProperty("PreviousInstructingAgent2")
    private String previousInstructingAgent2;

    @JsonProperty("PreviousInstructingAgent3")
    private String previousInstructingAgent3;

    @JsonProperty("IntermediaryAgent1")
    private String intermediaryAgent1;

    @JsonProperty("IntermediaryAgent2")
    private String intermediaryAgent2;

    @JsonProperty("IntermediaryAgent3")
    private String intermediaryAgent3;

    @JsonProperty("CreditorAgent")
    private String creditorAgent;
}
