package com.m2e.dashboard.dto.card.common.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class CCAccListBaseDTO {
    private String cardName;
    private String cardNumber;
    private String cardCurrency;
    private String creditLimit;
    private String outstandingBalance;
}
