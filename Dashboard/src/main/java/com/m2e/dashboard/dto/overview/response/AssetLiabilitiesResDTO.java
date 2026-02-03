package com.m2e.dashboard.dto.overview.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssetLiabilitiesResDTO {

    private String currency;

    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal totalAsset;

    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal totalLiabilities;

    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal totalAssetRatio;

    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal totalLiabilitiesRatio;

    private String lastUpdatedDate;

    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal workingRatio;

    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal quickRatio;

    //add all products response from SP
    private BigDecimal totalCA;
    private BigDecimal totalMFCA;
    private BigDecimal totalFCA;

    private BigDecimal totalVA;

    private BigDecimal totalFD;
    private BigDecimal totalTD;

    private BigDecimal totalInv;

    private BigDecimal totalDpOther;
    private BigDecimal totalLoanOther;

    private BigDecimal totalOD;
    private BigDecimal totalTL;
    private BigDecimal totalRc;

    private BigDecimal totalCrc;
    private BigDecimal totalChc;
    private BigDecimal totalDc;

    private BigDecimal totalTRD;
    private BigDecimal totalTRDI;
    private BigDecimal totalTRDE;
    private BigDecimal totalTRDG;
    private BigDecimal totalTRDL;

    private BigDecimal totalHp;
    private BigDecimal totalBhp;
    private BigDecimal totalFs;
    private BigDecimal totalBD;
    private BigDecimal totalPbr;

}
