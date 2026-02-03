package com.m2e.dashboard.dto.af.response.fs;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFFSAcctListViewSummaryResp<Object> implements Serializable, BaseESBMetadata {
    private String corpCifNo;      // for FE accordion
    private SearchCollectionResponse<Object> datas;
    private ESBMetadataDTO esbMetadata;
}
