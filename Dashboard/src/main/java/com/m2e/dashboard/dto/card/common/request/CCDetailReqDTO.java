package com.m2e.dashboard.dto.card.common.request;

import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;

@Data
public class CCDetailReqDTO {
    private String cardNumber;
    private String cardName;
    private ProviderEnum provider;
}
