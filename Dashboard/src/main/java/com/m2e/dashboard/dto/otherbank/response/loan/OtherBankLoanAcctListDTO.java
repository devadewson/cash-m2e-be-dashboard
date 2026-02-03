package com.m2e.dashboard.dto.otherbank.response.loan;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.otherbank.response.OtherBankLoanAcctListSpDTO;
import lombok.Data;

@Data
public class OtherBankLoanAcctListDTO {
    private SearchCollectionResponse<OtherBankLoanAcctListSpDTO> datas;
}
