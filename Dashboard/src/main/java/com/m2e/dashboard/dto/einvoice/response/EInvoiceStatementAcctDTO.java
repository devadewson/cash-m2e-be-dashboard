package com.m2e.dashboard.dto.einvoice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EInvoiceStatementAcctDTO implements Serializable {
    private String acctNumber;
    private String acctName;
}
