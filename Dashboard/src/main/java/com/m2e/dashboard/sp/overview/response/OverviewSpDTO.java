package com.m2e.dashboard.sp.overview.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.regex.Pattern;

@Data
@SuperBuilder
@NoArgsConstructor(force = true)
public class OverviewSpDTO implements Serializable {
    private String subProductDesc;
    private String noOfAccounts;
    private BigDecimal ledgerBalance;
    private BigDecimal availableBalance;
    private BigDecimal facilityLimit;
    private BigDecimal outstandingBalance;
}
