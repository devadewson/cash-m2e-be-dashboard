package com.m2e.dashboard.dto.loan.overdraft.response;

import com.m2e.dashboard.dto.base.AccountDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class LNOverdraftDTO extends AccountDTO {
    private String overdraftType;
    private String overdraftLimit;
    private String utilisedAmount;
    private String unutilisedAmount;
    private String sharingOverdraftGroup;
}