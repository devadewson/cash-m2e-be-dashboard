package com.m2e.dashboard.sp.deposit.ca.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class DepositCATrxHistoryListSpParam extends BasePageRequest {
    private String acctNumber;
    private String acctCurrency;
    private String trxCode;
    private String searchParam;
    private String startDateTime;
    private String endDateTime;
    private Double amtFrom;
    private Double amtTo;
    private String userId;
    private String corpId;
}
