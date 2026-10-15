package com.m2e.dashboard.model.DataMart;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "DATAMART_LOAN_PB_DRAW")
public class LoanPbDrawEntity extends LoanPbBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loanPbDrawSeq")
    @SequenceGenerator(name = "loanPbDrawSeq",
            sequenceName = "SEQ_DTM_LOAN_PB_DRAW", allocationSize = 500)
    @Column(name = "ID")
    private Long id;
}