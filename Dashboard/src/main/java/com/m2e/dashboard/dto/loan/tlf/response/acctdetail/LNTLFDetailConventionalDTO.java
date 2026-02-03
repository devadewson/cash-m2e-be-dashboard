package com.m2e.dashboard.dto.loan.tlf.response.acctdetail;

import com.m2e.dashboard.dto.loan.tlf.response.LNTLFDetailDTO;
import com.m2e.dashboard.enums.LNProductType;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LNTLFDetailConventionalDTO extends LNTLFDetailDTO implements BaseESBMetadata {
    private LNProductType productType;
    private String term;
    private String outstandingAmount;
    private String installmentAmount;
    private String installmentDueAmount;
    private String installmentDueDate;
    private String installmentFrequency;
    private String interestRate;
    private String interestDueAmount;
    private String interestDueDate;
    private String interestFrequency;
    private String firstDrawdownDate;
    private String lastDrawdownDate;
    private String paymentOption;
    private String paymentInAdvanceAmount;
    private String miscellaneousChargesAmount;
    private String interestOnMiscellaneous;
    private String commitmentFeesAmount;
    private String penaltyInterest;
    private String sstDueAmount;
    private ESBMetadataDTO esbMetadata;
}
