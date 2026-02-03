package com.m2e.dashboard.dto.af.response.fs;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFFSTrxHistoryResp implements Serializable, BaseESBMetadata {
    private SearchCollectionResponse<AFFSTrxHistDTO> datas;
    private ESBMetadataDTO esbMetadata;
}
