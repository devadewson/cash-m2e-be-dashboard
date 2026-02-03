package com.m2e.dashboard.dto.af.response.bd;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class AFBDDebitNoteSummaryDTO implements BaseESBMetadata {
    private SearchCollectionResponse<AFBDDebitNoteListDTO> datas;
    private ESBMetadataDTO esbMetadata;
}
