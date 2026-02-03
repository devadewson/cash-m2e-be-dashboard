package com.m2e.dashboard.controller;

import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.demoscreen.request.UserJourneyReqDTO;
import com.m2e.dashboard.service.demoscreen.DemoScreenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DemoScreenController", description = "Demo Screen")
@RestController
@RequestMapping("/demo-screen/v1")
public class DemoScreenController {
    @Autowired
    DemoScreenService demoScreenService;

    @PutMapping("/update-user-journey")
    public ResponseEntity<ApiResponse<String>> createCorpUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody UserJourneyReqDTO userJourneyReqDto) {
        return ResponseEntity.status(HttpStatus.OK).body(demoScreenService.updateUserJourney(auth, userJourneyReqDto));
    }
}
