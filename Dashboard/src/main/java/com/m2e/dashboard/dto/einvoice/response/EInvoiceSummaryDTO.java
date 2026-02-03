package com.m2e.dashboard.dto.einvoice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EInvoiceSummaryDTO implements Serializable {
    private String bankRef;
    private String tin;
    private String sst;

    private String companyName;
    private String brn;
}
