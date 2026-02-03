package com.m2e.dashboard.controller.einvoice;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceStatementDownloadReqDTO;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceStatementListingReqDTO;
import com.m2e.dashboard.dto.einvoice.response.EInvoiceStatementListingDTO;
import com.m2e.dashboard.service.einvoice.EInvoiceStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

@Tag(name = "e-Invoice Statement Reporting", description = "APIs for managing portfolio-related e-Invoice statement operations")
@RestController
@RequestMapping("report/e-invoice/statement/v1")
@RequiredArgsConstructor
public class EInvoiceStatementController {
    private final EInvoiceStatementService eInvoiceStatementService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "[PIC::Weixian] Get e-Invoice statements.",
            description = "Fetches a list of e-invoice statements based on the parameters provided.")
    @PostMapping("/listing")
    public ResponseEntity<ApiResponse<EInvoiceStatementListingDTO>> eInvoiceStatementListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody EInvoiceStatementListingReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(eInvoiceStatementService.eInvoiceStatementListing(userAndCorp, req));
    }

    @Operation(
            summary = "[PIC::Weixian] Download e-Invoice Statement.",
            description = "Download the statement for a selected e-invoice record."
    )
    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> downloadEInvoiceStatement(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody EInvoiceStatementDownloadReqDTO req) throws IOException {
        DownloadDTO result = eInvoiceStatementService.downloadEInvoiceStatement(req);
        HttpHeaders headers = setHttpHeadersForDownload(result);
        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
    }


}
