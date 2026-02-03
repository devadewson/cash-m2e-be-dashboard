package com.m2e.dashboard.dto.loan.tlf.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class LNTLFTrancheListDTO implements Serializable, BaseESBMetadata {
    private SearchCollectionResponse<LNTLFTrancheDTO> datas;
    private ESBMetadataDTO esbMetadata;
}
