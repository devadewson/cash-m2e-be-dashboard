//package com.m2e.dashboard.controller.einvoice;
//
//import com.m2e.dashboard.auth.JwtUtil;
//import com.m2e.dashboard.config.UserAndCorp;
//import com.m2e.dashboard.dto.base.DownloadDTO;
//import com.m2e.dashboard.dto.common.ApiResponse;
//import com.m2e.dashboard.dto.einvoice.request.*;
//import com.m2e.dashboard.dto.einvoice.response.EInvoiceCorpDetailDTO;
//import com.m2e.dashboard.dto.einvoice.response.EInvoiceListingDTO;
//import com.m2e.dashboard.dto.einvoice.response.EInvoiceValidateTINDTO;
//import com.m2e.dashboard.dto.task.response.EInvoiceRegApprovalDTO;
//import com.m2e.dashboard.service.einvoice.EInvoiceService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;
//
//@Tag(name = "e-Invoice Administration", description = "APIs for managing portfolio-related e-Invoice operations")
//@RestController
//@RequestMapping("admin/e-invoice/v1")
//@RequiredArgsConstructor
//public class EInvoiceController {
//
//    private final EInvoiceService eInvoiceService;
//    private final JwtUtil jwtUtil;
//
//    @Operation(
//            summary = "[PIC::Weixian] Get e-Invoice Listings.",
//            description = "Fetches a list of companies associated with a specified subscriber company for e-invoicing purposes.")
//    @PostMapping("/listing")
//    public ResponseEntity<ApiResponse<EInvoiceListingDTO>> eInvoiceListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody EInvoiceListingReqDTO req) {
//        // create an SP to get list of company under selected subscriber company (if no open tagged company, need to display the selected subscriber company onlu)
//        // call e-Invoice Linked Status via API per company to determine the status
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        return ResponseEntity.ok((eInvoiceService.eInvoiceListing(userAndCorp, req)));
//    }
//
//    @Operation(
//            summary = "[PIC::Roland] Get Corporate Details by Business Registration Number.",
//            description = "Retrieves detailed information for a corporate entity using its business registration number.")
//    @PostMapping("/detail")
//    public ResponseEntity<ApiResponse<EInvoiceCorpDetailDTO>> getEInvoiceCorpDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody EInvoiceCorpDetailReqDTO req) {
//        return ResponseEntity.ok(eInvoiceService.getCorpDetails(req));
//    }
//
//    @Operation(
//            summary = "[PIC::Caixin] Validate Tax Identification Number (TIN).",
//            description = "Checks if a provided Tax Identification Number (TIN) is valid.")
//    @PostMapping("/validate-tin")
//    public ResponseEntity<ApiResponse<EInvoiceValidateTINDTO>> validateTIN(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody EInvoiceValidateTINReqDTO req) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        return ResponseEntity.ok((eInvoiceService.validateTIN(userAndCorp, req)));
//    }
//
//    @Operation(
//            summary = "e-Invoice Registration",
//            description = "Performs a direct e-Invoice registration. This API is specifically intended for Super Admin users. [PIC: Caixin]")
//    @PostMapping("/registration")
//    public ResponseEntity<ApiResponse<EInvoiceRegApprovalDTO>> eInvoiceRegistration(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody EInvoiceRegReqDTO request) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        return ResponseEntity.status(HttpStatus.OK).body(eInvoiceService.eInvoiceRegistration(userAndCorp, request));
//    }
//
//    @Operation(summary = "e-Invoice Download")
//    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
//    public ResponseEntity<byte[]> downloadEInvoiceRegistration(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody EInvoiceListingDownloadReqDTO request) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        DownloadDTO result = eInvoiceService.downloadEInvoiceListing(userAndCorp, request);
//        HttpHeaders headers = setHttpHeadersForDownload(result);
//        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
//    }
//}
