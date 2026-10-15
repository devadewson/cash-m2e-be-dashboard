package com.m2e.dashboard.model.DataMart;

import jakarta.persistence.Entity;
import lombok.Data;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "DATAMART_LOAN_PRK")
public class LoanPrkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loanPrkSeq")
    @SequenceGenerator(name = "loanPrkSeq",
            sequenceName = "SEQ_DATAMART_LOAN_PRK", allocationSize = 500)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BASE_DATE")
    private LocalDate baseDate;

    @Column(name = "CIF_NO", nullable = false, length = 50)
    private String cifNo;

    @Column(name = "ACCOUNT_NO", nullable = false, length = 50)
    private String accountNo;

    @Column(name = "CURRENCY", length = 3)
    private String currency;

    @Column(name = "LIMIT_AMOUNT", precision = 30, scale = 2)
    private BigDecimal limitAmount;

    @Column(name = "OUTSTANDING", precision = 30, scale = 2)
    private BigDecimal outstanding;

    @Column(name = "MATURITY_DATE")
    private LocalDate maturityDate;

    @Column(name = "INTEREST_DUE", precision = 30, scale = 2)
    private BigDecimal interestDue;

    @Column(name = "AVAILABLE_LIMIT_AWAL", precision = 30, scale = 2)
    private BigDecimal availableLimitAwal;

    @Column(name = "LIMIT_AWAL", precision = 30, scale = 2)
    private BigDecimal limitAwal;

    @Column(name = "PROCESS_DATE", nullable = false)
    private LocalDateTime processDate;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
}
