package com.m2e.dashboard.dto.einvoice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EInvoiceStatementAcctReqDTO implements Serializable {
    private String acctType;
    private String searchParam;
}
