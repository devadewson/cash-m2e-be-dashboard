package com.m2e.dashboard.dto.task.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Approval DTO for E-Invoice Registration")
public class EInvoiceRegApprovalDTO extends BaseTaskApprovalDTO<EInvoiceRegSubmissionDTO> {
    private String companyName;
    private String taskId;

    @Override
    @Schema(
            description = "E-Invoice registration submission data.",
            implementation = EInvoiceRegSubmissionDTO.class
    )
    public EInvoiceRegSubmissionDTO getSubmissionData() {
        return super.getSubmissionData();
    }

    @Override
    public void setSubmissionData(EInvoiceRegSubmissionDTO submissionData) {
        super.setSubmissionData(submissionData);
    }
}
