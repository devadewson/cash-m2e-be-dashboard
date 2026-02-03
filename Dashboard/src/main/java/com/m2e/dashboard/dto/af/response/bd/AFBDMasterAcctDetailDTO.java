package com.m2e.dashboard.dto.af.response.bd;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class AFBDMasterAcctDetailDTO implements BaseESBMetadata {
    private String currency;
    private String lastUpdatedDateTime;

    private String acctName;
    private String companyName;
    private String acctNumber;

    private String facilityLimit;
    private String financedAmt;
    private String outstandingBalance;

    private String productName;
    private String productType;
    private String numberOfAcct;
    private String acctStatus;

    private String acctStatusDesc;
    private boolean isIslamic;
    private String islamicFlag;

    private ESBMetadataDTO esbMetadata;
    private MoreActionsEntitlement moreActions;
}
