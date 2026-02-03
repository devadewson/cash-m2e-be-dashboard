package com.m2e.dashboard.dto.deposit.fd.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import com.m2e.dashboard.sp.deposit.fd.response.DepositFDAcctSummarySpDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class DepositFDAcctSummaryDTO implements BaseESBMetadata {
    private String corpCifNo;     // mandatory attribute based on FE requirement to fix accordion problem
    private SearchCollectionResponse<DepositFDAcctSummarySpDTO> datas;
    private ESBMetadataDTO esbMetadata;
}
