package com.m2e.dashboard.dto.loan.tlf.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.deposit.ca.response.DepositTrxHistoryDTO;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class LNTLFTrxHistoryResponseDTO implements BaseESBMetadata {
    private SearchCollectionResponse<LNTLFTrxHistoryDTO> datas;
    private ESBMetadataDTO esbMetadata;

    public LNTLFTrxHistoryResponseDTO() {
        this.datas = new SearchCollectionResponse<>();
        this.esbMetadata = new ESBMetadataDTO();
    }
}
