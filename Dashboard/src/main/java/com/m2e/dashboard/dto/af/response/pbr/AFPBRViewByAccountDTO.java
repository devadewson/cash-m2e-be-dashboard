package com.m2e.dashboard.dto.af.response.pbr;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AFPBRViewByAccountDTO implements Serializable {
    private String acctName;
    private String acctNumber;
    private String accountCurrency;
    private String masterAcctNumber;
    private String financedAmount;
    private String masterAcctName;
    private String provider;
    private String settlementAmount;
    private String amountDue;
    private String outstandingBalance;
    private String acctStatusCode;
    private String acctStatusDesc;
    private MoreActionsEntitlement moreActions;

    public AFPBRViewByAccountDTO(String masterAcctNumber, String accountCurrency) {
        this.masterAcctNumber = masterAcctNumber;
        this.accountCurrency = accountCurrency;
    }
}