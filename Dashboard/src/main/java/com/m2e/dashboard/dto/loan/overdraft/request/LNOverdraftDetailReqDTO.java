package com.m2e.dashboard.dto.loan.overdraft.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class LNOverdraftDetailReqDTO implements Serializable {
    private String acctNumber;
    private String currency;
}