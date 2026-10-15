package com.m2e.dashboard.model.DataMart;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "DATAMART_LOAN_PB_FACILITY")
public class LoanPbFacilityEntity extends LoanPbBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loanPbFacilitySeq")
    @SequenceGenerator(name = "loanPbFacilitySeq",
            sequenceName = "SEQ_DTM_LOAN_PB_FACILITY", allocationSize = 500)
    @Column(name = "ID")
    private Long id;
}