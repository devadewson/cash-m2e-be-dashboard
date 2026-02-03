package com.m2e.dashboard.dto.deposit.ca.response;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class DepositCAAcctOmnibusDetailDTO implements BaseESBMetadata {
    private String odLimit;
    private String unUtilisedOD;
    private ESBMetadataDTO esbMetadata;
}
