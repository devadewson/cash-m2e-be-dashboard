package com.m2e.dashboard.dto.task.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskVarEInvoiceRegDTO implements Serializable {
    private String corpId;
    private String corpCifNo;
    private String brn;
    private String tin;
    private String ssn;
}
