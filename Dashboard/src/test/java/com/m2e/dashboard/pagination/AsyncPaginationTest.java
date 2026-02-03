package com.m2e.dashboard.pagination;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.m2e.dashboard.dto.deposit.fd.response.DepositFDCertificateDetailDTO;
import com.m2e.dashboard.service.AsyncMappingService;
import com.m2e.dashboard.service.AsyncPaginationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class AsyncPaginationTest {

    @Test
    public void processListAsync() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        AsyncMappingService asyncMappingService = new AsyncMappingService();
        AsyncPaginationService asyncPaginationService = new AsyncPaginationService();
        List<SourceDTO> datas = generateDummyData(1000000);
        long startTime = System.currentTimeMillis();
        CompletableFuture<List<TargetDTO>> futureResult = asyncMappingService.mapToListAsync(datas, DataMapper.INSTANCE::mapToTargetDTO);
        Function<List<SourceDTO>, List<TargetDTO>> trxMapper = dtos -> futureResult.join();
        log.info("Data {} -> ", mapper.writeValueAsString(asyncPaginationService.getPaginatedResponse(datas, trxMapper, 20, 1)));
        long endTime = System.currentTimeMillis();

        log.info("Data processing and pagination completed in {} ms", (endTime - startTime));
    }

    @Test
    public void processListAsyncWithFilterAndSortingFeature() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        AsyncMappingService asyncMappingService = new AsyncMappingService();
        AsyncPaginationService asyncPaginationService = new AsyncPaginationService();
        List<SourceDTO> datas = generateDummyData(1000000);
        long startTime = System.currentTimeMillis();

        Predicate<SourceDTO> filterCondition = dto -> "John Doe".equals(dto.getAccountName());
        Function<List<TargetDTO>, List<TargetDTO>> sortFunction = list -> list.stream()
                .sorted((a, b) -> a.getAccountNumber().compareTo(b.getAccountNumber()))
                .collect(Collectors.toList());
        CompletableFuture<List<TargetDTO>> futureResult = asyncMappingService.processListAsync(
                datas,
                DataMapper.INSTANCE::mapToTargetDTO,
                filterCondition,
                sortFunction
        );

        Function<List<SourceDTO>, List<TargetDTO>> trxMapper = dtos -> futureResult.join();
        log.info("Data {} -> ", mapper.writeValueAsString(asyncPaginationService.getPaginatedResponse(datas, trxMapper, 20, 1)));
        long endTime = System.currentTimeMillis();

        log.info("Data processing and pagination completed in {} ms", (endTime - startTime));
    }
    @Test
    public void processListAsyncWithFilterAndSortingFeatureList() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        AsyncMappingService asyncMappingService = new AsyncMappingService();
        AsyncPaginationService asyncPaginationService = new AsyncPaginationService();
        List<SourceDTO> datas = generateDummyData(1000000);
        long startTime = System.currentTimeMillis();

        List<Predicate<SourceDTO>> filterConditions = new ArrayList<>();
        filterConditions.add(dto -> StringUtils.containsIgnoreCase(dto.getAccountName(), "john"));
        filterConditions.add(dto -> StringUtils.containsIgnoreCase(dto.getCurrency(), "MYR"));

        List<Function<List<TargetDTO>, List<TargetDTO>>> sortFunctions = new ArrayList<>();
        sortFunctions.add(list -> list.stream()
                .sorted((a, b) -> a.getAccountNumber().compareTo(b.getAccountNumber()))
                .collect(Collectors.toList()));
        sortFunctions.add(list -> list.stream()
                .sorted((a, b) -> a.getAccountName().compareTo(b.getAccountName()))
                .collect(Collectors.toList()));

        CompletableFuture<List<TargetDTO>> futureResult = asyncMappingService.processListAsync(
                datas,
                DataMapper.INSTANCE::mapToTargetDTO,
                filterConditions,
                sortFunctions
        );

        Function<List<SourceDTO>, List<TargetDTO>> trxMapper = dtos -> futureResult.join();
        log.info("Data {} -> ", mapper.writeValueAsString(asyncPaginationService.getPaginatedResponse(datas, trxMapper, 20, 1)));
        long endTime = System.currentTimeMillis();

        log.info("Data processing and pagination completed in {} ms", (endTime - startTime));
    }


    public static List<SourceDTO> generateDummyData(int count) {
        Random random = new Random();

        return IntStream.range(0, count)
                .parallel() // Use parallelStream for faster generation
                .mapToObj(i -> SourceDTO.builder()
                        .accountNumber(generateRandomAccountNumber())
                        .accountName(generateRandomAccountName())
                        .beginBalance(generateRandomBalance(random))
                        .endBalance(generateRandomBalance(random))
                        .debit(generateRandomTransactionAmount(random))
                        .credit(generateRandomTransactionAmount(random))
                        .currency(generateRandomCurrency())
                        .build())
                .collect(Collectors.toList());
    }

    private static String generateRandomAccountNumber() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
    }

    private static String generateRandomAccountName() {
        String[] names = {"John Doe", "Jane Smith", "Alice Brown", "Bob Johnson", "Carol White"};
        return names[new Random().nextInt(names.length)];
    }

    private static String generateRandomCurrency() {
        String[] currencies = {"MYR", "JPY", "SGD", "USD", "THB"};
        return currencies[new Random().nextInt(currencies.length)];
    }

    private static String generateRandomBalance(Random random) {
        return BigDecimal.valueOf(1000 + (100000 - 1000) * random.nextDouble())
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .toString();
    }

    private static String generateRandomTransactionAmount(Random random) {
        return BigDecimal.valueOf(100 + (10000 - 100) * random.nextDouble())
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .toString();
    }

}
