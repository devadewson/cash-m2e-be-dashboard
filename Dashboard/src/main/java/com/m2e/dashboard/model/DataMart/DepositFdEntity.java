package com.m2e.dashboard.model.DataMart;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "DATAMART_DEPOSIT_FD")
public class DepositFdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "depositFdSeq")
    @SequenceGenerator(name = "depositFdSeq",
            sequenceName = "SEQ_DATAMART_DEPOSIT_FD", allocationSize = 500)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CIF_NO", nullable = false, length = 50)
    private String cifNo;

    @Column(name = "DEPOSIT_NUMBER", nullable = false, length = 50)
    private String depositNumber;

    @Column(name = "PLACEMENT_DATE")
    private LocalDate placementDate;

    @Column(name = "MATURITY_DATE")
    private LocalDate maturityDate;

    @Column(name = "CURRENCY", length = 3)
    private String currency;

    @Column(name = "PRINCIPAL", precision = 30, scale = 2)
    private BigDecimal principal;

    @Column(name = "TENOR")
    private Integer tenor;

    @Column(name = "INTEREST_RATE", precision = 12, scale = 6)
    private BigDecimal interestRate;

    @Column(name = "PROCESS_DATE", nullable = false)
    private LocalDateTime processDate;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
}