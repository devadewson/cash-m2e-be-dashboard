package com.m2e.dashboard.dto.einvoice.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class EIMInvoiceDTO implements Serializable {
    private String sourceInvoiceNumber;
    private String invoiceNumber;
    private String type;
    private String currencyCode;
    private String accountNumber;
    private String dateTimeIssued;
    private String lhdnValidationStatus;
    private String source;
    private String entity;
    private String entityCode;
    private String documentId;
    private String filename;
    private String downloadUrl;
}
