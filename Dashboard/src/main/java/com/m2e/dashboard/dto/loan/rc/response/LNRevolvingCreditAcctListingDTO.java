package com.m2e.dashboard.dto.loan.rc.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import lombok.Data;

@Data
public class LNRevolvingCreditAcctListingDTO {
    private String corpCifNo;
    private SearchCollectionResponse<LNRevolvingCreditDTO> datas;
}