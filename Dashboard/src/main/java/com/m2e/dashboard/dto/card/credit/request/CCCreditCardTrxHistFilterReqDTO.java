package com.m2e.dashboard.dto.card.credit.request;

import lombok.Data;

@Data
public class CCCreditCardTrxHistFilterReqDTO {
    private String startDate;
    private String endDate;
    private String selectedCardMember;
}
