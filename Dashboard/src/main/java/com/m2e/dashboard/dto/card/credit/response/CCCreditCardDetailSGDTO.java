package com.m2e.dashboard.dto.card.credit.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class CCCreditCardDetailSGDTO extends CCCreditCardDetailMYDTO {
    private String cardholderName;
}