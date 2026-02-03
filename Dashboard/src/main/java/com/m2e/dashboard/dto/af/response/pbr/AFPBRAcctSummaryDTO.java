package com.m2e.dashboard.dto.af.response.pbr;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFPBRAcctSummaryDTO implements BaseESBMetadata {
    private String corpCifNo;
    private SearchCollectionResponse<?> datas;
    private ESBMetadataDTO esbMetadata;
}
