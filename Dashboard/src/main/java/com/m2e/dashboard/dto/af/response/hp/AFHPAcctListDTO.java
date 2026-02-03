package com.m2e.dashboard.dto.af.response.hp;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFHPAcctListDTO implements Serializable {
    private String acctName;
    private String acctNumber;
    private String currency;
    private String masterAcctNumber;
    private String financedAmt;
    private String outstandingBalance;
    private String instalmentDue;
    private String monthlyInstalment;
    private String instalmentAmt;
    private String settlementAmt;
    private String otherCharges;
    private String provider;
    private String subProduct;
    private String vehicleRegistrationNumber;
    private String acctStatusCode;
    private String acctStatusDesc;
    private String tlastIndicator;
    private boolean isBHP;
    private MoreActionsEntitlement moreActions;
}
