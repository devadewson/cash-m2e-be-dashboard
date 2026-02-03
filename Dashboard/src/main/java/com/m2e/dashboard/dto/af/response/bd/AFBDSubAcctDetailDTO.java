package com.m2e.dashboard.dto.af.response.bd;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class AFBDSubAcctDetailDTO implements BaseESBMetadata {
    private String currency;
    private String lastUpdatedDateTime;

    private String acctName;
    private String companyName;
    private String agreementNumber;

    private String productName;
    private String financedAmt;
    private String outstandingBalance;

    private String utilisedAmt;
    private String numberOfInstalmentPaid;
    private String numberOfInstalmentInArreas;

    private String installmentAmount;
    private String amountDue;
    private String dueDate;
    private String settlementAmount;
    private String agreementStatus;

    private boolean isIslamic;
    private String islamicFlag;

    private String acctStatusDesc;
    private ESBMetadataDTO esbMetadata;
    private MoreActionsEntitlement moreActions;

}
