//package com.m2e.dashboard.service.einvoice;
//
//import com.m2e.dashboard.config.UserAndCorp;
//import com.m2e.dashboard.dto.base.DownloadDTO;
//import com.m2e.dashboard.dto.common.ApiResponse;
//import com.m2e.dashboard.dto.common.ErrorResponse;
//import com.m2e.dashboard.dto.common.SearchCollectionResponse;
//import com.m2e.dashboard.dto.einvoice.request.*;
//import com.m2e.dashboard.dto.einvoice.response.EIMRequestLinkedStatusDTO;
//import com.m2e.dashboard.dto.einvoice.response.EInvoiceCorpDetailDTO;
//import com.m2e.dashboard.dto.einvoice.response.EInvoiceListingDTO;
//import com.m2e.dashboard.dto.einvoice.response.EInvoiceValidateTINDTO;
//import com.m2e.dashboard.dto.task.request.TaskApprovalReqDTO;
//import com.m2e.dashboard.dto.task.request.TaskSubmitReqDTO;
//import com.m2e.dashboard.dto.task.request.TaskVarEInvoiceRegDTO;
//import com.m2e.dashboard.dto.task.response.EInvoiceRegApprovalDTO;
//import com.m2e.dashboard.dto.task.response.TaskDetailDTO;
//import com.m2e.dashboard.enums.ApprovalStatusEnum;
//import com.m2e.dashboard.enums.DownloadEnum;
//import com.m2e.dashboard.enums.MTFunctionActionEnum;
//import com.m2e.dashboard.jms.einvoice.response.tviewParty.ESBTViewPartyFullMsgBodyDTO;
//import com.m2e.dashboard.service.AsyncMappingService;
//import com.m2e.dashboard.service.AsyncPaginationService;
//import com.m2e.dashboard.service.einvoice.integration.EIMIntegration;
//import com.m2e.dashboard.service.einvoice.jms.EInvoiceESBService;
//import com.m2e.dashboard.service.einvoice.sp.EInvoiceSPService;
//import com.m2e.dashboard.service.task.TaskService;
//import com.m2e.dashboard.sp.einvoice.EinvoiceListingSpDTO;
//import com.m2e.utils.redis.helper.RedisHelper;
//import lombok.RequiredArgsConstructor;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Function;
//import java.util.function.Predicate;
//
//@Service
//@RequiredArgsConstructor
//public class EInvoiceService {
//    private static final Logger log = LoggerFactory.getLogger(EInvoiceService.class);
//
//    private final EInvoiceSPService eInvoiceSPService;
//    private final EInvoiceESBService eInvoiceESBService;
//    private final EIMIntegration eimIntegrationService;
//    private final TaskService taskService;
//    private final AsyncMappingService asyncMappingService;
//    private final AsyncPaginationService asyncPaginationService;
//    private final RedisHelper redisHelper;
//    private final EInvoiceListingFileService eInvoiceListingFileService;
//
//    public ApiResponse<EInvoiceListingDTO> eInvoiceListing(UserAndCorp auth, EInvoiceListingReqDTO req) {
//        SearchCollectionResponse<EinvoiceListingSpDTO> searchCollectionResponse = getEInvoiceSearchCollectionResponse(auth, req);
//
//        EInvoiceListingDTO dto = new EInvoiceListingDTO();
//        dto.setDatas(searchCollectionResponse);
//
//        return new ApiResponse<EInvoiceListingDTO>().buildSuccess(dto);
//    }
//
//    public ApiResponse<EInvoiceCorpDetailDTO> getCorpDetails(EInvoiceCorpDetailReqDTO corpDetailReqDTO){
//        ESBTViewPartyFullMsgBodyDTO response = eInvoiceESBService.cifInquiry(corpDetailReqDTO.getCifNo());
//        return eInvoiceESBService.getCorpDetail(response,corpDetailReqDTO);
//    }
//
//    public ApiResponse<EInvoiceValidateTINDTO> validateTIN(UserAndCorp auth, EInvoiceValidateTINReqDTO req){
//        return eInvoiceESBService.validateTin(req);
//    }
//
//    public ApiResponse<EInvoiceRegApprovalDTO> eInvoiceRegistration(UserAndCorp auth, EInvoiceRegReqDTO req){
//        TaskSubmitReqDTO<TaskVarEInvoiceRegDTO> request = new TaskSubmitReqDTO<>();
//        request.setTaskType(MTFunctionActionEnum.EINVOICE_REGISTRATION.name());
//        request.setData(new TaskVarEInvoiceRegDTO(req.getCorpId(), req.getCorpCifNo(), req.getBrn(), req.getTin(), req.getSsn()));
//
//        ApiResponse<TaskDetailDTO<EInvoiceRegApprovalDTO>> submitResponse = taskService.submit(auth, request);
//        if (!isSuccess(submitResponse)) {
//            log.warn("Failed to submit e-Invoice registration task: {}", submitResponse);
//            return buildErrorResponse();
//        }
//
//        String taskId = Optional.ofNullable(submitResponse.getMsgBody())
//                .map(TaskDetailDTO::getData)
//                .map(EInvoiceRegApprovalDTO::getTaskId)
//                .orElse(null);
//
//        if (taskId == null) {
//            log.warn("Task ID is missing in the submit response: {}", submitResponse);
//            return buildErrorResponse();
//        }
//
//        log.info("Admin submitted task e-Invoice registration with ID -> {}", taskId);
//
//        TaskApprovalReqDTO approvalReq = new TaskApprovalReqDTO(taskId, ApprovalStatusEnum.APPROVED, "");
//        log.info("Admin executing approval with request payload {} ", approvalReq);
//        ApiResponse<TaskDetailDTO<EInvoiceRegApprovalDTO>> approvalResponse = taskService.approval(auth, approvalReq);
//        log.info("Admin approval response -> {}", approvalResponse);
//        if (!isSuccess(approvalResponse)) {
//            log.warn("Failed to approve e-Invoice registration task: {}", approvalResponse);
//            return buildErrorResponse();
//        }
//        return new ApiResponse<EInvoiceRegApprovalDTO>().buildSuccess(approvalResponse.getMsgBody().getData());
//    }
//
//    private boolean isSuccess(ApiResponse<?> response) {
//        return response != null && "0".equalsIgnoreCase(response.getMsgHeader().getResponseCode());
//    }
//
//    private ApiResponse<EInvoiceRegApprovalDTO> buildErrorResponse() {
//        ApiResponse<EInvoiceRegApprovalDTO> resp = new ApiResponse<>();
//        resp.setMsgHeader(new ErrorResponse("500"));
//        return resp;
//    }
//
//    private List<Predicate<EinvoiceListingSpDTO>> constructFilter(EInvoiceListingReqDTO receivedRequest) {
//        List<Predicate<EinvoiceListingSpDTO>> filterConditions = new ArrayList<>();
//        Predicate<EinvoiceListingSpDTO> searchParamFilter = dto ->
//                StringUtils.containsIgnoreCase(dto.getCorpName(), receivedRequest.getSearchParam()) ||
//                        StringUtils.containsIgnoreCase(dto.getBusinessReqNumber(), receivedRequest.getSearchParam()) ||
//                        StringUtils.containsIgnoreCase(dto.getStatus(), receivedRequest.getSearchParam());
//
//        filterConditions.add(searchParamFilter);
//        return filterConditions;
//    }
//
//    private List<EinvoiceListingSpDTO> getListing(UserAndCorp userAndCorp) {
//        String key = redisHelper.createCacheKey("E_INVOICE_LIST", userAndCorp.getUserId());
//        List<EinvoiceListingSpDTO> list = redisHelper.getListFromCache(key, EinvoiceListingSpDTO.class);
//
//        if (list.isEmpty()) {
//            list = eInvoiceSPService.getEInvoiceListing(userAndCorp);
//
//            log.info("e-invoice list: {}", list);
//
//            if (!list.isEmpty())
//                redisHelper.saveListToCache(key, list, 10, TimeUnit.MINUTES);
//        }
//        return list;
//    }
//
//    public DownloadDTO downloadEInvoiceListing(UserAndCorp auth, EInvoiceListingDownloadReqDTO req) {
//        DownloadDTO result = new DownloadDTO();
//        EInvoiceListingReqDTO eInvoiceListingReqDTO = new EInvoiceListingReqDTO();
//        eInvoiceListingReqDTO.setSearchParam(req.getSearchParam());
//        eInvoiceListingReqDTO.setPage(1);
//        eInvoiceListingReqDTO.setPageSize(Integer.MAX_VALUE);
//
//        SearchCollectionResponse<EinvoiceListingSpDTO> searchCollectionResponse = getEInvoiceSearchCollectionResponse(auth, eInvoiceListingReqDTO);
//        List<EinvoiceListingSpDTO> dataList = searchCollectionResponse.getData();
//
//        switch (req.getFormat()) {
//            case CSV:
//                result.setData(eInvoiceListingFileService.generateCsv(dataList));
//                result.setFileName(DownloadEnum.EINVOICE_LISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
//                break;
//            case PDF:
//                result.setData(eInvoiceListingFileService.generatePdf(dataList));
//                result.setFileName(DownloadEnum.EINVOICE_LISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
//                break;
//            case TXT:
//                result.setData(eInvoiceListingFileService.generateTxt(dataList));
//                result.setFileName(DownloadEnum.EINVOICE_LISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
//                break;
//            default:
//                throw new IllegalArgumentException("Report format is undefined");
//        }
//        return result;
//    }
//
//    private SearchCollectionResponse<EinvoiceListingSpDTO> getEInvoiceSearchCollectionResponse(UserAndCorp auth, EInvoiceListingReqDTO req) {
//        List<EinvoiceListingSpDTO> listing = getListing(auth).stream()
//                .map(comp -> {
//                    try {
//                        EIMRequestLinkedStatusDTO eimStatus = eimIntegrationService.requestLinkedStatusDTO(
//                                auth.getUserId(), new EInvoiceRequestLinkedStatusReqDTO(comp.getCorpCifNo(), comp.getBusinessReqNumber()) // TODO: replace with CIF_NO, BRN
//                        );
//
//                        return EinvoiceListingSpDTO.builder()
//                                .corpId(comp.getCorpId())
//                                .corpCifNo(comp.getCorpCifNo())
//                                .corpName(comp.getCorpName())
//                                .businessReqNumber(comp.getBusinessReqNumber())
//                                .status("Y".equalsIgnoreCase(eimStatus.getIsLinked()) ? "Registered" : "Unregistered")
//                                .build();
//
//                    } catch (Exception ex) {
//                        log.info("Error fetching EIM status for Corp CIF: {}", comp.getCorpCifNo(), ex);
//                        return comp;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .toList();
//
//        List<Predicate<EinvoiceListingSpDTO>> filterConditions = constructFilter(req);
//
//        List<Function<List<EinvoiceListingSpDTO>, List<EinvoiceListingSpDTO>>> sortFunctions = new ArrayList<>();
//        sortFunctions.add(list -> list.stream()
//                .sorted(Comparator.comparing(EinvoiceListingSpDTO::getCorpName, Comparator.nullsLast(String::compareToIgnoreCase)))
//                .toList());
//
//        CompletableFuture<List<EinvoiceListingSpDTO>> filteredResult = asyncMappingService.processListAsyncAndFilter(listing,
//                dto -> dto,
//                filterConditions,
//                sortFunctions);
//
//        Function<List<EinvoiceListingSpDTO>, List<EinvoiceListingSpDTO>> trxMapper =
//                dtos -> filteredResult.join();
//
//        SearchCollectionResponse<EinvoiceListingSpDTO> searchCollectionResponse = asyncPaginationService.getPaginatedResponse(filteredResult.join(), trxMapper, req.getPageSize(), req.getPage());
//        return searchCollectionResponse;
//    }
//}
