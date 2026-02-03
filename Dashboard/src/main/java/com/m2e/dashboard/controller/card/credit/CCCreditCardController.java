package com.m2e.dashboard.controller.card.credit;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.card.common.request.CCDetailReqDTO;
import com.m2e.dashboard.dto.card.common.request.CCPaymentReqDTO;
import com.m2e.dashboard.dto.card.common.request.CCStatsReqDTO;
import com.m2e.dashboard.dto.card.common.response.CCAccListBaseDTO;
import com.m2e.dashboard.dto.card.common.response.CCBaseCardDetailDTO;
import com.m2e.dashboard.dto.card.common.response.CCStatsDTO;
import com.m2e.dashboard.dto.card.common.response.CCTrxHistoryDTO;
import com.m2e.dashboard.dto.card.credit.request.*;
import com.m2e.dashboard.dto.card.credit.response.*;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.AcctTypEnum;
import com.m2e.dashboard.enums.SpProductEnum;
import com.m2e.dashboard.exception.EntitlementException;
import com.m2e.dashboard.service.card.credit.CCCreditCardService;
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

import static com.m2e.dashboard.enums.Application.PORTFOLIO;
import static com.m2e.dashboard.enums.Product.CARD;
import static com.m2e.dashboard.enums.SubProductEnum.CREDIT_CARD;
import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

@Tag(name = "Corporate Card - Credit Card", description = "PIC: Cai Xin")
@RestController
@RequestMapping("/corp-card/credit-card/v1")
public class CCCreditCardController {
    @Autowired
    private CCCreditCardService service;
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


    @Operation(summary = "Credit Card Account Statistic")
    @PostMapping("/acct-stats")
    public ResponseEntity<ApiResponse<CCStatsDTO>> getAccountStats(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCStatsReqDTO req) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAccountStats(auth, req));
    }

    @Operation(summary = "Credit Card Master Account Statistic - SG")
    @PostMapping("/master-acct-stats")
    public ResponseEntity<ApiResponse<CCStatsDTO>> getMasterAccountStats(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCStatsReqDTO req) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getMasterAccountStats(auth, req));
    }

    @Operation(summary = "Credit card Listing")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    content = @Content(mediaType = "*/*", schema = @Schema(oneOf = {
                            CCCreditCardListMYDTO.class,
                            CCCreditCardListSGDTO.class})))
    })
    @PostMapping("/acct-list")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<CCAccListBaseDTO>>> getAccountList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCCreditCardAccListReqDTO req) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAccountListing(auth, req));
    }


    @Operation(summary = "Credit card Master Listing - SG")
    @PostMapping("/master-acct-list")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<CCCreditCardMasterListSGDTO>>> getMasterAccountList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCCreditCardMasterListReqDTO req) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getMasterAccountListing(auth, req));
    }

    @Operation(summary = "Credit card Details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    content = @Content(mediaType = "*/*", schema = @Schema(oneOf = {
                            CCCreditCardDetailMYDTO.class,
                            CCCreditCardDetailSGDTO.class})))
    })
    @PostMapping("/acct-dtl")
    public ResponseEntity<ApiResponse<CCBaseCardDetailDTO>> getAccountDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCDetailReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAccountDetail(request));
    }

    @Operation(summary = "Master Credit card Details - SG")
    @PostMapping("/master-acct-dtl")
    public ResponseEntity<ApiResponse<CCCreditCardMasterDetailSGDTO>> getMasterAccountDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCDetailReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getMasterAccountDetail(request));
    }

    @Operation(
            summary = "Credit Card Trx History List",
            description = "Retrieve Credit Card Trx History in the list")
    @PostMapping("/trxn-hist")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<CCTrxHistoryDTO>>> retrieveTrxHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCCreditCardTrxHistReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.retrieveTrxHistory(request));
    }

    @Operation(
            summary = "Credit Card Pending Trx History List",
            description = "Retrieve Credit Card Pending Trx History in the list")
    @PostMapping("/pend-trxn-hist")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<CCTrxHistoryDTO>>> retrievePendingTrxHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCCreditCardTrxHistReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.retrievePendingTrxHistory(request));
    }

    @Operation(
            summary = "Credit Card Card Member List",
            description = "Retrieve Credit Card Member in the list")
    @PostMapping("/card-member")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<CCCreditCardMemberDTO>>> retrieveCardMemberList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody CCCreditCardMemberReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.retrieveCardMemberList(request));
    }

    @Operation(summary = "Download Credit Card Report")
    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(oneOf = {CCCreditCardListDLReqDTO.class, CCCreditCardTrxHistDLReqDTO.class, CCCreditCardPendHistDLReqDTO.class, CCCreditCardMasterListDLReqDTO.class}))
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
        ivalidatorList.add(new AcctEntitlementValidator(context, redisHelper, baseSpService, jwtUtil, CARD.getCode(), AcctTypEnum.CRC.name()));
        if (acctNo != null)
            ivalidatorList.add(new AcctNumberEntitlementValidator(context, redisHelper, baseSpService, jwtUtil, acctNo));

        List<ValidationResult> validationResults = validatorContext.validate(auth, ivalidatorList);
        if(validationResults.stream().anyMatch(data -> !data.isResult()))
            throw new EntitlementException("User cannot access");
    }
}
