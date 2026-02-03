package com.m2e.dashboard.service;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.customize.request.CustomizeFieldReqDTO;
import com.m2e.dashboard.dto.customize.request.QueryCustomizeFieldReqDTO;
import com.m2e.dashboard.dto.customize.response.QueryCustomizeFieldDTO;
import com.m2e.dashboard.repository.PreferenceRepository;
import com.m2e.dashboard.sp.customize.request.CustomizeSpParam;
import com.m2e.dashboard.sp.customize.response.CustomizeSpDTO;
import com.m2e.model.sso.base.Preference;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.m2e.dashboard.enums.SPEnum.SP_CUSTOMIZE_FIELD;

@Service
public class CustomizeService {

    private static final Logger logger = LoggerFactory.getLogger(CustomizeService.class);

    private final PreferenceRepository preferenceRepository;
    private final JwtUtil jwtUtil;
    private final BaseSpService baseSpService;
    
    @Autowired
    public CustomizeService(PreferenceRepository preferenceRepository, JwtUtil jwtUtil, BaseSpService baseSpService){
        this.preferenceRepository = preferenceRepository;
        this.jwtUtil = jwtUtil;
        this.baseSpService = baseSpService;
    }

    @Transactional
    public ApiResponse customizeField(String auth, CustomizeFieldReqDTO request) {
        logger.info("customizeField is executing");
        ApiResponse apiResponse = new ApiResponse<>();

        String loggedUserId = jwtUtil.extractUserId(auth);

        BaseSpDto<CustomizeSpParam, CustomizeSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_CUSTOMIZE_FIELD.getValue());
        baseSpDto.setParameter(constructCustomizeSpParam(loggedUserId, request));
        baseSpDto.setResultClass(CustomizeSpDTO.class);

        CustomizeSpDTO customizeSpDTO = baseSpService.execute(baseSpDto).getResults().get(0);
        logger.info(customizeSpDTO.getMessage());

        apiResponse.setMsgHeader(new ErrorResponse("0"));
        return apiResponse;
    }

    public ApiResponse<QueryCustomizeFieldDTO> queryCustomizeField(String auth, QueryCustomizeFieldReqDTO request) {
        logger.info("queryCustomizeField is executing");
        ApiResponse<QueryCustomizeFieldDTO> apiResponse = new ApiResponse<>();
        QueryCustomizeFieldDTO queryCustomizeFieldRes = new QueryCustomizeFieldDTO();

        String loggedUserId = jwtUtil.extractUserId(auth);
        Set<String> selectedField = new HashSet<>();

        Optional<Preference> preferenceOptional = preferenceRepository.findByUsrIdAndSubProductAndScreen(loggedUserId, request.getSubProduct().name(), request.getScreen().name());
        if (preferenceOptional.isPresent()) {
            String attr = preferenceOptional.get().getAttribute();
            if (attr != null && !attr.isEmpty()) {
                String[] parts = attr.split(",");
                Collections.addAll(selectedField, parts);
            }
        }
        queryCustomizeFieldRes.setSelectedField(selectedField);

        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(queryCustomizeFieldRes);

        return apiResponse;
    }

    private CustomizeSpParam constructCustomizeSpParam(String userId, CustomizeFieldReqDTO request) {

        String selectedFields = String.join(",", request.getSelectedField());

        CustomizeSpParam customizeSpParam = new CustomizeSpParam();
        customizeSpParam.setUserId(userId);
        customizeSpParam.setSubProduct(request.getSubProduct().name());
        customizeSpParam.setScreen(request.getScreen().name());
        customizeSpParam.setAttribute(selectedFields);

        return customizeSpParam;
    }
}
