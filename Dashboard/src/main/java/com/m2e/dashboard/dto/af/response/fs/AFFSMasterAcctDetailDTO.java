package com.m2e.dashboard.dto.af.response.fs;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AFFSMasterAcctDetailDTO extends AFFSAcctDetailDTO implements BaseESBMetadata {
    private String productName;
    private String acctNumber;
    private String facilityLimit;
    private String financedAmt;
    private String productType;
    private String numberOfSubAcct;
    private String acctStatus;
    private ESBMetadataDTO esbMetadata;
}