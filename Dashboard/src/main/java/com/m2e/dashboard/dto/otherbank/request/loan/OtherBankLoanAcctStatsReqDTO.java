package com.m2e.dashboard.dto.otherbank.request.loan;

import com.m2e.dashboard.enums.OtherBankViewType;
import lombok.Data;

@Data
public class OtherBankLoanAcctStatsReqDTO {
    private OtherBankViewType viewType;
}
