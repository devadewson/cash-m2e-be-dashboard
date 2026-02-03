package com.m2e.dashboard.dto.card.common.request;

import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;

@Data
public class CCStatsReqDTO {
    private String corpId;
    private ProviderEnum provider;
    private boolean isTodayToggle;
}
