package com.m2e.dashboard.dto.loan.rc.response;

import com.m2e.dashboard.dto.base.AccountStatisticDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
public class LNRevolvingCreditAccountStatisticDTO extends AccountStatisticDTO {
    private String consolidatedDrawdownAmount;
    private String consolidatedOutstandingBalanceAmount;
    private String currency;

}
