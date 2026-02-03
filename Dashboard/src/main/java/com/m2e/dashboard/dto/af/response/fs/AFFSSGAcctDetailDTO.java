package com.m2e.dashboard.dto.af.response.fs;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class AFFSSGAcctDetailDTO extends AFFSAcctDetailDTO implements BaseESBMetadata {
    private String acctNumber;

    private String interestAmt;
    private String interestRate;
    private String nextPaymentDate;

    private String paymentType;
    private String collateralId;
    private String collateralInformation;

    private String chassisNumber;
    private String engineNumber;
    private String lastPaymentDate;

    private String lateInterestOverdue;
    private String collateralDescription;
    private String maturityDate;

    private String misc;
    private String otherCharges;
    private String accruedInterest;

    private String accruedLateCharges;
    private String maturityRate;
    private String loanTerm;

    private String lateChargeInterestRate;
    private String originalLoanAmt;
    private String settlementAmt;

    private String financedAmt; // for acct-list
    private String tlastIndicator;

    private MoreActionsEntitlement moreActions;
    private ESBMetadataDTO esbMetadata;
}
