package com.m2e.dashboard.sp.investment.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class INVUnitTrustTrxHistSpParam extends BasePageRequest {
    private String acctNumber;
    private String searchParam;
    private String fundName;
    private String startDate;
    private String endDate;
    private String userId;
    private String corpId;
}