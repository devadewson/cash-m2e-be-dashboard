package com.m2e.dashboard.dto.loan.tlf;


import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoanPbFacilityListingDTO implements BaseESBMetadata, Serializable {
    private String corpCifNo;
    private String plafond;
    private String limitAwal;
    private SearchCollectionResponse<LoanPbFacilityDTO> datas;
    private ESBMetadataDTO esbMetadata = new ESBMetadataDTO();
}
