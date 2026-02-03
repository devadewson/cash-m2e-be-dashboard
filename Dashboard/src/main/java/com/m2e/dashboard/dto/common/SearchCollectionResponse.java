package com.m2e.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class SearchCollectionResponse<E> {
    private Integer totalPages;
    private Integer currentPage;
    private Integer nextPage;
    private Integer totalRecordCount;
    private List<E> data = new ArrayList<>();

    public SearchCollectionResponse(){}

    //TODO: remove this one, mainly used for mock data only
    public SearchCollectionResponse(int currentPage, Page<E> page){
        this.setNextPage(page.hasNext() ? currentPage + 1 : -1);
        this.setCurrentPage(currentPage);
        this.setTotalPages(page.getTotalPages());
        this.setTotalRecordCount(Integer.valueOf(String.valueOf(page.getTotalElements())));
        this.setData(page.getContent());
    }

    // important notes: Page java start with 0, SP start with 1
    public SearchCollectionResponse(Page<E> page) {
        this.setCurrentPage(page.getPageable().getPageNumber() + 1);
        this.setNextPage(page.hasNext() ? this.getCurrentPage() + 1 : -1);
        this.setTotalPages(page.getTotalPages());
        this.setData(page.getContent());
        this.setTotalRecordCount(Integer.valueOf(String.valueOf(page.getTotalElements())));
    }

    public SearchCollectionResponse(Integer totalPages, Integer currentPage, Integer nextPage, Integer totalRecordCount) {
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.nextPage = nextPage;
        this.totalRecordCount = totalRecordCount;
    }
}
