package com.m2e.dashboard.dto.einvoice.request;

import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class EInvoiceListingDownloadReqDTO extends DownloadReq {
    private String searchParam;
}
