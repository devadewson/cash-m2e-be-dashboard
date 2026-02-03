package com.m2e.dashboard.dto.card.charge.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CCChargeCardAccListReqDTO extends BasePageRequest {
    private String openTaggedCorpId;
    private String searchParam;
}