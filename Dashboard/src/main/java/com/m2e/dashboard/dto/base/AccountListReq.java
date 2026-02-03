package com.m2e.dashboard.dto.base;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccountListReq extends BasePageRequest {
    private Boolean realTimeIndicator;
    private String searchParam;
    private String openTaggedCorpId;
    private String corpCifNo;
}
