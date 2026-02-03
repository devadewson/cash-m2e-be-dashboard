package com.m2e.dashboard.dto.einvoice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EInvoiceCorpDetailReqDTO implements Serializable {
    private String brn;
    private String companyName;
    private String status;
    private String cifNo;
}
