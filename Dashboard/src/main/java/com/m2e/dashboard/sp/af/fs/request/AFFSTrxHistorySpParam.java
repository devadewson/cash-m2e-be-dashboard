package com.m2e.dashboard.sp.af.fs.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AFFSTrxHistorySpParam extends BasePageRequest {
    private String acctNumber;
    private String currency;
    private String corpId;
    private String userId;
    private String searchParam;
    private String startDateTime;
    private String endDateTime;
    private BigDecimal amtFrom;
    private BigDecimal amtTo;
    private String trxCode;

    public AFFSTrxHistorySpParam(String acctNumber, String corpId, String userId){
        this.acctNumber = acctNumber;
        this.corpId = corpId;
        this.userId = userId;
        this.searchParam = "";
        this.startDateTime = null;
        this.endDateTime = null;
        this.amtFrom = null;
        this.amtTo = null;
        this.trxCode = "";
    }
}
