package com.m2e.dashboard.dto.otherbank.request.deposit;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.OtherBankViewType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class OtherBankDepositAcctListingReqDTO extends BasePageRequest {
    private String searchParam;
    private OtherBankViewType viewType;
}
