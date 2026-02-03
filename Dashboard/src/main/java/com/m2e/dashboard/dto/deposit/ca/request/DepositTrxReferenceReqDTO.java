package com.m2e.dashboard.dto.deposit.ca.request;

import lombok.Data;

@Data
public class DepositTrxReferenceReqDTO {
    private String bankRefNo;
    private String currency;
}
