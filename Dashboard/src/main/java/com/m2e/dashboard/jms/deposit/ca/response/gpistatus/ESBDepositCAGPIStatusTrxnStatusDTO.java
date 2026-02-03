package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositCAGPIStatusTrxnStatusDTO implements Serializable {
    private String status;
    private String reason;
}
