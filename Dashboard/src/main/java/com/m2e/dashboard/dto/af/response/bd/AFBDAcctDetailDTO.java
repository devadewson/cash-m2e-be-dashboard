package com.m2e.dashboard.dto.af.response.bd;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class AFBDAcctDetailDTO implements BaseESBMetadata {
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

    private String installmentAmount;
    private String amountDue;
    private String settlementAmount;

    private String numberOfAgreement;
    private String dueDate;
    private String blockStatus;

    private String debitNote;

    private String acctStatusDesc;
    private boolean isIslamic;
    private String islamicFlag;

    private ESBMetadataDTO esbMetadata;
    private MoreActionsEntitlement moreActions;

}
