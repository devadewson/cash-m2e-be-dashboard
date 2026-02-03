package com.m2e.dashboard.service.overview.strategy;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctListingDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctStatsDTO;
import com.m2e.dashboard.dto.maintenance.request.MTResetAccctNameReqDTO;
import com.m2e.dashboard.dto.maintenance.response.SubProductDTO;
import com.m2e.dashboard.dto.overview.request.OverviewReqDTO;
import com.m2e.dashboard.dto.overview.response.OverviewResDTO;
import com.m2e.dashboard.dto.overview.response.OverviewSubResDTO;
import com.m2e.dashboard.enums.*;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctListReqDTO;
import com.m2e.dashboard.repository.CorpRepository;
import com.m2e.dashboard.repository.SysParamRepository;
import com.m2e.dashboard.service.OverviewProductStrategy;
import com.m2e.dashboard.service.SubProductStrategy;
import com.m2e.dashboard.service.deposit.ca.jms.DepositCAESBService;
import com.m2e.dashboard.service.deposit.ca.sp.DepositCASPService;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import com.m2e.dashboard.util.DateTimeUtil;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.m2e.dashboard.enums.AcctRedisKeyEnum.BD_AM_OV_STATS;
import static com.m2e.dashboard.enums.SPEnum.SP_OV_DP_ACCT_STATS;
@Slf4j
@Component
public class OverviewDepositHandler implements OverviewProductStrategy {
    @Autowired
    private DepositCAESBService depositCAESBService;
    @Autowired
    private DepositCASPService depositCASPService;
    @Autowired
    BaseSpService baseSpService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
	private SysParamRepository sysParamRepository;

    @Autowired
    private CorpRepository corpRepository;

    public OverviewDepositHandler() {
    }

    @Override
    public boolean supports(OverviewEnum product) {
        return OverviewEnum.DEPOSIT.equals(product);
    }

    public Result retrieveCurrentAccounts(int spResult) {
    	String systemParam = sysParamRepository.findByCd("MAX_ACCT_REALTIME").orElse(null);
    	int MAX_CA_ACCOUNTS = Integer.parseInt(systemParam);
        int numberOfCAAccounts = spResult;

        if (numberOfCAAccounts <= MAX_CA_ACCOUNTS) return CurrentAccountResult.REAL_TIME;
        if (numberOfCAAccounts > MAX_CA_ACCOUNTS) return CurrentAccountResult.T_MINUS_ONE;

        return CurrentAccountResult.NONE;
    }

    private DepositAcctStatsDTO mergeStats(List<DepositAcctStatsDTO> statsList) {
        DepositAcctStatsDTO merged = new DepositAcctStatsDTO();
        DecimalFormat formatter = new DecimalFormat("#,##0.00");

        double totalLedger = statsList.stream()
                .map(DepositAcctStatsDTO::getTotalLedgerBal)
                .filter(Objects::nonNull)
                .mapToDouble(val -> {
                    try {
                        return Double.parseDouble(val);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }).sum();

        double totalAvailable = statsList.stream()
                .map(DepositAcctStatsDTO::getTotalAvailableBal)
                .filter(Objects::nonNull)
                .mapToDouble(val -> {
                    try {
                        return Double.parseDouble(val);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }).sum();

        merged.setTotalLedgerBal(formatter.format(totalLedger));
        merged.setTotalAvailableBal(formatter.format(totalAvailable));

        ESBMetadataDTO metadata = statsList.stream()
                .map(DepositAcctStatsDTO::getEsbMetadata)
                .filter(Objects::nonNull)
                .reduce((first, second) -> second)
                .orElse(null);
        merged.setEsbMetadata(metadata);

        return merged;
    }

    @Override
    public OverviewResDTO viewProduct(UserAndCorp userAndCorp, OverviewReqDTO overviewReqDto, OverviewResDTO overviewResDto) {
        DepositAcctStatsDTO depositAcctStatsDTO = new DepositAcctStatsDTO();
        ESBMetadataDTO esbMetadataDTO = new ESBMetadataDTO();
        AccountListReq req = new AccountListReq();
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        DepositAcctListingDTO spResult = depositCASPService.getDepositCAAccountListSp(userAndCorp, req);
        Result resultTotAcc = retrieveCurrentAccounts(spResult.getDatas().getTotalRecordCount());
        log.info("Result: " + resultTotAcc);

        List<OverviewSubResDTO> accountDp = new ArrayList<>();
        //call ESB to calculate consolidated balances for accounts across all corps if its equal or less than 20
        if (resultTotAcc == CurrentAccountResult.REAL_TIME) {
            Map<String, List<DepositAcctSummarySpDTO>> classifiedByProvider =
                    Optional.of(spResult.getDatas())
                            .map(SearchCollectionResponse::getData)
                            .orElseGet(List::of)
                            .stream()
                            .collect(Collectors.groupingBy(DepositAcctSummarySpDTO::getProvider));
            log.info("Overview deposit CA stats classifiedByProvider -> {} ", classifiedByProvider);

            Map<String, Object> esbResults = new HashMap<>();

            classifiedByProvider.entrySet().stream()
                    .filter(entry -> List.of(ProviderEnum.MY.name(), ProviderEnum.SG.name()).contains(entry.getKey()))
                    .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                    .forEach(entry -> {
                        String provider = entry.getKey();
                        List<DepositAcctSummarySpDTO> accounts = entry.getValue();

                        List<List<DepositAcctSummarySpDTO>> batches = IntStream.range(0, (accounts.size() + 9) / 10)
                                .mapToObj(i -> accounts.subList(i * 10, Math.min((i + 1) * 10, accounts.size())))
                                .filter(batch -> !batch.isEmpty())
                                .toList();

                        List<DepositAcctStatsDTO> batchStats = new ArrayList<>();

                        for (List<DepositAcctSummarySpDTO> batch : batches) {
                            ESBDepositCAAcctListReqDTO reqDTO = new ESBDepositCAAcctListReqDTO();
                            reqDTO.setCorpCifNo(spResult.getCorpCifNo());
                            reqDTO.setCorpProvider(provider);
                            reqDTO.setUserAndCorp(userAndCorp);

                            SearchCollectionResponse<DepositAcctSummarySpDTO> dataWrapper = new SearchCollectionResponse<>();
                            dataWrapper.setData(batch);
                            reqDTO.setDatas(dataWrapper);

                            Object result = switch (provider) {
                                case "MY" -> depositCAESBService.getAccountStats_MY(reqDTO).getMsgBody();
                                case "SG" -> depositCAESBService.getAccountStats_SG(reqDTO).getMsgBody();
                                default -> null;
                            };

                            if (result instanceof DepositAcctStatsDTO dto) {
                                batchStats.add(dto);
                            }
                        }

                        DepositAcctStatsDTO merged = mergeStats(batchStats);
                        esbResults.put(provider, merged);
                    });

            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            String mergedTotalLedgerBal = formatter.format(
                    esbResults.values().stream()
                            .filter(Objects::nonNull)
                            .map(DepositAcctStatsDTO.class::cast)
                            .map(DepositAcctStatsDTO::getTotalLedgerBal)
                            .filter(Objects::nonNull)
                            .map(bal -> bal.replaceAll(",", "")) // cleanup commas
                            .mapToDouble(bal -> {
                                try {
                                    return Double.parseDouble(bal);
                                } catch (NumberFormatException e) {
                                    return 0.00;
                                }
                            })
                            .sum()
            );

            String mergedTotalAvailableBal = formatter.format(
                    esbResults.values().stream()
                            .filter(Objects::nonNull)
                            .map(DepositAcctStatsDTO.class::cast)
                            .map(DepositAcctStatsDTO::getTotalAvailableBal)
                            .filter(Objects::nonNull)
                            .map(bal -> bal.replaceAll(",", "")) // cleanup commas
                            .mapToDouble(bal -> {
                                try {
                                    return Double.parseDouble(bal);
                                } catch (NumberFormatException e) {
                                    return 0.00;
                                }
                            })
                            .sum()
            );

            //set the total balances here after sum from MY and SG
            depositAcctStatsDTO.setTotalLedgerBal(mergedTotalLedgerBal);
            depositAcctStatsDTO.setTotalAvailableBal(mergedTotalAvailableBal);
            esbMetadataDTO = esbResults.values().stream()
                    .filter(Objects::nonNull)
                    .map(DepositAcctStatsDTO.class::cast)
                    .map(DepositAcctStatsDTO::getEsbMetadata)
                    .filter(Objects::nonNull)
                    .reduce((first, second) -> second)
                    .orElse(null);
            depositAcctStatsDTO.setEsbMetadata(esbMetadataDTO);

            depositAcctStatsDTO.setNumberOfAcct(String.valueOf(spResult.getDatas().getTotalRecordCount()));
            depositAcctStatsDTO.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
            depositAcctStatsDTO.setLastUpdatedDate(DateTimeUtil.getTodayDateOvFormat());

            OverviewSubResDTO overviewCA = createOverviewSubResDTO(depositAcctStatsDTO, overviewReqDto);
            overviewCA.setAsOf(DateTimeUtil.getTodayDateOvFormat());
            accountDp.add(overviewCA);
        } else {
            // If the condition is not REAL_TIME, get from SP directly
            depositAcctStatsDTO = depositCASPService.getDepositCAAccountStatsSp(userAndCorp);
            OverviewSubResDTO overviewCATminusone = createOverviewSubResDTO(depositAcctStatsDTO, overviewReqDto);
            accountDp.add(overviewCATminusone);
        }

        //add other deposit product here
        BaseSpDto<OverviewReqDTO, OverviewSubResDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_OV_DP_ACCT_STATS.getValue());
        baseSpDto.setParameter(overviewReqDto);
        baseSpDto.setResultClass(OverviewSubResDTO.class);
        BaseSpDto<OverviewReqDTO, OverviewSubResDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<OverviewSubResDTO> accountDpOthers = resultBaseSpDto.getResults().stream().toList();
        accountDp.addAll(accountDpOthers);
        log.info("accountDp ALL :: "+ accountDp);

        overviewResDto.setTitle(OverviewEnum.DEPOSIT.getDescription());
        overviewResDto.setAccount(accountDp);
        overviewResDto.setProductAccType("assets");

        //adding function to save in cache
        String overViewKey = redisHelper.createCacheKey(BD_AM_OV_STATS, userAndCorp.getUserId() + "_" + userAndCorp.getCorpId() + "_" + overviewReqDto.getProduct());
        redisHelper.save(overViewKey, overviewResDto, 2, TimeUnit.HOURS); //to confirm time

        return overviewResDto;
    }

    private OverviewSubResDTO createOverviewSubResDTO(DepositAcctStatsDTO depositAcctStatsDTO, OverviewReqDTO overviewReqDto) {
        OverviewSubResDTO overview = new OverviewSubResDTO();
        overview.setTotNoOfAccounts(depositAcctStatsDTO.getNumberOfAcct());
        overview.setSubProduct(OvSubProductEnum.CA);
        overview.setSubProductDesc(OvSubProductEnum.CA.getDescription());
        overview.setLabel("Ledger Balance");
        overview.setCurrency(corpRepository.findCurrencyByCorpProviderId(overviewReqDto.getCorpId()).orElse(null));
        overview.setAsOf(DateTimeUtil.getTMinusDateOvFormat());

        // Convert string to BigDecimal safely
        if (depositAcctStatsDTO.getTotalLedgerBal() != null && !depositAcctStatsDTO.getTotalLedgerBal().isEmpty()) {
            try {
                String totalLedgerBal = depositAcctStatsDTO.getTotalLedgerBal().replace(",", "");
                overview.setTotalAmount(new BigDecimal(totalLedgerBal));
            } catch (NumberFormatException e) {
                overview.setTotalAmount(BigDecimal.ZERO); // fallback
            }
        }
        return overview;
    }

}
