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

    private BigDecimal totalCA = BigDecimal.ZERO;
    private BigDecimal totalMFCA = BigDecimal.ZERO;
    private BigDecimal totalFCA = BigDecimal.ZERO;
    private BigDecimal totalSA = BigDecimal.ZERO;

    private BigDecimal totalVA = BigDecimal.ZERO;

    private BigDecimal totalFD = BigDecimal.ZERO;
    private BigDecimal totalTD = BigDecimal.ZERO;

    private BigDecimal totalInv = BigDecimal.ZERO;

    private BigDecimal totalDpOther = BigDecimal.ZERO;
    private BigDecimal totalLoanOther = BigDecimal.ZERO;

    private BigDecimal totalOD = BigDecimal.ZERO;
    private BigDecimal totalTL = BigDecimal.ZERO;
    private BigDecimal totalRc = BigDecimal.ZERO;

    private BigDecimal totalCrc = BigDecimal.ZERO;
    private BigDecimal totalChc = BigDecimal.ZERO;
    private BigDecimal totalDc = BigDecimal.ZERO;

    private BigDecimal totalTRD = BigDecimal.ZERO;
    private BigDecimal totalTRDI = BigDecimal.ZERO;
    private BigDecimal totalTRDE = BigDecimal.ZERO;
    private BigDecimal totalTRDG = BigDecimal.ZERO;
    private BigDecimal totalTRDL = BigDecimal.ZERO;

    private BigDecimal totalHp = BigDecimal.ZERO;
    private BigDecimal totalBhp = BigDecimal.ZERO;
    private BigDecimal totalFs = BigDecimal.ZERO;
    private BigDecimal totalBD = BigDecimal.ZERO;
    private BigDecimal totalPbr = BigDecimal.ZERO;
}
