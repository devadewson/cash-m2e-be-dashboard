package com.m2e.dashboard.service.demoscreen.demoscreen;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.demoscreen.request.UserJourneyReqDTO;
import com.m2e.dashboard.enums.DemoScreenEnum;
import com.m2e.dashboard.repository.CorpUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DemoScreenService {

    private final CorpUserRepository corpUserRepository;
    private final JwtUtil jwtUtil;

    public ApiResponse<String> updateUserJourney(String auth,UserAndCorp userAndCorp, UserJourneyReqDTO userJourneyReqDto) {
        ApiResponse<String> apiResponse = new ApiResponse<>();

        DemoScreenEnum demoScreen = userJourneyReqDto.getDemoScreen();
        if (demoScreen == null) {
            log.warn("[DemoScreen] demoScreen is null in request");
            apiResponse.setMsgHeader(new ErrorResponse("1002"));
            apiResponse.setMsgBody("Screen status does not update");
            return apiResponse;
        }

        String userId = userAndCorp.getUserId();
        String corpId = userAndCorp.getCorpId();

        log.info("[DemoScreen] START userId={},corpId={}, demoScreen={}", userId,corpId, demoScreen);

        try {
            int updated = corpUserRepository.updateDemoScreenExplored(userId, corpId, demoScreen.getColumnName());

            if (updated > 0) {
                log.info("[DemoScreen] SUCCESS userId={},corpId={}, demoScreen={}, rowsAffected={}", userId,corpId, demoScreen, updated);
                apiResponse.setMsgHeader(new ErrorResponse("0"));
                apiResponse.setMsgBody("Success");
            } else {
                log.warn("[DemoScreen] No rows updated. userId={},corpId={}, demoScreen={}", userId,corpId, demoScreen);
                apiResponse.setMsgHeader(new ErrorResponse("1002"));
                apiResponse.setMsgBody("Screen status does not update");
            }

        } catch (Exception e) {
            log.error("[DemoScreen] FAILED userId={},corpId={}, demoScreen={}, error={}", userId,corpId, demoScreen, e.getMessage(), e);
            apiResponse.setMsgHeader(new ErrorResponse("1002"));
            apiResponse.setMsgBody("Screen status does not update");
        }

        return apiResponse;
    }
}