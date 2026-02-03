package com.m2e.dashboard.controller.entitlement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.entitlement.request.EntitlementReqDto;
import com.m2e.dashboard.dto.entitlement.request.UserAccessReqDto;
import com.m2e.dashboard.dto.entitlement.response.AcctProdCifEntitlementDto;
import com.m2e.dashboard.service.entitlement.EntitlementService;
import com.m2e.dashboard.sp.entitlement.response.AcctProductResDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class EntitlementController {
    @Autowired
    private EntitlementService entitlementService;

    @Operation(
            summary = "Get user's product access",
            description = "Get user's product access"
    )
    @PostMapping("/getAcctProduct")
    public List<AcctProductResDto> getUserAccess(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody UserAccessReqDto userAccessReqDto) throws JsonProcessingException { //NOSONAR
        return entitlementService.getUserAccess(auth, userAccessReqDto);
    }
}
