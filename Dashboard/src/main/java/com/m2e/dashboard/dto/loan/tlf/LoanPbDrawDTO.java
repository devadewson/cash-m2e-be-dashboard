package com.m2e.dashboard.dto.loan.tlf;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoanPbDrawDTO implements Serializable {
    private String drawdownNumber;
    private String drawDate;
    private String currency;
    private String outstanding;
    private String maturityDate;
    private String nextPaymentDue;
    private String nextPaymentDate;
}