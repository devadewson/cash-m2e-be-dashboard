package com.m2e.dashboard.dto.af.response.bd;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class AFBDSGSubAcctDetailDTO implements BaseESBMetadata {
    private String currency;
    private String lastUpdatedDateTime;

    private String agreementNumber;
    private String acctNumber;
    private String acctName;

    private String outstandingLoan;

    private String productName;
    private String numberOfSubAcct;
    private String instalmentAmt;
    private String instalmentAmtOverdue;
    private String amountDue;
    private String interestRate;

    private String dueDate;
    private String originalTenure;
    private String numberOfInstalmentPaid;

    private String remainingTenure;
    private String lateInterest;

    private String lateCharges;
    private String misc;

    private String otherCharges;
    private String chassisNumber;
    private String engineNumber;

    private String collateralDescription;
    private String originalLoanAmt;
    private String settlementAmount;

    private String acctStatusDesc;
    private String tlastIndicator;

    private boolean isIslamic;
    private String islamicFlag;

    private ESBMetadataDTO esbMetadata;
    private MoreActionsEntitlement moreActions;

}
