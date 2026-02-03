package com.m2e.dashboard.sp.loan.tlf.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class LNTLFTrxHistorySpParam extends BasePageRequest {
    private String acctNumber;
    private String currency;
    private String provider;
    private String corpId;
    private String userId;
    private String isFitas;
    private String searchParam;
    private String startDateTime;
    private String endDateTime;
    private BigDecimal amtFrom;
    private BigDecimal amtTo;
    private String trxCode;

    public LNTLFTrxHistorySpParam(String acctNumber, String currency, String provider, String corpId, String userId, boolean fitasFlag, String searchParam, String trxCode){
        this.acctNumber = acctNumber;
        this.currency = currency;
        this.provider = provider;
        this.corpId = corpId;
        this.userId = userId;
        this.searchParam = searchParam;
        this.startDateTime = null;
        this.endDateTime = null;
        this.amtFrom = null;
        this.amtTo = null;
        this.trxCode = trxCode;

        if (fitasFlag) {
            this.isFitas = "Y";
        } else {
            this.isFitas = "N";
        }
    }
}
