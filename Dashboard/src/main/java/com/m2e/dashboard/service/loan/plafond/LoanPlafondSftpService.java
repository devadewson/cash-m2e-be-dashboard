package com.m2e.dashboard.service.loan.plafond;


import com.m2e.dashboard.service.Archiver.DatamartFileArchiver;
import com.m2e.dashboard.service.trade.Trademart.SftpDownloader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanPlafondSftpService {

    private final SftpDownloader downloader;
    private final LoanPlafondFileProcessor fileProcessor;
    private final CacheManager cacheManager;
    private final DatamartFileArchiver archiver;


    @Value("${m2e.sftp.local-temp-directory}")
    private String localTempDirectory;

    @Value("${m2e.sftp.plafond-file-name:EDW_Plafond.TXT}")
    private String fileName;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public boolean isRunning() {
        return running.get();
    }

    @PostConstruct
    public void init() {
        log.info("Plafond SFTP Service started - file: {}", fileName);
    }

    @Scheduled(cron = "${job.plafond-datamart.cron:-}", zone = "Asia/Jakarta")
    public void scheduledRun() {
        log.info("[PLAFOND-SFTP] Scheduled run triggered");
        try {
            long elapsed = execute();
            log.info("[PLAFOND-SFTP] Scheduled run finished in {} ms", elapsed);
        } catch (Exception e) {
            log.error("[PLAFOND-SFTP] Scheduled run failed", e);
        }
    }
    public long execute() {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("Plafond SFTP job sedang berjalan");
        }

        long start = System.currentTimeMillis();
        log.info("=== PLAFOND SFTP JOB STARTED | file={} ===", fileName);

        String localFile = localTempDirectory + "/" + fileName;
        boolean downloaded = false;
        boolean processed = false;

        try {
            downloader.download(fileName, localFile);
            downloaded = true;

            int inserted = fileProcessor.process(localFile);
            processed = true;

            evictCache();

            long elapsed = System.currentTimeMillis() - start;
            log.info("=== PLAFOND SFTP JOB COMPLETED: {} CIF dalam {}s ===", inserted, elapsed / 1000);
            return elapsed;

        } catch (Exception e) {
            log.error("=== PLAFOND SFTP JOB FAILED ===", e);
            throw new RuntimeException("Plafond SFTP job gagal: " + e.getMessage(), e);
        } finally {
            if (downloaded) {
                archiver.archive(localFile, processed);
            }
            running.set(false);
        }
    }

    private void evictCache() {
        try {
            Cache cache = cacheManager.getCache(LoanPlafondCacheNames.STATS);
            if (cache != null) {
                cache.clear();
            }
            log.info("[PLAFOND-SFTP] Cache plafond dibersihkan");
        } catch (Exception e) {
            log.warn("[PLAFOND-SFTP] Gagal membersihkan cache: {}", e.getMessage());
        }
    }
}
