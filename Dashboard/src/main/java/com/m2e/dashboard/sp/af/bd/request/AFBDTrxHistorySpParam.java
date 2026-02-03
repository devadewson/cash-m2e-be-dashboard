package com.m2e.dashboard.sp.af.bd.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AFBDTrxHistorySpParam extends BasePageRequest {
    private String accountNumber;
    private String corpId;
    private String userId;
    private String searchParam;
    private String startDateTime;
    private String endDateTime;
    private BigDecimal amtFrom;
    private BigDecimal amtTo;
    private String trxCode;

    public AFBDTrxHistorySpParam(String accountNumber, String corpId, String userId){
        this.accountNumber = accountNumber;
        this.corpId = corpId;
        this.userId = userId;
        this.searchParam = null;
        this.startDateTime = null;
        this.endDateTime = null;
        this.amtFrom = null;
        this.amtTo = null;
        this.trxCode = null;
    }
}
