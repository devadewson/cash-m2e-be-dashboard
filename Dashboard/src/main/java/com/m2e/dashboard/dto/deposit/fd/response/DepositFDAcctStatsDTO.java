package com.m2e.dashboard.dto.deposit.fd.response;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class DepositFDAcctStatsDTO implements BaseESBMetadata {
    private String lastUpdatedDate;
    private String lastUpdatedDateTime;
    private String numberOfAcct;
    private String totalOfCertificates;
    private String consolidatedPrincipalAmt;
    private String currency;
    private ESBMetadataDTO esbMetadata;
}
