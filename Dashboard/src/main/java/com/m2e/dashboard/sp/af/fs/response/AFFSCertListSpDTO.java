package com.m2e.dashboard.sp.af.fs.response;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AFFSCertListSpDTO implements Serializable {
    private String masterAcctNumber;
    private String chasisNo;
    private String masterAcctName;
    private String currency;
    private String certNo;
    private String provider;
    private String financedAmount;
    private String outstandingBalance;
    private String settlementAmt;
    private MoreActionsEntitlement moreActions;
    private String acctStatusCode;
    private String acctStatusDesc;
    private String tlastIndicator;

    public AFFSCertListSpDTO(String certNo, String currency) {
        this.certNo = certNo;
        this.currency = currency;
    }
}
