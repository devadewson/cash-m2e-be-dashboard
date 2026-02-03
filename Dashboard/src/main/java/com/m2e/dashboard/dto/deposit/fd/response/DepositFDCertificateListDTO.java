package com.m2e.dashboard.dto.deposit.fd.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class DepositFDCertificateListDTO implements BaseESBMetadata {
    private SearchCollectionResponse<DepositFDCertificateDetailDTO.FullData> datas;
    private String acctNumber;
    private String acctName;
    private String currency;
    private String acctProductCode;
    private Boolean isIslamic;
    private ESBMetadataDTO esbMetadata;
}
