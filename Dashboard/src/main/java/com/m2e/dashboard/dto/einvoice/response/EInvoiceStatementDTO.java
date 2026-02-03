package com.m2e.dashboard.dto.einvoice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EInvoiceStatementDTO implements Serializable {
    private String stmtDate;
    private String acctName;
    private String acctNumber;
    private String currency;
    private String downloadUrl;
    private Boolean isSME;
    private String filename;
}
