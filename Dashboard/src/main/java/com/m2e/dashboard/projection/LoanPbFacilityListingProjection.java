package com.m2e.dashboard.projection;


import java.math.BigDecimal;

public interface LoanPbFacilityListingProjection {
    String getNoteNo();
    String getCurrency();
    BigDecimal getPlafond();
    BigDecimal getAvailableLimit();
    String getAvailablePeriod();
}