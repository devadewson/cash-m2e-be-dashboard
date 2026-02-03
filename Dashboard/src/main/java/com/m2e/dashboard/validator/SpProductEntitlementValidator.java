package com.m2e.dashboard.validator;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.sp.entitlement.request.EntitlementSpParam;
import com.m2e.dashboard.sp.entitlement.response.SpProdEntitlementDTO;
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

import static com.m2e.dashboard.enums.SPEnum.GET_SP_PRODUCT_LIST;

public class SpProductEntitlementValidator implements Ivalidator {
    private static final Logger logger = LoggerFactory.getLogger(SpProductEntitlementValidator.class);
    private final RedisHelper redisHelper;
    private final BaseSpService baseSpService;
    private final JwtUtil jwtUtil;
    private final String productCode;
    private final ApplicationContext context;

    public SpProductEntitlementValidator(ApplicationContext context, RedisHelper redisHelper, BaseSpService baseSpService, JwtUtil jwtUtil, String productCode) {
        this.redisHelper = redisHelper;
        this.baseSpService = baseSpService;
        this.jwtUtil = jwtUtil;
        this.productCode = productCode;
        this.context = context;
    }

    @Override
    public ValidationResult validate(String auth) {
        logger.info("Enter check SP product entitlement");
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        ValidationResult validationResult = ValidationResult.builder().result(true).build();
        boolean isEnabled = Objects.equals(context.getEnvironment().getProperty("entitlement.enabled"), "true");

        if (isEnabled) {

            String key = redisHelper.createCacheKey("ENTITLEMENT", userAndCorp.getUserId());
            List<SpProdEntitlementDTO> entitlementList = redisHelper.getListFromCache(key, SpProdEntitlementDTO.class);

            if (entitlementList.isEmpty()) {
                BaseSpDto<EntitlementSpParam, SpProdEntitlementDTO> baseSpDto = new BaseSpDto<>();
                baseSpDto.setSpId(GET_SP_PRODUCT_LIST.getValue());
                EntitlementSpParam entitlementReqDTO = new EntitlementSpParam();
                entitlementReqDTO.setUserId(userAndCorp.getUserId());
                entitlementReqDTO.setCorpId(userAndCorp.getCorpId());

                baseSpDto.setParameter(entitlementReqDTO);
                baseSpDto.setResultClass(SpProdEntitlementDTO.class);
                entitlementList = baseSpService.execute(baseSpDto).getResults();

                if (!entitlementList.isEmpty())
                    redisHelper.saveListToCache(key, entitlementList);
            }

            boolean isEntitled = entitlementList != null && entitlementList.stream().anyMatch(s -> s.getProductCode().equals(productCode));

            if (!isEntitled)
                validationResult = ValidationResult.builder().result(false).message("User not entitled to access").build();
        }

        return validationResult;
    }
}
