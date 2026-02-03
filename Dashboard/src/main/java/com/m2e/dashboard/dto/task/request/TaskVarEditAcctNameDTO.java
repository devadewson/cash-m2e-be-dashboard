package com.m2e.dashboard.dto.task.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskVarEditAcctNameDTO implements Serializable {
    private String acctNumber;
    private String newAcctName;
}
