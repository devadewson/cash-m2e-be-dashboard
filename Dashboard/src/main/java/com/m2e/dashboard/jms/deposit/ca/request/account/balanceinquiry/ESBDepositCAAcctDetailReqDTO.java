package com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBDepositCAAcctDetailReqDTO {
    private String accountNo;
    private String accountCurrency;
    private String accountRegion;
    private String accountType;
}
