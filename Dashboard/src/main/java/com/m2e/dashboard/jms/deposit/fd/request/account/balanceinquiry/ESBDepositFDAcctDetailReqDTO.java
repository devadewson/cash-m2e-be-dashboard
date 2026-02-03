package com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBDepositFDAcctDetailReqDTO {
    private String accountNo;
    private String accountCurrency;
    private String accountRegion;
}
