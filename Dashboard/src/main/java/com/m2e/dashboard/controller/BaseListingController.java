package com.m2e.dashboard.controller;

import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.common.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 1. Pagination of open tagging companies with total of accounts given user id, corp id & sub-product <br>
 * 2. Pagination of list of accounts which will be used for Subscriber Linking, Open Tagging Individual & Open Tagging All - Same response body per sub product.This API will also be used during searching <br>
 * 3. Account statistic endpoint for total no. of accounts, consolidated balance, etc
 * <br>
 * NOTE: User ID & Corp ID should be obtained in JWT Token and NOT passed in request body
 * <br>
 * A - Account Listing POJO <br>
 * B - Account Stats POJO <br>
 * <br>
 * Standardize endpoint naming for all subproducts
 */
public interface BaseListingController<A, B> {
    //    @PostMapping("account-list")
    ResponseEntity<ApiResponse<SearchCollectionResponse<A>>> getAccountList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody AccountListReq req);

    //    @GetMapping("account-stats")
    ResponseEntity<ApiResponse<B>> getAccountStats(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth);

}