package com.m2e.dashboard.jms.af.bd.response.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBAFBDDebitNoteDetailDTO implements Serializable {

    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("DebitNote")
    private String debitNote;
    @JsonProperty("debitNoteAmount")
    private String debitNoteAmount;
    @JsonProperty("Region")
    private String region;
    @JsonProperty("Details")
    private List<List<ESBAFBDAccountDetailDTO>> details;
}
