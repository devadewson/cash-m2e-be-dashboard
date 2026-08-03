package com.m2e.dashboard.service.trade.Refactor.TradeMart;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchProcessor {

    private final DataMapper mapper;
    private final DatabaseWriter writer;

    private ExecutorService executor;
    private final AtomicInteger threadCounter = new AtomicInteger(0);
    private final AtomicInteger totalBatchCounter = new AtomicInteger(0);
    private final AtomicInteger failedBatchCounter = new AtomicInteger(0);
    private final List<CompletableFuture<Void>> futures = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        executor = new ThreadPoolExecutor(
                4, 8, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                r -> new Thread(r, "batch-" + threadCounter.incrementAndGet()),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @PreDestroy
    public void destroy() {
        executor.shutdown();
    }

    public void submit(List<String> lines) {
        int batchNo = totalBatchCounter.incrementAndGet();

        CompletableFuture<Void> future = CompletableFuture
                .runAsync(() -> processBatch(batchNo, lines), executor)
                .exceptionally(ex -> {
                    failedBatchCounter.incrementAndGet();
                    log.error("[BatchProcessor] Batch #{} FAILED. size={}, error={}",
                            batchNo, lines.size(), ex.getMessage(), ex);
                    return null;
                });

        futures.add(future);
    }

    public void waitCompletion() {
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        int total = totalBatchCounter.get();
        int failed = failedBatchCounter.get();

        if (failed > 0) {
            log.warn("[BatchProcessor] Completed with {} FAILED batch(es) out of {} total batches",
                    failed, total);
        } else {
            log.info("[BatchProcessor] All {} batches completed successfully", total);
        }

        futures.clear();
        totalBatchCounter.set(0);
        failedBatchCounter.set(0);
    }

    private void processBatch(int batchNo, List<String> lines) {
        long start = System.currentTimeMillis();
        var records = mapper.map(lines);

        if (records.isEmpty()) {
            log.warn("[BatchProcessor] Batch #{} produced 0 valid records out of {} lines (all failed to parse)",
                    batchNo, lines.size());
            return;
        }

        writer.save(records);

        long durationMs = System.currentTimeMillis() - start;
        log.info("[BatchProcessor] Batch #{} saved. linesRead={}, recordsSaved={}, durationMs={}",
                batchNo, lines.size(), records.size(), durationMs);
    }
}