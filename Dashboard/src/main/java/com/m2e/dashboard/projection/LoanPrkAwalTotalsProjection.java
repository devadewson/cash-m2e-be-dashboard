package com.m2e.dashboard.projection;

import java.math.BigDecimal;

/** Total LIMIT_AWAL dan AVAILABLE_LIMIT_AWAL satu CIF pada tanggal muat terakhir. */
public interface LoanPrkAwalTotalsProjection {
    BigDecimal getTotalLimitAwal();
    BigDecimal getTotalAvailableLimitAwal();
    String getCurrency();
}