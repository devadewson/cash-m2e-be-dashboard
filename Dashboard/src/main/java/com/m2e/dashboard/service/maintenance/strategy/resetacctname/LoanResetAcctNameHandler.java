package com.m2e.dashboard.service.maintenance.strategy.resetacctname;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.maintenance.request.MTResetAccctNameReqDTO;
import com.m2e.dashboard.enums.Product;
import com.m2e.dashboard.service.ResetAcctNameStrategy;
import com.m2e.dashboard.service.maintenance.sp.MTAcctNameSPService;
import com.m2e.dashboard.sp.maintenance.request.MTResetAcctNameSpParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoanResetAcctNameHandler implements ResetAcctNameStrategy {
    private final MTAcctNameSPService mtAcctNameSPService;

    public LoanResetAcctNameHandler(MTAcctNameSPService mtAcctNameSPService) {
        this.mtAcctNameSPService = mtAcctNameSPService;
    }

    @Override
    public boolean supports(Product product) {
        return Product.LOAN.equals(product);
    }

    @Override
    public boolean resetAcctName(UserAndCorp userAndCorp, MTResetAccctNameReqDTO request) {
        switch (request.getAccountTypeCode()) {
            case "TL", "TLS" -> {
                return mtAcctNameSPService.resetToDefaultAcctName(MTResetAcctNameSpParam.builder()
                        .userId(userAndCorp.getUserId())
                        .corpId(userAndCorp.getCorpId())
                        .acctNumber(request.getAcctNumber())
                        .currency(request.getCurrency())
                        .acctTypeCode(request.getAccountTypeCode())
                        .build());
            } case "TLF", "RC" -> {
                return mtAcctNameSPService.resetToDefaultAcctNameCifAsAcctNo(MTResetAcctNameSpParam.builder()
                        .userId(userAndCorp.getUserId())
                        .corpId(userAndCorp.getCorpId())
                        .acctNumber(request.getAcctNumber())
                        .currency(request.getCurrency())
                        .acctTypeCode(request.getAccountTypeCode())
                        .build());
            } case "OD" -> {
                return mtAcctNameSPService.resetToDefaultAcctNameLoanOverdraft(MTResetAcctNameSpParam.builder()
                        .userId(userAndCorp.getUserId())
                        .corpId(userAndCorp.getCorpId())
                        .acctNumber(request.getAcctNumber())
                        .currency(request.getCurrency())
                        .acctTypeCode(request.getAccountTypeCode())
                        .build());
            } default -> {
                log.error("Invalid loan sub product code");
                return false;
            }
        }
    }
}
