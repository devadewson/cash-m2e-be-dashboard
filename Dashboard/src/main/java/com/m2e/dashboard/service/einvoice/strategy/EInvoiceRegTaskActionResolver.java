package com.m2e.dashboard.service.einvoice.strategy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.m2e.dashboard.dto.task.request.TaskVarEInvoiceRegDTO;
import com.m2e.dashboard.dto.task.response.EInvoiceRegApprovalDTO;
import com.m2e.dashboard.dto.task.response.TaskDetailDTO;
import com.m2e.dashboard.enums.MTFunctionActionEnum;
import com.m2e.dashboard.service.TaskActionResolver;
import org.springframework.stereotype.Component;

@Component
public class EInvoiceRegTaskActionResolver implements TaskActionResolver<TaskVarEInvoiceRegDTO, TaskDetailDTO<EInvoiceRegApprovalDTO>, TaskDetailDTO<EInvoiceRegApprovalDTO>> {

    @Override
    public String getSupportedTaskType() {
        return MTFunctionActionEnum.EINVOICE_REGISTRATION.name();
    }

    @Override
    public Class<TaskVarEInvoiceRegDTO> getSubmitClass() {
        return TaskVarEInvoiceRegDTO.class;
    }

    @Override
    public TypeReference<TaskDetailDTO<EInvoiceRegApprovalDTO>> getApprovalTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    public TypeReference<TaskDetailDTO<EInvoiceRegApprovalDTO>> getDetailTypeReference() {
        return new TypeReference<>() {};
    }
}

