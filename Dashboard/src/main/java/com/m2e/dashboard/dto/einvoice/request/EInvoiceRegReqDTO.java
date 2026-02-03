package com.m2e.dashboard.dto.einvoice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
public class EInvoiceRegReqDTO implements Serializable {
    private String corpId;
    private String corpCifNo;
    private String brn;
    private String tin;
    private String ssn;
}
