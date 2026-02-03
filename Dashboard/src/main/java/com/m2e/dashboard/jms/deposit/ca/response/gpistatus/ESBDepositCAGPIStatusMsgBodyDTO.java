package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositCAGPIStatusMsgBodyDTO implements Serializable {
    @JsonProperty("payment_transaction_details_response")
    private ESBDepositCAGPIStatusPaymentTrxnDetailsDTO paymentTransactionDetail;
}
