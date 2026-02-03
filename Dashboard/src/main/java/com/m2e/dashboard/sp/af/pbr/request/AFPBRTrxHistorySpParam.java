package com.m2e.dashboard.sp.af.pbr.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class AFPBRTrxHistorySpParam extends BasePageRequest {
    private String accountNumber;
    private String corpId;
    private String userId;
    private String searchParam;
    private String startDateTime;
    private String endDateTime;
    private BigDecimal amtFrom;
    private BigDecimal amtTo;
    private String trxCode;

    public AFPBRTrxHistorySpParam(String accountNumber, String corpId, String userId){
        this.accountNumber = accountNumber;
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