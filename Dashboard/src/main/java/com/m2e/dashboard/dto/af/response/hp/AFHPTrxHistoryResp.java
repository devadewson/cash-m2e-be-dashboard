package com.m2e.dashboard.dto.af.response.hp;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFHPTrxHistoryResp implements Serializable, BaseESBMetadata {
    private SearchCollectionResponse<AFHPTrxHistDTO> datas;
    private ESBMetadataDTO esbMetadata;
}
