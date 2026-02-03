package com.m2e.dashboard.sp.deposit.fd.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DepositFDTrxHistoryListSpParam extends BasePageRequest {
    private String acctNumber;
    private String searchParam;
    private String startDateTime;
    private String endDateTime;
    private Double amtFrom;
    private Double amtTo;
    private String userId;
    private String corpId;
}
