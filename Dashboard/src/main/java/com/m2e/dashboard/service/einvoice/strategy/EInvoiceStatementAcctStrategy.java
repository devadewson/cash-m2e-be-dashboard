package com.m2e.dashboard.service.einvoice.strategy;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.einvoice.response.EInvoiceStatementAcctDTO;

import java.util.List;

public interface EInvoiceStatementAcctStrategy {
    boolean supports(String acctType);
    ApiResponse<List<EInvoiceStatementAcctDTO>> execute(UserAndCorp auth, String acctType, String searchParam);
}
