package com.m2e.dashboard.dto.investment.response;

import lombok.Data;


@Data
public class INVUnitTrustTransHistSearchDTO {
    private String id;
    private String fundName;
    private String trxDesc;
    private String description3;
    private String description4;
    private String nav;
    private String currency;
    private String units;
    private String transactionAmount;
    private String transactionDate;

    private String transactionType;
    private String debit;
    private String credit;

}
