package com.m2e.dashboard.jms.af.fs.request.account.activityinquiry;

import com.m2e.dashboard.dto.af.request.AFTrxHistoryFilterDTO;
import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;

@Data
public class ESBAFFSAcctActivityInquiryReqDTO extends BasePageRequest {
    private String acctNumber;
    private String currency;
    private String searchParam;
    private String provider;
    private AFTrxHistoryFilterDTO filters;
    private int page;
    private int pageSize;
    private String mbbLastKey;
}