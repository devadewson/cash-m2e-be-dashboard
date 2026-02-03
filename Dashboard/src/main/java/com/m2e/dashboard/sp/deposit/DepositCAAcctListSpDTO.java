package com.m2e.dashboard.sp.deposit;

import lombok.Data;

import java.io.Serializable;

@Data
public class DepositCAAcctListSpDTO implements Serializable {
    private String acctName;
    private String acctNumber;
    private String currency;
    private String ledgerBalance;
    private String availableBalance;
    private String acctType;
    private String provider;
}
