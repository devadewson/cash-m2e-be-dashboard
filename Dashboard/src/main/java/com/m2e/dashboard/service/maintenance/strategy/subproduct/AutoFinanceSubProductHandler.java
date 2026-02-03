package com.m2e.dashboard.service.maintenance.strategy.subproduct;

import com.m2e.dashboard.dto.maintenance.response.SubProductDTO;
import com.m2e.dashboard.enums.Product;
import com.m2e.dashboard.enums.SubProductEnum;
import com.m2e.dashboard.service.SubProductStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutoFinanceSubProductHandler implements SubProductStrategy {
    @Override
    public boolean supports(Product product) {
        return Product.AUTO_FINANCE.equals(product);
    }

    @Override
    public List<SubProductDTO> getSubProducts(List<SubProductDTO> input) {
        return input.stream()
                .map(dto -> {
                    if (SubProductEnum.HIRE_PURCHASE.getCode()
                            .equalsIgnoreCase(dto.getSubProductCode()) && dto.getSubProductDesc() != null) {
                        dto.setSubProductDesc(dto.getSubProductDesc().replace("Blanket", "Biz"));
                    }
                    return dto;
                })
                .toList();
    }

    @Override
    public List<SubProductDTO> getSubProductsEInvoiceDownload(List<SubProductDTO> input) {
        return input.stream()
                .map(dto -> {
                    if (SubProductEnum.HIRE_PURCHASE.getCode()
                            .equalsIgnoreCase(dto.getSubProductCode()) && dto.getSubProductDesc() != null) {
                        dto.setSubProductDesc(dto.getSubProductDesc().replace("Blanket", "Biz"));
                    }
                    return dto;
                })
                .toList();
    }
}
