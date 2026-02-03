package com.m2e.dashboard.service;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.overview.request.OverviewReqDTO;
import com.m2e.dashboard.dto.overview.response.OverviewResDTO;
import com.m2e.dashboard.enums.OverviewEnum;
import com.m2e.dashboard.enums.Product;

public interface OverviewProductStrategy {
    boolean supports(OverviewEnum product);
    public OverviewResDTO viewProduct(UserAndCorp userAndCorp, OverviewReqDTO overviewReqDto, OverviewResDTO overviewResDto);
}
