package com.m2e.dashboard.projection;


import java.math.BigDecimal;

public interface LoanPbDrawListingProjection {
    String getNoteNo();
    String getDrawNo();
    String getDrawDate();
    String getCurrency();
    BigDecimal getOutstanding();
    String getMaturityDate();
    BigDecimal getNextPaymentDue();
    String getNextPaymentDate();
}
