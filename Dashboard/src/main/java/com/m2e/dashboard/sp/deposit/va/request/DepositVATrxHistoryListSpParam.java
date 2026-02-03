package com.m2e.dashboard.sp.deposit.va.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class DepositVATrxHistoryListSpParam extends BasePageRequest {
    private String virtualAcctNumber;
    private String parentAcctNumber;
    private String trxCode;
    private String searchParam;
    private String startDateTime;
    private String endDateTime;
    private String amtFrom;
    private String amtTo;
    private String userId;
    private String corpId;

}
