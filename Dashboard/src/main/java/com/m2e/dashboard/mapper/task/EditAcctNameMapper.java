package com.m2e.dashboard.mapper.task;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.task.request.TaskVarEditAcctNameDTO;
import com.m2e.dashboard.dto.task.response.EditAcctNameApprovalDTO;
import com.m2e.dashboard.dto.task.response.EditAcctNameSubmissionDTO;
import com.m2e.dashboard.enums.ApprovalStatusEnum;
import com.m2e.dashboard.enums.MTFunctionActionEnum;
import com.m2e.dashboard.enums.MTFunctionEnum;
import com.m2e.dashboard.enums.TaskVarEnum;
import com.m2e.dashboard.projection.*;
import com.m2e.dashboard.repository.*;
import com.m2e.dashboard.service.task.TaskSpService;
import com.m2e.dashboard.sp.maintenance.request.MTUpdateAcctNameSpParam;
import com.m2e.dashboard.sp.task.request.TaskHistoryInsertSpParam;
import com.m2e.dashboard.sp.task.request.TaskInsertSpParam;
import com.m2e.dashboard.sp.task.request.TaskUpdateSpParam;
import com.m2e.dashboard.sp.task.request.TaskVarInsertSpParam;
import com.m2e.dashboard.sp.task.response.TaskSubmissionSpDTO;
import com.m2e.model.sso.base.CfoTaskVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Component
public class EditAcctNameMapper {

    private static final Logger log = LoggerFactory.getLogger(EditAcctNameMapper.class);

    private final AccountTypeRepository accountTypeRepository;
    private final ApprovalStatusRepository approvalStatusRepository;
    private final CFOFunctionRepository cfoFunctionRepository;
    private final CFOFunctionActionRepository cfoFunctionActionRepository;
    private final TaskSpService taskSpService;
    private final CorpRepository corpRepository;
    private final AccountRepository accountRepository;
    private final CifRepository cifRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm:ss", Locale.ENGLISH);
    private static final String ERROR_LOG = "Something went wrong : ";

    @Autowired
    public EditAcctNameMapper(AccountTypeRepository accountTypeRepository, ApprovalStatusRepository approvalStatusRepository,
                              CFOFunctionRepository cfoFunctionRepository,
                              CFOFunctionActionRepository cfoFunctionActionRepository,
                              TaskSpService taskSpService,
                              CorpRepository corpRepository,
                              AccountRepository accountRepository,
                              CifRepository cifRepository) {
        this.accountTypeRepository = accountTypeRepository;
        this.approvalStatusRepository = approvalStatusRepository;
        this.cfoFunctionRepository = cfoFunctionRepository;
        this.cfoFunctionActionRepository = cfoFunctionActionRepository;
        this.taskSpService = taskSpService;
        this.corpRepository = corpRepository;
        this.accountRepository = accountRepository;
        this.cifRepository = cifRepository;
    }

    public TaskSubmissionSpDTO createTask(UserAndCorp user, TaskVarEditAcctNameDTO request) {
        TaskInsertSpParam spParam = new TaskInsertSpParam();
        spParam.setId(UUID.randomUUID().toString());
        spParam.setCfoTaskId(String.valueOf(System.currentTimeMillis()));
        try{
            spParam.setCreatedBy(user.getUserId());
            spParam.setDtCreated(OffsetDateTime.now());
            spParam.setTsCreated(Timestamp.from(Instant.now()));
            spParam.setSysPvdCd(
                    corpRepository.findProviderByCorpId(user.getCorpId())
                            .orElseGet(() -> {
                                log.info("System provider not found for corp ID [{}], defaulting to 'SYS'", user.getCorpId());
                                return "SYS";
                            })
            );
            spParam.setMtApprSts(
                    approvalStatusRepository.findByCode(ApprovalStatusEnum.PENDING_APPR.name())
                            .map(ApprovalStatusProjection::getId)
                            .orElseGet(() -> {
                                log.info("Approval status [{}] not found in repository", ApprovalStatusEnum.PENDING_APPR.name());
                                return null;
                            })
            );
            spParam.setRefNo(request.getAcctNumber());
            spParam.setCfoFuncsId(
                    cfoFunctionRepository.findByCode(MTFunctionEnum.ACCT_NM.name())
                            .map(CFOFunctionProjection::getId)
                            .orElseGet(() -> {
                                log.info("CFO Function [{}] not found in repository", MTFunctionEnum.ACCT_NM.name());
                                return null;
                            }));
            spParam.setCfoFuncsActionId(
                    cfoFunctionActionRepository.findbyCode(MTFunctionActionEnum.EDIT_ACCT_NM.name())
                            .map(CFOFunctionActionProjection::getId)
                            .orElseGet(() -> {
                                log.info("CFO Function [{}] not found in repository", MTFunctionActionEnum.EDIT_ACCT_NM.name());
                                return null;
                            }));
            spParam.setRemark(null);
            taskSpService.insertTask(spParam);
            createTaskVar(user, spParam.getCfoTaskId(), request);
        } catch (Exception e){
            throw new RuntimeException(ERROR_LOG + e.getMessage());
        }
        return new TaskSubmissionSpDTO(spParam.getId(), spParam.getCfoTaskId());
    }

    public void createTaskVar(UserAndCorp user, String cfoTaskId, TaskVarEditAcctNameDTO request) {
        try {
            TaskVarInsertSpParam spParam = new TaskVarInsertSpParam();
            spParam.setId(UUID.randomUUID().toString());
            spParam.setCreatedBy(user.getUserId());
            spParam.setDtCreated(OffsetDateTime.now());
            spParam.setTsCreated(Timestamp.from(Instant.now()));
            spParam.setCfoTaskId(cfoTaskId);
            spParam.setVarName(TaskVarEnum.NEW_ACCT_NM.name());
            spParam.setVarValue(request.getNewAcctName());

            taskSpService.insertTaskVar(spParam);
        } catch (Exception e) {
            throw new RuntimeException(ERROR_LOG + e.getMessage());
        }
    }

    public void createTaskHistory(UserAndCorp user, TaskProjection task, ApprovalStatusEnum appStatus, String remarks) {
        try {
            TaskHistoryInsertSpParam spParam = new TaskHistoryInsertSpParam();
            spParam.setId(UUID.randomUUID().toString());
            spParam.setCreatedBy(user.getUserId());
            spParam.setDtCreated(OffsetDateTime.now());
            spParam.setTsCreated(Timestamp.from(Instant.now()));
            spParam.setCfoTaskId(task.getTaskId());
            spParam.setMtApprSts(
                    approvalStatusRepository.findByCode(appStatus.name())
                            .map(ApprovalStatusProjection::getId)
                            .orElseGet(() -> {
                                log.info("Approval status [{}] not found in repository", appStatus.name());
                                return null;
                            })
            );
            if(ApprovalStatusEnum.REJECTED.equals(appStatus)){
                spParam.setRemark(remarks);
            }

            taskSpService.insertTaskHistory(spParam);
        } catch (Exception e){
            throw new RuntimeException(ERROR_LOG + e.getMessage());
        }
    }

    public void updateTask(UserAndCorp user, String taskId, ApprovalStatusEnum appStatus, String remarks) {
        try {
            TaskUpdateSpParam spParam = new TaskUpdateSpParam();
            spParam.setCfoTaskId(taskId);
            spParam.setMtApprSts(
                    approvalStatusRepository.findByCode(appStatus.name())
                            .map(ApprovalStatusProjection::getId)
                            .orElseGet(() -> {
                                log.info("Approval status [{}] not found in repository", appStatus.name());
                                return null;
                            })
            );
            if(ApprovalStatusEnum.REJECTED.equals(appStatus)){
                spParam.setRemark(remarks);
            }
            spParam.setUpdatedBy(user.getUserId());
            spParam.setDtUpdated(OffsetDateTime.now());
            spParam.setTsUpdated(Timestamp.from(Instant.now()));

            taskSpService.updateTask(spParam);
        } catch (Exception e){
            throw new RuntimeException(ERROR_LOG + e.getMessage());
        }
    }

    public void updateOriginalData(UserAndCorp user, String acctNumber, String newAcctName){
        try{
            MTUpdateAcctNameSpParam spParam = new MTUpdateAcctNameSpParam();
            spParam.setAcctNumber(acctNumber);
            spParam.setAcctPrefName(newAcctName);
            spParam.setUpdatedBy(user.getUserId());
            spParam.setDtUpdated(OffsetDateTime.now());
            spParam.setTsUpdated(Timestamp.from(Instant.now()));
            taskSpService.updateAccountPreferredName(spParam);
        } catch(Exception e){
            throw new RuntimeException(ERROR_LOG + e.getMessage());
        }
    }

    public EditAcctNameApprovalDTO toApprovalDTO(TaskProjection task, List<CfoTaskVar> vars) {
        try{
            Optional<AcctProjection> accountOpt = accountRepository.findByAcctNumber(task.getRefNumber());
            if (accountOpt.isEmpty()) {
                log.info("Account {} not found.", task.getRefNumber());
                return null;
            }

            AcctProjection account = accountOpt.get();
            EditAcctNameApprovalDTO approvalDTO = new EditAcctNameApprovalDTO();

            // Submission info
            EditAcctNameSubmissionDTO submissionDTO = new EditAcctNameSubmissionDTO();
            submissionDTO.setAcctNumber(task.getRefNumber());
            submissionDTO.setDefaultName(account.getDefaultName());
            submissionDTO.setCurrentAcctName(account.getAcctName());
            submissionDTO.setNewAcctName(vars.stream()
                    .filter(v -> TaskVarEnum.NEW_ACCT_NM.name().equalsIgnoreCase(v.getVarName()))
                    .map(CfoTaskVar::getVarValue)
                    .findFirst().orElse(""));
            submissionDTO.setAction(
                    cfoFunctionActionRepository.findActionById(task.getFunctionAction())
                            .map(CFOFunctionActionProjection::getDescription)
                            .orElseGet(() -> {
                                log.info("Function Action [{}] not found in repository", task.getFunctionAction());
                                return null;
                            })
            );
            submissionDTO.setDateCreated(task.getCreatedDate());

            Optional<AccountTypeProjection> product = accountTypeRepository.findAcctTypByCd(account.getAcctTyp());
            submissionDTO.setProductType(product.map(AccountTypeProjection::getDescription).orElseGet(() -> {
                log.info("Product Type [{}] not found in repository", account.getAcctTyp());
                return null;
            }));

            Optional<ApprovalStatusProjection> status = approvalStatusRepository.findCFOApprovalStatusById(task.getApprSts());
            submissionDTO.setApprovalStatus(status.map(ApprovalStatusProjection::getDescription).orElseGet(() -> {
                log.info("Approval status [{}] not found in repository", task.getApprSts());
                return null;
            }));;

            submissionDTO.setRequestedBy(task.getCreatedBy());
            submissionDTO.setRequestedDate(submissionDTO.getDateCreated());

            approvalDTO.setSubmissionData(submissionDTO);
            approvalDTO.setCompanyName(
                    // direct to cif since all onboard and non-onboard company will be store in here
                    cifRepository.findCorpDetailByCifNo(account.getCifNumber()).map(CorpDetailProjection::getCorpShortName).orElseGet(() -> {
                        log.info("CIF number [{}] not found in repository", account.getCifNumber());
                        return null;
                    }));
            approvalDTO.setTaskId(task.getTaskId());
            return approvalDTO;
        } catch (Exception e){
            throw new RuntimeException(ERROR_LOG + e.getMessage());
        }
    }

    public void enrichWithHistoryInfo(EditAcctNameApprovalDTO dto, List<TaskHistoryProjection> histories) {
        histories.forEach(h -> {
            Optional<ApprovalStatusProjection> data = approvalStatusRepository.findCFOApprovalStatusById(h.getApprovalStatus());
            String approvalStatusCode = data.map(ApprovalStatusProjection::getCode).orElseGet(() -> {
                log.info("Approval status [{}] not found in repository", h.getApprovalStatus());
                return "";
            });
            if (ApprovalStatusEnum.APPROVED.name().equalsIgnoreCase(approvalStatusCode)) {
                dto.setApprovedBy(h.getCreatedBy());
                dto.setApprovedDate(h.getCreatedDate());
            } else if (ApprovalStatusEnum.REJECTED.name().equalsIgnoreCase(approvalStatusCode)) {
                dto.setRejectedBy(h.getCreatedBy());
                dto.setRejectedDate(h.getCreatedDate());
                dto.setRejectionNotes(h.getRemark());
            }
        });
    }

}
