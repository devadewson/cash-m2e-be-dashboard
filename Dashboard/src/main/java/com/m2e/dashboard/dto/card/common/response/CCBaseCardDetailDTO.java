package com.m2e.dashboard.dto.card.common.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class CCBaseCardDetailDTO {
    private String cardName;
    private String companyName;
    private String cardNumber;
    private String currency;
    private String lastUpdatedDateTime;

    private String outstandingBalance;

    private String productName;
}
