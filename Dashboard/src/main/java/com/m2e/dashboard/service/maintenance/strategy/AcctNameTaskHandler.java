package com.m2e.dashboard.service.maintenance.strategy;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.NotificationProcessingRequest;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.task.request.TaskVarEditAcctNameDTO;
import com.m2e.dashboard.dto.task.response.EditAcctNameApprovalDTO;
import com.m2e.dashboard.dto.task.response.TaskDetailDTO;
import com.m2e.dashboard.enums.AdminEventEnum;
import com.m2e.dashboard.enums.ApprovalStatusEnum;
import com.m2e.dashboard.enums.MTFunctionEnum;
import com.m2e.dashboard.enums.TaskVarEnum;
import com.m2e.dashboard.mapper.task.EditAcctNameMapper;
import com.m2e.dashboard.projection.AcctProjection;
import com.m2e.dashboard.projection.TaskHistoryProjection;
import com.m2e.dashboard.projection.TaskProjection;
import com.m2e.dashboard.projection.TaskVarProjection;
import com.m2e.dashboard.repository.*;
import com.m2e.dashboard.service.TaskHandlerStrategy;
import com.m2e.dashboard.sp.task.response.TaskSubmissionSpDTO;
import com.m2e.dashboard.util.UtilityFunctions;
import com.m2e.model.sso.base.CfoTaskVar;
import com.m2e.utils.webclient.dto.ApiRequestDTO;
import com.m2e.utils.webclient.service.APIClientService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpMethod.POST;

@Component
@RequiredArgsConstructor
public class AcctNameTaskHandler implements TaskHandlerStrategy {

    private static final Logger log = LoggerFactory.getLogger(AcctNameTaskHandler.class);
    private static final String PREFERRED_NAME_MAINTENANCE = "PREFERRED_NAME_MAINTENANCE";
    private final TaskRepository taskRepository;
    private final TaskVarRepository taskVarRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final EditAcctNameMapper editAcctNameMapper;
    private final AccountRepository accountRepository;
    private final APIClientService webClient;
    private final CorpUserRepository corpUserRepository;

    @Value("${m2e.integration.notificationProcessing.url}")
    private String notificationUrl;
    @Value("${m2e.integration.notificationProcessing.uri}")
    private String notificationUri;

    @Override
    public boolean supports(String taskType) {
        return MTFunctionEnum.ACCT_NM.name().equalsIgnoreCase(taskType);
    }

    @Override
    public boolean supports(Object requestData) {
        return requestData instanceof TaskVarEditAcctNameDTO;
    }

    @Override
    @Transactional
    public ApiResponse<?> submit(UserAndCorp user, Object data) {
        if (!(data instanceof TaskVarEditAcctNameDTO request) || user == null || user.getUserId() == null) {
            return new ApiResponse<String>().buildError("500", "Invalid request data or user context");
        }

        if (request.getAcctNumber() == null || request.getNewAcctName() == null) {
            return new ApiResponse<String>().buildError("500", "Account number and new account name are required");
        }

        TaskSubmissionSpDTO task = null;

        try {
            task = editAcctNameMapper.createTask(user, request);
            log.info("Task {} submitted successfully.", task.getTaskId());
            TaskProjection taskProjection = taskRepository.findByTaskId(task.getTaskId()).get();
            editAcctNameMapper.createTaskHistory(user, taskProjection, ApprovalStatusEnum.PENDING_APPR, "");
            log.info("Task history {} submitted successfully.", task.getTaskId());
//            prepareAndSendNotification(user.getUserId(), AdminEventEnum.MAKER_ADM_INIT, task.getTaskId(), null, request.getAcctNumber());
            return new ApiResponse<String>().buildSuccess(task.getTaskId());
        } catch (RuntimeException e) {
            log.error("Error during task submission, rolling back if partial save occurred", e);
            if (task != null && task.getId() != null) {
                taskRepository.deleteById(task.getId());
                List<TaskVarProjection> idTaskVars = taskVarRepository.findIdsByTaskId(task.getTaskId());
                if (Optional.ofNullable(idTaskVars).filter(list -> !list.isEmpty()).isPresent()) {
                    taskVarRepository.deleteById(task.getId());
                }
                List<TaskHistoryProjection> idTaskHis = taskHistoryRepository.findIdsByTaskId(task.getTaskId());
                if (Optional.ofNullable(idTaskHis).filter(list -> !list.isEmpty()).isPresent()) {
                    taskHistoryRepository.deleteByIdNative(task.getId());
                }
            }
            return new ApiResponse<String>().buildError("500", "Failed to submit task due to internal error");
        }
    }

    @Override
    @Transactional
    public ApiResponse<?> approveAction(UserAndCorp user, TaskProjection task, ApprovalStatusEnum approvalStatus, String remarks) {
        try{
            List<CfoTaskVar> varList = taskVarRepository.findByTaskId(task.getTaskId());
            if (varList.isEmpty()) return new ApiResponse<String>().buildError("500", "Task variables not found");

            String acctPrefNm = varList.stream()
                    .filter(taskVar -> TaskVarEnum.NEW_ACCT_NM.name().equalsIgnoreCase(taskVar.getVarName()))
                    .map(CfoTaskVar::getVarValue)
                    .findFirst().orElse(null);

            if (acctPrefNm == null) return new ApiResponse<String>().buildError("500", "New account name variable not found");

            Optional<AcctProjection> accountOpt = accountRepository.findByAcctNumber(task.getRefNumber());
            if (accountOpt.isEmpty()) {
                log.info("Account {} not found.", task.getRefNumber());
                return null;
            }
            AcctProjection account = accountOpt.get();
            String oldAcctName = account.getAcctName();

            if(ApprovalStatusEnum.APPROVED.equals(approvalStatus)) {
                editAcctNameMapper.updateOriginalData(user, task.getRefNumber(), acctPrefNm);
//                prepareAndSendNotification(task.getCreatedBy(), AdminEventEnum.CHK_ADM_APPROVED, task.getTaskId(), task.getCreatedBy(), task.getRefNumber());
            }
            editAcctNameMapper.updateTask(user, task.getTaskId(), approvalStatus, remarks);
            task = taskRepository.findByTaskId(task.getTaskId()).get();
            editAcctNameMapper.createTaskHistory(user, task, approvalStatus, remarks);

            EditAcctNameApprovalDTO detail = editAcctNameMapper.toApprovalDTO(task, varList);
            if(detail == null) {
                return new ApiResponse<String>().buildError("500", "Account { " + task.getRefNumber() + "} not found");
            }
            detail.getSubmissionData().setCurrentAcctName(oldAcctName);
            editAcctNameMapper.enrichWithHistoryInfo(detail, taskHistoryRepository.findByTaskId(task.getTaskId()));
            log.info("Task {} approval recorded successfully", task.getTaskId());

            TaskDetailDTO<EditAcctNameApprovalDTO> taskDetail = new TaskDetailDTO<>();
            taskDetail.setData(detail);
            return new ApiResponse<TaskDetailDTO<EditAcctNameApprovalDTO>>().buildSuccess(taskDetail);
        } catch (Exception e) {
            log.error("Error during task approval, rolling back if partial save occurred", e);
            TaskProjection finalTask = task;
            if(ApprovalStatusEnum.APPROVED.equals(approvalStatus)) {
                editAcctNameMapper.updateOriginalData(user, task.getRefNumber(), accountRepository.findByAcctNumber(task.getRefNumber()).map(AcctProjection::getAcctName).orElseGet(() -> {
                    log.info("Account number [{}] not found in repository", finalTask.getRefNumber());
                    return null;
                }));
            }
            editAcctNameMapper.updateTask(user, task.getTaskId(), ApprovalStatusEnum.PENDING_APPR, null);
            if (task != null && task.getId() != null) {
                List<TaskHistoryProjection> idTaskHis = taskHistoryRepository.findIdsByTaskId(task.getTaskId());
                if (Optional.ofNullable(idTaskHis).filter(list -> !list.isEmpty()).isPresent()) {
                    taskHistoryRepository.deleteByIdNative(task.getId());
                }
            }
            return new ApiResponse<String>().buildError("500", "Internal server error during task approval");
        }
    }

    @Override
    public ApiResponse<?> getDetail(UserAndCorp user, TaskProjection task) {
        try {
            List<CfoTaskVar> taskVars = taskVarRepository.findByTaskId(task.getTaskId());
            if (taskVars.isEmpty()) return new ApiResponse<String>().buildError("500", "Task variables not found");

            EditAcctNameApprovalDTO detail = editAcctNameMapper.toApprovalDTO(task, taskVars);
            editAcctNameMapper.enrichWithHistoryInfo(detail, taskHistoryRepository.findByTaskId(task.getTaskId()));
            if(detail == null) {
                log.info("Account {} not found ", task.getRefNumber());
                ApiResponse<TaskDetailDTO<EditAcctNameApprovalDTO>> notFound = new ApiResponse<>();
                notFound.setMsgHeader(new ErrorResponse("500"));
                return notFound;
            }
            TaskDetailDTO<EditAcctNameApprovalDTO> responseDTO = new TaskDetailDTO<>();
            responseDTO.setData(detail);
            return new ApiResponse<TaskDetailDTO<EditAcctNameApprovalDTO>>().buildSuccess(responseDTO);
        } catch (Exception e) {
            log.error("Failed to retrieve task detail", e);
            return new ApiResponse<String>().buildError("500", "Failed to retrieve task detail");
        }
    }

    @Override
    public void preIntercept(Map<?,?>... args) {
        log.info("processing preIntercept {}", args);
    }

    @Override
    public void postIntercept(Map<?, ?>... args) {
        log.info("processing postIntercept {}", args);

    }

    private void prepareAndSendNotification (String makerId, AdminEventEnum eventAction, String taskId, String authoriserId, String accountNumber){
        try {
            NotificationProcessingRequest notificationProcessingRequest = new NotificationProcessingRequest();
            notificationProcessingRequest.setMakerUserId(makerId);
            notificationProcessingRequest.setEventAction(eventAction);
            Map<String, String> data = new HashMap<>();
            data.put("serviceName", PREFERRED_NAME_MAINTENANCE);
            data.put("transactionRef", UtilityFunctions.maskTransactionRef(accountNumber));
            data.put("refNumber", taskId);
            data.put("makerName", findUsername(makerId));
            data.put("authoriserName", findUsername(authoriserId));
            notificationProcessingRequest.setData(data);

            ApiRequestDTO<NotificationProcessingRequest> requestDTO = new ApiRequestDTO<>();
            requestDTO.setBaseUrl(notificationUrl);
            requestDTO.setUri(notificationUri);
            requestDTO.setBody(notificationProcessingRequest);
            requestDTO.setHttpMethod(POST);
            requestDTO.setResponseType(new ParameterizedTypeReference<>() {});
            webClient.callHost(requestDTO);
        }
        catch (Exception exception) {
            log.error("Error sending notification:", exception);
        }
    }

    private String findUsername(String userId) {
        Optional<String> username = corpUserRepository.findByUserId(userId);

        return username.orElse(null);
    }

}
