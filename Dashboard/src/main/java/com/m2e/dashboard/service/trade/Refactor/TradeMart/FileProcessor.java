package com.m2e.dashboard.service.trade.Refactor.TradeMart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileProcessor {

    private final BatchProcessor batchProcessor;
    private static final int BATCH_SIZE = 500;
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public void process(String filePath) {
        long start = System.currentTimeMillis();
        List<String> batch = new ArrayList<>(BATCH_SIZE);
        int total = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                batch.add(line);

                if (batch.size() >= BATCH_SIZE) {
                    batchProcessor.submit(new ArrayList<>(batch));
                    total += batch.size();
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                batchProcessor.submit(new ArrayList<>(batch));
                total += batch.size();
            }

            batchProcessor.waitCompletion();

            long durationMs = System.currentTimeMillis() - start;
            log.info("[FileProcessor] File processing DONE. file={}, totalLinesRead={}, durationMs={}",
                    filePath, total, durationMs);

            markAsProcessed(filePath);

        } catch (Exception e) {
            log.error("[FileProcessor] File processing FAILED. file={}, error={}", filePath, e.getMessage(), e);
            throw new RuntimeException("File processing failed", e);
        }
    }

    private void markAsProcessed(String filePath) {
        try {
            Path source = Paths.get(filePath);
            String timestamp = LocalDateTime.now().format(TS_FMT);
            Path target = Paths.get(filePath + ".processed_" + timestamp);
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("[FileProcessor] Marked file as processed: {}", target);
        } catch (Exception e) {
            log.warn("[FileProcessor] Failed to mark file as processed: {}", e.getMessage());
        }
    }
}