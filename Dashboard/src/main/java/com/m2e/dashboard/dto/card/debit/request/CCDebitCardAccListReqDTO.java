package com.m2e.dashboard.dto.card.debit.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CCDebitCardAccListReqDTO extends BasePageRequest {
    private String openTaggedCorpId;
    private ProviderEnum provider;
}
