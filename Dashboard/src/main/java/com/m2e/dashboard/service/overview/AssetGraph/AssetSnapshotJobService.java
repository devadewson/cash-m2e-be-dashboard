package com.m2e.dashboard.service.overview.AssetGraph;

import com.m2e.dashboard.dto.overview.Refactor.KursResolution;
import com.m2e.dashboard.dto.overview.response.OverviewSubResDTO;
import com.m2e.dashboard.enums.OvSubProductEnum;
import com.m2e.dashboard.model.BTS.Request.Summary;
import com.m2e.dashboard.model.BTS.Response.Sumary.SummaryResponse;
import com.m2e.dashboard.model.Overview.AssetSnapshotEntity;
import com.m2e.dashboard.dto.overview.Refactor.JobRunResultDTO;
import com.m2e.dashboard.model.Trapi.Response.KursRateDTO;
import com.m2e.dashboard.repository.CorpCifRepository;
import com.m2e.dashboard.service.deposit.ca.Rest.BTSAccountClient;
import com.m2e.dashboard.service.overview.JobKursService.KursResolver;
import com.m2e.dashboard.service.overview.Refactor.CurrencyConverterService;
import com.m2e.dashboard.service.overview.Refactor.DepositAccountMapper;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetSnapshotJobService {

    private final CorpCifRepository corpCifRepository;
    private final KursResolver kursResolver;
    private final CurrencyConverterService currencyConverterService;
    private final GraphCurrencyConverter graphConverter;
    private final DepositAccountMapper accountMapper;
    private final BTSAccountClient btsAccountClient;
    private final AssetSnapshotWriter writer;

    private static final String BTS_USER_ID = "1234";
    private static final String IDR = "IDR";
    private static final int MAX_FAILED_DETAIL = 50;
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AtomicBoolean running = new AtomicBoolean(false);

    public boolean isRunning() {
        return running.get();
    }


    /** Sinkron. Balik setelah semua CIF selesai diproses. */
    public JobRunResultDTO execute() {
        JobRunResultDTO result = new JobRunResultDTO();
        result.setJobRunId(UUID.randomUUID().toString());
        result.setStartTime(LocalDateTime.now().format(TS));

        if (!running.compareAndSet(false, true)) {
            result.setStatus("ABORTED");
            result.setMessage("Job sedang berjalan, tidak bisa dijalankan bersamaan");
            result.setEndTime(LocalDateTime.now().format(TS));
            result.setDurationMs(0L);
            return result;
        }

        long startNano = System.nanoTime();
        try {
            doRun(result);
        } catch (Exception e) {
            log.error("[SNAPSHOT] Fatal error jobRunId={}", result.getJobRunId(), e);
            result.setStatus("FAILED");
            result.setMessage(e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            running.set(false);
            result.setEndTime(LocalDateTime.now().format(TS));
            result.setDurationMs((System.nanoTime() - startNano) / 1_000_000);
        }
        return result;
    }

    private void doRun(JobRunResultDTO result) {
        LocalDate snapshotDate = LocalDate.now();
        result.setSnapshotDate(snapshotDate.toString());

        log.info("[SNAPSHOT] START jobRunId={} date={}", result.getJobRunId(), snapshotDate);

        KursResolution kurs = kursResolver.resolve();
        if (kurs == null) {
            log.error("[SNAPSHOT] ABORT - kurs tidak tersedia dari Trapi maupun DB");
            result.setStatus("ABORTED");
            result.setMessage("Kurs tidak tersedia dari Trapi maupun database. Tidak ada data ditulis.");
            return;
        }
        if (kurs.isStale()) {
            log.error("[SNAPSHOT] ABORT - kurs DB berumur {} jam", kurs.getAgeHours());
            result.setStatus("ABORTED");
            result.setMessage("Kurs terakhir di DB berumur " + kurs.getAgeHours()
                    + " jam, melebihi batas. Job dibatalkan. Cek autosys kurs.");
            return;
        }

        Map<String, BigDecimal> rateMap = kurs.getRateMap();
        String kursValueDate = kurs.getValueDate();
        result.setRateMapSize(rateMap.size());
        result.setKursValueDate(kursValueDate);
        log.info("[SNAPSHOT] Kurs source={} pairs={} valueDate={} age={}h",
                kurs.getSource(), rateMap.size(), kursValueDate, kurs.getAgeHours());
        List<String> cifList = corpCifRepository.findAllDistinctCif();
        result.setTotalCif(cifList.size());
        log.info("[SNAPSHOT] {} distinct CIF to process", cifList.size());

        if (cifList.isEmpty()) {
            result.setStatus("NO_DATA");
            result.setMessage("Tidak ada CIF ditemukan di TB_CORP_USR");
            result.setInsertedCount(0);
            result.setSuccessCount(0);
            result.setPartialCount(0);
            result.setEmptyCount(0);
            result.setFailedCount(0);
            return;
        }

        int success = 0, partial = 0, empty = 0, failed = 0, processed = 0;

        for (String cif : cifList) {
            processed++;
            try {
                AssetSnapshotEntity entity = buildSnapshot(
                        cif, snapshotDate, rateMap, kursValueDate, result.getJobRunId());
                writer.upsert(entity);

                switch (entity.getSourceStatus()) {
                    case "SUCCESS" -> success++;
                    case "PARTIAL" -> partial++;
                    default -> empty++;
                }
            } catch (Exception e) {
                failed++;
                log.error("[SNAPSHOT] CIF={} failed: {}", cif, e.getMessage(), e);

                if (result.getFailedCifs().size() < MAX_FAILED_DETAIL) {
                    JobRunResultDTO.FailedCifDTO detail = new JobRunResultDTO.FailedCifDTO();
                    detail.setCorpCifNo(cif);
                    detail.setErrorType(e.getClass().getSimpleName());
                    detail.setErrorMessage(e.getMessage());
                    result.getFailedCifs().add(detail);
                }
            }

            if (processed % 100 == 0) {
                log.info("[SNAPSHOT] Progress {}/{} (success={} failed={})",
                        processed, cifList.size(), success, failed);
            }
        }

        int inserted = success + partial + empty;
        result.setInsertedCount(inserted);
        result.setSuccessCount(success);
        result.setPartialCount(partial);
        result.setEmptyCount(empty);
        result.setFailedCount(failed);

        if (inserted == 0) {
            result.setStatus("NO_DATA");
            result.setMessage("Tidak ada data yang berhasil disimpan. Semua " + failed + " CIF gagal.");
        } else if (failed > 0) {
            result.setStatus("COMPLETED_WITH_ERRORS");
            result.setMessage(inserted + " baris tersimpan, " + failed + " CIF gagal");
        } else {
            result.setStatus("COMPLETED");
            result.setMessage(inserted + " baris tersimpan");
        }

        log.info("[SNAPSHOT] END jobRunId={} status={} total={} inserted={} success={} partial={} empty={} failed={}",
                result.getJobRunId(), result.getStatus(), cifList.size(),
                inserted, success, partial, empty, failed);
    }

    private AssetSnapshotEntity buildSnapshot(String cif, LocalDate snapshotDate,
                                              Map<String, BigDecimal> rateMap,
                                              String kursValueDate, String jobRunId) {

        List<DepositAcctSummarySpDTO> btsAccounts = fetchFromBts(cif);
        List<OverviewSubResDTO> mapped = accountMapper.mapAll(btsAccounts);

        BigDecimal totalCa = BigDecimal.ZERO, totalMfca = BigDecimal.ZERO;
        BigDecimal totalFca = BigDecimal.ZERO, totalFd = BigDecimal.ZERO;
        BigDecimal totalTd = BigDecimal.ZERO, totalVa = BigDecimal.ZERO;
        BigDecimal totalOther = BigDecimal.ZERO;
        boolean rateMissing = false;

        for (OverviewSubResDTO acc : mapped) {
            BigDecimal idr = graphConverter.toIdr(acc.getTotalAmount(), acc.getCurrency(), rateMap);
            if (idr == null) {
                rateMissing = true;
                continue;
            }
            OvSubProductEnum sub = acc.getSubProduct();
            if (sub == null) {
                totalOther = totalOther.add(idr);
                continue;
            }
            switch (sub) {
                case CA:   totalCa   = totalCa.add(idr);   break;
                case MFCA: totalMfca = totalMfca.add(idr); break;
                case FCA:  totalFca  = totalFca.add(idr);  break;
                case FD:   totalFd   = totalFd.add(idr);   break;
                case TD:   totalTd   = totalTd.add(idr);   break;
                case VA:   totalVa   = totalVa.add(idr);   break;
                default:   totalOther = totalOther.add(idr); break;
            }
        }

        BigDecimal totalAsset = totalCa.add(totalMfca).add(totalFca)
                .add(totalFd).add(totalTd).add(totalVa).add(totalOther);

        AssetSnapshotEntity e = new AssetSnapshotEntity();
        e.setSnapshotDate(snapshotDate);
        e.setCorpCifNo(cif);
        e.setWeekKey(GraphPeriodUtil.weekKey(snapshotDate));
        e.setMonthKey(GraphPeriodUtil.monthKey(snapshotDate));
        e.setQuarterKey(GraphPeriodUtil.quarterKey(snapshotDate));
        e.setCurrency(IDR);
        e.setTotalAsset(totalAsset);
        e.setTotalLiabilities(BigDecimal.ZERO);
        e.setTotalCa(totalCa);
        e.setTotalMfca(totalMfca);
        e.setTotalFca(totalFca);
        e.setTotalFd(totalFd);
        e.setTotalTd(totalTd);
        e.setTotalVa(totalVa);
        e.setTotalDpOther(totalOther);
        e.setAccountCount(mapped.size());
        e.setKursValueDate(kursValueDate);
        e.setJobRunId(jobRunId);
        e.setCreatedDate(LocalDateTime.now());

        if (mapped.isEmpty())      e.setSourceStatus("EMPTY");
        else if (rateMissing)      e.setSourceStatus("PARTIAL");
        else                       e.setSourceStatus("SUCCESS");

        return e;
    }

    private List<DepositAcctSummarySpDTO> fetchFromBts(String cif) {
        Summary request = new Summary();
        request.setUserId(BTS_USER_ID);
        request.setCif(cif);
        request.setLocale("en_US");
        request.setIndicator("R");

        SummaryResponse resp = btsAccountClient.getAccountSummary(request);

        List<DepositAcctSummarySpDTO> accounts = new ArrayList<>();
        if (resp == null || resp.getResponseData() == null
                || resp.getResponseData().getAssetList() == null) {
            return accounts;
        }

        resp.getResponseData().getAssetList().forEach(category -> {
            if (category.getProductList() == null) return;
            category.getProductList().forEach(product -> {
                DepositAcctSummarySpDTO dto = new DepositAcctSummarySpDTO();
                dto.setAcctName(product.getProductName());
                dto.setAcctNumber(product.getAccNo());
                dto.setCurrency(product.getCurrencyCodeIso());
                dto.setLedgerBalance(product.getAmount());
                dto.setAvailableBalance(product.getAmount());
                dto.setAcctType(product.getAccountType());
                accounts.add(dto);
            });
        });
        return accounts;
    }

    private String resolveSpotValueDate(List<KursRateDTO> rates) {
        if (rates == null) return null;
        return rates.stream()
                .filter(r -> "SPOT".equalsIgnoreCase(r.getTenor()))
                .map(KursRateDTO::getValueDate)
                .filter(v -> v != null && !v.isBlank())
                .findFirst()
                .orElse(null);
    }
}