package com.m2e.dashboard.dto.loan.overdraft.response;

import com.m2e.dashboard.dto.base.AccountStatisticDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class LNOverdraftAccountStatisticDTO extends AccountStatisticDTO {
    private String consolidatedOverdraftLimitAmount;
    private String consolidatedUtilisedAmount;
    private String currency;
}
