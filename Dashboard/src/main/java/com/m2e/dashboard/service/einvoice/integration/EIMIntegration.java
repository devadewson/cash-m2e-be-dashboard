package com.m2e.dashboard.service.einvoice.integration;

import com.m2e.dashboard.dto.einvoice.request.EIMUpdateLinkedStatusReqDTO;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceRequestLinkedStatusReqDTO;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceStatementListingReqDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMRequestLinkedStatusDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMStatementDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMUpdatedLinkedStatusDTO;

public interface EIMIntegration {
    byte[] getDownloadByteData(String url);
    EIMRequestLinkedStatusDTO requestLinkedStatusDTO(String userId, EInvoiceRequestLinkedStatusReqDTO data);
    EIMUpdatedLinkedStatusDTO updatedEInvoiceLinkedStatus(EIMUpdateLinkedStatusReqDTO data);
    EIMStatementDTO getListEInvoice(String userId, EInvoiceStatementListingReqDTO req);
}