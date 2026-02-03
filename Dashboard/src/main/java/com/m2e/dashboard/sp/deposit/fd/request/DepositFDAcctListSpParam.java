package com.m2e.dashboard.sp.deposit.fd.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class DepositFDAcctListSpParam extends BasePageRequest {
    private String userId;
    private String corpId;
    private String searchParam;
    private String corpCifNo;
}
