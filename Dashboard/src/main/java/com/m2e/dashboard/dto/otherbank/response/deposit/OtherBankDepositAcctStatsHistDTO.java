package com.m2e.dashboard.dto.otherbank.response.deposit;

import lombok.Data;

@Data
public class OtherBankDepositAcctStatsHistDTO extends OtherBankDepositAcctStatsDTO {
    private String consolidatedClosingBalance;
}