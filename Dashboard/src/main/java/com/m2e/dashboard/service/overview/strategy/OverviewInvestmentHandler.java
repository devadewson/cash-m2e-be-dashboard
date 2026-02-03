package com.m2e.dashboard.service.overview.strategy;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctListingDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctStatsDTO;
import com.m2e.dashboard.dto.overview.request.OverviewReqDTO;
import com.m2e.dashboard.dto.overview.response.OverviewResDTO;
import com.m2e.dashboard.dto.overview.response.OverviewSubResDTO;
import com.m2e.dashboard.enums.*;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctListReqDTO;
import com.m2e.dashboard.service.OverviewProductStrategy;
import com.m2e.dashboard.service.deposit.ca.jms.DepositCAESBService;
import com.m2e.dashboard.service.deposit.ca.sp.DepositCASPService;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import com.m2e.dashboard.util.DateTimeUtil;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_OV_STATS;
import static com.m2e.dashboard.enums.SPEnum.SP_OV_DP_ACCT_STATS;
import static com.m2e.dashboard.enums.SPEnum.SP_OV_UT_ACCT_STATS;

@Slf4j
@Component
public class OverviewInvestmentHandler implements OverviewProductStrategy {
    @Autowired
    BaseSpService baseSpService;
    @Autowired
    private RedisHelper redisHelper;
    @Override
    public boolean supports(OverviewEnum product) {
        return OverviewEnum.INVESTMENT.equals(product);
    }

    @Override
    public OverviewResDTO viewProduct(UserAndCorp userAndCorp, OverviewReqDTO overviewReqDto, OverviewResDTO overviewResDto) {
        BaseSpDto<OverviewReqDTO, OverviewSubResDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_OV_UT_ACCT_STATS.getValue());
        baseSpDto.setParameter(overviewReqDto);
        baseSpDto.setResultClass(OverviewSubResDTO.class);
        BaseSpDto<OverviewReqDTO, OverviewSubResDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        overviewResDto.setTitle(OverviewEnum.INVESTMENT.getDescription());

        List<OverviewSubResDTO> accountInv = resultBaseSpDto.getResults().stream().toList();
        log.info("accountInv:: " + accountInv);
        overviewResDto.setAccount(accountInv);
        overviewResDto.setProductAccType("assets");

        //adding function to save in cache
        String overViewKey = redisHelper.createCacheKey(BD_AM_OV_STATS, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + overviewReqDto.getProduct());
        redisHelper.save(overViewKey, overviewResDto, 2, TimeUnit.HOURS); //to confirm time

        return overviewResDto;
    }
}
