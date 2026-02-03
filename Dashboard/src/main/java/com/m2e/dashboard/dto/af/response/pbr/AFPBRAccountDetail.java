package com.m2e.dashboard.dto.af.response.pbr;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class AFPBRAccountDetail implements BaseESBMetadata {
    private String acctName;
    private String acctNumber;
    private String currency;
    private String lastUpdated;
    private String numberOfDays;
    private String productName;
    private String islamicFlag;
    private boolean isIslamic;

    private ESBMetadataDTO esbMetadata;
}
