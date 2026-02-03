package com.m2e.dashboard.service.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.task.request.*;
import com.m2e.dashboard.dto.task.response.*;
import com.m2e.dashboard.enums.TaskMoreAction;
import com.m2e.dashboard.mapper.task.ApprovalStatusMapper;
import com.m2e.dashboard.mapper.task.CFOFunctionMapper;
import com.m2e.dashboard.projection.ApprovalStatusProjection;
import com.m2e.dashboard.projection.CFOFunctionProjection;
import com.m2e.dashboard.projection.TaskProjection;
import com.m2e.dashboard.repository.ApprovalStatusRepository;
import com.m2e.dashboard.repository.CFOFunctionActionRepository;
import com.m2e.dashboard.repository.CFOFunctionRepository;
import com.m2e.dashboard.repository.TaskRepository;
import com.m2e.dashboard.service.TaskActionResolver;
import com.m2e.dashboard.service.TaskActionResolverFactory;
import com.m2e.dashboard.service.TaskHandlerStrategy;
import com.m2e.dashboard.sp.task.request.TaskListCountSpParam;
import com.m2e.dashboard.sp.task.request.TaskListSpParam;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.m2e.dashboard.enums.ApprovalStatusEnum.PENDING_APPR;

@Service
@RequiredArgsConstructor
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private static final String ERROR_UNSUPPORTED_TASK_TYPE = "Unsupported task type: {}";
    private static final String ERROR_RESOLVER_NOTFOUND_TASK_TYPE = "No resolver found for taskType: {}";

    private final CFOFunctionRepository cfoFunctionRepository;
    private final ApprovalStatusRepository approvalStatusRepository;
    private final CFOFunctionMapper cfoFunctionMapper;
    private final ApprovalStatusMapper approvalStatusMapper;
    private final TaskRepository taskRepository;
    private final List<TaskHandlerStrategy> strategies;
    private final ObjectMapper objectMapper;
    private final TaskSpService spService;
    private final TaskActionResolverFactory taskTypeResolverFactory;
    private final CFOFunctionActionRepository cfoFunctionActionRepository;

    public ApiResponse<List<TaskFunctionDTO>> getMTTypes(){
        ApiResponse<List<TaskFunctionDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));

        List<CFOFunctionProjection> datas = cfoFunctionRepository.findMTTypes();
        List<TaskFunctionDTO> results = datas.isEmpty() ? new ArrayList<>() : cfoFunctionMapper.toDTOList(datas);

        apiResponse.setMsgBody(results);
        return apiResponse;
    }

    public ApiResponse<List<TaskApprovalStatusDTO>> getTaskStatuses(){
        ApiResponse<List<TaskApprovalStatusDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));

        List<ApprovalStatusProjection> datas = approvalStatusRepository.findCFOApprovalStatus();
        List<TaskApprovalStatusDTO> results = datas.isEmpty() ? new ArrayList<>() : approvalStatusMapper.toDTOList(datas);

        apiResponse.setMsgBody(results);
        return apiResponse;
    }

    public ApiResponse<TaskListingDTO> getTaskList(UserAndCorp auth, TaskListingReqDTO request){
        log.info("getTaskList : {}", request);

        String funcType = Optional.ofNullable(request.getFilter().getType())
                .filter(type -> !type.isEmpty())
                .map(type -> String.join(",", type))
                .orElse(null);

        String status = Optional.ofNullable(request.getFilter().getStatus())
                .filter(stat -> !stat.isEmpty())
                .map(stat -> String.join(",", stat))
                .orElse(null);

        TaskListSpParam taskListSpParam = new TaskListSpParam(auth.getUserId(), auth.getCorpId(), auth.getMainCorpId(), funcType, status, request.getCorpCifNo(), request.getSearchParam(), request.getSortBy().name());
        taskListSpParam.setPage(request.getPage());
        taskListSpParam.setPageSize(request.getPageSize());

        SearchCollectionResponse<TaskDTO> taskSearchResponse = spService.getTaskList(taskListSpParam);

        taskSearchResponse.getData().forEach(row -> {
            List<String> moreActions = new ArrayList<>();
            if(row.getTaskStatus().equals(PENDING_APPR.getValue())) {
                moreActions = Arrays.stream(TaskMoreAction.values()).map(TaskMoreAction::getValue).toList();
                row.setMoreActions(moreActions);
            } else {
                moreActions.add(TaskMoreAction.VIEW.getValue());
                row.setMoreActions(moreActions);
            }
        });

        TaskListingDTO taskListingDTO = new TaskListingDTO(request.getCorpCifNo(), taskSearchResponse);

        ApiResponse<TaskListingDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(taskListingDTO);
        return apiResponse;
    }

    public <D> ApiResponse<D> getTaskDetail(UserAndCorp auth, TaskDetailReqDTO request) {
        ApiResponse<D> response = new ApiResponse<>();

        if (request == null || request.getTaskId() == null) {
            log.error("taskId is required");
            response.setMsgHeader(new ErrorResponse("500"));
            return response;
        }

        return taskRepository.findByTaskId(request.getTaskId())
                .map(task -> {
                    String taskType = cfoFunctionRepository.findFunctionById(task.getFunction())
                            .map(f -> f.getCode())
                            .orElse(null);

                    if (taskType == null) {
                        log.error("Function code not found for task function ID: {}", task.getFunction());
                        response.setMsgHeader(new ErrorResponse("500"));
                        return response;
                    }

                    Optional<TaskHandlerStrategy> strategyOpt = getStrategy(taskType);
                    if (strategyOpt.isEmpty()) {
                        log.error(ERROR_UNSUPPORTED_TASK_TYPE, taskType);
                        response.setMsgHeader(new ErrorResponse("500"));
                        return response;
                    }

                    TaskHandlerStrategy strategy = strategyOpt.get();
                    ApiResponse<?> raw = strategy.getDetail(auth, task);

                    response.setMsgHeader(raw.getMsgHeader());

                    String taskAction = cfoFunctionActionRepository.findActionById(task.getFunctionAction())
                            .map(f -> f.getCode())
                            .orElse(null);
                    @SuppressWarnings("unchecked")
                    Optional<TaskActionResolver<?, ?, D>> resolverOpt =
                            (Optional<TaskActionResolver<?, ?, D>>) (Optional<?>) taskTypeResolverFactory.getResolver(taskAction);

                    if (resolverOpt.isPresent()) {
                        TaskActionResolver<?, ?, D> resolver = resolverOpt.get();
                        D detailDTO = resolver.wrapDetail(raw.getMsgBody(), objectMapper).getData();
                        response.setMsgBody(detailDTO);
                    } else {
                        log.info(ERROR_RESOLVER_NOTFOUND_TASK_TYPE, taskType);
                        response.setMsgHeader(new ErrorResponse("500"));
                    }

                    return response;
                })
                .orElseGet(() -> {
                    log.error("Task not found: {}", request.getTaskId());
                    response.setMsgHeader(new ErrorResponse("500"));
                    return response;
                });
    }

    public <S, D> ApiResponse<D> submit(UserAndCorp auth, TaskSubmitReqDTO request) {
        ApiResponse<D> apiResponse = new ApiResponse<>();

        if (request == null || request.getData() == null) {
            apiResponse.setMsgHeader(new ErrorResponse("500"));
            log.error("Data is required for submission");
            return apiResponse;
        }

        @SuppressWarnings("unchecked")
        Optional<TaskActionResolver<S, ?, D>> optionalResolver =
                (Optional<TaskActionResolver<S, ?, D>>) (Optional<?>) taskTypeResolverFactory.getResolver(request.getTaskType());

        if (optionalResolver.isEmpty()) {
            apiResponse.setMsgHeader(new ErrorResponse("500"));
            log.info(ERROR_RESOLVER_NOTFOUND_TASK_TYPE, request.getTaskType());
            return apiResponse;
        }

        TaskActionResolver<S, ?, D> resolver = optionalResolver.get();
        S actualDto = objectMapper.convertValue(request.getData(), resolver.getSubmitClass());

        TaskHandlerStrategy strategy = strategies.stream()
                .filter(s -> s.supports(actualDto))
                .findFirst()
                .orElse(null);

        if (strategy == null) {
            apiResponse.setMsgHeader(new ErrorResponse("500"));
            log.error(ERROR_UNSUPPORTED_TASK_TYPE, actualDto.getClass().getSimpleName());
            return apiResponse;
        }

        ApiResponse<String> rawResponse = (ApiResponse<String>) strategy.submit(auth, actualDto);
        log.info("rawResponse -> {}", rawResponse);

        ApiResponse<D> result = new ApiResponse<>();
        result.setMsgHeader(rawResponse.getMsgHeader());

        String taskId = rawResponse.getMsgBody();

        if (taskId == null) {
            log.warn("Task ID is null in the response.");
            return result;
        }

        Optional<TaskProjection> taskOpt = taskRepository.findByTaskId(taskId);

        if (taskOpt.isEmpty()) {
            log.warn("Task not found for ID: {}", taskId);
            return result;
        }

        TaskProjection task = taskOpt.get();
        ApiResponse<?> rawDetailResp = strategy.getDetail(auth, task);
        log.info("Task detail response -> {}", rawDetailResp);

        D detailDTO = resolver.wrapDetail(rawDetailResp.getMsgBody(), objectMapper).getData();
        log.info("detailDTO after wrapDetail -> {} ", detailDTO);
        result.setMsgBody(detailDTO);

        return result;
    }

    public <A> ApiResponse<A> approval(UserAndCorp auth, TaskApprovalReqDTO request) {
        if (request == null || request.getTaskId() == null) {
            log.error("taskId is required for approval");
            ApiResponse<A> response = new ApiResponse<>();
            response.setMsgHeader(new ErrorResponse("500"));
            return response;
        }

        return taskRepository.findByTaskId(request.getTaskId())
                .map(task -> {
                    String taskType = cfoFunctionRepository.findFunctionById(task.getFunction())
                            .map(f -> f.getCode())
                            .orElse(null);

                    if (taskType == null) {
                        log.error("Function code not found for task function ID: {}", task.getFunction());
                        ApiResponse<A> error = new ApiResponse<>();
                        error.setMsgHeader(new ErrorResponse("500"));
                        return error;
                    }

                    Optional<TaskHandlerStrategy> strategyOpt = getStrategy(taskType);
                    if (strategyOpt.isEmpty()) {
                        log.error(ERROR_UNSUPPORTED_TASK_TYPE, taskType);
                        ApiResponse<A> error = new ApiResponse<>();
                        error.setMsgHeader(new ErrorResponse("500"));
                        return error;
                    }

                    TaskHandlerStrategy strategy = strategyOpt.get();
                    ApiResponse<?> raw = strategy.approveAction(auth, task, request.getApprovalStatus(), request.getRejectionNote());

                    ApiResponse<A> result = new ApiResponse<>();
                    result.setMsgHeader(raw.getMsgHeader());

                    String taskAction = cfoFunctionActionRepository.findActionById(task.getFunctionAction())
                            .map(f -> f.getCode())
                            .orElse(null);

                    @SuppressWarnings("unchecked")
                    Optional<TaskActionResolver<?, A, ?>> resolverOpt =
                            (Optional<TaskActionResolver<?, A, ?>>) (Optional<?>) taskTypeResolverFactory.getResolver(taskAction);

                    if (resolverOpt.isPresent()) {
                        TaskActionResolver<?, A, ?> resolver = resolverOpt.get();
                        A converted = objectMapper.convertValue(raw.getMsgBody(), resolver.getApprovalTypeReference());
                        result.setMsgBody(converted);
                    } else {
                        log.info(ERROR_RESOLVER_NOTFOUND_TASK_TYPE, taskType);
                        result.setMsgBody(null);
                    }

                    return result;
                })
                .orElseGet(() -> {
                    log.error("Task not found: {}", request.getTaskId());
                    ApiResponse<A> notFound = new ApiResponse<>();
                    notFound.setMsgHeader(new ErrorResponse("500"));
                    return notFound;
                });
    }

    private Optional<TaskHandlerStrategy> getStrategy(String taskType) {
        return strategies.stream().filter(s -> s.supports(taskType)).findFirst();
    }

    public ApiResponse<TaskStatsDTO> getTotalTasks(UserAndCorp userAndCorp) {
        ApiResponse<TaskStatsDTO> response = new ApiResponse<>();
        log.info("Corp id {} ", userAndCorp.getCorpId());

        TaskListCountSpParam taskListSpParam = new TaskListCountSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId());
        TaskStatsDTO taskSearchResp = spService.getCountTaskList(taskListSpParam);
        log.info("StatTask :: {} ", taskSearchResp.toString());

        response.setMsgBody(taskSearchResp);
        response.setMsgHeader(new ErrorResponse("0"));
        return response;
    }

    public List<TaskStatsDTO> groupByStatus(List<TaskListStatsDTO> taskSearchResp) {
        return taskSearchResp.stream()
                .collect(Collectors.groupingBy(TaskListStatsDTO::getTaskStatus, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    TaskStatsDTO dto = new TaskStatsDTO();
                    dto.setStatus(entry.getKey());
                    dto.setTotalTasks(String.valueOf(entry.getValue()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
