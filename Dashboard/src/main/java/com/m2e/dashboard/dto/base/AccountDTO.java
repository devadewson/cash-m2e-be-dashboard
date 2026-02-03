package com.m2e.dashboard.dto.base;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@SuperBuilder
public class AccountDTO implements Serializable {
    private String acctName;
    private String acctNumber;
    private String accountCurrency;

    private String acctStatusCode;
    private String acctStatusDesc;
}