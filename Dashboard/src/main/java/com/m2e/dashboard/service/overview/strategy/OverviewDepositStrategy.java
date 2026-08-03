package com.m2e.dashboard.service.overview.strategy;


import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.overview.request.OverviewReqDTO;
import com.m2e.dashboard.dto.overview.response.OverviewResDTO;
import com.m2e.dashboard.enums.OverviewEnum;
import com.m2e.dashboard.service.OverviewProductStrategy;
import com.m2e.dashboard.service.overview.Refactor.DepositOverviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OverviewDepositStrategy implements OverviewProductStrategy {

    private final DepositOverviewService depositOverviewService;

    @Override
    public boolean supports(OverviewEnum product) {
        // Supports DEPOSIT and VIRTUALACCOUNT
        return product == OverviewEnum.DEPOSIT ||
                product == OverviewEnum.VIRTUALACCOUNT;
    }

    @Override
    public OverviewResDTO viewProduct(
            UserAndCorp userAndCorp,
            OverviewReqDTO overviewReqDto,
            OverviewResDTO overviewResDto) {

        log.info("OverviewDepositStrategy.viewProduct for: {}",
                overviewReqDto.getProduct());

        String baseCurrency = overviewReqDto.getBaseCurrency() != null ?
                overviewReqDto.getBaseCurrency() : "IDR";

        if (overviewReqDto.getProduct() == OverviewEnum.DEPOSIT) {
            // Get all deposits (CA + SA + FD )
            return depositOverviewService.getDepositOverview(
                    userAndCorp,
                    baseCurrency,
                    overviewReqDto.getCorpCifNo());

        } else if (overviewReqDto.getProduct() == OverviewEnum.VIRTUALACCOUNT) {
            // Get only Virtual Accounts (sub-type of deposit)
            return depositOverviewService.getDepositSubTypeOverview(
                    userAndCorp,
                    com.m2e.dashboard.enums.OvSubProductEnum.VA,
                    baseCurrency,
                    overviewReqDto.getCorpCifNo());
        }

        return overviewResDto;
    }
}