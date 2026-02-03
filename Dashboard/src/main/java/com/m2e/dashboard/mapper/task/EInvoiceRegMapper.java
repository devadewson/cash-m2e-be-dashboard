package com.m2e.dashboard.mapper.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.einvoice.request.EIMUpdateLinkedStatusReqDTO;
import com.m2e.dashboard.dto.task.request.TaskVarEInvoiceRegDTO;
import com.m2e.dashboard.dto.task.response.EInvoiceRegApprovalDTO;
import com.m2e.dashboard.dto.task.response.EInvoiceRegSubmissionDTO;
import com.m2e.dashboard.enums.*;
import com.m2e.dashboard.jms.einvoice.response.tviewParty.ESBTViewPartyFullMsgBodyDTO;
import com.m2e.dashboard.projection.*;
import com.m2e.dashboard.repository.*;
import com.m2e.dashboard.service.einvoice.integration.EIMIntegrationService;
import com.m2e.dashboard.service.einvoice.jms.EInvoiceESBService;
import com.m2e.dashboard.service.task.TaskSpService;
import com.m2e.dashboard.sp.BaseSPExecDTO;
import com.m2e.dashboard.sp.einvoice.EinvoiceUpdateCorpSpParam;
import com.m2e.dashboard.sp.task.request.TaskHistoryInsertSpParam;
import com.m2e.dashboard.sp.task.request.TaskInsertSpParam;
import com.m2e.dashboard.sp.task.request.TaskUpdateSpParam;
import com.m2e.dashboard.sp.task.request.TaskVarInsertSpParam;
import com.m2e.dashboard.sp.task.response.TaskSubmissionSpDTO;
import com.m2e.model.sso.base.CfoTaskVar;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EInvoiceRegMapper {
    private static final Logger log = LoggerFactory.getLogger(EInvoiceRegMapper.class);
    private static final String ERROR_LOG = "Something went wrong : ";

    private final CorpRepository corpRepository;
    private final ApprovalStatusRepository approvalStatusRepository;
    private final CFOFunctionRepository cfoFunctionRepository;
    private final CFOFunctionActionRepository cfoFunctionActionRepository;
    private final TaskSpService taskSpService;
    private final CifRepository cifRepository;
    private final EIMIntegrationService eimIntegrationService;
    private final EInvoiceESBService eInvoiceESBService;
    private final BaseSpService baseSpService;
    private final ObjectMapper objectMapper;

    public TaskSubmissionSpDTO createTask(UserAndCorp user, TaskVarEInvoiceRegDTO request) {
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
            spParam.setRefNo(request.getCorpId());
            spParam.setCfoFuncsId(
                    cfoFunctionRepository.findByCode(MTFunctionEnum.EINVOICE.name())
                            .map(CFOFunctionProjection::getId)
                            .orElseGet(() -> {
                                log.info("CFO Function [{}] not found in repository", MTFunctionEnum.EINVOICE.name());
                                return null;
                            }));
            spParam.setCfoFuncsActionId(
                    cfoFunctionActionRepository.findbyCode(MTFunctionActionEnum.EINVOICE_REGISTRATION.name())
                            .map(CFOFunctionActionProjection::getId)
                            .orElseGet(() -> {
                                log.info("CFO Function [{}] not found in repository", MTFunctionActionEnum.EINVOICE_REGISTRATION.name());
                                return null;
                            }));
            spParam.setRemark(null);
            taskSpService.insertTask(spParam);
            createTaskVar(user, spParam.getCfoTaskId(), request);
        } catch (Exception e){
            log.error("createTask mapper error {}", e);
            throw new RuntimeException(ERROR_LOG + e.getMessage());
        }
        return new TaskSubmissionSpDTO(spParam.getId(), spParam.getCfoTaskId());
    }

    public void createTaskVar(UserAndCorp user, String cfoTaskId, TaskVarEInvoiceRegDTO request) {
        try {
            OffsetDateTime now = OffsetDateTime.now();
            Timestamp tsNow = Timestamp.from(Instant.now());

            Map<TaskVarEnum, String> varMappings = Map.of(
                    TaskVarEnum.EINVOICE_CORPCIFNO, request.getCorpCifNo(),
                    TaskVarEnum.EINVOICE_CORPBRN, request.getBrn(),
                    TaskVarEnum.EINVOICE_TIN, request.getTin(),
                    TaskVarEnum.EINVOICE_SSN, request.getSsn()
            );

            varMappings.entrySet().stream()
                    .map(entry -> buildSpParam(entry.getKey(), entry.getValue(), user.getUserId(), cfoTaskId, now, tsNow))
                    .forEach(taskSpService::insertTaskVar);
        } catch (Exception e) {
            log.error("createTaskVar mapper error {}", e);
            throw new RuntimeException(ERROR_LOG + e.getMessage());
        }
    }

    private TaskVarInsertSpParam buildSpParam(
            TaskVarEnum varEnum,
            String value,
            String createdBy,
            String taskId,
            OffsetDateTime createdDate,
            Timestamp createdTs) {

        TaskVarInsertSpParam param = new TaskVarInsertSpParam();
        param.setId(UUID.randomUUID().toString());
        param.setCreatedBy(createdBy);
        param.setDtCreated(createdDate);
        param.setTsCreated(createdTs);
        param.setCfoTaskId(taskId);
        param.setVarName(varEnum.name());
        param.setVarValue(value);
        return param;
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
            log.error("createTaskHistory mapper error {}", e);
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
            log.error("updateTask mapper error {}", e);
            throw new RuntimeException(ERROR_LOG + e.getMessage());
        }
    }

    public void updateOriginalData(UserAndCorp user, String corpId, String corpCifNo, String brn, String sstNo, String taxId){
        try{
            // call EIM API - Update linked status
            String userId = user.getUserId();
            EIMUpdateLinkedStatusReqDTO req = new EIMUpdateLinkedStatusReqDTO();
            req.setGcif(corpCifNo);
            req.setLinked(EInvoiceStatementReqParamEnum.LINKED.getDefaultValue());
//            req.setIdValue(brn);
            req.setReqChannel(EInvoiceStatementReqParamEnum.REQCHANNEL.getDefaultValue());
            req.setReqLoginId(userId);
            req.setReqDateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            req.setReqReferenceId(String.valueOf(System.currentTimeMillis()));
            eimIntegrationService.updatedEInvoiceLinkedStatus(req);
            //calling tviewparty and update
            ESBTViewPartyFullMsgBodyDTO viewPartyResp = eInvoiceESBService.cifInquiry(corpCifNo);
            eInvoiceESBService.updateParty(viewPartyResp, taxId, sstNo);
            updateCorp(corpId, taxId, sstNo);
        } catch(Exception e){
            log.error("updateOriginalData mapper error {}", e);
            throw new RuntimeException(ERROR_LOG + e.getMessage());
        }
    }

    public EInvoiceRegApprovalDTO toApprovalDTO(TaskProjection task, List<CfoTaskVar> vars) {
        // TODO : need modified
        try{
            EInvoiceRegApprovalDTO approvalDTO = new EInvoiceRegApprovalDTO();
            String corpCifNumber = vars.stream()
                    .filter(v -> TaskVarEnum.EINVOICE_CORPCIFNO.name().equalsIgnoreCase(v.getVarName()))
                    .map(CfoTaskVar::getVarValue)
                    .findFirst().orElse("");
            String corpBrn = vars.stream()
                    .filter(v -> TaskVarEnum.EINVOICE_CORPBRN.name().equalsIgnoreCase(v.getVarName()))
                    .map(CfoTaskVar::getVarValue)
                    .findFirst().orElse("");
            String corpTIN = vars.stream()
                    .filter(v -> TaskVarEnum.EINVOICE_TIN.name().equalsIgnoreCase(v.getVarName()))
                    .map(CfoTaskVar::getVarValue)
                    .findFirst().orElse("");
            String corpSSN = vars.stream()
                    .filter(v -> TaskVarEnum.EINVOICE_SSN.name().equalsIgnoreCase(v.getVarName()))
                    .map(v -> Optional.ofNullable(v.getVarValue()).orElse(""))
                    .findFirst().orElse("");
            // Submission info
            EInvoiceRegSubmissionDTO submissionDTO = new EInvoiceRegSubmissionDTO();
            submissionDTO.setCorpId(task.getRefNumber());
            submissionDTO.setBrn(corpBrn);
            submissionDTO.setTin(corpTIN);
            submissionDTO.setSsn(corpSSN);
            submissionDTO.setAction(
                    cfoFunctionActionRepository.findActionById(task.getFunctionAction())
                            .map(CFOFunctionActionProjection::getDescription)
                            .orElseGet(() -> {
                                log.info("Function Action [{}] not found in repository", task.getFunctionAction());
                                return null;
                            })
            );
            submissionDTO.setDateCreated(task.getCreatedDate());

            Optional<ApprovalStatusProjection> status = approvalStatusRepository.findCFOApprovalStatusById(task.getApprSts());
            submissionDTO.setApprovalStatus(status.map(ApprovalStatusProjection::getDescription).orElseGet(() -> {
                log.info("Approval status [{}] not found in repository", task.getApprSts());
                return null;
            }));;

            submissionDTO.setRequestedBy(task.getCreatedBy());
            submissionDTO.setRequestedDate(submissionDTO.getDateCreated());

            approvalDTO.setSubmissionData(submissionDTO);
            approvalDTO.setCompanyName(
                    corpRepository.findCorpShortNameByCifNoAndCorpId(corpCifNumber, task.getRefNumber()).orElseGet(() -> {
                        return cifRepository.findCorpDetailByCifNoAndCorpId(corpCifNumber, task.getRefNumber()).map(CorpDetailProjection::getCorpShortName).orElseGet(() -> {
                            log.info("CIF number [{}] not found in repository", corpCifNumber);
                            return null;
                        });
                    }));
            approvalDTO.setTaskId(task.getTaskId());
            return approvalDTO;
        } catch (Exception e){
            log.error("toApprovalDTO mapper error {}", e);
            throw new RuntimeException(ERROR_LOG + e.getMessage());
        }
    }

    public void enrichWithHistoryInfo(EInvoiceRegApprovalDTO dto, List<TaskHistoryProjection> histories) {
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

    private void updateCorp(String corpId, String taxId, String sstNo){
        EinvoiceUpdateCorpSpParam spParam = new EinvoiceUpdateCorpSpParam(corpId, taxId, sstNo);

        BaseSpDto<EinvoiceUpdateCorpSpParam, BaseSPExecDTO> spDto = new BaseSpDto<>();
        spDto.setSpId(SPEnum.SP_E_INVOICE_UPDATE_CORP.getValue());
        spDto.setParameter(spParam);
        BaseSpDto<EinvoiceUpdateCorpSpParam, BaseSPExecDTO> resultDto = baseSpService.execute(spDto);
        BaseSPExecDTO spResponse = objectMapper.convertValue(resultDto.getResults().get(0), BaseSPExecDTO.class);
        if (!"0".equals(spResponse.getStatusCode())) {
            throw new RuntimeException("Failed to update corp: " + spResponse.getStatusMessage());
        }
    }
}
