package com.m2e.dashboard.controller.task;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.investment.response.INVUnitTrustStatsDTO;
import com.m2e.dashboard.dto.task.request.*;
import com.m2e.dashboard.dto.task.response.*;
import com.m2e.dashboard.service.task.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Portfolio Administration Task")
@RestController
@Slf4j
@RequestMapping("/task/v1")
public class TaskController {

    private final TaskService taskService;
    private final JwtUtil jwtUtil;

    @Autowired
    public TaskController(TaskService taskService, JwtUtil jwtUtil){
        this.taskService = taskService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "Get list of Task Maintenance Types.",
            description = "Retrieves available Task Maintenance Types.")
    @PostMapping("/mt-type-list")
    public ResponseEntity<ApiResponse<List<TaskFunctionDTO>>> getMTTypes(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return ResponseEntity.ok(taskService.getMTTypes());
    }

    @Operation(
            summary = "Get list of Task Statuses.",
            description = "Retrieves available Task Statues.")
    @PostMapping("/status-list")
    public ResponseEntity<ApiResponse<List<TaskApprovalStatusDTO>>> getTaskStatuses(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return ResponseEntity.ok(taskService.getTaskStatuses());
    }

    @Operation(
            summary = "Get Portfolio tasks.",
            description = "Retrieves all tasks under the Portfolio module.")
    @PostMapping("/list")
    public ResponseEntity<ApiResponse<TaskListingDTO>> search(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody TaskListingReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(taskService.getTaskList(userAndCorp, request));
    }

    @Operation(
            summary = "Get task detail.",
            description = "Retrieves detailed information of a specific task.")
    @PostMapping("/dtl")
    public ResponseEntity<ApiResponse<TaskDetailDTO<Object>>> detail(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody TaskDetailReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        @SuppressWarnings("unchecked")
        ApiResponse<TaskDetailDTO<Object>> response = (ApiResponse<TaskDetailDTO<Object>>) (ApiResponse<?>) taskService.getTaskDetail(userAndCorp, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Submit task.",
            description = "Submits a task for approval processing. The request body structure depends on the taskType.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TaskSubmitReqDTO.class)
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            oneOf = {
                                                    TaskDetailEditAcctNameResponse.class,
                                                    TaskDetailEInvoiceRegResponse.class
                                            }
                                    )
                            )
                    )
            }
    )
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<TaskDetailDTO<Object>>> submit(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody TaskSubmitReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        @SuppressWarnings("unchecked")
        ApiResponse<TaskDetailDTO<Object>> response = (ApiResponse<TaskDetailDTO<Object>>) (ApiResponse<?>) taskService.submit(userAndCorp, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Approve or reject task.",
            description = "Processes approval or rejection for a submitted task.")
    @PostMapping("/approval")
    public ResponseEntity<ApiResponse<Object>> approval(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody TaskApprovalReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        @SuppressWarnings("unchecked")
        ApiResponse<Object> response = (ApiResponse<Object>) (ApiResponse<?>) taskService.approval(userAndCorp, request);
        return ResponseEntity.ok(response);
    }

    @Schema(description = "TaskDetailDTO containing EditAcctNameApprovalDTO as data when taskType is EDIT_ACCT_NM")
    public class TaskDetailEditAcctNameResponse extends TaskDetailDTO<EditAcctNameApprovalDTO> {}

    @Schema(description = "TaskDetailDTO with EInvoiceRegApprovalDTO as data when taskType is EINVOICE_REGISTRATION")
    public class TaskDetailEInvoiceRegResponse extends TaskDetailDTO<EInvoiceRegApprovalDTO> {}


    @Operation(
            summary = "Calculate Task - Approval",
            description = "Calculate total Tasks for Admin Maker and Checker.")
    @PostMapping("/totalTasks")
    public ResponseEntity<ApiResponse<TaskStatsDTO>> getTotalTasks(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getTotalTasks(userAndCorp));
    }

}
