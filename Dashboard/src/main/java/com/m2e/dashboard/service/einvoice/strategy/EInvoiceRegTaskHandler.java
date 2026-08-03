//package com.m2e.dashboard.service.einvoice.strategy;
//
//import com.m2e.dashboard.config.UserAndCorp;
//import com.m2e.dashboard.dto.common.ApiResponse;
//import com.m2e.dashboard.dto.common.ErrorResponse;
//import com.m2e.dashboard.dto.task.request.TaskVarEInvoiceRegDTO;
//import com.m2e.dashboard.dto.task.response.EInvoiceRegApprovalDTO;
//import com.m2e.dashboard.dto.task.response.EditAcctNameApprovalDTO;
//import com.m2e.dashboard.dto.task.response.TaskDetailDTO;
//import com.m2e.dashboard.enums.AdminEventEnum;
//import com.m2e.dashboard.enums.ApprovalStatusEnum;
//import com.m2e.dashboard.enums.MTFunctionEnum;
//import com.m2e.dashboard.enums.TaskVarEnum;
//import com.m2e.dashboard.mapper.task.EInvoiceRegMapper;
//import com.m2e.dashboard.projection.AcctProjection;
//import com.m2e.dashboard.projection.TaskHistoryProjection;
//import com.m2e.dashboard.projection.TaskProjection;
//import com.m2e.dashboard.projection.TaskVarProjection;
//import com.m2e.dashboard.repository.TaskHistoryRepository;
//import com.m2e.dashboard.repository.TaskRepository;
//import com.m2e.dashboard.repository.TaskVarRepository;
//import com.m2e.dashboard.service.TaskHandlerStrategy;
//import com.m2e.dashboard.sp.task.response.TaskSubmissionSpDTO;
//import com.m2e.model.sso.base.CfoTaskVar;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@Component
//public class EInvoiceRegTaskHandler implements TaskHandlerStrategy {
//
//    private final TaskRepository taskRepository;
//    private final TaskVarRepository taskVarRepository;
//    private final TaskHistoryRepository taskHistoryRepository;
//    private final EInvoiceRegMapper eInvoiceRegMapper;
//
//    @Autowired
//    public EInvoiceRegTaskHandler(TaskRepository taskRepository,
//                               TaskVarRepository taskVarRepository,
//                               TaskHistoryRepository taskHistoryRepository,
//                                  EInvoiceRegMapper eInvoiceRegMapper){
//        this.taskRepository = taskRepository;
//        this.taskVarRepository = taskVarRepository;
//        this.taskHistoryRepository = taskHistoryRepository;
//        this.eInvoiceRegMapper = eInvoiceRegMapper;
//    }
//
//    private static final Logger log = LoggerFactory.getLogger(EInvoiceRegTaskHandler.class);
//    @Override
//    public boolean supports(String taskType) {
//        return MTFunctionEnum.EINVOICE.name().equalsIgnoreCase(taskType);
//    }
//
//    @Override
//    public boolean supports(Object requestData) {
//        return requestData instanceof TaskVarEInvoiceRegDTO;
//    }
//
//    @Override
//    public ApiResponse<?> submit(UserAndCorp user, Object data) {
//        if (!(data instanceof TaskVarEInvoiceRegDTO request) || user == null || user.getUserId() == null) {
//            return new ApiResponse<String>().buildError("500", "Invalid request data or user context");
//        }
//
//        if (request.getCorpId() == null || request.getTin() == null || request.getSsn() == null) {
//            return new ApiResponse<String>().buildError("500", "Corp ID, TIN and SSN are required");
//        }
//
//        TaskSubmissionSpDTO task = null;
//
//        try {
//            task = eInvoiceRegMapper.createTask(user, request);
//            log.info("Task {} submitted successfully.", task.getTaskId());
//            TaskProjection taskProjection = taskRepository.findByTaskId(task.getTaskId()).get();
//            eInvoiceRegMapper.createTaskHistory(user, taskProjection, ApprovalStatusEnum.PENDING_APPR, "");
//            log.info("Task history {} submitted successfully.", task.getTaskId());
//            // TODO : integrate to notification
////            prepareAndSendNotification(user.getUserId(), AdminEventEnum.MAKER_ADM_INIT, task.getTaskId(), null, request.getAcctNumber());
//            return new ApiResponse<String>().buildSuccess(task.getTaskId());
//        } catch (RuntimeException e) {
//            log.error("Error during task submission, rolling back if partial save occurred", e);
//            if (task != null && task.getId() != null) {
//                taskRepository.deleteById(task.getId());
//                List<TaskVarProjection> idTaskVars = taskVarRepository.findIdsByTaskId(task.getTaskId());
//                if (Optional.ofNullable(idTaskVars).filter(list -> !list.isEmpty()).isPresent()) {
//                    taskVarRepository.deleteById(task.getId());
//                }
//                List<TaskHistoryProjection> idTaskHis = taskHistoryRepository.findIdsByTaskId(task.getTaskId());
//                if (Optional.ofNullable(idTaskHis).filter(list -> !list.isEmpty()).isPresent()) {
//                    taskHistoryRepository.deleteByIdNative(task.getId());
//                }
//            }
//            return new ApiResponse<String>().buildError("500", "Failed to submit task due to internal error");
//        }
//    }
//
//    @Override
//    public ApiResponse<?> approveAction(UserAndCorp user, TaskProjection task, ApprovalStatusEnum approvalStatus, String remarks) {
//        try{
//            List<CfoTaskVar> varList = taskVarRepository.findByTaskId(task.getTaskId());
//            if (varList.isEmpty()) return new ApiResponse<String>().buildError("500", "Task variables not found");
//
//            if(ApprovalStatusEnum.APPROVED.equals(approvalStatus)) {
//                String corpCifNo = varList.stream()
//                        .filter(taskVar -> TaskVarEnum.EINVOICE_CORPCIFNO.name().equalsIgnoreCase(taskVar.getVarName()))
//                        .map(CfoTaskVar::getVarValue)
//                        .findFirst().orElse(null);
//
//                String corpBrn = varList.stream()
//                        .filter(taskVar -> TaskVarEnum.EINVOICE_CORPBRN.name().equalsIgnoreCase(taskVar.getVarName()))
//                        .map(CfoTaskVar::getVarValue)
//                        .findFirst().orElse(null);
//
//                String ssnNo = varList.stream()
//                        .filter(taskVar -> TaskVarEnum.EINVOICE_SSN.name().equalsIgnoreCase(taskVar.getVarName()))
//                        .map(v -> Optional.ofNullable(v.getVarValue()).orElse(""))
//                        .findFirst().orElse(null);
//
//                String taxId = varList.stream()
//                        .filter(taskVar -> TaskVarEnum.EINVOICE_TIN.name().equalsIgnoreCase(taskVar.getVarName()))
//                        .map(CfoTaskVar::getVarValue)
//                        .findFirst().orElse(null);
//
//                eInvoiceRegMapper.updateOriginalData(user, task.getRefNumber(), corpCifNo, corpBrn, ssnNo, taxId);
//                // TODO : integrate to notification
////                prepareAndSendNotification(task.getCreatedBy(), AdminEventEnum.CHK_ADM_APPROVED, task.getTaskId(), task.getCreatedBy(), task.getRefNumber());
//            }
//            eInvoiceRegMapper.updateTask(user, task.getTaskId(), approvalStatus, remarks);
//            task = taskRepository.findByTaskId(task.getTaskId()).get();
//            eInvoiceRegMapper.createTaskHistory(user, task, approvalStatus, remarks);
//
//            EInvoiceRegApprovalDTO detail = eInvoiceRegMapper.toApprovalDTO(task, varList);
//            if(detail == null) {
//                return new ApiResponse<String>().buildError("500", "Corp { " + task.getRefNumber() + "} not found");
//            }
//            eInvoiceRegMapper.enrichWithHistoryInfo(detail, taskHistoryRepository.findByTaskId(task.getTaskId()));
//            log.info("Task {} approval recorded successfully", task.getTaskId());
//            TaskDetailDTO<EInvoiceRegApprovalDTO> taskDetail = new TaskDetailDTO<>();
//            taskDetail.setData(detail);
//            return new ApiResponse<TaskDetailDTO<EInvoiceRegApprovalDTO>>().buildSuccess(taskDetail);
//        } catch (Exception e) {
//            log.error("Error during task approval, rolling back if partial save occurred", e);
//            TaskProjection finalTask = task;
//            if(ApprovalStatusEnum.APPROVED.equals(approvalStatus)) {
//                // TODO: Reevaluate rollback handling — consider storing the original value in TaskVar/corp table, since current implementation does not persist historical data in the database.
////                eInvoiceRegMapper.updateOriginalData(user, task.getRefNumber(), accountRepository.findByAcctNumber(task.getRefNumber()).map(AcctProjection::getAcctName).orElseGet(() -> {
////                    log.info("Account number [{}] not found in repository", finalTask.getRefNumber());
////                    return null;
////                }));
//            }
//            eInvoiceRegMapper.updateTask(user, task.getTaskId(), ApprovalStatusEnum.PENDING_APPR, null);
//            if (task != null && task.getId() != null) {
//                List<TaskHistoryProjection> idTaskHis = taskHistoryRepository.findIdsByTaskId(task.getTaskId());
//                if (Optional.ofNullable(idTaskHis).filter(list -> !list.isEmpty()).isPresent()) {
//                    taskHistoryRepository.deleteByIdNative(task.getId());
//                }
//            }
//            return new ApiResponse<String>().buildError("500", "Internal server error during task approval");
//        }
//    }
//
//    @Override
//    public ApiResponse<?> getDetail(UserAndCorp user, TaskProjection task) {
//        try {
//            List<CfoTaskVar> taskVars = taskVarRepository.findByTaskId(task.getTaskId());
//            if (taskVars.isEmpty()) return new ApiResponse<String>().buildError("500", "Task variables not found");
//
//            EInvoiceRegApprovalDTO detail = eInvoiceRegMapper.toApprovalDTO(task, taskVars);
//            eInvoiceRegMapper.enrichWithHistoryInfo(detail, taskHistoryRepository.findByTaskId(task.getTaskId()));
//            if(detail == null) {
//                log.info("Corp {} not found ", task.getRefNumber());
//                ApiResponse<TaskDetailDTO<EInvoiceRegApprovalDTO>> notFound = new ApiResponse<>();
//                notFound.setMsgHeader(new ErrorResponse("500"));
//                return notFound;
//            }
//            TaskDetailDTO<EInvoiceRegApprovalDTO> responseDTO = new TaskDetailDTO<>();
//            responseDTO.setData(detail);
//            return new ApiResponse<TaskDetailDTO<EInvoiceRegApprovalDTO>>().buildSuccess(responseDTO);
//        } catch (Exception e) {
//            log.error("Failed to retrieve task detail", e);
//            return new ApiResponse<String>().buildError("500", "Failed to retrieve task detail");
//        }
//    }
//
//    @Override
//    public void preIntercept(Map<?, ?>... args) {
//
//    }
//
//    @Override
//    public void postIntercept(Map<?, ?>... args) {
//
//    }
//}
