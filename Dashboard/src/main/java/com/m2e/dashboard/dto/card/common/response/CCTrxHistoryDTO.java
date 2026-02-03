package com.m2e.dashboard.dto.card.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CCTrxHistoryDTO {
    private String trxDate;
    private String trxDescription;
    private String debit;
    private String credit;
    private String currency;
}
