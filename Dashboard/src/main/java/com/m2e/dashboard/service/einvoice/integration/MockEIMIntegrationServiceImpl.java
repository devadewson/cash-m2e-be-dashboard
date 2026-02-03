package com.m2e.dashboard.service.einvoice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.dto.einvoice.request.EIMUpdateLinkedStatusReqDTO;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceRequestLinkedStatusReqDTO;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceStatementListingReqDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMInvoiceDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMRequestLinkedStatusDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMStatementDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMUpdatedLinkedStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Primary
@ConditionalOnProperty(name = "m2e.implementation", havingValue = "performance-test")
public class MockEIMIntegrationServiceImpl extends EIMIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(MockEIMIntegrationServiceImpl.class);

    public MockEIMIntegrationServiceImpl() {
        super(null, new ObjectMapper(), null);
        log.warn("Using MOCK EIMIntegrationService (performance-test)");
    }

    @Override
    public byte[] getDownloadByteData(String url) {
        log.info("[MOCK] getDownloadByteData url={}", url);
        // Return a simple fake PDF/CSV/bytes
        return "FAKE_EINVOICE_BYTES".getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public EIMRequestLinkedStatusDTO requestLinkedStatusDTO(String userId, EInvoiceRequestLinkedStatusReqDTO data) {
        log.info("[MOCK] requestLinkedStatusDTO userId={}, data={}", userId, data);
        EIMRequestLinkedStatusDTO dto = new EIMRequestLinkedStatusDTO();
        dto.setGcif(data.getCifNo());
        dto.setIsLinked("Y");
        return dto;
    }

    @Override
    public EIMUpdatedLinkedStatusDTO updatedEInvoiceLinkedStatus(EIMUpdateLinkedStatusReqDTO data) {
        log.info("[MOCK] updatedEInvoiceLinkedStatus, body={}", data);
        EIMUpdatedLinkedStatusDTO dto = new EIMUpdatedLinkedStatusDTO();
        dto.setGcif(data.getGcif());
        dto.setStatus("DONE");
        dto.setAction("UPDATE");
        dto.setMessage("Successfully updated!");
        return dto;
    }

    @Override
    public EIMStatementDTO getListEInvoice(String userId, EInvoiceStatementListingReqDTO req) {
        log.info("[MOCK] getListEInvoice userId={}, req={}", userId, req);
        EIMStatementDTO dto = new EIMStatementDTO();
        dto.setPageNo(req.getPage());
        dto.setPageSize(req.getPageSize());
        dto.setTotal(2);
        dto.setHasNextPage("-1");
        List<EIMInvoiceDTO> invoices = List.of(
                createMockInvoice("INV-MOCK-001", "SRC-001", "Sales", "MYR", "000712361000020"),
                createMockInvoice("INV-MOCK-002", "SRC-002", "Credit Note", "MYR", "000712361000020")
        );
        dto.setInvoiceList(invoices);
        return dto;
    }
    private EIMInvoiceDTO createMockInvoice(String invoiceNumber, String sourceInvoiceNumber, String type,
                                            String currencyCode, String accountNumber) {
        EIMInvoiceDTO inv = new EIMInvoiceDTO();
        inv.setInvoiceNumber(invoiceNumber);
        inv.setSourceInvoiceNumber(sourceInvoiceNumber);
        inv.setType(type);
        inv.setCurrencyCode(currencyCode);
        inv.setAccountNumber(accountNumber);
        inv.setDateTimeIssued(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        inv.setLhdnValidationStatus("VALIDATED");
        inv.setSource("MOCK_SOURCE");
        inv.setEntity("MOCK_ENTITY");
        inv.setEntityCode("MCK01");
        inv.setDocumentId("DOC-" + invoiceNumber);
        inv.setFilename(invoiceNumber + ".pdf");
        inv.setDownloadUrl("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
        return inv;
    }
}