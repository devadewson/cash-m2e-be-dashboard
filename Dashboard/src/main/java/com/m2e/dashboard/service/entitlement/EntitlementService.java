package com.m2e.dashboard.service.entitlement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.entitlement.request.EntitlementReqDto;
import com.m2e.dashboard.dto.entitlement.request.UserAccessReqDto;
import com.m2e.dashboard.dto.entitlement.response.AcctProdCifEntitlementDto;
import com.m2e.dashboard.dto.entitlement.response.AcctProdEntitlementDto;
import com.m2e.dashboard.mapper.entitlement.SpResMapper;
import com.m2e.dashboard.sp.entitlement.request.EntitlementSpParam;
import com.m2e.dashboard.sp.entitlement.response.AcctProductResDto;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.m2e.dashboard.enums.SPEnum.SP_ACCT_PROD_ENTITLEMENT;

@Service
public class EntitlementService {
    private static final Logger logger = LoggerFactory.getLogger(EntitlementService.class);
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BaseSpService baseSpService;
    @Autowired
    private SpResMapper spResMapper;

    public List<AcctProductResDto> getUserAccess(String jwtToken, UserAccessReqDto userAccessReqDto) throws JsonProcessingException {
        logger.info("user access request");
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(jwtToken);

        EntitlementSpParam accessEntitlementSpParam = new EntitlementSpParam();
        accessEntitlementSpParam.setCorpId(userAccessReqDto.getCorpId());
        accessEntitlementSpParam.setUserId(userAndCorp.getUserId());

        BaseSpDto<EntitlementSpParam, AcctProductResDto> spBaseSpDto = new BaseSpDto<>();
        spBaseSpDto.setSpId(SP_ACCT_PROD_ENTITLEMENT.getValue());
        spBaseSpDto.setParameter(accessEntitlementSpParam);
        spBaseSpDto.setResultClass(AcctProductResDto.class);

        BaseSpDto<EntitlementSpParam, AcctProductResDto> resultBaseSpDto = baseSpService.execute(spBaseSpDto);
        logger.debug("AcctProductResDto: {} ", resultBaseSpDto);

        List<AcctProductResDto> result = resultBaseSpDto.getResults().stream().toList();

        logger.debug("get prod access res: {}", result);

        return result;
    }

}
