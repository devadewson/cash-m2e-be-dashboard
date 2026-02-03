package com.m2e.dashboard.service;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.maintenance.request.MTResetAccctNameReqDTO;
import com.m2e.dashboard.enums.Product;

public interface ResetAcctNameStrategy {
    boolean supports(Product product);
    boolean resetAcctName(UserAndCorp userAndCorp, MTResetAccctNameReqDTO request);
}
