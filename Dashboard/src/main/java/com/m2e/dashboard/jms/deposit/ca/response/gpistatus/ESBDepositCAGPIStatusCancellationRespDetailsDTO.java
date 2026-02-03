package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositCAGPIStatusCancellationRespDetailsDTO implements Serializable {
    @JsonProperty("investigation_execution_status")
    private String investigationExecutionStatus;

    @JsonProperty("investigation_execution_status_reason")
    private ESBDepositCAGPIStatusInvestigationExecStatusReasonDTO investigationExecutionStatusReason;
}
