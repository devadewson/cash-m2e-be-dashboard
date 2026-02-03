package com.m2e.dashboard.dto.loan.rc.response;

import com.m2e.dashboard.dto.base.AccountDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class LNRevolvingCreditDTO extends AccountDTO {
    private String drawdownAmount;
    private String outstandingBalance;
    private String provider;
    boolean isIslamic;
    boolean isFITAS;
}
