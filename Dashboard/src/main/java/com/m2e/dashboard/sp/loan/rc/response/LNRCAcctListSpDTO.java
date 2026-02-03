package com.m2e.dashboard.sp.loan.rc.response;

import com.m2e.dashboard.dto.base.AccountDTO;
import com.m2e.dashboard.dto.loan.rc.response.LNRevolvingCreditDetailDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class LNRCAcctListSpDTO extends AccountDTO {
    private String drawdownAmount;
    private String outstandingBalance;
    private String provider;
    private String isIslamic;
    private String isFITAS;
}
