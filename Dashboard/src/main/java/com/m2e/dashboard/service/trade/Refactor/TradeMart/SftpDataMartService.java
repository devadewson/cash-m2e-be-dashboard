package com.m2e.dashboard.service.trade.Refactor.TradeMart;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    @PostConstruct
    public void init() {
        log.info("SFTP Service started - Batch processing ready");
    }

    @Scheduled(fixedDelayString = "${m2e.sftp.polling-interval}")
    public void execute() {
        long start = System.currentTimeMillis();
        log.info("=== SFTP JOB STARTED ===");

        try {
            String localFile = localTempDirectory + "/" + fileName;
            downloader.download(localFile);
            fileProcessor.process(localFile);

            log.info("=== SFTP JOB COMPLETED in {}s ===",
                    (System.currentTimeMillis() - start) / 1000);
        } catch (Exception e) {
            log.error("=== SFTP JOB FAILED ===", e);
        }
    }
}
