package com.m2e.dashboard.model.DataMart;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "DATAMART_PLAFOND", indexes = {
        @Index(name = "IX_DTM_PLAFOND_CIF_DATE", columnList = "CIF_NO, PROCESS_DATE"),
        @Index(name = "IX_DTM_PLAFOND_PROCESS",  columnList = "PROCESS_DATE")
})
public class LoanPlafondEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loanPlafondSeq")
    @SequenceGenerator(name = "loanPlafondSeq",
            sequenceName = "SEQ_DATAMART_PLAFOND", allocationSize = 500)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BASE_DATE")
    private LocalDate baseDate;

    @Column(name = "CIF_NO", nullable = false, length = 20)
    private String cifNo;

    @Column(name = "PLAFOND", precision = 30, scale = 2)
    private BigDecimal plafond;

    @Column(name = "PROCESS_DATE", nullable = false)
    private LocalDateTime processDate;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
}