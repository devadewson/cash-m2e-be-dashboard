package com.m2e.dashboard.dto.einvoice.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.einvoice.EinvoiceListingSpDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class EInvoiceListingDTO implements Serializable {
    private SearchCollectionResponse<EinvoiceListingSpDTO> datas;
}
