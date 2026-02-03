package com.m2e.dashboard.dto.deposit.ca.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class DepositCATrxHistoryDTO implements BaseESBMetadata {
    private SearchCollectionResponse<DepositTrxHistoryDTO> datas;
    private ESBMetadataDTO esbMetadata;
}
