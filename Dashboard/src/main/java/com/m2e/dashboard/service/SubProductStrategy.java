package com.m2e.dashboard.service;

import com.m2e.dashboard.dto.maintenance.response.SubProductDTO;
import com.m2e.dashboard.enums.Product;

import java.util.List;

public interface SubProductStrategy {
    boolean supports(Product product);
    List<SubProductDTO> getSubProducts(List<SubProductDTO> input);
    List<SubProductDTO> getSubProductsEInvoiceDownload(List<SubProductDTO> input);
}
