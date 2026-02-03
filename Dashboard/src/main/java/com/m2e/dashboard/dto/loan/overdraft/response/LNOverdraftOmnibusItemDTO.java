package com.m2e.dashboard.dto.loan.overdraft.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class LNOverdraftOmnibusItemDTO implements Serializable {
    private String acctName;
    private String acctNumber;
    private String accountCurrency;
    private String utilisedAmount;
}
