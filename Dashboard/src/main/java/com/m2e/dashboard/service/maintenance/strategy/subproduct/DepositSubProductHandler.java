package com.m2e.dashboard.service.maintenance.strategy.subproduct;

import com.m2e.dashboard.dto.maintenance.response.SubProductDTO;
import com.m2e.dashboard.enums.Product;
import com.m2e.dashboard.enums.SubProductEnum;
import com.m2e.dashboard.service.SubProductStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class DepositSubProductHandler implements SubProductStrategy {
    @Override
    public boolean supports(Product product) {
        return Product.DEPOSIT.equals(product);
    }

    @Override
    public List<SubProductDTO> getSubProducts(List<SubProductDTO> input) {
        List<String> allowedCodes = List.of(
                SubProductEnum.CURRENT_ACCOUNT.getCode(),
                SubProductEnum.FIXED_DEPOSIT.getCode(),
                SubProductEnum.TIME_DEPOSIT.getCode()
        );
        return allowedCodes.stream()
                .map(code -> input.stream()
                        .filter(dto -> code.equalsIgnoreCase(dto.getSubProductCode()))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<SubProductDTO> getSubProductsEInvoiceDownload(List<SubProductDTO> input) {
        List<String> allowedCodes = List.of(
                SubProductEnum.CURRENT_ACCOUNT.getCode(),
                SubProductEnum.FIXED_DEPOSIT.getCode()
        );
        return allowedCodes.stream()
                .map(code -> input.stream()
                        .filter(dto -> code.equalsIgnoreCase(dto.getSubProductCode()))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }
}
