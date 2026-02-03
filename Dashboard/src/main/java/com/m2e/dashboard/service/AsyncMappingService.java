package com.m2e.dashboard.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class AsyncMappingService {

    public <T, R> CompletableFuture<List<R>> mapToListAsync(List<T> dtos, Function<T, R> mapperFunction) {
        return CompletableFuture.supplyAsync(() -> dtos.stream()
                .map(mapperFunction)
                .toList());
    }

    public <T, R> CompletableFuture<List<R>> processListAsync(List<T> dtos, Function<T, R> mapperFunction, Predicate<T> filterCondition, Function<List<R>, List<R>> sortFunction) {
        return CompletableFuture.supplyAsync(() -> dtos.stream()
                        .filter(filterCondition)
                        .map(mapperFunction)
                        .toList())
                .thenApply(sortFunction);
    }

    public <T, R> CompletableFuture<List<R>> processListAsync(List<T> dtos, Function<T, R> mapperFunction, List<Predicate<T>> filterConditions, List<Function<List<R>, List<R>>> sortFunctions
    ) {
        return CompletableFuture.supplyAsync(() -> dtos.stream()
                        .filter(dto -> filterConditions.stream().anyMatch(filter -> filter.test(dto)))
                        .map(mapperFunction)
                        .toList())
                .thenApply(mappedList -> {
                    List<R> result = mappedList;
                    for (Function<List<R>, List<R>> sortFunction : sortFunctions) {
                        result = sortFunction.apply(result);
                    }
                    return result;
                });
    }

    public <T, R> CompletableFuture<List<R>> processListAsyncAndFilter(List<T> dtos, Function<T, R> mapperFunction, List<Predicate<T>> filterConditions, List<Function<List<R>, List<R>>> sortFunctions
    ) {
        return CompletableFuture.supplyAsync(() -> dtos.stream()
                        .filter(dto -> filterConditions.stream().allMatch(filter -> filter.test(dto)))
                        .map(mapperFunction)
                        .toList())
                .thenApply(mappedList -> {
                    List<R> result = mappedList;
                    for (Function<List<R>, List<R>> sortFunction : sortFunctions) {
                        result = sortFunction.apply(result);
                    }
                    return result;
                });
    }
}
