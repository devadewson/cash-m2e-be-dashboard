package com.m2e.dashboard.dto.task.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Approval DTO for Edit Account Name")
public class EditAcctNameApprovalDTO extends BaseTaskApprovalDTO<EditAcctNameSubmissionDTO> {
    private String companyName;
    private String taskId;

    @Override
    @Schema(
            description = "Edit Account Name submission data.",
            implementation = EditAcctNameSubmissionDTO.class
    )
    public EditAcctNameSubmissionDTO getSubmissionData() {
        return super.getSubmissionData();
    }

    @Override
    public void setSubmissionData(EditAcctNameSubmissionDTO submissionData) {
        super.setSubmissionData(submissionData);
    }
}
