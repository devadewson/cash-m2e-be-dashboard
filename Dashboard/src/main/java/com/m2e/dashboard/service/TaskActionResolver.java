package com.m2e.dashboard.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.dto.task.response.TaskDetailDTO;

public interface TaskActionResolver<S, A, D> {
    String getSupportedTaskType();

    Class<S> getSubmitClass();
    TypeReference<A> getApprovalTypeReference();
    TypeReference<D> getDetailTypeReference();

    default S wrapSubmit(Object rawData, ObjectMapper mapper) {
        return mapper.convertValue(rawData, getSubmitClass());
    }

    default A wrapApproval(Object rawData, ObjectMapper mapper) {
        return mapper.convertValue(rawData, getApprovalTypeReference());
    }

    default TaskDetailDTO<D> wrapDetail(Object rawData, ObjectMapper mapper) {
        D detailData = mapper.convertValue(rawData, getDetailTypeReference());
        return new TaskDetailDTO<>(detailData);
    }
}


