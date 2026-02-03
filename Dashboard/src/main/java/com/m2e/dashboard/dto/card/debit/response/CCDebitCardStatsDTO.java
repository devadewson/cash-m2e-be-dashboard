package com.m2e.dashboard.dto.card.debit.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class CCDebitCardStatsDTO {
    private String lastUpdatedTime;
    private String numberOfCard;
}
