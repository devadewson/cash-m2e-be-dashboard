package com.m2e.dashboard.service.demoscreen;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.demoscreen.request.UserJourneyReqDTO;
import com.m2e.dashboard.repository.CorpUserRepository;
import com.m2e.dashboard.sp.demoscreen.request.DemoScreenSpParam;
import com.m2e.dashboard.sp.demoscreen.response.DemoScreenSpDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemoScreenService {
    private static final Logger logger = LoggerFactory.getLogger(DemoScreenService.class);

    @Autowired
    private CorpUserRepository corpUserRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private DemoScreenSPService spService;

    public ApiResponse<String> updateUserJourney(String auth, UserJourneyReqDTO userJourneyReqDto) {
        String userId = jwtUtil.extractUserId(auth);

        ApiResponse<String> apiResponse = new ApiResponse<>();

        DemoScreenSpParam demoScreenSpParam = new DemoScreenSpParam();
        demoScreenSpParam.setDemoScreen(userJourneyReqDto.getDemoScreen().name());
        demoScreenSpParam.setUserId(userId);

        DemoScreenSpDTO result = spService.updateUserJourney(demoScreenSpParam);

        logger.info("update count {}", result);

        if (result.getUpdateStatus() > 0) {
            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody("Success");
        } else {
            apiResponse.setMsgHeader(new ErrorResponse("1002"));
            apiResponse.setMsgBody("Screen status does not update");
        }
        return apiResponse;
    }
}
