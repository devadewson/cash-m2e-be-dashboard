package com.m2e.dashboard.repository;


import java.math.BigDecimal;
import java.time.LocalDate;

public interface DepositFdListingProjection {
    String getDepositNumber();
    LocalDate getPlacementDate();
    LocalDate getMaturityDate();
    String getCurrency();
    BigDecimal getPrincipal();
    Integer getTenor();
    BigDecimal getInterestRate();
}