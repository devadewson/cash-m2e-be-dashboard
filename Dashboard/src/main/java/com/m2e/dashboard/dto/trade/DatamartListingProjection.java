package com.m2e.dashboard.dto.trade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DatamartListingProjection {
    String getRefNo();
    String getProductType();
    String getCurrency();
    BigDecimal getInitialAmount();
    BigDecimal getOutstandingAmount();
    Integer getTenor();
    LocalDateTime getReleaseDate();
    LocalDate getMaturityDate();
    String getDrawer();
    String getRemitingBank();
    String getBeneficiary();
    String getApplicant();
    String getIssuingBank();
}