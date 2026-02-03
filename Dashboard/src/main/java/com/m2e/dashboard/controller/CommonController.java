package com.m2e.dashboard.controller;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.CurrencyConversionDTO;
import com.m2e.dashboard.dto.base.CurrencyConversionReqDTO;
import com.m2e.dashboard.dto.base.SearchResultCountDTO;
import com.m2e.dashboard.dto.base.SearchResultCountReqDTO;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.PaymentTypeDTO;
import com.m2e.dashboard.dto.common.PaymentTypeReq;
import com.m2e.dashboard.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Common Controller", description = "PIC: Wei Xian")
@RestController
@RequestMapping("common/v1")
public class CommonController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    CommonService commonService;

    @Operation(
            summary = "search result count for specific products",
            description = "search result count for specific products")
    @PostMapping("search-result-count")
    public ResponseEntity<ApiResponse<SearchResultCountDTO>> getSearchResultCount(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody SearchResultCountReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(commonService.getSearchResultCount(userAndCorp, req));
    }

    @Operation(summary = "Convert multiple amounts to a target currency",
            description = "Accepts a list of amounts with their respective buy rates and conversion units, and returns the converted amounts in the target currency.")
    @PostMapping("/convert")
    public ResponseEntity<ApiResponse<CurrencyConversionDTO>> convertCurrency(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CurrencyConversionReqDTO req) {
        return ResponseEntity.ok(commonService.convertCurrency(req));
    }

    @Operation(
            summary = "Get all Payment type value for dropdown",
            description = "Get all Payment type for dropdown"
    )
    @PostMapping("/pymt-type-list")
    public ResponseEntity<ApiResponse<List<PaymentTypeDTO>>> getPaymentTypeList(@RequestBody PaymentTypeReq paymentTypeReq) {
        return ResponseEntity.status(HttpStatus.OK).body(commonService.getPaymentTypeDropList(paymentTypeReq));
    }

}
