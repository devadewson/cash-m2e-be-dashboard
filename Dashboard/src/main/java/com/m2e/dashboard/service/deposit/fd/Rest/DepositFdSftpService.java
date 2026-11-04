package com.m2e.dashboard.service.deposit.fd.Rest;

import com.m2e.dashboard.service.Archiver.DatamartFileArchiver;
import com.m2e.dashboard.service.trade.Trademart.SftpDownloader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepositFdSftpService {

    private final SftpDownloader downloader;
    private final DepositFdFileProcessor fileProcessor;
    private final DatamartFileArchiver archiver;

    @Value("${m2e.sftp.local-temp-directory}")
    private String localTempDirectory;

    @Value("${m2e.sftp.fd-file-name:EDW_LOAN_FD.TXT}")
    private String fileName;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public boolean isRunning() {
        return running.get();
    }

    @PostConstruct
    public void init() {
        log.info("FD SFTP Service started - file: {}", fileName);
    }

    @Scheduled(cron = "${job.fd-datamart.cron:-}", zone = "Asia/Jakarta")
    public void scheduledRun() {
        log.info("[FD-SFTP] Scheduled run triggered");
        try {
            long elapsed = execute();
            log.info("[FD-SFTP] Scheduled run finished in {} ms", elapsed);
        } catch (Exception e) {
            log.error("[FD-SFTP] Scheduled run failed", e);
        }
    }

    public long execute() {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("FD SFTP job sedang berjalan");
        }

        long start = System.currentTimeMillis();
        log.info("=== FD SFTP JOB STARTED | file={} ===", fileName);

        String localFile = localTempDirectory + "/" + fileName;
        boolean downloaded = false;
        boolean processed = false;

        try {
            downloader.download(fileName, localFile);
            downloaded = true;

            int inserted = fileProcessor.process(localFile);
            processed = true;

            long elapsed = System.currentTimeMillis() - start;
            log.info("=== FD SFTP JOB COMPLETED: {} baris dalam {}s ===", inserted, elapsed / 1000);
            return elapsed;

        } catch (Exception e) {
            log.error("=== FD SFTP JOB FAILED ===", e);
            throw new RuntimeException("FD SFTP job gagal: " + e.getMessage(), e);
        } finally {
            if (downloaded) {
                archiver.archive(localFile, processed);
            }
            running.set(false);
        }
    }
}