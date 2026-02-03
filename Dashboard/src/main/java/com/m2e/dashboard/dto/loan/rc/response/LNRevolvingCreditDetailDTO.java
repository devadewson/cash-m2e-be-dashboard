package com.m2e.dashboard.dto.loan.rc.response;

import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@SuperBuilder
public class LNRevolvingCreditDetailDTO implements Serializable {
    private String acctName;
    private String acctNumber;
    private String currency;
    private String accountOwnerCompany;
    private String lastUpdated; // format: 01/02/2024. HH:mm:ss (GMT+8)
    private ProviderEnum provider;
    boolean isIslamic;
    boolean isFITAS;
}
