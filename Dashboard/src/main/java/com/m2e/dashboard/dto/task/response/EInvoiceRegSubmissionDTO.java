package com.m2e.dashboard.dto.task.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EInvoiceRegSubmissionDTO extends BaseTaskSubmissionDTO {
    private String corpId;
    private String brn;
    private String tin;
    private String ssn;
}
