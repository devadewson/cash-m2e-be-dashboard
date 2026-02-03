package com.m2e.dashboard.dto.otherbank.response.loan;

import lombok.Data;

@Data
public class OtherBankLoanAcctStatsHistDTO extends OtherBankLoanAcctStatsDTO {
    private String outstandingBalance;
}