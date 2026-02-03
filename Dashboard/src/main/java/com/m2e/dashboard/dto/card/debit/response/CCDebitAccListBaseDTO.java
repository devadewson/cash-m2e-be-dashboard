package com.m2e.dashboard.dto.card.debit.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class CCDebitAccListBaseDTO {
    private String cardName;
    private String cardNumber;
    private String cardCurrency;
    private String cardHolderName;
}
