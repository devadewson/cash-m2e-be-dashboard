package com.m2e.dashboard.controller;

import com.m2e.dashboard.dto.customize.request.CustomizeFieldReqDTO;
import com.m2e.dashboard.dto.customize.request.QueryCustomizeFieldReqDTO;
import com.m2e.dashboard.service.CustomizeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User's Customization on Field Views", description = "PIC: Ben")
@RestController
@RequestMapping("customize/v1")
public class CustomizeController {

    private static final Logger logger = LoggerFactory.getLogger(CustomizeController.class);

    @Autowired
    CustomizeService customizeService;

    @Operation(
            summary = "Deposit US 18 - Update User Customize Field",
            description = "Update User Customize Field in each of the module",
            responses = {@ApiResponse(responseCode = "200", description = "OK", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = com.m2e.dashboard.dto.common.ApiResponse.class))})}
    )
    @PostMapping("/customize-field")
    public ResponseEntity<?> customizeField(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody CustomizeFieldReqDTO request) {
        logger.info(request.toString());
        return ResponseEntity.status(HttpStatus.OK).body(customizeService.customizeField(auth, request));
    }

    @Operation(
            summary = "Deposit US 16 - Retrieve user selected field",
            description = "Retrieve user selected field in the display module, on no customised fields, return default list",
            responses = {@ApiResponse(responseCode = "200", description = "OK", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = com.m2e.dashboard.dto.common.ApiResponse.class))})}
    )
    @PostMapping("/query-customize-field")
    public ResponseEntity<?> queryCustomizeField(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody QueryCustomizeFieldReqDTO request) {
        logger.info(request.toString());
        return ResponseEntity.status(HttpStatus.OK).body(customizeService.queryCustomizeField(auth, request));
    }
}
