package com.m2e.dashboard.controller;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.demoscreen.request.UserJourneyReqDTO;
import com.m2e.dashboard.service.demoscreen.demoscreen.DemoScreenService;
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
    @Autowired
    JwtUtil jwtUtil;

    @PutMapping("/update-user-journey")
    public ResponseEntity<ApiResponse<String>> createCorpUser
            (@RequestHeader(value = HttpHeaders.AUTHORIZATION,required = false) String auth,
             @RequestBody UserJourneyReqDTO userJourneyReqDto) {
//        String userId = "LCUS-RAHGLOLI";
//        String corpId = "LCUS-RAHGLOLI";
//        UserAndCorp userAndCorp = new UserAndCorp();
//        userAndCorp.setUserId(userId);
//        userAndCorp.setCorpId(corpId);
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        return ResponseEntity.status(HttpStatus.OK).body(demoScreenService.updateUserJourney(auth, userAndCorp,userJourneyReqDto));
    }
}
