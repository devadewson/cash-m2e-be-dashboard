package com.m2e.dashboard.controller;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.OpenTagCorpPageDTO;
import com.m2e.dashboard.dto.base.OpenTagCorpPageReqDTO;
import com.m2e.dashboard.dto.card.credit.request.CCCreditCardMemberDropDownReqDTO;
import com.m2e.dashboard.dto.card.credit.response.CCCreditCardMemberDTO;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.company.CorpDropdownReqDTO;
import com.m2e.dashboard.dto.deposit.ca.response.SubscriberCorpLinkDTO;
import com.m2e.dashboard.dto.dropdown.DropdownDto;
import com.m2e.dashboard.dto.dropdown.TrxCodeDropdownReqDto;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceStatementAcctReqDTO;
import com.m2e.dashboard.dto.einvoice.response.EInvoiceMonthReqDTO;
import com.m2e.dashboard.dto.einvoice.response.EInvoiceStatementAcctDTO;
import com.m2e.dashboard.dto.einvoice.response.EInvoiceStatementPeriodDTO;
import com.m2e.dashboard.dto.maintenance.request.SubProductListReqDTO;
import com.m2e.dashboard.dto.maintenance.response.ProductDTO;
import com.m2e.dashboard.dto.maintenance.response.SubProductDTO;
import com.m2e.dashboard.enums.CreditCardType;
import com.m2e.dashboard.enums.ErrorMessageEnum;
import com.m2e.dashboard.service.DropdownService;
import com.m2e.dashboard.service.card.credit.CCCreditCardService;
import com.m2e.dashboard.service.company.CorpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Dropdown Controller", description = "PIC: Wei Xian")
@RestController
@RequestMapping("dropdown/v1")
@RequiredArgsConstructor
public class DropdownController {

    private static final Logger logger = LoggerFactory.getLogger(DropdownController.class);

    private final  JwtUtil jwtUtil;

    private final CorpService corpService;

    private final CCCreditCardService creditCardService;

    private final DropdownService dropdownService;

    @Operation(
            summary = "Get subscriber link list for corps",
            description = "Get subscriber list for corps",
            responses = {@ApiResponse(responseCode = "200", description = "OK", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = SubscriberCorpLinkDTO.class))})}
    )
    @PostMapping("subscriber-link-company-list")
    public ResponseEntity<?> getSubscriberLinkCompanyList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        try {
            UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
            return ResponseEntity.ok(corpService.getSubscriberCompanyLinkListSp(userAndCorp.getUserId()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessageEnum.MSG_ERROR.getDescription());
        }
    }

    @Operation(
            summary = "open tagging corp list",
            description = "open tagging corp list")
    @PostMapping("open-tag-corps")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<OpenTagCorpPageDTO>> getPageOfOpenTaggingCompanies(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody OpenTagCorpPageReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(corpService.getOpenTagCompanyList(userAndCorp, req));
    }

    @Operation(
            summary = "Get list of transaction codes"
    )
    @PostMapping("trxn-code-list")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<List<DropdownDto>>> getTransactionCodeList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody TrxCodeDropdownReqDto trxCodeDropdownReqDto) {
        try {
            return ResponseEntity.ok(corpService.getTransactionCodeList(trxCodeDropdownReqDto));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessageEnum.MSG_ERROR.getDescription());
        }
    }

    @Operation(
            summary = "[MOCK DATA] Get list of payment type"
    )
    @PostMapping("payment-type-list")
    public ResponseEntity<?> getPaymentTypeList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody CorpDropdownReqDTO request) {
        try {
            return ResponseEntity.ok(corpService.getPaymentTypeList());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessageEnum.MSG_ERROR.getDescription());
        }
    }

    @Operation(
            summary = "[MOCK DATA] Get list of transaction types"
    )
    @PostMapping("trxn-type-list")
    public ResponseEntity<?> getTransactionTypeList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody CorpDropdownReqDTO request) {
        try {
            return ResponseEntity.ok(corpService.getTransactionTypeList());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessageEnum.MSG_ERROR.getDescription());
        }
    }

    @Operation(
            summary = "[MOCK DATA] Get list of card member"
    )
    @PostMapping("credit-card-member")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<List<CCCreditCardMemberDTO>>> getCardMemberList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCCreditCardMemberDropDownReqDTO request) {
        try {
            return ResponseEntity.ok(creditCardService.getCardMemberDropDownList(request));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessageEnum.MSG_ERROR.getDescription());
        }
    }

    @Operation(
            summary = "Credit Card Type"
    )
    @PostMapping("credit-card-type")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<List<DropdownDto>>> getCardType(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        CreditCardType[] cardTypeList = CreditCardType.values();
        List<DropdownDto> results = new ArrayList<>();

        for (CreditCardType creditCardType : cardTypeList) {
            results.add(new DropdownDto(creditCardType.name(), creditCardType.getDescription()));
        }
        return ResponseEntity.ok(new com.m2e.dashboard.dto.common.ApiResponse<>(new ErrorResponse(), results));
    }

    @Operation(
            summary = "Get e-Invoice statement product.",
            description = "Retrieves a list of product.")
    @PostMapping("/report/e-invoice/products")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<List<ProductDTO>>> getEInvoiceProducts(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return ResponseEntity.ok(dropdownService.getEInvoiceStatementProducts());
    }

    @Operation(
            summary = "Get e-Invoice statement account types by product.",
            description = "Retrieves a list of account types by product.")
    @PostMapping("/report/e-invoice/account-types")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<List<SubProductDTO>>> getEInvoiceAcctTypesByProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody SubProductListReqDTO req) {
        return ResponseEntity.ok(dropdownService.getEInvoiceStatementAcctTypesByProduct(req));
    }

    @Operation(
            summary = "[PIC::Weixian] Get e-Invoice statement product types.",
            description = "Retrieves a list of product types.")
    @PostMapping("/report/e-invoice/product-types")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<List<SubProductDTO>>> getEInvoiceProductTypes(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(dropdownService.getEInvoiceStatementAcctTypes());
    }

    @Operation(
            summary = "[PIC::Weixian] Get e-Invoice statement product types.",
            description = "Retrieves a list of product types.")
    @PostMapping("/report/e-invoice/accounts")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<List<EInvoiceStatementAcctDTO>>> getEInvoiceAcctNumbers(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody EInvoiceStatementAcctReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(dropdownService.getEInvoiceStatementAccounts(userAndCorp, req));
    }

    @Operation(
            summary = "[PIC::Weixian] Get e-Invoice statement years.",
            description = "Retrieves a list of e-Invoice statement years.")
    @PostMapping("/report/e-invoice/years")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<List<String>>> getEinvoiceStatementYears(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return ResponseEntity.ok(dropdownService.getEinvoiceStatementYears());
    }

    @Operation(
            summary = "[PIC::Weixian] Get e-Invoice statement months",
            description = "Retrieves a list of e-Invoice statement months.")
    @PostMapping("/report/e-invoice/months")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<List<EInvoiceStatementPeriodDTO>>> getEinvoiceStatementMonths(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody EInvoiceMonthReqDTO req) {
        return ResponseEntity.ok(dropdownService.generateStatementPeriods(req.getFrequencyEnum(), Integer.valueOf(req.getYear())));
    }

    @Operation(
            summary = "[PIC::CaiXin] Get e-Invoice download product types.",
            description = "Retrieves a list of product types.")
    @PostMapping("/report/e-invoice/download/product-types")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<List<SubProductDTO>>> getEInvoiceDownloadProductTypes(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(dropdownService.getEInvoiceDownloadAcctTypes());
    }
}
