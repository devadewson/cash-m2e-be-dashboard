package com.m2e.dashboard.service.loan.jobDataMart;

import com.m2e.dashboard.service.trade.Refactor.TradeMart.SftpDownloader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanPbSftpService {

    private final SftpDownloader downloader;
    private final LoanPbDataLoader loader;
    private final CacheManager cacheManager;

    @Value("${m2e.sftp.local-temp-directory}")
    private String localTempDirectory;

    @Value("${m2e.sftp.pb-facility-file-name:EDW_LOAN_PBPPB_Fasilitas.TXT}")
    private String facilityFileName;

    @Value("${m2e.sftp.pb-draw-file-name:EDW_LOAN_PBPPB_DrawNumber.TXT}")
    private String drawFileName;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public boolean isRunning() {
        return running.get();
    }

    @PostConstruct
    public void init() {
        log.info("PB/PPB SFTP Service started - files: {}, {}", facilityFileName, drawFileName);
    }

    @Scheduled(cron = "${job.pb-datamart.cron:-}", zone = "Asia/Jakarta")
    public void scheduledRun() {
        log.info("[PB-SFTP] Scheduled run triggered");
        try {
            LoanPbDataLoader.LoadResult result = execute();
            log.info("[PB-SFTP] Scheduled run finished: fasilitas={} draw={}",
                    result.facilityRows(), result.drawRows());
        } catch (Exception e) {
            log.error("[PB-SFTP] Scheduled run failed", e);
        }
    }

    public LoanPbDataLoader.LoadResult execute() {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("PB/PPB SFTP job sedang berjalan");
        }

        long start = System.currentTimeMillis();
        log.info("=== PB/PPB SFTP JOB STARTED ===");

        try {
            // Unduh di luar transaksi supaya koneksi DB tidak tertahan selama transfer
            String facilityLocal = localTempDirectory + "/" + facilityFileName;
            String drawLocal = localTempDirectory + "/" + drawFileName;
            downloader.download(facilityFileName, facilityLocal);
            downloader.download(drawFileName, drawLocal);

            // Transaksi commit di dalam loader; setelah method ini kembali, data sudah tersimpan
            LoanPbDataLoader.LoadResult result = loader.load(facilityLocal, drawLocal);

            evictCaches();

            log.info("=== PB/PPB SFTP JOB COMPLETED: fasilitas={} draw={} dalam {}s ===",
                    result.facilityRows(), result.drawRows(),
                    (System.currentTimeMillis() - start) / 1000);
            return result;

        } catch (Exception e) {
            log.error("=== PB/PPB SFTP JOB FAILED ===", e);
            throw new RuntimeException("PB/PPB SFTP job gagal: " + e.getMessage(), e);
        } finally {
            running.set(false);
        }
    }

    /**
     * Dipanggil SETELAH commit. Kalau sebelum commit, request di sela-selanya
     */
    private void evictCaches() {
        for (String name : List.of(LoanPbCacheNames.STATS, LoanPbCacheNames.LISTING)) {
            try {
                Cache cache = cacheManager.getCache(name);
                if (cache != null) {
                    cache.clear();
                }
            } catch (Exception e) {
                log.warn("[PB-SFTP] Gagal membersihkan cache {}: {}", name, e.getMessage());
            }
        }
        log.info("[PB-SFTP] Cache PB/PPB dibersihkan");
    }
}