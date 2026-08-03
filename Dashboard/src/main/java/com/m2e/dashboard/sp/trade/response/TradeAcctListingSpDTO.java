package com.m2e.dashboard.sp.trade.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor(force = true)
public class TradeAcctListingSpDTO implements Serializable {
    private String refNumber;
    private String facilityType;
    private String currency;
    private String initialAmount;
    private String outstandingAmount;
    private String tenor;
    private Date releaseDate;
    private Date maturityDate;
    private String drawerName;
    private String remitingBank;
    private String beneficiaryName;
    private String applicantName;
    private String issuingBank;
    private String numberOfItems;
}