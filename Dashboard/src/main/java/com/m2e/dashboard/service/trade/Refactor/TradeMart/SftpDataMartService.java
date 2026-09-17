package com.m2e.dashboard.service.trade.Refactor.TradeMart;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class SftpDataMartService {

    private final SftpDownloader downloader;
    private final FileProcessor fileProcessor;

    @Value("${m2e.sftp.local-temp-directory}")
    private String localTempDirectory;

    @Value("${m2e.sftp.file-name}")
    private String fileName;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public boolean isRunning() {
        return running.get();
    }

    //Cron Job Triger
    @Scheduled(cron = "${job.sftp-datamart.cron:-}", zone = "Asia/Jakarta")
    public void scheduledRun() {
        log.info("[SFTP] Scheduled run triggered");
        try {
            long elapsed = execute();
            log.info("[SFTP] Scheduled run finished in {} ms", elapsed);
        } catch (Exception e) {
            log.error("[SFTP] Scheduled run failed", e);
        }
    }
    @PostConstruct
    public void init() {
        log.info("SFTP Service started - manual trigger + scheduled");
    }
    public long execute() {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("SFTP job sedang berjalan");
        }

        long start = System.currentTimeMillis();
        log.info("=== SFTP JOB STARTED ===");

        try {
            String localFile = localTempDirectory + "/" + fileName;
            downloader.download(localFile);
            fileProcessor.process(localFile);

            long elapsed = System.currentTimeMillis() - start;
            log.info("=== SFTP JOB COMPLETED in {}s ===", elapsed / 1000);
            return elapsed;

        } catch (Exception e) {
            log.error("=== SFTP JOB FAILED ===", e);
            throw new RuntimeException("SFTP job gagal: " + e.getMessage(), e);
        } finally {
            running.set(false);
        }
    }
}