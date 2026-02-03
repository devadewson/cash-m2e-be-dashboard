package com.m2e.dashboard.dto.loan.tlf.response.acctdetail;

import com.m2e.dashboard.dto.loan.tlf.response.LNTLFDetailDTO;
import com.m2e.dashboard.enums.LNProductType;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LNTLFDetailSGSIBSDTO extends LNTLFDetailDTO implements BaseESBMetadata {
    private LNProductType productType;
    private String monthlyInstallmentAmount;
    private String nextPaymentDueDate;
    private String lastPaymentDate;
    private String interestAmount;
    private String defaultLateInterest;
    private String paymentType;
    private String totalPrincipalAmountBilledAmount;
    private ESBMetadataDTO esbMetadata;
}
