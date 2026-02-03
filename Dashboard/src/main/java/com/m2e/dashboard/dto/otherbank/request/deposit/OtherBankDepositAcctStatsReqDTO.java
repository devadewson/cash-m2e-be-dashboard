package com.m2e.dashboard.dto.otherbank.request.deposit;

import com.m2e.dashboard.enums.OtherBankViewType;
import lombok.Data;

@Data
public class OtherBankDepositAcctStatsReqDTO {
    private String corpId;
    private OtherBankViewType viewType;
}
