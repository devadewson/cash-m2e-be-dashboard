package com.m2e.dashboard.sp.va.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class DepositVASpParam extends BasePageRequest {
    String corpId;
    String userId;
    String searchParam;
    String virtualAcctNumber;
    String corporateCode;
    String corpCifNo;
}