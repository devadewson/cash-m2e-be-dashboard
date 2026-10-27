package com.m2e.dashboard.dto.loan.overdraft.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import lombok.Data;

@Data
public class LNOverdraftAcctListingDTO {
    private String corpCifNo;
    private String plafond;
    private String limitAwal;
    private SearchCollectionResponse<LNOverdraftDTO> datas;
}