package com.m2e.dashboard.service.maintenance.strategy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.m2e.dashboard.dto.task.request.TaskVarEditAcctNameDTO;
import com.m2e.dashboard.dto.task.response.EditAcctNameApprovalDTO;
import com.m2e.dashboard.dto.task.response.TaskDetailDTO;
import com.m2e.dashboard.enums.MTFunctionActionEnum;
import com.m2e.dashboard.service.TaskActionResolver;
import org.springframework.stereotype.Component;

@Component
public class EditAcctNameTaskActionResolver
        implements TaskActionResolver<TaskVarEditAcctNameDTO, TaskDetailDTO<EditAcctNameApprovalDTO>, TaskDetailDTO<EditAcctNameApprovalDTO>> {

    @Override
    public String getSupportedTaskType() {
        return MTFunctionActionEnum.EDIT_ACCT_NM.name();
    }

    @Override
    public Class<TaskVarEditAcctNameDTO> getSubmitClass() {
        return TaskVarEditAcctNameDTO.class;
    }

    @Override
    public TypeReference<TaskDetailDTO<EditAcctNameApprovalDTO>> getApprovalTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    public TypeReference<TaskDetailDTO<EditAcctNameApprovalDTO>> getDetailTypeReference() {
        return new TypeReference<>() {};
    }
}
