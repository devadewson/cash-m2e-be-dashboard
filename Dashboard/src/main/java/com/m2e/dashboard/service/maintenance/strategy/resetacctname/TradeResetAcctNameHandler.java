package com.m2e.dashboard.service.maintenance.strategy.resetacctname;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.maintenance.request.MTResetAccctNameReqDTO;
import com.m2e.dashboard.enums.Product;
import com.m2e.dashboard.service.ResetAcctNameStrategy;
import com.m2e.dashboard.service.maintenance.sp.MTAcctNameSPService;
import com.m2e.dashboard.sp.maintenance.request.MTResetAcctNameSpParam;
import org.springframework.stereotype.Component;

@Component
public class TradeResetAcctNameHandler implements ResetAcctNameStrategy {
    private final MTAcctNameSPService mtAcctNameSPService;

    public TradeResetAcctNameHandler(MTAcctNameSPService mtAcctNameSPService) {
        this.mtAcctNameSPService = mtAcctNameSPService;
    }

    @Override
    public boolean supports(Product product) {
        return Product.TRADE.equals(product);
    }

    @Override
    public boolean resetAcctName(UserAndCorp userAndCorp, MTResetAccctNameReqDTO request) {
        return mtAcctNameSPService.resetToDefaultAcctName(MTResetAcctNameSpParam.builder()
                .userId(userAndCorp.getUserId())
                .corpId(userAndCorp.getCorpId())
                .acctNumber(request.getAcctNumber())
                .acctTypeCode(request.getAccountTypeCode())
                .currency(request.getCurrency())
                .build());
    }
}
