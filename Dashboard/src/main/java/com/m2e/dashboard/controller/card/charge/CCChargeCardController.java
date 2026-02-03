package com.m2e.dashboard.controller.card.charge;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.card.charge.request.*;
import com.m2e.dashboard.dto.card.charge.response.CCChargeCardDetailDTO;
import com.m2e.dashboard.dto.card.charge.response.CCChargeCardListDTO;
import com.m2e.dashboard.dto.card.common.request.CCPaymentReqDTO;
import com.m2e.dashboard.dto.card.common.response.CCStatsDTO;
import com.m2e.dashboard.dto.card.common.response.CCTrxHistoryDTO;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.AcctTypEnum;
import com.m2e.dashboard.enums.SpProductEnum;
import com.m2e.dashboard.exception.EntitlementException;
import com.m2e.dashboard.service.card.charge.CCChargeCardService;
import com.m2e.dashboard.validator.AcctEntitlementValidator;
import com.m2e.dashboard.validator.AcctNumberEntitlementValidator;
import com.m2e.dashboard.validator.SpProductEntitlementValidator;
import com.m2e.utils.jdbc.service.BaseSpService;
import com.m2e.utils.redis.helper.RedisHelper;
import com.m2e.utils.validator.Ivalidator;
import com.m2e.utils.validator.ValidatorContext;
import com.m2e.utils.validator.dto.ValidationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.m2e.dashboard.enums.Application.PORTFOLIO;
import static com.m2e.dashboard.enums.Product.CARD;
import static com.m2e.dashboard.enums.SubProductEnum.CHARGE_CARD;
import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

@Tag(name = "Corporate Card - Charge Card", description = "PIC: Cai Xin")
@RestController
@RequestMapping("/corp-card/charge-card/v1")
public class CCChargeCardController {
    @Autowired
    private CCChargeCardService service;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ValidatorContext validatorContext;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private BaseSpService baseSpService;

    @Operation(summary = "Charge Card Account Statistic")
    @PostMapping("/acct-stats")
    public ResponseEntity<ApiResponse<CCStatsDTO>> getAccountStats(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCChargeCardStatsReqDTO req) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAccountStats(auth, req));
    }

    @Operation(summary = "Charge card Listing")
    @PostMapping("/acct-list")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<CCChargeCardListDTO>>> getAccountList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCChargeCardAccListReqDTO req) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAccountListing(auth, req));
    }

    @Operation(summary = "Charge card Details")
    @PostMapping("/acct-dtl")
    public ResponseEntity<ApiResponse<CCChargeCardDetailDTO>> getAccountDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCChargeCardDetailReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAccountDetail(request));
    }

    @Operation(
            summary = "Charge Card Trx History List",
            description = "Retrieve Charge Card Trx History in the list")
    @PostMapping("/trxn-hist")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<CCTrxHistoryDTO>>> retrieveTrxHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCChargeCardTrxHistReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.retrieveTrxHistory(request));
    }

    @Operation(
            summary = "Charge Card Pending Trx History List",
            description = "Retrieve Charge Card Pending Trx History in the list")
    @PostMapping("/pend-trxn-hist")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<CCTrxHistoryDTO>>> retrievePendingTrxHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCChargeCardTrxHistReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.retrievePendingTrxHistory(request));
    }

    @Operation(summary = "Download Charge Card Report")
    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(oneOf = {CCChargeCardListDLReqDTO.class, CCChargeCardTrxHistDLReqDTO.class, CCChargeCardPendHistDLReqDTO.class}))
    )
    public ResponseEntity<byte[]> downloadCC(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DownloadReq request) {
        DownloadDTO result = service.downloadCC(auth, request);
        HttpHeaders headers = setHttpHeadersForDownload(result);
        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
    }

    @Operation(summary = "Make payment")
    @PostMapping(value = "/make-payment")
    public ResponseEntity<ApiResponse<?>> makePayment(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCPaymentReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.makePayment(auth, request));
    }

    private void validateEntitlement(String auth, SpProductEnum spProduct, String acctNo) {
        List<Ivalidator> ivalidatorList = new ArrayList<>();

        ivalidatorList.add(new SpProductEntitlementValidator(context, redisHelper, baseSpService, jwtUtil, spProduct.name()));
        ivalidatorList.add(new AcctEntitlementValidator(context, redisHelper, baseSpService, jwtUtil, CARD.getCode(), AcctTypEnum.CHC.name()));
        if (acctNo != null)
            ivalidatorList.add(new AcctNumberEntitlementValidator(context, redisHelper, baseSpService, jwtUtil, acctNo));

        List<ValidationResult> validationResults = validatorContext.validate(auth, ivalidatorList);
        if(validationResults.stream().anyMatch(data -> !data.isResult()))
            throw new EntitlementException("User cannot access");
    }
}
