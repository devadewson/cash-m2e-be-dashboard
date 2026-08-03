package com.m2e.dashboard.model.XLBT.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoanAccount {
    @JsonProperty("NoteNo")
    private String NoteNo;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("MaturityDate")
    private String maturityDate;
    @JsonProperty("NoteDate")
    private String noteDate;
    @JsonProperty("NoteType")
    private String noteType;
    @JsonProperty("PrincipalBalance")
    private String principalBalance;
    @JsonProperty("CommitBalance")
    private String commitBalance;
    @JsonProperty("Description")
    private String description;

    public String getNoteNo() {
        return NoteNo;
    }

    public void setNoteNo(String noteNo) {
        NoteNo = noteNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String getPrincipalBalance() {
        return principalBalance;
    }

    public void setPrincipalBalance(String principalBalance) {
        this.principalBalance = principalBalance;
    }

    public String getCommitBalance() {
        return commitBalance;
    }

    public void setCommitBalance(String commitBalance) {
        this.commitBalance = commitBalance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
