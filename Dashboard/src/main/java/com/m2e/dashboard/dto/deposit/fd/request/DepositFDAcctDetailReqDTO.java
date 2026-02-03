package com.m2e.dashboard.dto.deposit.fd.request;

import lombok.Data;

@Data
public class DepositFDAcctDetailReqDTO {
    private String acctNumber;
    private String corpCifNo;
    private String currency;
}
