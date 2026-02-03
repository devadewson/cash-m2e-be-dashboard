package com.m2e.dashboard.dto.card.charge.request;

import lombok.Data;

@Data
public class CCChargeCardStatsReqDTO {
    private String corpId;
    private boolean isTodayToggle;
}
