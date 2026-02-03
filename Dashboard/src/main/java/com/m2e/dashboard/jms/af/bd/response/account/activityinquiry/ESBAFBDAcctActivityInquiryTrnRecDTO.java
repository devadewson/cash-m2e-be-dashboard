package com.m2e.dashboard.jms.af.bd.response.account.activityinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBAFBDAcctActivityInquiryTrnRecDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("ValueDate")
    private String valueDate;
    @JsonProperty("ValueTime")
    private String valueTime;
    @JsonProperty("EffectiveDate")
    private String effectiveDate;
    @JsonProperty("TransactionCode")
    private String transactionCode;
    @JsonProperty("TransactionType")
    private String transactionType;
    @JsonProperty("AuxTransactionCode")
    private String auxTransactionCode;
    @JsonProperty("CreditDebitIndicator")
    private String creditDebitIndicator;
    @JsonProperty("SourceCode")
    private String sourceCode;
    @JsonProperty("TellerId")
    private String tellerId;
    @JsonProperty("OwnerInstituition")
    private String ownerInstitution;
    @JsonProperty("ChequeNo")
    private String chequeNo;
    @JsonProperty("Reference")
    private String reference;
    @JsonProperty("TransactionDescription1")
    private String transactionDescription1;
    @JsonProperty("TransactionDescription2")
    private String transactionDescription2;
    @JsonProperty("TransactionDescription3")
    private String transactionDescription3;
    @JsonProperty("TransactionDescription4")
    private String transactionDescription4;
    @JsonProperty("TransactionDescription5")
    private String transactionDescription5;
    @JsonProperty("TransactionDescription6")
    private String transactionDescription6;
    @JsonProperty("Amount")
    private String amount;
    @JsonProperty("TransactionStatus")
    private String transactionStatus;
}
