package com.m2e.dashboard.dto.loan.tlf.response;

import com.m2e.dashboard.dto.base.AccountStatisticDTO;
import lombok.Data;

@Data
public class LNTLFAccountStatisticDTO extends AccountStatisticDTO {
    private String consolidatedDrawdownAmount;
    private String currency;
    private String consolidatedOutstandingBalanceAmount;
}
