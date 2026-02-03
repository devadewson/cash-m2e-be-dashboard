package com.m2e.dashboard.controller.card.debit;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.card.common.request.CCStatsReqDTO;
import com.m2e.dashboard.dto.card.debit.request.CCDebitCardAccListReqDTO;
import com.m2e.dashboard.dto.card.debit.request.CCDebitCardListDLReqDTO;
import com.m2e.dashboard.dto.card.debit.response.CCDebitAccListBaseDTO;
import com.m2e.dashboard.dto.card.debit.response.CCDebitCardListMYDTO;
import com.m2e.dashboard.dto.card.debit.response.CCDebitCardListSGDTO;
import com.m2e.dashboard.dto.card.debit.response.CCDebitCardStatsDTO;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.AcctTypEnum;
import com.m2e.dashboard.enums.SpProductEnum;
import com.m2e.dashboard.exception.EntitlementException;
import com.m2e.dashboard.service.card.debit.CCDebitCardService;
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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import static com.m2e.dashboard.enums.Product.CARD;
import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

@Tag(name = "Corporate Card - Debit Card", description = "PIC: Cai Xin")
@RestController
@RequestMapping("/corp-card/debit-card/v1")
public class CCDebitCardController {
    @Autowired
    private CCDebitCardService service;
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

    @Operation(summary = "Debit Card Account Statistic")
    @PostMapping("/acct-stats")
    public ResponseEntity<ApiResponse<CCDebitCardStatsDTO>> getAccountStats(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCStatsReqDTO req) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAccountStats(auth, req));
    }

    @Operation(summary = "Debit card Listing")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    content = @Content(mediaType = "*/*", schema = @Schema(oneOf = {
                            CCDebitCardListMYDTO.class,
                            CCDebitCardListSGDTO.class})))
    })
    @PostMapping("/acct-list")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<CCDebitAccListBaseDTO>>> getAccountList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCDebitCardAccListReqDTO req) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAccountListing(auth, req));
    }

    @Operation(summary = "Download Debit Card Report")
    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> downloadCC(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCDebitCardListDLReqDTO request) {
        DownloadDTO result = service.downloadCC(auth, request);
        HttpHeaders headers = setHttpHeadersForDownload(result);
        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
    }

    private void validateEntitlement(String auth, SpProductEnum spProduct, String acctNo) {
        List<Ivalidator> ivalidatorList = new ArrayList<>();

        ivalidatorList.add(new SpProductEntitlementValidator(context, redisHelper, baseSpService, jwtUtil, spProduct.name()));
        ivalidatorList.add(new AcctEntitlementValidator(context, redisHelper, baseSpService, jwtUtil, CARD.getCode(), AcctTypEnum.DC.name()));
        if (acctNo != null)
            ivalidatorList.add(new AcctNumberEntitlementValidator(context, redisHelper, baseSpService, jwtUtil, acctNo));

        List<ValidationResult> validationResults = validatorContext.validate(auth, ivalidatorList);
        if(validationResults.stream().anyMatch(data -> !data.isResult()))
            throw new EntitlementException("User cannot access");
    }
}
