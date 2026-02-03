package com.m2e.dashboard.dto.task.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskSubmitReqDTO<T> implements Serializable {
    @Schema(description = "Type of task. E.g., EDIT_ACCT_NM, EINVOICE_REGISTRATION")
    private String taskType;

    @Schema(
            description = """
            Task-specific payload. Varies based on taskType:
            - EDIT_ACCT_NM → TaskVarEditAcctNameDTO
            - EINVOICE_REGISTRATION → TaskVarEInvoiceRegDTO
            """,
            oneOf = {
                    TaskVarEditAcctNameDTO.class,
                    TaskVarEInvoiceRegDTO.class
            }
    )
    private T data;
}
