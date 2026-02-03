package com.m2e.dashboard.dto.task.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditAcctNameSubmissionDTO extends BaseTaskSubmissionDTO {

    private String defaultName;
    private String acctNumber;
    private String currentAcctName;
    private String newAcctName;

}
