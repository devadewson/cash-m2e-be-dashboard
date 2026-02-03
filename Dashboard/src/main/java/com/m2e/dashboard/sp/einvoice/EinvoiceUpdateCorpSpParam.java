package com.m2e.dashboard.sp.einvoice;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EinvoiceUpdateCorpSpParam {
    private String corpId;
    private String taxId;
    private String sstNo;
}
