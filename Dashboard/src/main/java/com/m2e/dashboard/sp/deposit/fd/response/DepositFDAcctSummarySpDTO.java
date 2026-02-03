package com.m2e.dashboard.sp.deposit.fd.response;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor(force = true)
public class DepositFDAcctSummarySpDTO implements Serializable {
    private String acctName;
    private String acctNumber;
    private String noOfCertificates;
    private String totalPrincipalAmt;
    private String currency;
    private String provider;
    private String acctType;
    private String productCode;
    private String acctStatusCode;
    private String acctStatusDesc;
    private MoreActionsEntitlement moreActions;
}
