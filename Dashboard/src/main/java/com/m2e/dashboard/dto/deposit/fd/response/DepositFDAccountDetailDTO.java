package com.m2e.dashboard.dto.deposit.fd.response;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class DepositFDAccountDetailDTO implements BaseESBMetadata {
    private String acctName;
    private String companyName;
    private String acctNumber;
    private String currency;
    private String lastUpdatedDateTime;

    private String productName;
    private String principalAmount;
    private String noOfCertificates;
    private String acctType;
    private String productCode;
    private String acctStatusDesc;
    private ESBMetadataDTO esbMetadata;
}
