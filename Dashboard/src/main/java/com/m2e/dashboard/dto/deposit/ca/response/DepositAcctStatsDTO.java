package com.m2e.dashboard.dto.deposit.ca.response;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class DepositAcctStatsDTO implements BaseESBMetadata {
    private String numberOfAcct;
    private String totalLedgerBal;
    private String totalAvailableBal;
    private String currency;
    private String lastUpdatedDate;
    private String lastUpdatedDateTime;
    private ESBMetadataDTO esbMetadata;


}
