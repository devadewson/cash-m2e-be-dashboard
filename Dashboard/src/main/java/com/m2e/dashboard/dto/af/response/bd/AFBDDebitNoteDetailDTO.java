package com.m2e.dashboard.dto.af.response.bd;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class AFBDDebitNoteDetailDTO implements BaseESBMetadata {
    private String debitNoteNumber;
    private String currency;
    private String lastUpdatedDateTime;

    private String acctNumber;
    private String outstandingBalance;
    private String masterAcctNumber;
    private String provider;

    private ESBMetadataDTO esbMetadata;
}
