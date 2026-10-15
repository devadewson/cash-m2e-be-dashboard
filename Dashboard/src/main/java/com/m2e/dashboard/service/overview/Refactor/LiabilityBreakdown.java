package com.m2e.dashboard.service.overview.Refactor;

import java.math.BigDecimal;

public record LiabilityBreakdown(
        BigDecimal trd,
        BigDecimal trdImport,
        BigDecimal trdExport,
        BigDecimal trdGuarantee,
        BigDecimal trdFinancing,
        BigDecimal od,
        BigDecimal tl,
        BigDecimal rc) {

    public static LiabilityBreakdown zero() {
        BigDecimal z = BigDecimal.ZERO;
        return new LiabilityBreakdown(z, z, z, z, z, z, z, z);
    }

    /** Tanpa bucket trade, untuk job snapshot. */
    public static LiabilityBreakdown totalsOnly(BigDecimal trd, BigDecimal od, BigDecimal tl, BigDecimal rc) {
        BigDecimal z = BigDecimal.ZERO;
        return new LiabilityBreakdown(nz(trd), z, z, z, z, nz(od), nz(tl), nz(rc));
    }

    public BigDecimal total() {
        return trd.add(od).add(tl).add(rc);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
