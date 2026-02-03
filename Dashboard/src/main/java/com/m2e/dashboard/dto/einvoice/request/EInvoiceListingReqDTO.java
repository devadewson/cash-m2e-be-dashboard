package com.m2e.dashboard.dto.einvoice.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class EInvoiceListingReqDTO extends BasePageRequest {
    private String searchParam;
}
