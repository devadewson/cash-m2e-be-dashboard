package com.m2e.dashboard.dto.loan.tlf.response;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
public class LNTLFTranchesDetailIslamicDTO implements BaseESBMetadata {
    private String currency;
    private String trancheNumber;
    private String drawdownAmount;
    private String profitRate;
    private String profitPaymentFrequency;
    private String profitDueDate; // format: dd MMM yyyy
    private String profitDueAmount;
    private String latePaymentChargesOnProfitDueAmount;
    private ESBMetadataDTO esbMetadata = new ESBMetadataDTO();
}
