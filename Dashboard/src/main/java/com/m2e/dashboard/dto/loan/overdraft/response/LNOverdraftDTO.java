package com.m2e.dashboard.dto.loan.overdraft.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LNOverdraftDTO {
    private String account;
    private String currency;
    private String limit;
    private String outstanding;
    private String maturity;
    private String interestDue;
}