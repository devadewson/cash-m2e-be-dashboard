package com.m2e.dashboard.dto.einvoice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EInvoiceCorpDetailDTO implements Serializable {
    private String corpName;
    private String brn;
    private String contactNumber;
    private String emailAddress;
    private String status;

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String stateProvince;
    private String prefecture;
    private String cityDistrict;
    private String postcode;
    private String country;

    private String tin;
    private String sst;
    private Boolean eInvoiceConsent;
}
