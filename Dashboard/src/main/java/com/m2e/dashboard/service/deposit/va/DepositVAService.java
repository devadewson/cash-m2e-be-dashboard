//package com.m2e.dashboard.service.deposit.va;
//
//import com.m2e.dashboard.config.UserAndCorp;
//import com.m2e.dashboard.dto.base.DownloadDTO;
//import com.m2e.dashboard.dto.base.DownloadReq;
//import com.m2e.dashboard.dto.common.ApiResponse;
//import com.m2e.dashboard.dto.common.ErrorResponse;
//import com.m2e.dashboard.dto.common.SearchCollectionResponse;
//import com.m2e.dashboard.dto.deposit.va.request.*;
//import com.m2e.dashboard.dto.deposit.va.response.*;
//import com.m2e.dashboard.dto.otherbank.request.deposit.OtherBankDepositAcctListingReqDTO;
//import com.m2e.dashboard.dto.otherbank.response.deposit.OtherBankDepositAcctListingDTO;
//import com.m2e.dashboard.enums.DownloadEnum;
//import com.m2e.dashboard.jms.deposit.ca.request.account.activityinquiry.ESBDepositCAAcctActivityReqDTO;
//import com.m2e.dashboard.projection.DepositAcctDetailPageProjection;
//import com.m2e.dashboard.repository.AccountRepository;
//import com.m2e.dashboard.repository.VaAccountRepository;
//import com.m2e.dashboard.service.AsyncPaginationService;
//import com.m2e.dashboard.sp.deposit.va.request.DepositVATrxHistoryListSpParam;
//import com.m2e.dashboard.sp.va.request.DepositLinkedVASpParam;
//import com.m2e.dashboard.sp.va.request.DepositVASpParam;
//import com.m2e.dashboard.util.DateTimeUtil;
//import com.m2e.model.sso.base.PortfolioVirtualAccount;
//import com.m2e.utils.jdbc.model.dto.BaseSpDto;
//import com.m2e.utils.jdbc.service.BaseSpService;
//import com.m2e.utils.redis.helper.RedisHelper;
//import lombok.extern.slf4j.Slf4j;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_DP_VA_ACCTLIST;
//import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_OB_DP_ACCTLIST;
//import static com.m2e.dashboard.enums.SPEnum.*;
//
//@Service
//@Slf4j
//public class DepositVAService {
//
//    private final BaseSpService baseSpService;
//
//    private final DepositVAFileService fileService;
//
//    private final DepositLinkedVAFileService fileLinkedService;
//
//    private final DepositVATrxHistFileService virtualAccTrxHistFileService;
//
//    private final VaAccountRepository vaAccountRepository;
//
//    private final ModelMapper modelMapper;
//
//    private final AccountRepository accountRepository;
//
//    private final DepositVAESBService depositVAESBService;
//    private final AsyncPaginationService asyncPaginationService;
//    @Autowired
//    private RedisHelper redisHelper;
//
//    @Autowired
//    public DepositVAService(BaseSpService baseSpService, DepositVAFileService fileService, DepositLinkedVAFileService fileLinkedService,
//                            DepositVATrxHistFileService virtualAccTrxHistFileService, VaAccountRepository vaAccountRepository,
//                            ModelMapper modelMapper, AccountRepository accountRepository, DepositVAESBService depositVAESBService, AsyncPaginationService asyncPaginationService) {
//        this.baseSpService = baseSpService;
//        this.fileService = fileService;
//        this.fileLinkedService = fileLinkedService;
//        this.virtualAccTrxHistFileService = virtualAccTrxHistFileService;
//        this.vaAccountRepository = vaAccountRepository;
//        this.modelMapper = modelMapper;
//        this.accountRepository = accountRepository;
//        this.depositVAESBService = depositVAESBService;
//        this.asyncPaginationService = asyncPaginationService;
//    }
//
//
//    public ApiResponse<SearchCollectionResponse<DepositVASearchDto>> getListVaPerCa(UserAndCorp userAndCorp, DepositVASearchReqDTO virtualAccReqDto) {
//        log.info("Current account no : {}",virtualAccReqDto.getCurrAcctNumber());
//        SearchCollectionResponse<DepositVASearchDto> response;
//        response = this.getLinkedListVirtualAccountSp(userAndCorp, virtualAccReqDto);
//        ApiResponse<SearchCollectionResponse<DepositVASearchDto>> apiResponse = new ApiResponse<>();
//        apiResponse.setMsgHeader(new ErrorResponse("0"));
//        apiResponse.setMsgBody(response);
//
//         return apiResponse;
//
//    }
//    public SearchCollectionResponse<DepositVASearchDto> getLinkedListVirtualAccountSp(UserAndCorp userAndCorp, DepositVASearchReqDTO virtualAccReqDto) {
//        log.info("Corp ID LinkedListVirtualAccount : {}", userAndCorp.getCorpId());
//
//        DepositLinkedVASpParam spParam = new DepositLinkedVASpParam();
//        spParam.setUserId(userAndCorp.getUserId());
//        spParam.setCorpId(userAndCorp.getCorpId());
//        spParam.setSearchParam(virtualAccReqDto.getSearchParam());
//        //add parameter
//        spParam.setVirtualAcctNumber(virtualAccReqDto.getVirtualAcctNumber());
//        spParam.setCorporateCode(virtualAccReqDto.getCorporateCode());
//        spParam.setCurrAcctNumber(virtualAccReqDto.getCurrAcctNumber());
//
//        spParam.setPageSize(virtualAccReqDto.getPageSize());
//        spParam.setPage(virtualAccReqDto.getPage());
//
//        BaseSpDto<DepositLinkedVASpParam, DepositVASearchDto> baseSpDto = new BaseSpDto<>();
//        baseSpDto.setSpId(SP_DP_LINKED_VA_ACCT_LIST.getValue());
//        baseSpDto.setParameter(spParam);
//        baseSpDto.setPageSize(virtualAccReqDto.getPageSize());
//        baseSpDto.setPageNumber(virtualAccReqDto.getPage());
//        baseSpDto.setResultClass(DepositVASearchDto.class);
//
//        BaseSpDto<DepositLinkedVASpParam, DepositVASearchDto> resultBaseSpDto = baseSpService.execute(baseSpDto);
//        List<DepositVASearchDto> spResultList = resultBaseSpDto.getResults().stream().toList();
//
//        int totalPage = Optional.of(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
//        int totalRecordCount = Optional.of(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);
//
//        SearchCollectionResponse<DepositVASearchDto> result = new SearchCollectionResponse<>();
//        result.setNextPage(virtualAccReqDto.getPage() >= resultBaseSpDto.getTotalPage() ?
//                resultBaseSpDto.getTotalPage() : virtualAccReqDto.getPage() + 1);
//        result.setCurrentPage(virtualAccReqDto.getPage());
//        result.setTotalPages(totalPage);
//        result.setTotalRecordCount(totalRecordCount);
//        result.setData(spResultList);
//
//        return result;
//
//    }
//
//    public DownloadDTO downloadVAFiles(UserAndCorp userAndCorp, DownloadReq virtualAccDownloadReqDto){
//        DownloadDTO result;
//
//        switch(virtualAccDownloadReqDto.getReportType()) {
//            case DEPOSIT_VA_ACCOUNTLISTING_RPT_TYPE -> {
//                DepositVADownloadReqDTO requestData = (DepositVADownloadReqDTO) virtualAccDownloadReqDto;
//                result = downloadVirtualAccount(userAndCorp,requestData);
//            }
//            case DEPOSIT_VA_TRXHISTORIES_RPT_TYPE -> {
//                DepositVATransHistDownloadReqDTO requestData = (DepositVATransHistDownloadReqDTO) virtualAccDownloadReqDto;
//                result = downloadVirtualAccTrxHist(userAndCorp,requestData);
//            }
//            case DEPOSIT_LINKED_VA_ACCOUNT_LISTING_RPT_TYPE -> {
//                DepositVADownloadReqDTO requestData = (DepositVADownloadReqDTO) virtualAccDownloadReqDto;
//                result = downloadLinkedVirtualAccount(userAndCorp,requestData);
//            }
//            default -> throw new IllegalStateException("Unexpected value: " + virtualAccDownloadReqDto.getReportType());
//        }
//
//        return result;
//    }
//
//    public DownloadDTO downloadVirtualAccount(UserAndCorp userAndCorp, DepositVADownloadReqDTO virtualAccDownloadReqDto) throws IllegalArgumentException {
//        DownloadDTO result = new DownloadDTO();
//        DepositVAAccSearchReqDTO accountListReq = new DepositVAAccSearchReqDTO();
//        accountListReq.setPage(1);
//        accountListReq.setPageSize(Integer.MAX_VALUE);
//        accountListReq.setOpenTaggedCorpId(userAndCorp.getCorpId());
//        accountListReq.setCorpCifNo(virtualAccDownloadReqDto.getCorpCifNo());
//        DepositVASummaryResp spResultList = this.getListVirtualAccountSp(userAndCorp, accountListReq);
//        List<DepositVASubSearchDTO> virtualAccRespDtoList = spResultList.getDatas().getData();
//
//        switch (virtualAccDownloadReqDto.getFormat()) {
//            case CSV -> {
//                result.setData(fileService.generateCsv(virtualAccRespDtoList));
//                result.setFileName(DownloadEnum.DEPOSIT_VA_ACCTLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
//            }
//            case PDF -> {
//                result.setData(fileService.generatePdf(virtualAccRespDtoList));
//                result.setFileName(DownloadEnum.DEPOSIT_VA_ACCTLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
//            }
//            case TXT -> {
//                result.setData(fileService.generateTxt(virtualAccRespDtoList));
//                result.setFileName(DownloadEnum.DEPOSIT_VA_ACCTLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
//            }
//            default -> throw new IllegalArgumentException("Report format for Download Virtual Account is undefined");
//        }
//        return result;
//    }
//
//    public DownloadDTO downloadLinkedVirtualAccount(UserAndCorp userAndCorp, DepositVADownloadReqDTO virtualAccDownloadReqDto){
//        DownloadDTO result = new DownloadDTO();
//        DepositVASearchReqDTO accountListReq = new DepositVASearchReqDTO();
//        accountListReq.setPage(1);
//        accountListReq.setPageSize(Integer.MAX_VALUE);
//        accountListReq.setCorporateCode(virtualAccDownloadReqDto.getCorporateCode());
//        accountListReq.setVirtualAcctNumber(virtualAccDownloadReqDto.getVirtualAcctNumber());
//        accountListReq.setCurrAcctNumber(virtualAccDownloadReqDto.getCurrAcctNumber());
//
//        SearchCollectionResponse<DepositVASearchDto> spLinkedVaList = this.getLinkedListVirtualAccountSp(userAndCorp, accountListReq);
//        List<DepositVASearchDto> virtualAccRespDtoList = Optional.ofNullable(spLinkedVaList)
//                .map(SearchCollectionResponse::getData)
//                .orElse(new ArrayList<>());
//
//        switch (virtualAccDownloadReqDto.getFormat()) {
//            case CSV -> {
//                result.setData(fileLinkedService.generateCsv(virtualAccRespDtoList));
//                result.setFileName(DownloadEnum.DEPOSIT_LINKED_VA_ACCT_LISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
//            }
//            case PDF -> {
//                result.setData(fileLinkedService.generatePdf(virtualAccRespDtoList));
//                result.setFileName(DownloadEnum.DEPOSIT_LINKED_VA_ACCT_LISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
//            }
//            case TXT -> {
//                result.setData(fileLinkedService.generateTxt(virtualAccRespDtoList));
//                result.setFileName(DownloadEnum.DEPOSIT_LINKED_VA_ACCT_LISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
//            }
//            default -> throw new IllegalArgumentException("Report format for Download Linked Virtual Account is undefined");
//        }
//        return result;
//    }
//
//    public ApiResponse<DepositVADTO> getVirtualAccountDetails(DepositVAReqDTO virtualAccReqDto) {
//        ApiResponse<DepositVADTO> response = new ApiResponse<>();
//        log.info("Virtual account No {} ",virtualAccReqDto.getVirtualAcctNumber());
//        //change to SP
//        BaseSpDto<DepositVAReqDTO, DepositVADTO> baseSpDto = new BaseSpDto<>();
//        baseSpDto.setSpId(SP_DP_VA_ACCT_DTLS.getValue());
//        baseSpDto.setParameter(virtualAccReqDto);
//        baseSpDto.setResultClass(DepositVADTO.class);
//        DepositVADTO vaAccDetails;
//        try{
//            vaAccDetails = baseSpService.execute(baseSpDto).getResults().get(0);
//            //add format date created & closed
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
//            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
//
//            if(!vaAccDetails.getDateCreatedInt().equals("0")
//                    && !vaAccDetails.getDateCreatedInt().isEmpty()
//                    && !vaAccDetails.getDateCreatedInt().equals("00000000")) {
//             String formattedInput = DateTimeUtil.ensureTwoDigitFormat(vaAccDetails.getDateCreatedInt());
//             LocalDate dateCreated = LocalDate.parse(formattedInput, formatter);
//             vaAccDetails.setDateCreated(dateCreated.format(outputFormatter));
//            }else{
//                vaAccDetails.setDateCreated("");
//            }
//
//            if(!vaAccDetails.getDateClosed().equals("0")
//                        && !vaAccDetails.getDateClosed().isEmpty()
//                        && !vaAccDetails.getDateClosed().equals("00000000")) {
//                String formattedInput = DateTimeUtil.ensureTwoDigitFormat(vaAccDetails.getDateClosed());
//                LocalDate dateClosed = LocalDate.parse(formattedInput, formatter);
//                vaAccDetails.setDateClosed(dateClosed.format(outputFormatter));
//            }else{
//                vaAccDetails.setDateClosed("");
//            }
//            vaAccDetails.setDateUpdated(DateTimeUtil.getCurrentDateTime());
//
//        } catch (IndexOutOfBoundsException  e) {
//            vaAccDetails = new DepositVADTO();
//            log.error("No result found for SP: {}", baseSpDto.getSpId());
//        }
//
//        response.setMsgBody(vaAccDetails);
//        response.setMsgHeader(new ErrorResponse("0"));
//
//        return response;
//    }
//
//    public ApiResponse<DepositVATrxHistoryDTO> getTransHistListVirtualAccount(UserAndCorp userAndCorp, DepositVATransHistSearchReqDTO transHistVaSearchReqDto) {
//        ApiResponse<DepositVATrxHistoryDTO> apiResponse = new ApiResponse<>();
//        DepositVATrxHistoryDTO depositVATrxHistoryDTO = new DepositVATrxHistoryDTO();
//        //get account currency and provider to pass to ESB
//        Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetails(transHistVaSearchReqDto.getParentAcctNumber(), transHistVaSearchReqDto.getParentAcctCcy());
//        //check if account exists or not
//        if (accountData.isEmpty()) {
//            log.error("Account Not Found for: " + transHistVaSearchReqDto.getParentAcctNumber());
//            return new ApiResponse<>(new ErrorResponse(), depositVATrxHistoryDTO);
//        }
//
//        ESBDepositCAAcctActivityReqDTO esbDepositCAAcctActivityReqDTO = new ESBDepositCAAcctActivityReqDTO();
//        esbDepositCAAcctActivityReqDTO.setAccountNo(transHistVaSearchReqDto.getParentAcctNumber());
//        esbDepositCAAcctActivityReqDTO.setTrxCode(transHistVaSearchReqDto.getTrxCode());
//        esbDepositCAAcctActivityReqDTO.setFilters(transHistVaSearchReqDto.getFilters());
//        esbDepositCAAcctActivityReqDTO.setCurrency(transHistVaSearchReqDto.getParentAcctCcy());
//        esbDepositCAAcctActivityReqDTO.setSearchParam(transHistVaSearchReqDto.getSearchParam());
//        esbDepositCAAcctActivityReqDTO.setPage(transHistVaSearchReqDto.getPage());
//        esbDepositCAAcctActivityReqDTO.setPageSize(transHistVaSearchReqDto.getPageSize());
//        try {
//        //get trx hist data from ESB
//        depositVATrxHistoryDTO = depositVAESBService.getDepositVATrxHistory_MY(esbDepositCAAcctActivityReqDTO).getMsgBody();
//        // set virtual account name that is stored on TBL_ACCT_VA after ESB call
//        if (depositVATrxHistoryDTO != null && depositVATrxHistoryDTO.getDatas() != null && depositVATrxHistoryDTO.getDatas().getData() != null) {
//            depositVATrxHistoryDTO.getDatas().getData()
//                    .forEach(dto -> {
//                        //fetch virtual account details from TBL_ACCT_VA
//                        Optional<PortfolioVirtualAccount> vaAccDetails = vaAccountRepository.findByVaAccNo(dto.getVirtualAcctNumber());
//                        String vaAccountName = vaAccDetails.map(PortfolioVirtualAccount::getVirtualAccountName).orElse("");
//                        dto.setVirtualAcctName(vaAccountName);
//                    });
//        }
//        depositVATrxHistoryDTO = depositVATrxHistoryDTO != null ? depositVATrxHistoryDTO : new DepositVATrxHistoryDTO();
//        } catch (Exception e) {
//            depositVATrxHistoryDTO = depositVATrxHistoryDTO != null ? depositVATrxHistoryDTO : new DepositVATrxHistoryDTO();
//            depositVATrxHistoryDTO.setDatas(new SearchCollectionResponse<>());
//        }
//
//        // get trx hist data from SP
//        SearchCollectionResponse<DepositVATransHistSearchDTO> spDepositTrxHistoryResList = this.getDepositVATrxHistorySp(userAndCorp, transHistVaSearchReqDto);
//
//        List<DepositVATransHistSearchDTO> esbDataList = Optional.ofNullable(depositVATrxHistoryDTO.getDatas()).map(SearchCollectionResponse::getData).orElse(new ArrayList<>());
//        List<DepositVATransHistSearchDTO> spDataList = Optional.of(spDepositTrxHistoryResList).map(SearchCollectionResponse::getData).orElse(new ArrayList<>());
//
//        // if esb data is equal or less than pageSize, then return it to front end
//        if (esbDataList.size() <= transHistVaSearchReqDto.getPageSize() && spDataList.isEmpty()) {
//            apiResponse.setMsgHeader(new ErrorResponse("0"));
//            apiResponse.setMsgBody(depositVATrxHistoryDTO);
//            return apiResponse;
//        } else if (!esbDataList.isEmpty() && !spDataList.isEmpty()) { // else combine both esb and sp data, with esb comes first
//            List<DepositVATransHistSearchDTO> fusedDepositTrxHistoryDTOList = new ArrayList<>(esbDataList);
//            fusedDepositTrxHistoryDTOList.addAll(spDataList);
//            //sort by desc date after combining both esb and sp data
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH);
//            List<DepositVATransHistSearchDTO> sortedByDate = fusedDepositTrxHistoryDTOList.stream()
//                    .sorted(Comparator.comparing(
//                            dto -> LocalDateTime.parse(dto.getDate(), formatter),
//                            Comparator.reverseOrder()
//                    ))
//                    .toList();
//            //paginate data after combining and sorting by date
//            List<DepositVATransHistSearchDTO> paginatedFusedData = asyncPaginationService.paginateData(sortedByDate, transHistVaSearchReqDto.getPageSize(), transHistVaSearchReqDto.getPage());
//            log.info("paginatedFusedData --> {}", paginatedFusedData);
//
//            // total record count of both esb and sp data
//            int esbTotalRecordCount = Optional.ofNullable(depositVATrxHistoryDTO.getDatas())
//                    .map(SearchCollectionResponse::getTotalRecordCount)
//                    .orElse(0);
//            int spTotalRecordCount = Optional.of(spDepositTrxHistoryResList)
//                    .map(SearchCollectionResponse::getTotalRecordCount)
//                    .orElse(0);
//            int totalRecordCount = esbTotalRecordCount + spTotalRecordCount;
//
//            int totalPage = (int) Math.ceil((double) totalRecordCount / transHistVaSearchReqDto.getPageSize());
//
//            SearchCollectionResponse<DepositVATransHistSearchDTO> fusedDepositTrxHistoryRes = new SearchCollectionResponse<>();
//            fusedDepositTrxHistoryRes.setNextPage(transHistVaSearchReqDto.getPage() < totalPage ? transHistVaSearchReqDto.getPage() + 1 : -1);
//            fusedDepositTrxHistoryRes.setCurrentPage(transHistVaSearchReqDto.getPage());
//            fusedDepositTrxHistoryRes.setTotalPages(totalPage);
//            fusedDepositTrxHistoryRes.setTotalRecordCount(totalRecordCount);
//            fusedDepositTrxHistoryRes.setData(paginatedFusedData);
//
//            depositVATrxHistoryDTO.setDatas(fusedDepositTrxHistoryRes);
//
//            apiResponse.setMsgHeader(new ErrorResponse("0"));
//            apiResponse.setMsgBody(depositVATrxHistoryDTO);
//            return apiResponse;
//        } else { //else only return spDataList
//            depositVATrxHistoryDTO.setDatas(spDepositTrxHistoryResList);
//            apiResponse.setMsgHeader(new ErrorResponse("0"));
//            apiResponse.setMsgBody(depositVATrxHistoryDTO);
//            return apiResponse;
//        }
//    }
//
//    public SearchCollectionResponse<DepositVATransHistSearchDTO> getDepositVATrxHistorySp(UserAndCorp userAndCorp, DepositVATransHistSearchReqDTO request) {
//        DepositVATrxHistoryListSpParam vaTrxHistoryListSpParam = new DepositVATrxHistoryListSpParam();
//        vaTrxHistoryListSpParam.setParentAcctNumber(request.getParentAcctNumber());
//        vaTrxHistoryListSpParam.setVirtualAcctNumber(request.getVirtualAcctNumber());
//        vaTrxHistoryListSpParam.setTrxCode(StringUtils.hasText(request.getTrxCode()) ? request.getTrxCode() : null);
//        vaTrxHistoryListSpParam.setSearchParam(StringUtils.hasText(request.getSearchParam()) ? request.getSearchParam() : null);
//        vaTrxHistoryListSpParam.setUserId(userAndCorp.getUserId());
//        vaTrxHistoryListSpParam.setCorpId(userAndCorp.getCorpId());
//        vaTrxHistoryListSpParam.setPageSize(request.getPageSize());
//        vaTrxHistoryListSpParam.setPage(request.getPage());
//        if (request.getFilters() != null) {
//            vaTrxHistoryListSpParam.setStartDateTime(StringUtils.hasText(request.getFilters().getStartDate()) ? DateTimeUtil.convert12HourDateTime(request.getFilters().getStartDate(), request.getFilters().getStartTime(), true) : null);
//            vaTrxHistoryListSpParam.setEndDateTime(StringUtils.hasText(request.getFilters().getEndDate()) ? DateTimeUtil.convert12HourDateTime(request.getFilters().getEndDate(), request.getFilters().getEndTime(), false) : null);
//            vaTrxHistoryListSpParam.setAmtFrom(StringUtils.hasText(request.getFilters().getAmtFrom()) ? request.getFilters().getAmtFrom() : null);
//            vaTrxHistoryListSpParam.setAmtTo(StringUtils.hasText(request.getFilters().getAmtTo()) ? request.getFilters().getAmtTo() : null);
//        }
//
//        BaseSpDto<DepositVATrxHistoryListSpParam, DepositVATransHistSearchDTO> baseSpDto = new BaseSpDto<>();
//        baseSpDto.setSpId(SP_DP_VA_TRXN_HIST.getValue());
//        baseSpDto.setParameter(vaTrxHistoryListSpParam);
//        baseSpDto.setPageSize(vaTrxHistoryListSpParam.getPageSize());
//        baseSpDto.setPageNumber(vaTrxHistoryListSpParam.getPage());
//        baseSpDto.setResultClass(DepositVATransHistSearchDTO.class);
//        BaseSpDto<DepositVATrxHistoryListSpParam, DepositVATransHistSearchDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
//        List<DepositVATransHistSearchDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
//                .map(BaseSpDto::getResults)
//                .orElse(Collections.emptyList());
//
//        SearchCollectionResponse<DepositVATransHistSearchDTO> result = new SearchCollectionResponse<>();
//
//        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
//        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);
//
//        result.setNextPage(vaTrxHistoryListSpParam.getPage() >= totalPage ? totalPage : vaTrxHistoryListSpParam.getPage() + 1);
//        result.setCurrentPage(vaTrxHistoryListSpParam.getPage());
//        result.setTotalPages(totalPage);
//        result.setTotalRecordCount(totalRecordCount);
//        result.setData(spResultList);
//        return result;
//    }
//
//    public DownloadDTO downloadVirtualAccTrxHist(UserAndCorp userAndCorp, DepositVATransHistDownloadReqDTO virtualAccDownloadReqDto){
//        DownloadDTO result = new DownloadDTO();
//        DepositVATransHistSearchReqDTO transHistVaSearchReqDto = new DepositVATransHistSearchReqDTO();
//        transHistVaSearchReqDto.setTrxCode(virtualAccDownloadReqDto.getTrxCode());
//        transHistVaSearchReqDto.setVirtualAcctNumber(virtualAccDownloadReqDto.getVirtualAcctNumber());
//        transHistVaSearchReqDto.setPage(1);
//        transHistVaSearchReqDto.setPageSize(Integer.MAX_VALUE);
//
//        SearchCollectionResponse<DepositVATransHistSearchDTO> spDepositTrxHistoryResList = this.getDepositVATrxHistorySp(userAndCorp, transHistVaSearchReqDto);
//        List<DepositVATransHistSearchDTO> transHistVaRespDtoList = Optional.ofNullable(spDepositTrxHistoryResList)
//                .map(SearchCollectionResponse::getData)
//                .orElse(new ArrayList<>());
//
//        switch (virtualAccDownloadReqDto.getFormat()) {
//            case CSV -> {
//                result.setData(virtualAccTrxHistFileService.generateCsv(transHistVaRespDtoList, virtualAccDownloadReqDto.getHeaders()));
//                result.setFileName(DownloadEnum.DEPOSIT_VA_TRXHISTORIES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
//            }
//            case PDF -> {
//                result.setData(virtualAccTrxHistFileService.generatePdf(transHistVaRespDtoList, virtualAccDownloadReqDto.getHeaders()));
//                result.setFileName(DownloadEnum.DEPOSIT_VA_TRXHISTORIES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
//            }
//            case TXT -> {
//                result.setData(virtualAccTrxHistFileService.generateTxt(transHistVaRespDtoList, virtualAccDownloadReqDto.getHeaders()));
//                result.setFileName(DownloadEnum.DEPOSIT_VA_TRXHISTORIES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
//            }
//            default -> throw new IllegalArgumentException("Report format is undefined");
//        }
//        return result;
//    }
//
//
//    public DepositVASummaryResp getListVirtualAccountSp(UserAndCorp userAndCorp, DepositVAAccSearchReqDTO virtualAccReqDto) {
//        log.info("Current corp ID : {}", virtualAccReqDto.getOpenTaggedCorpId());
//
//        DepositVASpParam spParam = new DepositVASpParam();
//        spParam.setUserId(userAndCorp.getUserId());
//        spParam.setCorpId(userAndCorp.getCorpId());
//        spParam.setSearchParam(virtualAccReqDto.getSearchParam());
//        //add parameter
//        spParam.setVirtualAcctNumber(virtualAccReqDto.getVirtualAcctNumber());
//        spParam.setCorporateCode(virtualAccReqDto.getCorporateCode());
//        //add corpCifNo
//        spParam.setCorpCifNo(virtualAccReqDto.getCorpCifNo());
//
//        spParam.setPageSize(virtualAccReqDto.getPageSize());
//        spParam.setPage(virtualAccReqDto.getPage());
//
//        BaseSpDto<DepositVASpParam, DepositVARawSearchDTO> baseSpDto = new BaseSpDto<>();
//        baseSpDto.setSpId(SP_DP_VA_ACCT_LIST.getValue());
//        baseSpDto.setParameter(spParam);
//        baseSpDto.setPageSize(virtualAccReqDto.getPageSize());
//        baseSpDto.setPageNumber(virtualAccReqDto.getPage());
//        baseSpDto.setResultClass(DepositVARawSearchDTO.class);
//
//        BaseSpDto<DepositVASpParam, DepositVARawSearchDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
//        List<DepositVARawSearchDTO> spResultList = resultBaseSpDto.getResults().stream().toList();
//
//        Map<String, List<DepositVARawSearchDTO>> groupedData = spResultList.stream()
//                .collect(Collectors.groupingBy(
//                        item -> item.getCurrAcctName() + "\t" + item.getCurrAcctNumber() + "\t" + item.getTotVa()
//                ));
//
//        List<DepositVASubSearchDTO> results = groupedData.entrySet().stream()
//                .map(entry -> {
//                    String[] keyParts = entry.getKey().split("\t");
//                    String currentAccName = keyParts[0];
//                    String currentAccNo = keyParts[1];
//                    String totVa = keyParts[2];
//
//                    List<DepositVASearchDto> virtualAccounts = entry.getValue().stream()
//                            .map(rawData -> new DepositVASearchDto(
//                                    rawData.getCorpCode(),
//                                    rawData.getVirtualAcctName(),
//                                    rawData.getVirtualAcctNumber(),
//                                    rawData.getVirtualAcctRefNumber()
//                            ))
//                            .toList();
//
//                    return new DepositVASubSearchDTO(
//                            currentAccNo,
//                            currentAccName,
//                            Optional.ofNullable(totVa).filter(s -> !s.isEmpty()).map(Integer::valueOf).orElse(0),
//                            virtualAccounts
//                    );
//                })
//                .toList();
//
//        int totalPage = Optional.of(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
//        int totalRecordCount = Optional.of(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);
//
//        SearchCollectionResponse<DepositVASubSearchDTO> result = new SearchCollectionResponse<>();
//        result.setNextPage(virtualAccReqDto.getPage() >= resultBaseSpDto.getTotalPage() ?
//                resultBaseSpDto.getTotalPage() : virtualAccReqDto.getPage() + 1);
//        result.setCurrentPage(virtualAccReqDto.getPage());
//        result.setTotalPages(totalPage);
//        result.setTotalRecordCount(totalRecordCount);
//        result.setData(results);
//
//        DepositVASummaryResp depositVASummaryResp = new DepositVASummaryResp();
//        depositVASummaryResp.setCorpCifNo(virtualAccReqDto.getCorpCifNo());
//        depositVASummaryResp.setDatas(result);
//
//        return depositVASummaryResp;
//
//    }
//    public ApiResponse<DepositVASummaryResp> getListVirtualAccount(UserAndCorp userAndCorp, DepositVAAccSearchReqDTO virtualAccReqDto) {
//        log.info("Current corp ID : {}", virtualAccReqDto.getOpenTaggedCorpId());
//        ApiResponse<DepositVASummaryResp> apiResponse;
//        DepositVASummaryResp result = null;
//
//        apiResponse = getAcctFromCache(userAndCorp, virtualAccReqDto);
//
//        if (apiResponse == null) {
//            apiResponse = new ApiResponse<>();
//             result = this.getListVirtualAccountSp(userAndCorp, virtualAccReqDto);
//             saveToCache(userAndCorp, virtualAccReqDto,result);
//        }
//
//        apiResponse.setMsgHeader(new ErrorResponse("0"));
//        apiResponse.setMsgBody(result);
//
//        return apiResponse;
//
//    }
//    public ApiResponse<DepositVAStatsDTO> getMainVirtualAccountDetails(UserAndCorp userAndCorp, DepositVAStatsReqDTO depositVAStatsReqDTO) {
//        ApiResponse<DepositVAStatsDTO> response = new ApiResponse<>();
//        log.info("Corp id {} ", userAndCorp.getCorpId());
//        log.info("Corp Cif No {} ", depositVAStatsReqDTO.getCorpCifNo());
//        removeAllFromCache(userAndCorp);
//        depositVAStatsReqDTO.setCorpId(userAndCorp.getCorpId());
//        BaseSpDto<DepositVAStatsReqDTO, DepositVAStatsDTO> baseSpDto = new BaseSpDto<>();
//        baseSpDto.setSpId(SP_DP_VA_ACCT_STATS.getValue());
//        baseSpDto.setParameter(depositVAStatsReqDTO);
//        baseSpDto.setResultClass(DepositVAStatsDTO.class);
//        DepositVAStatsDTO vaMainResp = baseSpService.execute(baseSpDto).getResults().get(0);
//        response.setMsgBody(vaMainResp);
//        response.setMsgHeader(new ErrorResponse("0"));
//
//        return response;
//    }
//
//    public ApiResponse<List<DepositVADropdownDTO>> getDropdownVACorpCode(UserAndCorp userAndCorp, DepositVADropdownReqDTO depositVADropdownReqDTO) {
//        log.info("Current Acc No: {}", depositVADropdownReqDTO.getCurrAcctNumber());
//        List<DepositVADropdownDTO> vaAccRespDtoList = new ArrayList<>();
//        List<PortfolioVirtualAccount> vaAccount;
//        if(!depositVADropdownReqDTO.getCurrAcctNumber().isEmpty()){
//           vaAccount  = vaAccountRepository.findByParentAcc(depositVADropdownReqDTO.getCurrAcctNumber());
//        } else {
//            vaAccount  = vaAccountRepository.findByCorpId(userAndCorp.getCorpId(), userAndCorp.getUserId());
//        }
//
//        List<String> virtualAccNo = vaAccount.stream()
//                .map(PortfolioVirtualAccount::getVirtualAccount)
//                .distinct()
//                .toList();
//
//        List<String> corpCode = vaAccount.stream()
//                .map(PortfolioVirtualAccount::getCorpCode)
//                .distinct()
//                .toList();
//
//        DepositVADropdownDTO depositVADropdownDTO = new DepositVADropdownDTO();
//        depositVADropdownDTO.setVirtualAcctNumber(virtualAccNo);
//        depositVADropdownDTO.setCorpCode(corpCode);
//        vaAccRespDtoList.add(depositVADropdownDTO);
//
//        ApiResponse<List<DepositVADropdownDTO>> apiResponse = new ApiResponse<>();
//        apiResponse.setMsgHeader(new ErrorResponse("0"));
//        apiResponse.setMsgBody(vaAccRespDtoList);
//
//        return apiResponse;
//
//    }
//
//    public ApiResponse<List<DepositCADropdownDTO>> getDropdownCa(UserAndCorp userAndCorp) {
//        log.info("Corp Id & User Id {} {} ", userAndCorp.getCorpId(), userAndCorp.getUserId());
//
//        List<DepositCADropdownDTO> listOfCurrAcct  = vaAccountRepository.findCaByCorpAndUserId(userAndCorp.getCorpId(), userAndCorp.getUserId());
//
//        ApiResponse<List<DepositCADropdownDTO>> apiResponse = new ApiResponse<>();
//        apiResponse.setMsgHeader(new ErrorResponse("0"));
//        apiResponse.setMsgBody(listOfCurrAcct);
//        return apiResponse;
//    }
//
//    public ApiResponse<List<DepositVATrxHistDropdownDTO>> getDropdownVa(UserAndCorp userAndCorp, DepositVADropdownReqDTO depositVADropdownReqDTO) {
//        log.info("Corp Id & User Id {} {} ", userAndCorp.getCorpId(), userAndCorp.getUserId());
//        log.info("Current Acc No: {}", depositVADropdownReqDTO.getCurrAcctNumber());
//        List<PortfolioVirtualAccount> vaAccount;
//        if(!depositVADropdownReqDTO.getCurrAcctNumber().isEmpty()){
//            vaAccount  = vaAccountRepository.findByParentAcc(depositVADropdownReqDTO.getCurrAcctNumber());
//        } else {
//            vaAccount  = vaAccountRepository.findByCorpId(userAndCorp.getCorpId(), userAndCorp.getUserId());
//        }
//        List<DepositVATrxHistDropdownDTO> vaAcctListDropdown = new ArrayList<>();
//
//        for (PortfolioVirtualAccount account : vaAccount) {
//            DepositVATrxHistDropdownDTO dto = new DepositVATrxHistDropdownDTO(
//                    account.getVirtualAccountName(),
//                    account.getVirtualAccount()
//            );
//            vaAcctListDropdown.add(dto);
//        }
//
//        ApiResponse<List<DepositVATrxHistDropdownDTO>> apiResponse = new ApiResponse<>();
//        apiResponse.setMsgHeader(new ErrorResponse("0"));
//        apiResponse.setMsgBody(vaAcctListDropdown);
//
//        return apiResponse;
//
//    }
//    public void removeAllFromCache(UserAndCorp userAndCorp){
//        String redisKey = redisHelper.createCacheKey(BD_AM_DP_VA_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_*");
//        redisHelper.deleteCachesWithPattern(redisKey);
//        log.info("removed cache for {}", redisKey);
//    }
//    private ApiResponse<DepositVASummaryResp> getAcctFromCache(UserAndCorp userAndCorp, DepositVAAccSearchReqDTO reqDTO) {
//        String redisKey = redisHelper.createCacheKey(BD_AM_DP_VA_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + "_" + reqDTO.getCorpCifNo() + "_"  + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
//        return (ApiResponse<DepositVASummaryResp>) redisHelper.getFromCache(redisKey, ApiResponse.class);
//    }
//
//    private void saveToCache(UserAndCorp userAndCorp, DepositVAAccSearchReqDTO reqDTO, DepositVASummaryResp result) {
//        String redisKey = redisHelper.createCacheKey(BD_AM_DP_VA_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + "_" + reqDTO.getCorpCifNo() + "_"  + reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
//        redisHelper.save(redisKey, result);
//    }
//}
