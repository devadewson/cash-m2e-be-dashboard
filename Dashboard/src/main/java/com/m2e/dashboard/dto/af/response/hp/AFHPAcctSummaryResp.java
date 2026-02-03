package com.m2e.dashboard.dto.af.response.hp;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFHPAcctSummaryResp implements Serializable, BaseESBMetadata {
    private String corpCifNo;
    private SearchCollectionResponse<AFHPAcctListDTO> datas;
    private ESBMetadataDTO esbMetadata;
}
