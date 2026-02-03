package com.m2e.dashboard.dto.loan.tlf.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LNTLFAcctListingDTO implements BaseESBMetadata {
    private String corpCifNo;
    private SearchCollectionResponse<LNTLFDTO> datas;
    private ESBMetadataDTO esbMetadata = new ESBMetadataDTO();
}