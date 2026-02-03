package com.m2e.dashboard.dto.otherbank.request.loan;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.OtherBankViewType;
import lombok.Data;

@Data
public class OtherBankLoanAcctListReqDTO extends BasePageRequest {
    private String searchParam;
    private OtherBankViewType viewType;
}
