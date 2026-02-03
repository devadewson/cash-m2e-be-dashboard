package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositCAGPIStatusCancellationReqDetailsDTO implements Serializable {
    @JsonProperty("cancellation_reason_information")
    private String cancellationReasonInformation;

    @JsonProperty("indemnity_agreement")
    private String indemnityAgreement;
}
