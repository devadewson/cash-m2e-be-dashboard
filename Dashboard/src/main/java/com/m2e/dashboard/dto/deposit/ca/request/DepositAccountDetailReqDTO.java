package com.m2e.dashboard.dto.deposit.ca.request;

import lombok.Data;

@Data
public class DepositAccountDetailReqDTO {
    private String acctNumber;
    private String corpCifNo;
    private String currency;
}
