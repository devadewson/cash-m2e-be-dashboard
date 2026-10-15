package com.m2e.dashboard.model.DataMart;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Kolom bersama file fasilitas dan draw PB/PPB.
 */
@Data
@MappedSuperclass
public abstract class LoanPbBaseEntity {

    @Column(name = "BASE_DATE")
    private LocalDate baseDate;

    @Column(name = "CIF_NO", nullable = false, length = 20)
    private String cifNo;

    @Column(name = "NOTE_NO", nullable = false, length = 20)
    private String noteNo;

    @Column(name = "DRAW_NO", length = 5)
    private String drawNo;

    @Column(name = "DRAW_DATE", length = 8)
    private String drawDate;

    @Column(name = "OUTSTANDING", precision = 30, scale = 2)
    private BigDecimal outstanding;

    @Column(name = "MATURITY_DATE", length = 8)
    private String maturityDate;

    @Column(name = "NEXT_PAYMENT_DUE", precision = 30, scale = 2)
    private BigDecimal nextPaymentDue;

    @Column(name = "NEXT_PAYMENT_DATE", length = 8)
    private String nextPaymentDate;

    @Column(name = "PLAFOND", precision = 30, scale = 2)
    private BigDecimal plafond;

    @Column(name = "AVAILABLE_LIMIT", precision = 30, scale = 2)
    private BigDecimal availableLimit;

    @Column(name = "AVAILABLE_PERIOD", length = 8)
    private String availablePeriod;

    @Column(name = "ACCRUE_METHOD", length = 2)
    private String accrueMethod;

    @Column(name = "SUBLIMIT", length = 2)
    private String sublimit;

    @Column(name = "STATUS", length = 2)
    private String status;

    @Column(name = "CURRENCY", length = 3)
    private String currency;

    @Column(name = "PROCESS_DATE", nullable = false)
    private LocalDateTime processDate;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
}