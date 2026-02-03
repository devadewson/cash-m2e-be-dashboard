package com.m2e.dashboard.dto.deposit.ca.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import lombok.Data;

@Data
public class DepositAcctListingDTO implements BaseESBMetadata {
    private String corpCifNo;      // mandatory attribute based on FE requirement to fix accordion problem
    private SearchCollectionResponse<DepositAcctSummarySpDTO> datas;
    private ESBMetadataDTO esbMetadata;
}
