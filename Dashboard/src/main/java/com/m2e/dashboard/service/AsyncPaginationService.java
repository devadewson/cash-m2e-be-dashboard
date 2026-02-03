package com.m2e.dashboard.service;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


@Service
public class AsyncPaginationService {

    public <T, R> List<R> mapAndPaginateData(List<T> datas, Function<List<T>, List<R>> mapper, int pageSize, int pageNumber) {
        List<R> mappedData = mapData(datas, mapper);
        return paginateData(mappedData, pageSize, pageNumber);
    }

    private <T, R> List<R> mapData(List<T> datas, Function<List<T>, List<R>> mapper) {
        return Optional.ofNullable(mapper.apply(datas)).orElse(Collections.emptyList());
    }

    public <R> List<R> paginateData(List<R> data, int pageSize, int pageNumber) {
        int skipCount = pageSize * (pageNumber - 1);

        return Flux.fromStream(data.stream())
                .skip(skipCount)
                .take(pageSize)
                .collectList()
                .block();
    }

    public <T, R> SearchCollectionResponse<R> getPaginatedResponse(List<T> datas, Function<List<T>, List<R>> mapper, int pageSize, int pageNumber) {
        List<R> mappedData = mapData(datas, mapper);
        int totalRecordCount = mappedData.size();

        List<R> paginatedData = paginateData(mappedData, pageSize, pageNumber);
        return constructSearchCollectionResponse(paginatedData, pageSize, pageNumber, totalRecordCount);
    }

    public <R> SearchCollectionResponse<R> constructSearchCollectionResponse(List<R> paginatedData, int pageSize, int pageNumber, int totalRecordCount) {
        SearchCollectionResponse<R> response = new SearchCollectionResponse<>();
        response.setData(paginatedData);
        response.setCurrentPage(pageNumber);
        response.setTotalRecordCount(totalRecordCount);
        response.setTotalPages((int) Math.ceil((double) totalRecordCount / pageSize));
        response.setNextPage(response.getCurrentPage() < response.getTotalPages() ? response.getCurrentPage() + 1 : -1);

        return response;
    }
}
