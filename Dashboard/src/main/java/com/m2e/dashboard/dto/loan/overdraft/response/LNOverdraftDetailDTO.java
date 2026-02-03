package com.m2e.dashboard.dto.loan.overdraft.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@SuperBuilder
public class LNOverdraftDetailDTO implements Serializable {
    private String acctNumber;
    private String currency;
    private String utilisedAmount;
    private String unutilisedAmount;
    private String overdraftLimitAmount;
    private String intradayODLimitAmount;
    private String overdraftType;
}
