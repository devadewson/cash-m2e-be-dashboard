package com.m2e.dashboard.dto.card.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CCStatsDTO {
    private String lastUpdatedTime;
    private String numberOfPrincipalCard;
    private String creditLimit;
    private String outstandingBalance;
    private String currency;
}
