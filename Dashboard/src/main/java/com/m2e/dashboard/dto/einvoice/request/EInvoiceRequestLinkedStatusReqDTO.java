package com.m2e.dashboard.dto.einvoice.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class EInvoiceRequestLinkedStatusReqDTO implements Serializable {
    private String cifNo;
    private String brn;
}
