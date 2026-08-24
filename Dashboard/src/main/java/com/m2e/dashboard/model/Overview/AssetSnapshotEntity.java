package com.m2e.dashboard.model.Overview;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "TB_OV_ASSET_SNAPSHOT")
public class AssetSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ovAssetSnapshotSeq")
    @SequenceGenerator(name = "ovAssetSnapshotSeq",
            sequenceName = "SEQ_OV_ASSET_SNAPSHOT", allocationSize = 500)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SNAPSHOT_DATE", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "CORP_CIF_NO", nullable = false, length = 50)
    private String corpCifNo;

    @Column(name = "WEEK_KEY", nullable = false, length = 10)
    private String weekKey;

    @Column(name = "MONTH_KEY", nullable = false, length = 10)
    private String monthKey;

    @Column(name = "QUARTER_KEY", nullable = false, length = 10)
    private String quarterKey;

    @Column(name = "CURRENCY", nullable = false, length = 3)
    private String currency;

    @Column(name = "TOTAL_ASSET", nullable = false, precision = 30, scale = 6)
    private BigDecimal totalAsset;

    @Column(name = "TOTAL_LIABILITIES", nullable = false, precision = 30, scale = 6)
    private BigDecimal totalLiabilities;

    @Column(name = "TOTAL_CA",       precision = 30, scale = 6) private BigDecimal totalCa;
    @Column(name = "TOTAL_MFCA",     precision = 30, scale = 6) private BigDecimal totalMfca;
    @Column(name = "TOTAL_FCA",      precision = 30, scale = 6) private BigDecimal totalFca;
    @Column(name = "TOTAL_FD",       precision = 30, scale = 6) private BigDecimal totalFd;
    @Column(name = "TOTAL_TD",       precision = 30, scale = 6) private BigDecimal totalTd;
    @Column(name = "TOTAL_VA",       precision = 30, scale = 6) private BigDecimal totalVa;
    @Column(name = "TOTAL_DP_OTHER", precision = 30, scale = 6) private BigDecimal totalDpOther;

    @Column(name = "ACCOUNT_COUNT")
    private Integer accountCount;

    @Column(name = "SOURCE_STATUS", nullable = false, length = 20)
    private String sourceStatus;

    @Column(name = "KURS_VALUE_DATE", length = 20)
    private String kursValueDate;

    @Column(name = "JOB_RUN_ID", length = 50)
    private String jobRunId;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
}
