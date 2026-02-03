package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBDepositCAGPIStatusPaymentTrxnDetailsDTO implements Serializable {
    private String uetr;

    @JsonProperty("transaction_status")
    private ESBDepositCAGPIStatusTrxnStatusDTO transactionStatus;

    @JsonProperty("cancellation_status")
    private ESBDepositCAGPIStatusTrxnCancellationStatusDTO cancellationStatus;

    @JsonProperty("cases")
    private String cases;

    @JsonProperty("initiation_time")
    private String initiationTime;

    @JsonProperty("completion_time")
    private String completionTime;

    @JsonProperty("last_update_time")
    private String lastUpdateTime;

    @JsonProperty("payment_event")
    private List<ESBDepositCAGPIStatusPaymentEventDTO> paymentEvent;

}