package com.m2e.dashboard.service.overview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.entitlement.request.UserAccessReqDto;
import com.m2e.dashboard.dto.overview.request.AssetLiabilitiesGraphReqDTO;
import com.m2e.dashboard.dto.overview.request.AssetLiabilitiesReqDTO;
import com.m2e.dashboard.dto.overview.request.OverviewDownloadReqDTO;
import com.m2e.dashboard.dto.overview.request.OverviewReqDTO;
import com.m2e.dashboard.dto.overview.response.*;
import com.m2e.dashboard.enums.DownloadEnum;
import com.m2e.dashboard.enums.OverviewEnum;
import com.m2e.dashboard.mapper.overview.OverviewMapper;
import com.m2e.dashboard.service.OverviewProductStrategy;
import com.m2e.dashboard.service.entitlement.EntitlementService;
import com.m2e.dashboard.sp.entitlement.response.AcctProductResDto;
import com.m2e.dashboard.sp.overview.response.OverviewSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_OV_GRAPH;
import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_OV_STATS;
import static com.m2e.dashboard.enums.SPEnum.*;
import static com.m2e.dashboard.mapper.overview.OverviewMapper.mapToAssetLiabilities;
import static java.util.Objects.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OverviewService {
    @Autowired
    BaseSpService baseSpService;
    @Autowired
    private OverviewFileService fileService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private EntitlementService entitlementService;

    private final List<OverviewProductStrategy> overviewProductStrategies;

    public ApiResponse getAssetLiabilities(String jwtToken, UserAndCorp userAndCorp, AssetLiabilitiesReqDTO assetLiabilitiesReqDto) throws JsonProcessingException {
        log.info("Asset & Liabilities Service : " );
        ApiResponse apiResponse = new ApiResponse();
        AssetLiabilitiesResDTO assetLiabilitiesResDto = new AssetLiabilitiesResDTO();

        assetLiabilitiesReqDto.setBaseCurrency(assetLiabilitiesReqDto.getBaseCurrency()!=null?assetLiabilitiesReqDto.getBaseCurrency():"MYR");
        assetLiabilitiesResDto.setCurrency(assetLiabilitiesReqDto.getBaseCurrency());
        //here need to change the entitlement
        UserAccessReqDto userAccessReqDto = new UserAccessReqDto();
        userAccessReqDto.setCorpId(userAndCorp.getCorpId());
        List<AcctProductResDto> allEntProducts = entitlementService.getUserAccess(jwtToken,userAccessReqDto);
        OverviewResDTO overviewResDto;
        //add function calling from cache first instead of calling SP
        List<OverviewSubResDTO> overviewList = new ArrayList<>();
        for (AcctProductResDto item : allEntProducts) {
                String overViewWidgetKey = redisHelper.createCacheKey(BD_AM_OV_STATS, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + item.getProductCode());
                log.info("Overview Key retrieved: {}", overViewWidgetKey);
                if (Boolean.TRUE.equals(redisHelper.isPresent(overViewWidgetKey))) {
                    overviewResDto = redisHelper.getFromCache(overViewWidgetKey, OverviewResDTO.class);
                    overviewList = Optional.ofNullable(overviewResDto)
                            .map(OverviewResDTO::getAccount)
                            .orElse(new ArrayList<>());
                    log.info("Overview retrieved from Redis: {}", overviewList);
                } else {
                    OverviewReqDTO overviewReqDto = new OverviewReqDTO();
                    overviewReqDto.setBaseCurrency(assetLiabilitiesReqDto.getBaseCurrency());
                    overviewReqDto.setProduct(OverviewEnum.valueOf(item.getProductCode()));
                    overviewReqDto.setUserId(userAndCorp.getUserId());
                    overviewReqDto.setCorpId(userAndCorp.getCorpId());
                    overviewReqDto.setCorpCifNo(assetLiabilitiesReqDto.getCorpCifNo());
                    OverviewResDTO overviewProduct = this.getProductOverviewFromRepo(userAndCorp, overviewReqDto);
                    log.info("OverviewProduct called : {}", overviewProduct.toString());
                    List<OverviewSubResDTO> accounts = overviewProduct.getAccount();
                    if (accounts != null && !accounts.isEmpty()) {
                        overviewList.addAll(accounts);  // Now safe to add
                    }
                }
                //map all
                assetLiabilitiesResDto = mapToAssetLiabilities(overviewList, assetLiabilitiesResDto);
                log.info("After map assetLiabilitiesResDto: {}", assetLiabilitiesResDto.toString());
                calculateTotals(assetLiabilitiesResDto);
        }

        try {
            BigDecimal workingRatio = BigDecimal.ZERO;
            try {
                BigDecimal asset = assetLiabilitiesResDto.getTotalAsset();
                if (asset != null) {
                    workingRatio = asset.divide(assetLiabilitiesResDto.getTotalLiabilities(), 4, RoundingMode.HALF_UP);
                }
                log.info("Working Ratio: " + workingRatio);
            } catch (ArithmeticException e) {
                log.info("Division by zero is not allowed.");
            }
            assetLiabilitiesResDto.setWorkingRatio(workingRatio);

            BigDecimal quickRatio = BigDecimal.ZERO;
            try {
                BigDecimal asset = assetLiabilitiesResDto.getTotalAssetRatio();
                if (asset != null) {
                    quickRatio = asset.divide(assetLiabilitiesResDto.getTotalLiabilitiesRatio(), 4, RoundingMode.HALF_UP);
                }
                log.info("Quick Ratio: " + quickRatio);
            } catch (ArithmeticException e) {
                log.info("Division by zero is not allowed.");
            }
            assetLiabilitiesResDto.setQuickRatio(quickRatio);

            OffsetDateTime lastUpdate = OffsetDateTime.now().minusDays(1);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss '(GMT+8)'");
            String lastUpdateString = lastUpdate.format(dateTimeFormatter);
            assetLiabilitiesResDto.setLastUpdatedDate(lastUpdateString);

        } catch (DataIntegrityViolationException e) {
            // Handle the specific exception
            log.info("Data integrity violation: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            log.info("An unexpected error occurred: " + e.getMessage());
        }

        apiResponse.setMsgBody(assetLiabilitiesResDto);
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        return apiResponse;
    }

    public void calculateTotals(AssetLiabilitiesResDTO dto) {
        if (dto == null) return;

        BigDecimal totalAsset = safeAdd(dto.getTotalCA())
                .add(safeAdd(dto.getTotalFCA()))
                .add(safeAdd(dto.getTotalMFCA()))
                .add(safeAdd(dto.getTotalFD()))
                .add(safeAdd(dto.getTotalTD()))
                .add(safeAdd(dto.getTotalInv()))
                .add(safeAdd(dto.getTotalDpOther()));

        dto.setTotalAsset(totalAsset);

        BigDecimal totalLiabilities = safeAdd(dto.getTotalHp())
                .add(safeAdd(dto.getTotalBhp()))
                .add(safeAdd(dto.getTotalBD()))
                .add(safeAdd(dto.getTotalFs()))
                .add(safeAdd(dto.getTotalTRD()))
                .add(safeAdd(dto.getTotalTRDE()))
                .add(safeAdd(dto.getTotalTRDI()))
                .add(safeAdd(dto.getTotalTRDG()))
                .add(safeAdd(dto.getTotalTRDL()))
                .add(safeAdd(dto.getTotalTL()))
                .add(safeAdd(dto.getTotalPbr()))
                .add(safeAdd(dto.getTotalRc()))
                .add(safeAdd(dto.getTotalOD()))
                .add(safeAdd(dto.getTotalChc()))
                .add(safeAdd(dto.getTotalCrc()))
                .add(safeAdd(dto.getTotalDc()))
                .add(safeAdd(dto.getTotalLoanOther()));

        dto.setTotalLiabilities(totalLiabilities);

        BigDecimal totalAssetRatios = safeAdd(dto.getTotalCA())
                .add(safeAdd(dto.getTotalFCA()))
                .add(safeAdd(dto.getTotalMFCA()))
                .add(safeAdd(dto.getTotalInv()))
                .add(safeAdd(dto.getTotalDpOther()));
        dto.setTotalAssetRatio(totalAssetRatios);

        BigDecimal totalLiabilityRatios = safeAdd(dto.getTotalHp())
                .add(safeAdd(dto.getTotalBhp()))
                .add(safeAdd(dto.getTotalBD()))
                .add(safeAdd(dto.getTotalFs()))
                .add(safeAdd(dto.getTotalTRD()))
                .add(safeAdd(dto.getTotalTRDE()))
                .add(safeAdd(dto.getTotalTRDI()))
                .add(safeAdd(dto.getTotalTRDG()))
                .add(safeAdd(dto.getTotalTRDL()))
                .add(safeAdd(dto.getTotalPbr()))
                .add(safeAdd(dto.getTotalRc()))
                .add(safeAdd(dto.getTotalOD()))
                .add(safeAdd(dto.getTotalChc()))
                .add(safeAdd(dto.getTotalCrc()))
                .add(safeAdd(dto.getTotalDc()))
                .add(safeAdd(dto.getTotalLoanOther()));
        dto.setTotalLiabilitiesRatio(totalLiabilityRatios);
    }

    private BigDecimal safeAdd(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    public ApiResponse getAssetLiabilitiesGraph(UserAndCorp userAndCorp, AssetLiabilitiesGraphReqDTO assetLiabilitiesReqDto) {
        ApiResponse apiResponse = new ApiResponse();
        assetLiabilitiesReqDto.setBaseCurrency(assetLiabilitiesReqDto.getBaseCurrency()!=null?assetLiabilitiesReqDto.getBaseCurrency():"MYR");
        assetLiabilitiesReqDto.setUserId(userAndCorp.getUserId());

        AssetLiabilitiesGraphResDTO mainAsset = new AssetLiabilitiesGraphResDTO();
        List<AssetLiabilitiesGraphSubResDTO> assetListDtos = null;

        log.info("Choosing graph interval : " + assetLiabilitiesReqDto.getInterval());

        switch (assetLiabilitiesReqDto.getInterval()) {
            case W -> {
                String overViewGraphWeeklyWidgetKey = getRedisKey(userAndCorp, String.valueOf(assetLiabilitiesReqDto.getInterval()));
                assetListDtos = getGraphFromCache(overViewGraphWeeklyWidgetKey);

                if (isNull(assetListDtos) || assetListDtos.isEmpty()) {
                    String listWeeks = getWeeks().toString();
                    log.info("listWeeks -- " + listWeeks);
                    BaseSpDto<AssetLiabilitiesGraphReqDTO, AssetLiabilitiesGraphSubResDTO> baseSpDpDto = new BaseSpDto<>();
                    baseSpDpDto.setSpId(SP_OV_WEEK_ASSET_STATS.getValue());
                    baseSpDpDto.setParameter(assetLiabilitiesReqDto);
                    baseSpDpDto.setResultClass(AssetLiabilitiesGraphSubResDTO.class);
                    BaseSpDto<AssetLiabilitiesGraphReqDTO, AssetLiabilitiesGraphSubResDTO> resultBaseSpDpDto = baseSpService.execute(baseSpDpDto);
                    assetListDtos = resultBaseSpDpDto.getResults().stream().toList();
                    //adding function to save in cache
                    saveGraphIntoCache(overViewGraphWeeklyWidgetKey,assetListDtos);
                }
            } case M -> {
                String overViewGraphMonthlyWidgetKey = getRedisKey(userAndCorp, String.valueOf(assetLiabilitiesReqDto.getInterval()));
                assetListDtos = getGraphFromCache(overViewGraphMonthlyWidgetKey);

                if (isNull(assetListDtos) || assetListDtos.isEmpty()) {
                    log.info("Monthly -- " );
                    BaseSpDto<AssetLiabilitiesGraphReqDTO, AssetLiabilitiesGraphSubResDTO> baseSpDpDto = new BaseSpDto<>();
                    baseSpDpDto.setSpId(SP_OV_MONTH_ASSET_STATS.getValue());
                    baseSpDpDto.setParameter(assetLiabilitiesReqDto);
                    baseSpDpDto.setResultClass(AssetLiabilitiesGraphSubResDTO.class);
                    BaseSpDto<AssetLiabilitiesGraphReqDTO, AssetLiabilitiesGraphSubResDTO> resultBaseSpDpDto = baseSpService.execute(baseSpDpDto);
                    assetListDtos = resultBaseSpDpDto.getResults().stream().toList();
                    //adding function to save in cache
                    saveGraphIntoCache(overViewGraphMonthlyWidgetKey,assetListDtos);
                }
            } case Q -> {
                String overViewGraphQuarterlyWidgetKey = getRedisKey(userAndCorp, String.valueOf(assetLiabilitiesReqDto.getInterval()));
                assetListDtos = getGraphFromCache(overViewGraphQuarterlyWidgetKey);

                if (isNull(assetListDtos) || assetListDtos.isEmpty()) {
                    log.info("Quarterly -- " );
                    BaseSpDto<AssetLiabilitiesGraphReqDTO, AssetLiabilitiesGraphSubResDTO> baseSpDpDto = new BaseSpDto<>();
                    baseSpDpDto.setSpId(SP_OV_QUARTER_ASSET_STATS.getValue());
                    baseSpDpDto.setParameter(assetLiabilitiesReqDto);
                    baseSpDpDto.setResultClass(AssetLiabilitiesGraphSubResDTO.class);
                    BaseSpDto<AssetLiabilitiesGraphReqDTO, AssetLiabilitiesGraphSubResDTO> resultBaseSpDpDto = baseSpService.execute(baseSpDpDto);
                    assetListDtos = resultBaseSpDpDto.getResults().stream().toList();
                    //adding function to save in cache
                    saveGraphIntoCache(overViewGraphQuarterlyWidgetKey,assetListDtos);
                }
            }
                default -> System.out.println("Invalid Interval ");
            }

        log.info("View asset graph : " + assetListDtos);
        mainAsset.setAssetGraph(assetListDtos);
        mainAsset.setCurrency(assetLiabilitiesReqDto.getBaseCurrency());
        OffsetDateTime lastUpdate = OffsetDateTime.now().minusDays(1);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        String lastUpdateString = lastUpdate.format(dateTimeFormatter);
        mainAsset.setLastUpdatedDate(lastUpdateString);

        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(mainAsset);
        return apiResponse;
    }

    public List<String> getWeeks() {
        LocalDate today = LocalDate.now();
        // Get the start date of the current week (Monday)
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        // List to hold the start and end dates of the last 8 weeks
        List<String> last8WeekNumbers = new ArrayList<>();
        // Calculate the week number for each of the last 8 weeks
        for (int i = 0; i < 8; i++) {
            LocalDate weekStart = startOfWeek.minusWeeks(i);
            int year = weekStart.getYear();
            int weekOfYear = weekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

            // Format week number in YYYY-WW format
            String weekNumber = String.format("%d-%02d", year, weekOfYear);
            last8WeekNumbers.add(weekNumber);
        }

        return last8WeekNumbers;
    }


    public DownloadDTO downloadOverview(String jwtToken, UserAndCorp userAndCorp, DownloadReq overviewDownloadReqDTO) throws JsonProcessingException {
        DownloadDTO result = new DownloadDTO();

        switch (overviewDownloadReqDTO.getReportType()) {
            case OVERVIEW_LISTING_RPT_TYPE -> {
                OverviewDownloadReqDTO requestData = (OverviewDownloadReqDTO) overviewDownloadReqDTO;
                result = downloadOverviewSummary(jwtToken, userAndCorp,requestData);
            }
        }
        return result;
    }
    public DownloadDTO downloadOverviewSummary(String jwtToken, UserAndCorp userAndCorp, OverviewDownloadReqDTO overviewDownloadReqDTO) throws JsonProcessingException {
        DownloadDTO result = new DownloadDTO();
        log.info("Download Overview Summary parameter :: " + overviewDownloadReqDTO);

        OverviewReqDTO overviewReqDTO = new OverviewReqDTO();
        overviewReqDTO.setCorpId(overviewDownloadReqDTO.getCorpId());
        overviewReqDTO.setUserId(overviewDownloadReqDTO.getUserId());
        overviewReqDTO.setCorpCifNo(overviewDownloadReqDTO.getCorpCifNo());

        //here need to change the entitlement
        UserAccessReqDto userAccessReqDto = new UserAccessReqDto();
        userAccessReqDto.setCorpId(userAndCorp.getCorpId());
        List<AcctProductResDto> allEntProducts = entitlementService.getUserAccess(jwtToken, userAccessReqDto);

        List<OverviewSpDTO> ovAllRespDtoList = new ArrayList<>();
        for (AcctProductResDto item : allEntProducts) {
            overviewReqDTO.setProduct(OverviewEnum.valueOf(item.getProductCode()));
            OverviewResDTO overviewResDto = this.getProductOverviewFromRepo(userAndCorp, overviewReqDTO);
            log.info("Products :: " + overviewResDto.toString());
            overviewResDto.getAccount().stream()
                    .map(OverviewMapper::mapToSpDto)
                    .forEach(sp -> {
                        boolean exists = ovAllRespDtoList.stream()
                                .anyMatch(subProduct -> subProduct.getSubProductDesc().equals(sp.getSubProductDesc()));
                        if (!exists) {
                            ovAllRespDtoList.add(sp);
                        }
                    });
        }

        log.info("Download Overview Summary Products :: " + ovAllRespDtoList.toString());

        switch (overviewDownloadReqDTO.getFormat()) {
            case CSV -> {
                result.setData(fileService.generateCsv(ovAllRespDtoList));
                result.setFileName(DownloadEnum.OVERVIEW_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".csv");
            }
            case TXT -> {
                result.setData(fileService.generateTxt(ovAllRespDtoList));
                result.setFileName(DownloadEnum.OVERVIEW_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".txt");
            }
            case PDF -> {
                result.setData(fileService.generatePdf(ovAllRespDtoList));
                result.setFileName(DownloadEnum.OVERVIEW_FILENAME.getDescription() + String.valueOf(System.currentTimeMillis()) + ".pdf");
            }
            default -> throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    public ApiResponse getProductOverview(UserAndCorp userAndCorp, OverviewReqDTO request) {
        ApiResponse apiResponse = new ApiResponse();
        OverviewResDTO overviewResDto;
        //adding logic with redis cache
        String overViewKey = redisHelper.createCacheKey(BD_AM_OV_STATS, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + request.getProduct());
        log.info("Overview Key retrieved: {}", overViewKey);
        if (Boolean.TRUE.equals(redisHelper.isPresent(overViewKey))) {
            //set header
            overviewResDto = redisHelper.getFromCache(overViewKey, OverviewResDTO.class);
            log.info("Overview retrieved from Redis: {}", overviewResDto.toString());
        } else {
            overviewResDto = getProductOverviewFromRepo(userAndCorp, request);
            log.info("Overview retrieved from SP :: " + overviewResDto);
        }
        apiResponse.setMsgBody(overviewResDto);
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        return apiResponse;
    }


    public OverviewResDTO getProductOverviewFromRepo(UserAndCorp userAndCorp, OverviewReqDTO request) {
        log.info("getProductOverviewFromRepo IN -- " + request.getProduct());
        return getProductOverviewData(userAndCorp, request);
    }
    List<OverviewSubResDTO> getCATeeMinusOne(final OverviewReqDTO overviewReqDto) {
        BaseSpDto<OverviewReqDTO, OverviewSubResDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_OV_DP_ACCT_STATS.getValue());
        log.info("accountDp Param:: "+ overviewReqDto);
        baseSpDto.setParameter(overviewReqDto);
        baseSpDto.setResultClass(OverviewSubResDTO.class);
        BaseSpDto<OverviewReqDTO, OverviewSubResDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);


        return resultBaseSpDto.getResults().stream().toList();
    }

    public interface Order {
        default int getPosition() {
            return Integer.MAX_VALUE;
        }
    }

    public OverviewResDTO getProductOverviewData(UserAndCorp userAndCorp, OverviewReqDTO overviewReqDto) {

        OverviewResDTO overviewResDto = new OverviewResDTO();
        OffsetDateTime lastUpdate = OffsetDateTime.now().minusDays(1);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        String lastUpdateString = lastUpdate.format(dateTimeFormatter);
        overviewResDto.setLastUpdatedDate(lastUpdateString);

        OverviewProductStrategy overviewProductStrategy = overviewProductStrategies.stream()
                    .filter(strategy -> strategy.supports(overviewReqDto.getProduct()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Product is undefined"));
            overviewResDto = overviewProductStrategy.viewProduct(userAndCorp, overviewReqDto, overviewResDto);

        return overviewResDto;
    }
    private String getRedisKey(UserAndCorp userAndCorp, String graphInterval) {
        return redisHelper.createCacheKey(BD_AM_OV_GRAPH, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + graphInterval);
    }

    private List<AssetLiabilitiesGraphSubResDTO> getGraphFromCache(String overviewGraphKey) {
        return (List<AssetLiabilitiesGraphSubResDTO>) redisHelper.getListFromCache(overviewGraphKey, AssetLiabilitiesGraphSubResDTO.class);
    }

    private void saveGraphIntoCache(String overviewGraphKey, List<AssetLiabilitiesGraphSubResDTO> assetListDtos) {
        redisHelper.saveListToCache(overviewGraphKey, assetListDtos, 2, TimeUnit.HOURS); //to confirm time
    }
}