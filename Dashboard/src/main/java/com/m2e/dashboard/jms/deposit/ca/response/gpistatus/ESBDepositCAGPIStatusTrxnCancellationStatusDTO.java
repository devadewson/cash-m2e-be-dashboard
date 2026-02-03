package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositCAGPIStatusTrxnCancellationStatusDTO implements Serializable {
    @JsonProperty("transaction_cancellation_status")
    private String transactionCancellationStatus;
}
