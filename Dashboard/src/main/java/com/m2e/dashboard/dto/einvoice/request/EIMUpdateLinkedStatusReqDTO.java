package com.m2e.dashboard.dto.einvoice.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class EIMUpdateLinkedStatusReqDTO implements Serializable {
    private String gcif;
    private String tin;
    private String linked;
    private String idType;
    // Removed due to ticket M2UBIZM25-64143
//    private String idValue;
    private String reqChannel; // M2U, MAE, M2E
    private String reqLoginId;
    private String reqDateTime; // Format: YYYYMMDDHHMMSS
    private String reqReferenceId;
}
