package com.m2e.dashboard.jms.loan.tlf.response.account.activityinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBLoanTLFAcctActivityInquiryTrnRecDTO implements Serializable {
    @JsonProperty("TransactionDate")
    private String transactionDate;
    @JsonProperty("StatusCode")
    private String statusCode;
    @JsonProperty("ProcessingTime")
    private String processTime;
    @JsonProperty("EffectiveDate")
    private String effectiveDate;
    @JsonProperty("TransactionCode")
    private String transactionCode;
    @JsonProperty("TransactionType")
    private String txnType;
    @JsonProperty("TellerId")
    private String tellerId;
    @JsonProperty("OwnerInstitution")
    private String ownerInstitution;
    @JsonProperty("TransactionRef")
    private String txnReference;
    @JsonProperty("TransactionDesc1")
    private String transactionDescription1;
    @JsonProperty("SenderName")
    private String senderName;
    @JsonProperty("RecepientRef")
    private String recepientRef;
    @JsonProperty("OtherDescription")
    private String otherDescription;
    @JsonProperty("TransactionAmount")
    private String amount;
    @JsonProperty("TransactionStatus")
    private String transactionStatus;
    @JsonProperty("BranchCode")
    private String branchCode;
    @JsonProperty("Currency")
    private String currency;
}
