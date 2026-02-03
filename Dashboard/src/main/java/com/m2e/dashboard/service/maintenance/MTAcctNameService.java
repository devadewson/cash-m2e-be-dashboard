package com.m2e.dashboard.service.maintenance;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.maintenance.request.MTResetAccctNameReqDTO;
import com.m2e.dashboard.dto.maintenance.request.MTUpdateAcctNameReqDTO;
import com.m2e.dashboard.dto.maintenance.request.SubProductListReqDTO;
import com.m2e.dashboard.dto.maintenance.response.SubProductDTO;
import com.m2e.dashboard.dto.task.request.TaskApprovalReqDTO;
import com.m2e.dashboard.dto.task.request.TaskSubmitReqDTO;
import com.m2e.dashboard.dto.task.request.TaskVarEditAcctNameDTO;
import com.m2e.dashboard.dto.task.response.EditAcctNameApprovalDTO;
import com.m2e.dashboard.dto.task.response.TaskDetailDTO;
import com.m2e.dashboard.enums.ApprovalStatusEnum;
import com.m2e.dashboard.enums.ErrorEnum;
import com.m2e.dashboard.enums.MTFunctionActionEnum;
import com.m2e.dashboard.enums.Product;
import com.m2e.dashboard.mapper.maintenance.AccountTypeMapper;
import com.m2e.dashboard.projection.AccountTypeProjection;
import com.m2e.dashboard.repository.AccountTypeRepository;
import com.m2e.dashboard.service.ResetAcctNameStrategy;
import com.m2e.dashboard.service.SubProductStrategy;
import com.m2e.dashboard.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MTAcctNameService {

    private static final Logger log = LoggerFactory.getLogger(MTAcctNameService.class);

    private final AccountTypeRepository accountTypeRepository;
    private final AccountTypeMapper accountTypeMapper;
    private final List<SubProductStrategy> subProductStrategies;
    private final List<ResetAcctNameStrategy> resetAcctNameStrategies;
    private final TaskService taskService;

    public ApiResponse<List<SubProductDTO>> getSubProducts(SubProductListReqDTO req) {
        ApiResponse<List<SubProductDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));

        if (req == null || req.getProductCode() == null) {
            apiResponse.setMsgBody(new ArrayList<>());
            return apiResponse;
        }

        apiResponse.setMsgBody(findSubProducts(req.getProductCode()));
        return apiResponse;
    }

    public List<SubProductDTO> findSubProducts(Product product){
        List<AccountTypeProjection> datas = Optional.ofNullable(
                accountTypeRepository.findAcctTypByProdCode(product.getCode())
        ).orElseGet(ArrayList::new);

        List<SubProductDTO> mappedResults = accountTypeMapper.toDTOList(datas);

        List<SubProductDTO> results = subProductStrategies.stream()
                .filter(s -> s.supports(product))
                .findFirst()
                .map(strategy -> strategy.getSubProducts(mappedResults))
                .orElse(mappedResults);
        return results;
    }

    public List<SubProductDTO> findSubProductsEInvoiceDL(Product product){
        List<AccountTypeProjection> datas = Optional.ofNullable(
                accountTypeRepository.findAcctTypByProdCode(product.getCode())
        ).orElseGet(ArrayList::new);

        List<SubProductDTO> mappedResults = accountTypeMapper.toDTOList(datas);

        List<SubProductDTO> results = subProductStrategies.stream()
                .filter(s -> s.supports(product))
                .findFirst()
                .map(strategy -> strategy.getSubProductsEInvoiceDownload(mappedResults))
                .orElse(mappedResults);
        return results;
    }

    public ApiResponse<String> resetToDefaultAcctName(UserAndCorp userAndCorp, MTResetAccctNameReqDTO req) {
        ResetAcctNameStrategy resetAcctNameStrategy = resetAcctNameStrategies.stream()
                .filter(strategy -> strategy.supports(req.getProduct()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product type is undefined"));

        boolean dbUpdateSuccess = resetAcctNameStrategy.resetAcctName(userAndCorp, req);
        ApiResponse<String> response = new ApiResponse<>();

        return dbUpdateSuccess ? response.buildSuccess("Successfully updated account name to default value") : response.buildError("500", ErrorEnum.ERR_DB_UPDATE.getValue());
    }
    public ApiResponse<EditAcctNameApprovalDTO> updateAcctNm(UserAndCorp userAndCorp, MTUpdateAcctNameReqDTO req) {
        TaskSubmitReqDTO<TaskVarEditAcctNameDTO> request = new TaskSubmitReqDTO<>();
        request.setTaskType(MTFunctionActionEnum.EDIT_ACCT_NM.name());
        request.setData(new TaskVarEditAcctNameDTO(req.getAcctNumber(), req.getNewAcctName()));

        ApiResponse<TaskDetailDTO<EditAcctNameApprovalDTO>> submitResponse = taskService.submit(userAndCorp, request);
        if (!isSuccess(submitResponse)) {
            log.warn("Failed to submit Edit Account Name task: {}", submitResponse);
            return buildErrorResponse();
        }

        String taskId = Optional.ofNullable(submitResponse.getMsgBody())
                .map(TaskDetailDTO::getData)
                .map(EditAcctNameApprovalDTO::getTaskId)
                .orElse(null);

        if (taskId == null) {
            log.warn("Task ID is missing in the submit response: {}", submitResponse);
            return buildErrorResponse();
        }

        log.info("Admin submitted task edit account number with ID -> {}", taskId);

        TaskApprovalReqDTO approvalReq = new TaskApprovalReqDTO(taskId, ApprovalStatusEnum.APPROVED, "");
        ApiResponse<TaskDetailDTO<EditAcctNameApprovalDTO>> approvalResponse = taskService.approval(userAndCorp, approvalReq);

        if (!isSuccess(approvalResponse)) {
            log.warn("Failed to approve Edit Account Name task: {}", approvalResponse);
            return buildErrorResponse();
        }

        return new ApiResponse<EditAcctNameApprovalDTO>().buildSuccess(approvalResponse.getMsgBody().getData());
    }

    private boolean isSuccess(ApiResponse<?> response) {
        return response != null && "0".equalsIgnoreCase(response.getMsgHeader().getResponseCode());
    }

    private ApiResponse<EditAcctNameApprovalDTO> buildErrorResponse() {
        ApiResponse<EditAcctNameApprovalDTO> resp = new ApiResponse<>();
        resp.setMsgHeader(new ErrorResponse("500"));
        return resp;
    }

}