package com.m2e.dashboard.dto.einvoice.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EInvoiceStatementListingReqDTO extends BasePageRequest {
    private String acctNumber;
    private String startDate;
    private String endDate;
}
