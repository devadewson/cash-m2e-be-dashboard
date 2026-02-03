package com.m2e.dashboard.dto.af.response.fs;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AFFSCertDetailDTO extends AFFSAcctDetailDTO implements BaseESBMetadata {
    private String productName;
    private String certNo;
    private String agreementNumber; //todo
    private String financedAmt;
    private String interestCharged;
    private String amtDue;
    private String settlementAmt;
    private String solaStatus;
    private String expiryDate;
    private MoreActionsEntitlement moreActions;
    private ESBMetadataDTO esbMetadata;
}