package com.m2e.dashboard.dto.card.credit.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.CreditCardType;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CCCreditCardAccListReqDTO extends BasePageRequest {
    private List<CreditCardType> cardType;
    private String openTaggedCorpId;
    private String searchParam;
    private ProviderEnum provider;
}
