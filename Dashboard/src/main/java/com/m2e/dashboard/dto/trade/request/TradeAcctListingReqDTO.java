package com.m2e.dashboard.dto.trade.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TradeAcctListingReqDTO extends BasePageRequest {
    private String corpId;
    private String userId;
    private String corpCifNo;
    private String searchKey;
    private String tradeCategory;
    private String acctType;
}
