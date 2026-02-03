package com.m2e.dashboard.dto.af.response.bd;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AFBDAcctListViewAcctDTO extends AFBDAcctListDTO implements Serializable {
    private String acctName;
    private String acctNumber;
    private String financedAmt;
    private String outstandingBalance;
    private String amountDue;
    private String settlementAmount;
    private String instalmentAmt;
    private String instalmentAmtOverdue;
    private String tlastIndicator;

    public AFBDAcctListViewAcctDTO(String masterAcctNumber, String currency) {
        this.masterAcctNumber = masterAcctNumber;
        super.setCurrency(currency);
    }
}
