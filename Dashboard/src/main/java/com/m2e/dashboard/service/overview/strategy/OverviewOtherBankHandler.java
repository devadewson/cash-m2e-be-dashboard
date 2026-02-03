package com.m2e.dashboard.service.overview.strategy;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.overview.request.OverviewReqDTO;
import com.m2e.dashboard.dto.overview.response.OverviewResDTO;
import com.m2e.dashboard.dto.overview.response.OverviewSubResDTO;
import com.m2e.dashboard.enums.OverviewEnum;
import com.m2e.dashboard.enums.Product;
import com.m2e.dashboard.service.OverviewProductStrategy;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_OV_STATS;
import static com.m2e.dashboard.enums.SPEnum.SP_OV_LN_ACCT_STATS;
import static com.m2e.dashboard.enums.SPEnum.SP_OV_OB_ACCT_STATS;

@Slf4j
@Component
public class OverviewOtherBankHandler implements OverviewProductStrategy {
    @Autowired
    BaseSpService baseSpService;
    @Autowired
    private RedisHelper redisHelper;
    @Override
    public boolean supports(OverviewEnum product) {
        return OverviewEnum.OTHER.equals(product);
    }

    @Override
    public OverviewResDTO viewProduct(UserAndCorp userAndCorp, OverviewReqDTO overviewReqDto, OverviewResDTO overviewResDto) {
        BaseSpDto<OverviewReqDTO, OverviewSubResDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_OV_OB_ACCT_STATS.getValue());
        baseSpDto.setParameter(overviewReqDto);
        baseSpDto.setResultClass(OverviewSubResDTO.class);
        BaseSpDto<OverviewReqDTO, OverviewSubResDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<OverviewSubResDTO> accountOther = resultBaseSpDto.getResults().stream().toList();
        log.info("accountOther:: "+ accountOther);
        overviewResDto.setTitle(OverviewEnum.OTHER.getDescription());
        overviewResDto.setAccount(accountOther);

        StringBuilder productTypes = new StringBuilder();
        boolean depositAdded = false;
        boolean liabilityAdded = false;
        // Loop through the accountOther list
        for (OverviewSubResDTO subAccount : accountOther) {
            String subProduct = String.valueOf(subAccount.getSubProduct());
            if (subProduct != null) {
                subProduct = subProduct.trim();
                if (subProduct.equalsIgnoreCase("DEPOSIT")) {
                    if (!depositAdded) {
                        if (productTypes.length() > 0) {
                            productTypes.append(",");
                        }
                        productTypes.append("assets");
                        depositAdded = true;
                    }
                } else if (subProduct.equalsIgnoreCase("LOAN")) {
                    if (!liabilityAdded) {
                        if (productTypes.length() > 0) {
                            productTypes.append(",");
                        }
                        productTypes.append("liabilities");
                        liabilityAdded = true;
                    }
                } else {
                    log.info("Not under Other Bank Account: " + subProduct);
                }
            } else {
                log.info("SubProduct is null");
            }
        }
        String result = productTypes.toString();
        overviewResDto.setProductAccType(result);

        //adding function to save in cache
        String overViewKey = redisHelper.createCacheKey(BD_AM_OV_STATS, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + overviewReqDto.getProduct());
        redisHelper.save(overViewKey, overviewResDto, 2, TimeUnit.HOURS); //to confirm time

        return overviewResDto;
    }
}
