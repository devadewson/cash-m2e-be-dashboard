package com.m2e.dashboard.dto.card.debit.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class CCDebitCardListSGDTO extends CCDebitAccListBaseDTO {
    private String reinstatementDate;
    private String dailySpendingLimit;
    private String monthlySpendingLimit;
}
