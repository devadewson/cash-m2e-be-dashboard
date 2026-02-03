package com.m2e.dashboard.dto.card.credit.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CCCreditCardMemberReqDTO extends BasePageRequest {
    private String cardNumber;
    private String searchParam;
    private ProviderEnum provider;
}
