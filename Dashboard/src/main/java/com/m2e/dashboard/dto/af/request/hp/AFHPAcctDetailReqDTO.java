package com.m2e.dashboard.dto.af.request.hp;

import com.m2e.dashboard.enums.AccountLevelEnum;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AFHPAcctDetailReqDTO {
    private String acctNumber;
    private ProviderEnum provider;
    private AccountLevelEnum accountLevel;
    private String corpCifNo;
    private String currency;
}
