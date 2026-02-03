package com.m2e.dashboard.dto.deposit.va.response;

import lombok.Data;



@Data
public class DepositVARawSearchDTO {
    private String currAcctNumber;
    private String currAcctName;
    private Integer totVa;
    private String corpCode;
    private String virtualAcctNumber;
    private String virtualAcctName;
    private String virtualAcctRefNumber;
}
