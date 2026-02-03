package com.m2e.dashboard.dto.deposit.va.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class DepositVATrxHistoryDTO implements BaseESBMetadata {
    SearchCollectionResponse<DepositVATransHistSearchDTO> datas;
    private ESBMetadataDTO esbMetadata;
}
