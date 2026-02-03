package com.m2e.dashboard.dto.einvoice.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EInvoiceStatementListingDTO implements Serializable {
    private SearchCollectionResponse<EInvoiceStatementDTO> datas;
}
