package com.m2e.dashboard.dto.af.response.bd;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class AFBDSGAcctDetailDTO implements BaseESBMetadata {
    private String currency;
    private String lastUpdatedDateTime;

    private String acctName;
    private String companyName;
    private String acctNumber;

    private String productName;
    private String financedAmt;
    private String outstandingBalance;

    private String numberOfInstalmentPaid;
    private String numberOfInstalmentInArreas;

    private String numberOfAgreement;
    private String dueDate;
    private String blockStatus;

    private String instalmentAmt;
    private String amountDue;
    private String interestRate;
    private String originalTenure;

    private String remainingTenure;
    private String lateCharges;

    private String misc;
    private String otherCharges;
    private String originalLoanAmt;

    private String settlementAmount;
    private String instalmentAmtOverdue;

    private String acctStatusDesc;
    private String islamicFlag;
    private boolean isIslamic;

    private String tlastIndicator;
    private ESBMetadataDTO esbMetadata;
    private MoreActionsEntitlement moreActions;

}
