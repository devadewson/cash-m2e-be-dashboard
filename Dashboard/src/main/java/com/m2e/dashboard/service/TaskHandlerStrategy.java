package com.m2e.dashboard.service;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.enums.ApprovalStatusEnum;
import com.m2e.dashboard.projection.TaskProjection;

import java.util.Map;

public interface TaskHandlerStrategy{
    boolean supports(String taskType); // For getDetail and approval
    boolean supports(Object requestData); // For submission

    ApiResponse<?> submit(UserAndCorp user, Object data);

    default ApiResponse<?> approve(UserAndCorp user, TaskProjection task, ApprovalStatusEnum approvalStatus, String remarks, Map<?,?>... args){
        preIntercept(args);
        ApiResponse<?> result = approveAction(user, task, approvalStatus,remarks);
        postIntercept(args);
        return result;
    }
    ApiResponse<?> approveAction(UserAndCorp user, TaskProjection task, ApprovalStatusEnum approvalStatus, String remarks);
    ApiResponse<?> getDetail(UserAndCorp user, TaskProjection task);

    void preIntercept(Map<?,?>... args);

    void postIntercept(Map<?,?>... args);
}
