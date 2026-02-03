package com.m2e.dashboard.dto.deposit.ca.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class DepositAcctFloatDTO implements BaseESBMetadata {
    private String expiryDate;
    private String totalFloat;
    private String currency;
    private SearchCollectionResponse<DepositAcctFloatDtlDTO> depositAcctFloatDetails;
    private ESBMetadataDTO esbMetadata;
}
