package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositCAGPIStatusUnderlyingCancellationDetailsDTO implements Serializable {
    @JsonProperty("cancellation_request_details")
    private ESBDepositCAGPIStatusCancellationReqDetailsDTO cancellationRequestDetails;

    @JsonProperty("cancellation_response_details")
    private ESBDepositCAGPIStatusCancellationRespDetailsDTO cancellationResponseDetails;

    @JsonProperty("cancellation_request_tracking_status")
    private String cancellationRequestTrackingStatus;
}
