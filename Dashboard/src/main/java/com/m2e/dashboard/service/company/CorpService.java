package com.m2e.dashboard.service.company;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.OpenTagComp;
import com.m2e.dashboard.dto.base.OpenTagCompanyListReq;
import com.m2e.dashboard.dto.base.OpenTagCorpPageDTO;
import com.m2e.dashboard.dto.base.OpenTagCorpPageReqDTO;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.deposit.ca.request.DepositAcctBaseReqDTO;
import com.m2e.dashboard.dto.deposit.ca.response.SubscriberCorpLinkDTO;
import com.m2e.dashboard.dto.dropdown.DropdownDto;
import com.m2e.dashboard.dto.dropdown.TrxCodeDropdownReqDto;
import com.m2e.dashboard.enums.SourcePageEnum;
import com.m2e.dashboard.exception.ResourceNotFoundException;
import com.m2e.dashboard.repository.CorpRepository;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.m2e.dashboard.enums.SPEnum.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class CorpService {
    private static final String OPEN_TAG_CORPS_REDIS_KEY = "OPEN_TAG_CORPS";
    private final CorpServiceDataGenerator corpServiceDataGenerator;
    private final BaseSpService baseSpService;
    private final CorpRepository corpRepository;
    private final RedisHelper redisHelper;

    public ApiResponse<OpenTagCorpPageDTO> getOpenTagCompanyList(UserAndCorp userAndCorp, OpenTagCorpPageReqDTO req) {

        String redisKey = redisHelper.buildCacheKey(
                userAndCorp.getUserId(),
                userAndCorp.getCorpId(),
                OPEN_TAG_CORPS_REDIS_KEY,
                req.getProduct(),
                req.getSubProduct(),
                req.getSource() != null ? req.getSource().name() : "null",
                String.valueOf(req.getPageSize()),
                String.valueOf(req.getPage()));

        if (StringUtils.hasText(req.getSearchParam())) {
            log.info("Search param is not empty, getting fresh open tag company list");
            return getOpenTagCompanyListFromRequest(userAndCorp, req);
        }

        OpenTagCorpPageDTO dto = redisHelper.getFromCache(redisKey, OpenTagCorpPageDTO.class);
        ApiResponse<OpenTagCorpPageDTO> response;
        if (dto != null) {
            log.info("Reusing cached open tag company list");
            response = new ApiResponse<>().buildSuccess(dto);
        } else {
            log.info("No cached version found, getting fresh open tag company list");
            response = getOpenTagCompanyListFromRequest(userAndCorp, req);
            if (response.getMsgHeader().getResponseCode().equals("0")) {
                log.info("Caching fresh open tag company list");
                redisHelper.save(redisKey, response.getMsgBody());
            }
        }

        return response;
    }

    private ApiResponse<OpenTagCorpPageDTO> getOpenTagCompanyListFromRequest(UserAndCorp userAndCorp, OpenTagCorpPageReqDTO req) {
        return switch (req.getProduct()) {
            case "TRADE" -> switch (req.getSubProduct()) {
                case "I", "E", "G", "L" -> getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_TD.getValue());
                default -> {
                    List<OpenTagCorpPageDTO> spResultList = List.of(
                            getOpenTagCompanyListSpForSubProduct(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_TD.getValue(), "I").getMsgBody(),
                            getOpenTagCompanyListSpForSubProduct(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_TD.getValue(), "E").getMsgBody(),
                            getOpenTagCompanyListSpForSubProduct(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_TD.getValue(), "L").getMsgBody(),
                            getOpenTagCompanyListSpForSubProduct(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_TD.getValue(), "G").getMsgBody()
                    );
                    ApiResponse<OpenTagCorpPageDTO> tradeOpenTagResponse = new ApiResponse<>();
                    tradeOpenTagResponse.setMsgHeader(new ErrorResponse("0"));
                    tradeOpenTagResponse.setMsgBody(combineOpenTagSpResult(spResultList, req.getPage(), req.getPageSize()));
                    yield tradeOpenTagResponse;
                }
            };
            case "DEPOSIT" -> switch (req.getSubProduct()) {
                case "CA" -> getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_DP_CA.getValue());
                case "VA" -> getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_DP_VA.getValue());
                case "FD", "TD" -> getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST.getValue());
                default -> {
                    String fdSubProduct = resolveFdSubProductByCorpProvider(userAndCorp.getCorpId());
                    List<OpenTagCorpPageDTO> spResultList = List.of(
                            getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_DP_CA.getValue()).getMsgBody(),
                            getOpenTagCompanyListSpForSubProduct(userAndCorp, req, SP_CORP_OPEN_TAG_LIST.getValue(), fdSubProduct).getMsgBody()
                    );
                    ApiResponse<OpenTagCorpPageDTO> depositOpenTagResponse = new ApiResponse<>();
                    depositOpenTagResponse.setMsgHeader(new ErrorResponse("0"));
                    depositOpenTagResponse.setMsgBody(combineOpenTagSpResult(spResultList, req.getPage(), req.getPageSize()));
                    yield depositOpenTagResponse;
                }
            };
            case "LOAN" -> switch (req.getSubProduct()) {
                case "TL" -> getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_LN_TL.getValue());
                case "RC" -> getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_LN_RC.getValue());
                case "OD" -> getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_LN_OD.getValue());
                default -> {
                    List<OpenTagCorpPageDTO> spResultList;
                    // Special case: If coming from Account Name Maintenance, omit RC
                    if (req.getSource() == SourcePageEnum.ACCT_NM_MAINTENANCE) {
                        // In SP, source will also be used to omit FITAS in SP
                        spResultList = List.of(
                                getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_LN_TL.getValue()).getMsgBody(),
                                getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_LN_OD.getValue()).getMsgBody()
                        );
                    } else {
                        // Include all
                        spResultList = List.of(
                                getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_LN_TL.getValue()).getMsgBody(),
                                getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_LN_RC.getValue()).getMsgBody(),
                                getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_LN_OD.getValue()).getMsgBody()
                        );
                    }
                    ApiResponse<OpenTagCorpPageDTO> loanOpenTagResponse = new ApiResponse<>();
                    loanOpenTagResponse.setMsgHeader(new ErrorResponse("0"));
                    loanOpenTagResponse.setMsgBody(combineOpenTagSpResult(spResultList, req.getPage(), req.getPageSize()));
                    yield loanOpenTagResponse;
                }
            };
            case "INVESTMENT" -> switch (req.getSubProduct()) {
                case "UT" -> getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_INV_UT.getValue());
                default -> getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST.getValue());
            };
            case "AUTOFINANCE" -> switch (req.getSubProduct()) {
                case "HP_ACCOUNT" -> getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_AF_HP.getValue());
                case "BD_FACILITY" ->
                        getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_AF_BD_FACILITY.getValue());
                case "BD_ACCOUNT" ->
                        getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_AF_BD_ACCT.getValue());
                case "FS_FACILITY" ->
                        getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_AF_FS_FACILITY.getValue());
                case "FS_ACCOUNT" ->
                        getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_AF_FS_ACCT.getValue());
                case "PBR_FACILITY" ->
                        getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_AF_PBR_FACILITY.getValue());
                case "PBR_ACCOUNT" ->
                        getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_AF_PBR_ACCT.getValue());
                default -> getOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST.getValue());
            };
            default -> getOverviewOpenTagCompanyListSp(userAndCorp, req, SP_CORP_OPEN_TAG_LIST_OV.getValue()); //overview page, all products
        };
    }


    private OpenTagCorpPageDTO combineOpenTagSpResult(List<OpenTagCorpPageDTO> resultList, int currentPage, int pageSize) {
        if (pageSize == 0) {
            log.error("Page size should not be 0");
        }

        int isNotSubscriberCompFlag = 0;

        List<OpenTagComp> allComps = resultList.stream()
                .flatMap(dto -> dto.getData().getData().stream())
                .toList();

        List<OpenTagComp> combinedResult = new ArrayList<>(allComps.stream()
                .collect(Collectors.toMap(
                        OpenTagComp::getCorpCifNo,
                        comp -> comp,
                        (existing, newComp) -> {
                            existing.setNoOfAccounts(existing.getNoOfAccounts() + newComp.getNoOfAccounts());
                            existing.setIsSubscriberCompOnly(existing.getIsSubscriberCompOnly() || newComp.getIsSubscriberCompOnly());
                            return existing;
                        }))
                .values());

        // modify next page, total records, total page after combining sp result and remove duplicate corps
        int totalRecords = combinedResult.size();
        int totalPages = (pageSize != 0) ? (int) Math.ceil((double) totalRecords / pageSize) : 0;
        int nextPage = (currentPage >= totalPages) ? currentPage : currentPage + 1;

        // check subscriber flag
        for (OpenTagCorpPageDTO result : resultList) {
            if (Boolean.FALSE.equals(result.getIsSubscriberCompOnly())) {
                isNotSubscriberCompFlag++;
            }
        }

        return OpenTagCorpPageDTO.builder()
                .totalPages(totalPages)
                .currentPage(currentPage)
                .nextPage(nextPage)
                .isSubscriberCompOnly(isNotSubscriberCompFlag == 0)
                .data(new SearchCollectionResponse<>(totalPages, currentPage, nextPage, totalRecords, combinedResult))
                .build();
    }

    public ApiResponse<List<SubscriberCorpLinkDTO>> getSubscriberCompanyLinkListSp(String userId) {
        BaseSpDto<DepositAcctBaseReqDTO, SubscriberCorpLinkDTO> baseSpDto = new BaseSpDto<>();
        DepositAcctBaseReqDTO depositAcctBaseReqDTO = new DepositAcctBaseReqDTO();
        depositAcctBaseReqDTO.setUserId(userId);
        baseSpDto.setSpId(SP_CORP_SUBSCRIBER_LINK_LIST.getValue());
        baseSpDto.setParameter(depositAcctBaseReqDTO);
        baseSpDto.setResultClass(SubscriberCorpLinkDTO.class);
        List<SubscriberCorpLinkDTO> respList = baseSpService.execute(baseSpDto).getResults().stream().toList();
        ApiResponse<List<SubscriberCorpLinkDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(respList);

        return apiResponse;
    }

    public ApiResponse<OpenTagCorpPageDTO> getOpenTagCompanyListSp(UserAndCorp userAndCorp, OpenTagCorpPageReqDTO req, String spId) {
        OpenTagCompanyListReq openTagCompanyListReq = buildOpenTagCompanyListReq(userAndCorp, req, req.getSubProduct());
        return executeStoredProcedure(openTagCompanyListReq, spId, req.getPage());
    }

    public ApiResponse<OpenTagCorpPageDTO> getOpenTagCompanyListSpForSubProduct(UserAndCorp userAndCorp, OpenTagCorpPageReqDTO req, String spId, String subProductCode) {
        OpenTagCompanyListReq openTagCompanyListReq = buildOpenTagCompanyListReq(userAndCorp, req, subProductCode);
        return executeStoredProcedure(openTagCompanyListReq, spId, req.getPage());
    }

    private ApiResponse<OpenTagCorpPageDTO> executeStoredProcedure(OpenTagCompanyListReq request, String spId, int currentPage) {
        BaseSpDto<OpenTagCompanyListReq, OpenTagComp> resultBaseSpDto = baseSpService.execute(buildBaseSpDto(request, spId));
        List<OpenTagComp> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);
        Boolean isSubscriberCompOnly = spResultList.stream().findFirst().map(OpenTagComp::getIsSubscriberCompOnly).orElse(Boolean.TRUE);

        SearchCollectionResponse<OpenTagComp> result = new SearchCollectionResponse<>();
        result.setNextPage(currentPage >= totalPage ? totalPage : currentPage + 1);
        result.setCurrentPage(currentPage);
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);

        OpenTagCorpPageDTO openTagCorpPageDTO = OpenTagCorpPageDTO.builder()
                .isSubscriberCompOnly(isSubscriberCompOnly)
                .data(result)
                .build();

        ApiResponse<OpenTagCorpPageDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(openTagCorpPageDTO);

        return apiResponse;
    }

    private OpenTagCompanyListReq buildOpenTagCompanyListReq(UserAndCorp userAndCorp, OpenTagCorpPageReqDTO req, String subProduct) {
        OpenTagCompanyListReq request = new OpenTagCompanyListReq();
        request.setCorpId(userAndCorp.getCorpId());
        request.setUserId(userAndCorp.getUserId());
        request.setSearchParam(req.getSearchParam());
        request.setProduct(StringUtils.hasText(req.getProduct()) ? req.getProduct() : null);
        request.setSubProduct(StringUtils.hasText(subProduct) ? subProduct : null);
        request.setSourcePage(req.getSource() != null ? req.getSource().name() : null);
        request.setPageSize(req.getPageSize());
        request.setPage(req.getPage());
        return request;
    }

    private BaseSpDto<OpenTagCompanyListReq, OpenTagComp> buildBaseSpDto(OpenTagCompanyListReq request, String spId) {
        BaseSpDto<OpenTagCompanyListReq, OpenTagComp> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(spId);
        baseSpDto.setParameter(request);
        baseSpDto.setPageSize(request.getPageSize());
        baseSpDto.setPageNumber(request.getPage());
        baseSpDto.setResultClass(OpenTagComp.class);
        return baseSpDto;
    }

    public ApiResponse<OpenTagCorpPageDTO> getOverviewOpenTagCompanyListSp(UserAndCorp userAndCorp, OpenTagCorpPageReqDTO req, String spId) {
        OpenTagCompanyListReq openTagCompanyListReq = new OpenTagCompanyListReq();
        openTagCompanyListReq.setCorpId(userAndCorp.getCorpId());
        openTagCompanyListReq.setUserId(userAndCorp.getUserId());
        openTagCompanyListReq.setSearchParam(req.getSearchParam());
        openTagCompanyListReq.setPageSize(req.getPageSize());
        openTagCompanyListReq.setPage(req.getPage());

        BaseSpDto<OpenTagCompanyListReq, OpenTagComp> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(spId);
        baseSpDto.setParameter(openTagCompanyListReq);
        baseSpDto.setPageSize(openTagCompanyListReq.getPageSize());
        baseSpDto.setPageNumber(openTagCompanyListReq.getPage());
        baseSpDto.setResultClass(OpenTagComp.class);
        BaseSpDto<OpenTagCompanyListReq, OpenTagComp> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<OpenTagComp> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchCollectionResponse<OpenTagComp> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);
        Boolean isSubscriberCompOnly = spResultList.stream().findFirst().map(OpenTagComp::getIsSubscriberCompOnly).orElse(Boolean.TRUE);

        result.setNextPage(req.getPage() >= totalPage ? totalPage : req.getPage() + 1);
        result.setCurrentPage(req.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);

        OpenTagCorpPageDTO openTagCorpPageDTO = OpenTagCorpPageDTO.builder().build();
        openTagCorpPageDTO.setIsSubscriberCompOnly(isSubscriberCompOnly);
        openTagCorpPageDTO.setData(result);

        ApiResponse<OpenTagCorpPageDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(openTagCorpPageDTO);

        return apiResponse;
    }

    public ApiResponse<List<DropdownDto>> getTransactionCodeList(TrxCodeDropdownReqDto trxCodeDropdownReqDto) {
        BaseSpDto<TrxCodeDropdownReqDto, DropdownDto> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_TRX_CODE_LIST.getValue());
        baseSpDto.setParameter(trxCodeDropdownReqDto);
        baseSpDto.setResultClass(DropdownDto.class);
        BaseSpDto<TrxCodeDropdownReqDto, DropdownDto> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<DropdownDto> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());

        ApiResponse<List<DropdownDto>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(spResultList);
        return apiResponse;
    }

    public ApiResponse<Map<String, List<String>>> getPaymentTypeList() {
        Map<String, List<String>> transactionCodeMap = Map.of("paymentTypes", corpServiceDataGenerator.generatePaymentTypeList());
        ApiResponse<Map<String, List<String>>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(transactionCodeMap);
        return apiResponse;
    }

    public ApiResponse<Map<String, List<String>>> getTransactionTypeList() {
        Map<String, List<String>> transactionCodeMap = Map.of("transactionTypes", corpServiceDataGenerator.generateTransactionTypeList());
        ApiResponse<Map<String, List<String>>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(transactionCodeMap);
        return apiResponse;
    }

    private String resolveFdSubProductByCorpProvider(String corpId) {
        String provider = corpRepository.findProviderByCorpId(corpId)
                .orElseThrow(() -> new ResourceNotFoundException("Corp Provider Not Found for Corp Id: " + corpId));
        return provider.equals("SG") ? "TD" : "FD";
    }
}
