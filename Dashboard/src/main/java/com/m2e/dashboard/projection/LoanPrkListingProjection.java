package com.m2e.dashboard.projection;


import java.math.BigDecimal;
import java.time.LocalDate;

public interface LoanPrkListingProjection {
    String getAccountNo();
    String getCurrency();
    BigDecimal getLimitAmount();
    BigDecimal getOutstanding();
    LocalDate getMaturityDate();
    BigDecimal getInterestDue();
}