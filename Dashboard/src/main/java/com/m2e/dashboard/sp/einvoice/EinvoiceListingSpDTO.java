package com.m2e.dashboard.sp.einvoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EinvoiceListingSpDTO implements Serializable {
    private String corpId;
    private String corpCifNo;
    private String corpName;
    private String businessReqNumber;
    private String status;
}
