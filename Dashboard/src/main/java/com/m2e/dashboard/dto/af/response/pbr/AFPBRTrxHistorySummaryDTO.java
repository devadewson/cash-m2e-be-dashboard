package com.m2e.dashboard.dto.af.response.pbr;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class AFPBRTrxHistorySummaryDTO implements BaseESBMetadata {
    private SearchCollectionResponse<AFPBRTrxHistoryDTO> datas;
    private ESBMetadataDTO esbMetadata;
}
