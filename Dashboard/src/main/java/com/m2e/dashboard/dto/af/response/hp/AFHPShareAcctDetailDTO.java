package com.m2e.dashboard.dto.af.response.hp;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class AFHPShareAcctDetailDTO implements BaseESBMetadata {

    //    Share Attribute
    private String acctName;
    private String companyName;
    private String acctNumber;
    private String currency;
    private String lastUpdatedDateTime;

    private String productName;
    private String otherCharges;
    private String productType;
    private boolean isIslamic;

    private String acctStatusDesc;

    private ESBMetadataDTO esbMetadata;

}
