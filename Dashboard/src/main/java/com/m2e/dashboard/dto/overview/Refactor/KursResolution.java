package com.m2e.dashboard.dto.overview.Refactor;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class KursResolution {

    private String source;
    private Map<String, BigDecimal> rateMap;
    private String valueDate;
    private String fetchTime;
    private long ageHours;
    private boolean stale;
}
