package com.m2e.dashboard.dto.deposit.va;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class DepositAcctCompanyReq extends BasePageRequest {
    String searchKey;
    String userId;
    String corpId;
}