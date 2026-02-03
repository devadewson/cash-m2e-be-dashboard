package com.m2e.dashboard.dto.loan.tlf.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.loan.tlf.response.LNTLFAcctListingSpDTO;
import lombok.Data;

@Data
public class LNTLFAcctListingReportDTO {
    private String corpCifNo;
    private SearchCollectionResponse<LNTLFDTO> datas;
}