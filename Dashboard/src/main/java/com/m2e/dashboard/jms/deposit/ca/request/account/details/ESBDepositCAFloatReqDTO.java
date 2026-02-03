package com.m2e.dashboard.jms.deposit.ca.request.account.details;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBDepositCAFloatReqDTO {
    private String accountNo;
    private String currency;
    private String expiryDate;
    private String mbbLastKey;
    private int page;
    private int pageSize;
}
