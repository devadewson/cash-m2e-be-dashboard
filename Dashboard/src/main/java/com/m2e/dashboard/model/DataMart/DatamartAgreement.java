package com.m2e.dashboard.model.DataMart;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "DATAMART_AGREEMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatamartAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "datamart_seq")
    @SequenceGenerator(name = "datamart_seq", sequenceName = "DATAMART_SEQ", allocationSize = 500)
    private Long id;

    @Column(name = "RPT_DT")
    private LocalDate rptDt;

    @Column(name = "MASTER_CODE")
    private String masterCode;

    @Column(name = "SEQ_CODE")
    private String seqCode;

    @Column(name = "SEQ_SRL")
    private Integer seqSrl;

    @Column(name = "PAYMENT_OPTION")
    private String paymentOption;

    @Column(name = "PRODUCT_TYPE")
    private String productType;

    @Column(name = "REF_NO")
    private String refNo;

    @Column(name = "SEQ_NO")
    private String seqNo;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "INITIAL_AMOUNT")
    private BigDecimal initialAmount;

    @Column(name = "OUTSTANDING_AMOUNT")
    private BigDecimal outstandingAmount;

    @Column(name = "RELEASE_DATE")
    private LocalDateTime releaseDate;

    @Column(name = "MATURITY_DATE")
    private LocalDate maturityDate;

    @Column(name = "TENOR")
    private Integer tenor;

    @Column(name = "CIF_DRAWER")
    private String cifDrawer;

    @Column(name = "DRAWER")
    private String drawer;

    @Column(name = "CIF_DRAWEE")
    private String cifDrawee;

    @Column(name = "DRAWEE")
    private String drawee;

    @Column(name = "CIF_REMITING_BANK")
    private String cifRemitingBank;

    @Column(name = "REMITING_BANK")
    private String remitingBank;

    @Column(name = "CIF_APPLICANT")
    private String cifApplicant;

    @Column(name = "APPLICANT")
    private String applicant;

    @Column(name = "CIF_BENEFICIARY")
    private String cifBeneficiary;

    @Column(name = "BENEFICIARY")
    private String beneficiary;

    @Column(name = "CIF_ISSUING_BANK")
    private String cifIssuingBank;

    @Column(name = "ISSUING_BANK")
    private String issuingBank;

    @Column(name = "CIF_DEBIT_NAME")
    private String cifDebitName;

    @Column(name = "DEBIT_NAME")
    private String debitName;

    @Column(name = "FACILITY")
    private String facility;

    @Column(name = "FACILITY_NAME")
    private String facilityName;

    @Column(name = "PROCESS_DATE")
    private LocalDateTime processDate;
}