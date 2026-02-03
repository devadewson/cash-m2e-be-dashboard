package com.m2e.dashboard.dto.card.credit.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class CCCreditCardMemberSGDTO extends CCCreditCardMemberDTO{
    private String creditLimit;
    private String availableCreditLimit;
    private String outstandingBalance;
    private String currency;
}
