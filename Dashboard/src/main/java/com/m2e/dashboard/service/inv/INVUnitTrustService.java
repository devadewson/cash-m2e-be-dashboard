package com.m2e.dashboard.service.inv;

import com.m2e.cts.PortfolioActivityUnitTrust;
import com.m2e.cts.PortfolioUnitTrust;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.investment.request.*;
import com.m2e.dashboard.dto.investment.response.*;
import com.m2e.dashboard.enums.DownloadEnum;
import com.m2e.dashboard.repository.PortfolioActivityUTRepository;
import com.m2e.dashboard.repository.UnitTrustFundRepository;
import com.m2e.dashboard.sp.investment.request.INVUnitTrustFundStatsSpParam;
import com.m2e.dashboard.sp.investment.request.INVUnitTrustSpParam;
import com.m2e.dashboard.sp.investment.request.INVUnitTrustStatsSpParam;
import com.m2e.dashboard.sp.investment.request.INVUnitTrustTrxHistSpParam;
import com.m2e.dashboard.sp.investment.response.INVUnitTrustSearchSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.m2e.dashboard.enums.AcctRedisKeyEnum.*;
import static com.m2e.dashboard.enums.SPEnum.*;
import static org.springframework.data.domain.Sort.Order.desc;

@Service
@Slf4j
public class INVUnitTrustService {

    private final BaseSpService baseSpService;
    private final INVUnitTrustFileService fileService;
    private final INVUnitTrustFundFileService fundFileService;
    private final INVUnitTrustFundHistFileService fundHistFileService;
    private final UnitTrustFundRepository utRepository;
    private final PortfolioActivityUTRepository activityUTRepository;
    private final ModelMapper modelMapper;
    @Autowired
    private RedisHelper redisHelper;
    public static final String INV_KEY = "INV_KEY";

    @Autowired
    public INVUnitTrustService(BaseSpService baseSpService, INVUnitTrustFileService fileService, INVUnitTrustFundFileService fundFileService,
                               INVUnitTrustFundHistFileService fundHistFileService, UnitTrustFundRepository utRepository,
                               PortfolioActivityUTRepository activityUTRepository, ModelMapper modelMapper) {
        this.baseSpService = baseSpService;
        this.fileService = fileService;
        this.fundFileService = fundFileService;
        this.fundHistFileService = fundHistFileService;
        this.utRepository = utRepository;
        this.activityUTRepository = activityUTRepository;
        this.modelMapper = modelMapper;
    }

    public ApiResponse<INVUnitTrustStatsDTO> getINVUTAccountStats(UserAndCorp userAndCorp, INVUnitTrustStatReqDTO utStatReq) {
        ApiResponse<INVUnitTrustStatsDTO> response = new ApiResponse<>();
        removeAllFromCache(userAndCorp);

        log.info("Corp id {} ", userAndCorp.getCorpId());
        log.info("Corp Cif No {} ", utStatReq.getCorpCifNo());
        BaseSpDto<INVUnitTrustStatsSpParam, INVUnitTrustStatsDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_IV_UT_ACCT_STATS.getValue());
        INVUnitTrustStatsSpParam spParam = new INVUnitTrustStatsSpParam();
        spParam.setCorpId(userAndCorp.getCorpId());
        spParam.setUserId(userAndCorp.getUserId());
        spParam.setCorpCifNo(utStatReq.getCorpCifNo());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(INVUnitTrustStatsDTO.class);
        INVUnitTrustStatsDTO utMainResp = baseSpService.execute(baseSpDto).getResults().get(0);

        response.setMsgBody(utMainResp);
        response.setMsgHeader(new ErrorResponse("0"));

        return response;
    }

    public ApiResponse<INVUnitTrustSearchDTO> getINVUTAccountLists(UserAndCorp userAndCorp, AccountListReq unitTrustReqDto) {
        log.info("Unit Trust account corp ID : {}", unitTrustReqDto.getOpenTaggedCorpId());
        log.info("unitTrustReqDto :: "+ unitTrustReqDto);
        ApiResponse<INVUnitTrustSearchDTO> apiResponse = new ApiResponse<>();

        apiResponse = getAcctFromCache(userAndCorp, unitTrustReqDto);

        if (apiResponse == null) {
            apiResponse = new ApiResponse<>();
            INVUnitTrustSpParam spParam = new INVUnitTrustSpParam();
            spParam.setUserId(userAndCorp.getUserId());
            spParam.setCorpId(userAndCorp.getCorpId());
            spParam.setCorpCifNo(unitTrustReqDto.getCorpCifNo());
            spParam.setSearchParam(unitTrustReqDto.getSearchParam());
            spParam.setPageSize(unitTrustReqDto.getPageSize());
            spParam.setPage(unitTrustReqDto.getPage());

            BaseSpDto<INVUnitTrustSpParam, INVUnitTrustSearchSpDTO> baseSpDto = new BaseSpDto<>();
            baseSpDto.setSpId(SP_IV_UT_ACCT_LIST.getValue());
            baseSpDto.setParameter(spParam);
            baseSpDto.setPageSize(spParam.getPageSize());
            baseSpDto.setPageNumber(spParam.getPage());
            baseSpDto.setResultClass(INVUnitTrustSearchSpDTO.class);

            log.info("baseSpDto PageSize :: " + baseSpDto.getPageSize());

            BaseSpDto<INVUnitTrustSpParam, INVUnitTrustSearchSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
            log.info("resultBaseSpDto :: " + resultBaseSpDto.getTotalRecord());
            List<INVUnitTrustSearchSpDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                    .map(BaseSpDto::getResults)
                    .orElse(Collections.emptyList());
            SearchCollectionResponse<INVUnitTrustSearchSpDTO> result = new SearchCollectionResponse<>();

            int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
            int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

            result.setNextPage(spParam.getPage() >= totalPage ? totalPage : spParam.getPage() + 1);
            result.setCurrentPage(spParam.getPage());
            result.setTotalPages(totalPage);
            result.setTotalRecordCount(totalRecordCount);
            result.setData(spResultList);

            INVUnitTrustSearchDTO invUnitTrustSearchDTO = new INVUnitTrustSearchDTO();
            invUnitTrustSearchDTO.setCorpCifNo(unitTrustReqDto.getCorpCifNo());
            invUnitTrustSearchDTO.setDatas(result);

            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(invUnitTrustSearchDTO);

            redisHelper.save(getRedisKey(userAndCorp, unitTrustReqDto), apiResponse);
        }

        return apiResponse;
    }

    public ApiResponse<INVUnitTrustFundStatsDTO> getINVUTFundAccountStats(UserAndCorp userAndCorp, INVUnitTrustFundStatsSpParam spParam) {
        ApiResponse<INVUnitTrustFundStatsDTO> response = new ApiResponse<>();
        log.info("Corp id {} ", userAndCorp.getCorpId());
        BaseSpDto<INVUnitTrustFundStatsSpParam, INVUnitTrustFundStatsDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_IV_UT_ACCT_FUND_STATS.getValue());
        spParam.setCorpId(userAndCorp.getCorpId());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(INVUnitTrustFundStatsDTO.class);
        INVUnitTrustFundStatsDTO utMainResp = baseSpService.execute(baseSpDto).getResults().get(0);
        utMainResp.setCorpId(userAndCorp.getCorpId());

        response.setMsgBody(utMainResp);
        response.setMsgHeader(new ErrorResponse("0"));

        return response;
    }

    public ApiResponse<SearchCollectionResponse<INVUnitTrustFundSearchDTO>> getINVUTFunds(UserAndCorp userAndCorp, INVUnitTrustFundReqDTO unitTrustFundReqDto) {
        log.info("Unit Trust CorpId and UserId: {}", userAndCorp);
        log.info("Unit Trust Acc No: {}", unitTrustFundReqDto.getAcctNumber());

        Pageable pageable = PageRequest.of(unitTrustFundReqDto.getPage() - 1, unitTrustFundReqDto.getPageSize(), Sort.by(desc("FUND_NAME")));
        Page<PortfolioUnitTrust> unitTrustDetails = utRepository.findByAccNoAndSearhParam(unitTrustFundReqDto.getAcctNumber(), unitTrustFundReqDto.getSearchParam(), pageable);
        List<INVUnitTrustFundSearchDTO> utFundRespDtoList = unitTrustDetails.stream()
                .map(entity -> {
                    INVUnitTrustFundSearchDTO dto = modelMapper.map(entity, INVUnitTrustFundSearchDTO.class);

                    BigDecimal units = new BigDecimal(dto.getUnits());
                    BigDecimal lastClosingNav = new BigDecimal(dto.getLastClosingNav());
                    BigDecimal totalValue = units.multiply(lastClosingNav);
                    dto.setTotalValue(totalValue.toString());

                    return dto;
                })
                .toList();

        ApiResponse<SearchCollectionResponse<INVUnitTrustFundSearchDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(new SearchCollectionResponse<>(new PageImpl<>(utFundRespDtoList, pageable, unitTrustDetails.getTotalElements())));

        return apiResponse;
    }

    public ApiResponse<INVUnitTrustDTO> getINVUTFundDetails(UserAndCorp userAndCorp, INVUnitTrustDetailReqDTO unitTrustDetailReqDto) throws ParseException {
        ApiResponse<INVUnitTrustDTO> response = new ApiResponse<>();
        log.info("Trust Fund ID {} ", unitTrustDetailReqDto.getId());
        Optional<PortfolioUnitTrust> unitTrustDetails = utRepository.findById(unitTrustDetailReqDto.getId());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMM yyyy");

        String dateCreatedString = "";
        Date dateFundOpened = null;
        if (unitTrustDetails.isPresent()) {
            dateCreatedString = unitTrustDetails.get().getCreatedDate().format(dateTimeFormatter);
            dateFundOpened = inputDateFormat.parse(unitTrustDetails.get().getDateFundOpened());
        }
        String dateOpenedString = outputDateFormat.format(dateFundOpened);

        modelMapper.typeMap(PortfolioUnitTrust.class, INVUnitTrustDTO.class)
                .addMappings(mapper -> mapper.map(PortfolioUnitTrust::getAccountNo, INVUnitTrustDTO::setAcctNumber));

        INVUnitTrustDTO utAccountDetailRes = modelMapper.map(unitTrustDetails, INVUnitTrustDTO.class);
        utAccountDetailRes.setCorpId(userAndCorp.getCorpId());
        if (unitTrustDetails.isEmpty()) {
            log.error("unitTrustDetails is empty");
            return response;
        }
        BigDecimal units = unitTrustDetails.get().getUnits();
        BigDecimal lastClosingNav = unitTrustDetails.get().getLastClosingNav();
        BigDecimal totalEstMarketValue = (units != null && lastClosingNav != null)
                ? units.multiply(lastClosingNav).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        utAccountDetailRes.setEstMarketValue(String.valueOf((totalEstMarketValue)));

        DecimalFormat twoDecimalFormat = new DecimalFormat("0.00");
        DecimalFormat fourDecimalFormat = new DecimalFormat("0.000000");

        String unitsPledgedStr = utAccountDetailRes.getUnitsPledged();
        unitsPledgedStr = (unitsPledgedStr == null || unitsPledgedStr.isEmpty() || unitsPledgedStr.equals("0")) ? "0.00" : unitsPledgedStr;
        // Parse the value and format it
        Double unitPledge = Double.parseDouble(unitsPledgedStr);
        String formattedUnitPledge = twoDecimalFormat.format(unitPledge);
        utAccountDetailRes.setUnitsPledged(formattedUnitPledge);

        String unitStr = utAccountDetailRes.getUnits();
        unitStr = (unitStr == null || unitStr.isEmpty() || unitStr.equals("0")) ? "0.000000" : unitStr;
        // Parse the value and format it
        Double unit = Double.parseDouble(unitStr);
        String formattedUnit = fourDecimalFormat.format(units);
        utAccountDetailRes.setUnits(formattedUnit);

        String costStr = utAccountDetailRes.getInitialCost();
        costStr = (costStr == null || costStr.isEmpty() || costStr.equals("0")) ? "0.00" : costStr;
        // Parse the value and format it
        Double cost = Double.parseDouble(costStr);
        String formattedCost = twoDecimalFormat.format(cost);
        utAccountDetailRes.setInitialCost(formattedCost);

        OffsetDateTime lastUpdate = OffsetDateTime.now();
        String lastUpdateString = lastUpdate.format(dateTimeFormatter);

        utAccountDetailRes.setDateUpdated(lastUpdateString);
        utAccountDetailRes.setDateCreated(dateCreatedString);
        utAccountDetailRes.setDateFundOpened(dateOpenedString);

        response.setMsgBody(utAccountDetailRes);
        response.setMsgHeader(new ErrorResponse("0"));

        return response;
    }

    public DownloadDTO downloadINVUTAccount(UserAndCorp userAndCorp, DownloadReq unitTrustReqDto) {
        DownloadDTO result ;
        switch (unitTrustReqDto.getReportType()) {
            case UNIT_TRUST_ACCOUNTLISTING_RPT_TYPE -> {
                INVUnitTrustAccDownloadReqDTO requestData = (INVUnitTrustAccDownloadReqDTO) unitTrustReqDto;
                result = downloadUnitTrustAccount(userAndCorp,requestData);
            }
            case UNIT_TRUST_FUND_LISTING_RPT_TYPE -> {
                INVUnitTrustFundDownloadReqDTO requestData = (INVUnitTrustFundDownloadReqDTO) unitTrustReqDto;
                result = downloadUnitTrustFund(requestData);
            }
            case UNIT_TRUST_TRXHISTORIES_RPT_TYPE -> {
                INVUnitTrustHistDownloadReqDTO requestData = (INVUnitTrustHistDownloadReqDTO) unitTrustReqDto;
                result = downloadUnitTrustFundHist(requestData);
            }
            default -> throw new IllegalArgumentException("Report type is undefined");

        }
        return result;
    }

    public DownloadDTO downloadUnitTrustAccount(UserAndCorp userAndCorp, INVUnitTrustAccDownloadReqDTO unitTrustReqDto){
        DownloadDTO result = new DownloadDTO();
        log.info("unitTrustReqDto for DownloadUnitTrustAccount :: "+ unitTrustReqDto);

        unitTrustReqDto.setCorpId(userAndCorp.getCorpId());
        unitTrustReqDto.setUserId(userAndCorp.getUserId());
        unitTrustReqDto.setCorpCifNo(unitTrustReqDto.getCorpCifNo());
        unitTrustReqDto.setSearchParam(unitTrustReqDto.getSearchParam());

        BaseSpDto<INVUnitTrustAccDownloadReqDTO, INVUnitTrustSearchSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_IV_UT_ACCT_LIST.getValue());
        baseSpDto.setPageSize(Integer.MAX_VALUE);
        baseSpDto.setPageNumber(1);
        baseSpDto.setParameter(unitTrustReqDto);
        baseSpDto.setResultClass(INVUnitTrustSearchSpDTO.class);

        BaseSpDto<INVUnitTrustAccDownloadReqDTO, INVUnitTrustSearchSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<INVUnitTrustSearchSpDTO> utRespDtoList = resultBaseSpDto.getResults().stream().toList();

        switch (unitTrustReqDto.getFormat()) {
            case CSV -> {
                result.setData(fileService.generateCsv(utRespDtoList));
                result.setFileName(DownloadEnum.UNIT_TRUST_ACCLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
            }
            case PDF -> {
                result.setData(fileService.generatePdf(utRespDtoList));
                result.setFileName(DownloadEnum.UNIT_TRUST_ACCLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
            }
            case TXT -> {
                result.setData(fileService.generateTxt(utRespDtoList));
                result.setFileName(DownloadEnum.UNIT_TRUST_ACCLISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
            }
            default -> throw new IllegalArgumentException("Report format for UT trx history is undefined");
        }
        return result;
    }

    public DownloadDTO downloadUnitTrustFund(INVUnitTrustFundDownloadReqDTO unitTrustReqDto){
        DownloadDTO result = new DownloadDTO();
        log.info("Acc No for Fund List : "+ unitTrustReqDto.getAcctNumber());
        List<PortfolioUnitTrust> unitTrustDetails = utRepository.findByAccNo(unitTrustReqDto.getAcctNumber());
        List<INVUnitTrustFundSearchDTO> utRespDtoList = unitTrustDetails.stream()
                .map(entity -> {
                    INVUnitTrustFundSearchDTO dto = modelMapper.map(entity, INVUnitTrustFundSearchDTO.class);

                    BigDecimal units = new BigDecimal(dto.getUnits());
                    BigDecimal lastClosingNav = new BigDecimal(dto.getLastClosingNav());
                    BigDecimal totalValue = (units != null && lastClosingNav != null)
                            ? units.multiply(lastClosingNav).setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                    dto.setTotalValue(totalValue.toString());

                    return dto;
                })
                .toList();

        switch (unitTrustReqDto.getFormat()) {
            case CSV -> {
                result.setData(fundFileService.generateCsv(utRespDtoList));
                result.setFileName(DownloadEnum.UNIT_TRUST_FUND_LISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
            }
            case PDF -> {
                result.setData(fundFileService.generatePdf(utRespDtoList));
                result.setFileName(DownloadEnum.UNIT_TRUST_FUND_LISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
            }
            case TXT -> {
                result.setData(fundFileService.generateTxt(utRespDtoList));
                result.setFileName(DownloadEnum.UNIT_TRUST_FUND_LISTING_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
            }
            default -> throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    public ApiResponse<SearchCollectionResponse<INVUnitTrustTransHistSearchDTO>> getINVUTTransHist(UserAndCorp userAndCorp, INVUnitTrustHistReqDTO unitTrustHistReqDto) {
        log.info("Unit Trust CorpId and UserId: {}", userAndCorp);
        SearchCollectionResponse<INVUnitTrustTransHistSearchDTO> spInvTrxHistoryResList = this.getInvTrxHistorySp(unitTrustHistReqDto);
        ApiResponse<SearchCollectionResponse<INVUnitTrustTransHistSearchDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(spInvTrxHistoryResList);
        return apiResponse;
    }

    public SearchCollectionResponse<INVUnitTrustTransHistSearchDTO> getInvTrxHistorySp(INVUnitTrustHistReqDTO request) {
        INVUnitTrustTrxHistSpParam utTrxHistoryListSpParam = new INVUnitTrustTrxHistSpParam();

        log.info("INVUnitTrustHistReqDTO :: "+ request.toString());

        utTrxHistoryListSpParam.setAcctNumber(request.getAcctNumber());

        utTrxHistoryListSpParam.setSearchParam(StringUtils.hasText(request.getSearchParam()) ? request.getSearchParam() : null);

        if(request.getFilters().getFundName()!=null
                || !request.getFilters().getFundName().isEmpty()
                || !request.getFilters().getFundName().equals("")) {
            Optional<PortfolioActivityUnitTrust> fundAccount = activityUTRepository.findById(request.getFilters().getFundName());
            fundAccount.ifPresent(portfolioActivityUnitTrust -> utTrxHistoryListSpParam.setFundName(portfolioActivityUnitTrust.getFundName()));
            log.info("Fund Name :: "+ utTrxHistoryListSpParam.getFundName());
        }

        utTrxHistoryListSpParam.setPageSize(request.getPageSize());
        utTrxHistoryListSpParam.setPage(request.getPage());
        utTrxHistoryListSpParam.setStartDate(StringUtils.hasText(request.getFilters().getStartDate()) ? request.getFilters().getStartDate() : null);
        utTrxHistoryListSpParam.setEndDate(StringUtils.hasText(request.getFilters().getEndDate()) ? request.getFilters().getEndDate() : null);

        BaseSpDto<INVUnitTrustTrxHistSpParam, INVUnitTrustTransHistSearchDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_IV_UT_TRXN_HIST.getValue());
        baseSpDto.setParameter(utTrxHistoryListSpParam);
        baseSpDto.setPageSize(utTrxHistoryListSpParam.getPageSize());
        baseSpDto.setPageNumber(utTrxHistoryListSpParam.getPage());
        baseSpDto.setResultClass(INVUnitTrustTransHistSearchDTO.class);
        BaseSpDto<INVUnitTrustTrxHistSpParam, INVUnitTrustTransHistSearchDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<INVUnitTrustTransHistSearchDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());

        SearchCollectionResponse<INVUnitTrustTransHistSearchDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(utTrxHistoryListSpParam.getPage() >= totalPage ? totalPage : utTrxHistoryListSpParam.getPage() + 1);
        result.setCurrentPage(utTrxHistoryListSpParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);
        return result;
    }

    public DownloadDTO downloadUnitTrustFundHist(INVUnitTrustHistDownloadReqDTO unitTrustReqDto){
        DownloadDTO result = new DownloadDTO();
        INVUnitTrustHistReqDTO unitTrustHistReqDto = new INVUnitTrustHistReqDTO();
        unitTrustHistReqDto.setAcctNumber(unitTrustReqDto.getAcctNumber());
        unitTrustHistReqDto.setFilters(unitTrustReqDto.getFilters());
        unitTrustHistReqDto.setPage(1);
        unitTrustHistReqDto.setPageSize(Integer.MAX_VALUE);

        SearchCollectionResponse<INVUnitTrustTransHistSearchDTO> spInvUtTrxHistoryResList = this.getInvTrxHistorySp(unitTrustHistReqDto);
        List<INVUnitTrustTransHistSearchDTO> transHistVaRespDtoList = Optional.ofNullable(spInvUtTrxHistoryResList)
                .map(SearchCollectionResponse::getData)
                .orElse(new ArrayList<>());
        log.info("File Format:: {}",unitTrustReqDto.getFormat());

        switch (unitTrustReqDto.getFormat()) {
            case CSV -> {
                result.setData(fundHistFileService.generateCsv(transHistVaRespDtoList));
                result.setFileName(DownloadEnum.UNIT_TRUST_FUND_TRXHISTORIES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
            }
            case PDF -> {
                result.setData(fundHistFileService.generatePdf(transHistVaRespDtoList));
                result.setFileName(DownloadEnum.UNIT_TRUST_FUND_TRXHISTORIES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
            }
            case TXT -> {
                result.setData(fundHistFileService.generateTxt(transHistVaRespDtoList));
                result.setFileName(DownloadEnum.UNIT_TRUST_FUND_TRXHISTORIES_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
            }
            default -> throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    public ApiResponse<List<INVUnitTrustFundDropdownDTO>> getINVUTDropdownFunds(INVUnitTrustFundDropdownReqDTO fundDropdownReqDto) {
        log.info("Unit Trust Acc No: {}", fundDropdownReqDto.getAcctNumber());

        List<PortfolioActivityUnitTrust> unitTrustList = activityUTRepository.findByAccountNo(fundDropdownReqDto.getAcctNumber());
        ApiResponse<List<INVUnitTrustFundDropdownDTO>> apiResponse = new ApiResponse<>();
        List<INVUnitTrustFundDropdownDTO> utFundRespDtoList = unitTrustList.stream()
                .map(entity -> modelMapper.map(entity, INVUnitTrustFundDropdownDTO.class)) // Convert to DTO
                .collect(Collectors.toMap(                            // Collect into a map, using fundName as the key
                        INVUnitTrustFundDropdownDTO::getFundName,         // Key by fundName
                        dto -> dto,                                       // Value is the DTO itself
                        (existing, replacement) -> existing))              // In case of duplicates, keep the first (existing)
                .values()                                              // Get all the values (which are the unique fund names)
                .stream()
                .toList();

        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(utFundRespDtoList);

        return apiResponse;
    }

    public void removeAllFromCache(UserAndCorp userAndCorp){
        String redisKey = redisHelper.createCacheKey(BD_AM_INV_UT_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_*");
        redisHelper.deleteCachesWithPattern(redisKey);
        log.info("removed cache for {}", redisKey);
    }
    private ApiResponse<INVUnitTrustSearchDTO> getAcctFromCache(UserAndCorp userAndCorp, AccountListReq reqDTO) {
        return (ApiResponse<INVUnitTrustSearchDTO>) redisHelper.getFromCache(getRedisKey(userAndCorp, reqDTO), ApiResponse.class);
    }

    private String getRedisKey(UserAndCorp userAndCorp, AccountListReq reqDTO) {
        return redisHelper.createCacheKey(BD_AM_INV_UT_ACCTLIST, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + "HIS" + "_" + reqDTO.getCorpCifNo() + "_" +reqDTO.getPage() + "_" + reqDTO.getPageSize() + "_" + reqDTO.getSearchParam());
    }
}