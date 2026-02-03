package com.m2e.dashboard.sp.deposit.ca.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class DepositCAAccountListSpParam extends BasePageRequest {
    String corpId;
    String userId;
    String searchParam;
    String corpCifNo;
}
