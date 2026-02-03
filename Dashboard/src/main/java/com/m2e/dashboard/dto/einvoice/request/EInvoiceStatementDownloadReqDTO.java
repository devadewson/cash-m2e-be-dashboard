package com.m2e.dashboard.dto.einvoice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EInvoiceStatementDownloadReqDTO implements Serializable {

    private String stmtDate;
    private String acctName;
    private String acctNumber;
    private String currency;
    private String filename;
    private String downloadUrl;
    private Boolean isSME;
}
