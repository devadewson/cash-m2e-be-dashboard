package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositCAGPIStatusAmountDTO implements Serializable {
    private String currency;

    private String amount;
}
