package com.m2e.dashboard.validator;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.sp.entitlement.request.EntitlementSpParam;
import com.m2e.dashboard.sp.entitlement.response.AcctEntitlementDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import com.m2e.utils.redis.helper.RedisHelper;
import com.m2e.utils.validator.Ivalidator;
import com.m2e.utils.validator.dto.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Objects;

import static com.m2e.dashboard.enums.SPEnum.GET_ACCT_LIST;

public class AcctNumberEntitlementValidator implements Ivalidator {
    private static final Logger logger = LoggerFactory.getLogger(AcctNumberEntitlementValidator.class);
    private final RedisHelper redisHelper;
    private final BaseSpService baseSpService;
    private final JwtUtil jwtUtil;
    private final String acctNo;
    private final ApplicationContext context;

    public AcctNumberEntitlementValidator(ApplicationContext context, RedisHelper redisHelper, BaseSpService baseSpService, JwtUtil jwtUtil, String acctNo) {
        this.context = context;
        this.redisHelper = redisHelper;
        this.baseSpService = baseSpService;
        this.jwtUtil = jwtUtil;
        this.acctNo = acctNo;
    }

    @Override
    public ValidationResult validate(String auth) {
        logger.info("Enter check acct entitlement");
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        ValidationResult validationResult = ValidationResult.builder().result(true).build();
        boolean isEnabled = Objects.equals(context.getEnvironment().getProperty("entitlement.enabled"), "true");

        if (isEnabled) {

            String key = redisHelper.createCacheKey("ACCT_ENTITLEMENT", userAndCorp.getUserId());
            List<AcctEntitlementDTO> entitlementList = redisHelper.getListFromCache(key, AcctEntitlementDTO.class);

            if (entitlementList.isEmpty()) {
                BaseSpDto<EntitlementSpParam, AcctEntitlementDTO> baseSpDto = new BaseSpDto<>();
                baseSpDto.setSpId(GET_ACCT_LIST.getValue());
                EntitlementSpParam entitlementReqDTO = new EntitlementSpParam();
                entitlementReqDTO.setUserId(userAndCorp.getUserId());
                entitlementReqDTO.setCorpId(userAndCorp.getCorpId());

                baseSpDto.setParameter(entitlementReqDTO);
                baseSpDto.setResultClass(AcctEntitlementDTO.class);
                entitlementList = baseSpService.execute(baseSpDto).getResults();

                if (!entitlementList.isEmpty())
                    redisHelper.saveListToCache(key, entitlementList);
            }

            boolean isEntitled = entitlementList != null && entitlementList.stream().anyMatch(s -> s.getAcctNumber().equals(acctNo));

            logger.info("acct entitle " + entitlementList + " " + acctNo);

            if (!isEntitled)
                validationResult = ValidationResult.builder().result(false).message("User not entitled to access").build();
        }

        return validationResult;
    }
}