package com.m2e.dashboard.dto.card.credit.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class CCCreditCardMemberDTO {
    private String cardNumber;
    private String cardHolderName;
}
