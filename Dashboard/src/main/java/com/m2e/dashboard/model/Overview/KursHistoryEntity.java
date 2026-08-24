package com.m2e.dashboard.model.Overview;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "TB_KURS_HISTORY")
public class KursHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kursHistorySeq")
    @SequenceGenerator(name = "kursHistorySeq",
            sequenceName = "SEQ_KURS_HISTORY", allocationSize = 500)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BATCH_ID", nullable = false, length = 50)
    private String batchId;

    @Column(name = "KURS_DATE", nullable = false)
    private LocalDate kursDate;

    @Column(name = "FETCH_TIME", nullable = false)
    private LocalDateTime fetchTime;

    @Column(name = "RATE_NAME", length = 100)      private String rateName;
    @Column(name = "STR_INDEX", length = 10)       private String strIndex;
    @Column(name = "TENOR", length = 20)           private String tenor;
    @Column(name = "CCY", length = 20)             private String ccy;
    @Column(name = "BID_STATE", length = 10)       private String bidState;
    @Column(name = "ASK_STATE", length = 10)       private String askState;
    @Column(name = "CLIENT_NAME", length = 50)     private String clientName;
    @Column(name = "RATE_TIMESTAMP", length = 60)  private String rateTimestamp;
    @Column(name = "BID", length = 50)             private String bid;
    @Column(name = "ASK", length = 50)             private String ask;
    @Column(name = "BID_ALL_IN", length = 50)      private String bidAllIn;
    @Column(name = "ASK_ALL_IN", length = 50)      private String askAllIn;
    @Column(name = "BASE_DPS", length = 10)        private String baseDps;
    @Column(name = "TERMS_DPS", length = 10)       private String termsDps;
    @Column(name = "UNIT_QUOTATION", length = 10)  private String unitQuotation;
    @Column(name = "VALUE_DATE", length = 20)      private String valueDate;
    @Column(name = "VALUE_DATE_TYPE", length = 20) private String valueDateType;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
}
