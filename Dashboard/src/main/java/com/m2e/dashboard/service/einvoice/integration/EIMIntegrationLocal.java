package com.m2e.dashboard.service.einvoice.integration;

import com.m2e.dashboard.dto.einvoice.request.EIMUpdateLinkedStatusReqDTO;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceRequestLinkedStatusReqDTO;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceStatementListingReqDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMRequestLinkedStatusDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMStatementDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMUpdatedLinkedStatusDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@Slf4j
public class EIMIntegrationLocal implements EIMIntegration {

    public EIMIntegrationLocal() {
        log.info("EIM Integration LOCAL MODE - Dummy implementation");
    }

    @Override
    public byte[] getDownloadByteData(String url) {
        log.debug("EIM getDownloadByteData called - LOCAL MODE");
        return new byte[0];
    }

    @Override
    public EIMRequestLinkedStatusDTO requestLinkedStatusDTO(String userId, EInvoiceRequestLinkedStatusReqDTO data) {
        log.debug("EIM requestLinkedStatusDTO called - LOCAL MODE");
        return null;
    }

    @Override
    public EIMUpdatedLinkedStatusDTO updatedEInvoiceLinkedStatus(EIMUpdateLinkedStatusReqDTO data) {
        log.debug("EIM updatedEInvoiceLinkedStatus called - LOCAL MODE");
        return null;
    }

    @Override
    public EIMStatementDTO getListEInvoice(String userId, EInvoiceStatementListingReqDTO req) {
        log.debug("EIM getListEInvoice called - LOCAL MODE");
        return new EIMStatementDTO();
    }
}