package com.m2e.dashboard.dto.einvoice.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class EInvoiceStatementPeriodDTO implements Serializable {
    private String month;
    private int year;
    private String description;
    private Long startDate;
    private Long endDate;
}
