package com.m2e.dashboard.service.maintenance.strategy.subproduct;

import com.m2e.dashboard.dto.maintenance.response.SubProductDTO;
import com.m2e.dashboard.enums.Product;
import com.m2e.dashboard.enums.SubProductEnum;
import com.m2e.dashboard.service.SubProductStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoanSubProductHandler implements SubProductStrategy {
    @Override
    public boolean supports(Product product) {
        return Product.LOAN.equals(product);
    }

    @Override
    public List<SubProductDTO> getSubProducts(List<SubProductDTO> input) {
        List<String> allowedCodes = List.of(
                SubProductEnum.OVERDRAFT.getCode(),
                SubProductEnum.TERM_LOAN.getCode(),
                SubProductEnum.REVOLVING_CREDIT.getCode()
        );

        List<SubProductDTO> results = allowedCodes.stream()
                .map(code -> input.stream()
                        .filter(dto -> code.equalsIgnoreCase(dto.getSubProductCode()))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));

        if (results.stream().noneMatch(dto -> SubProductEnum.OVERDRAFT.getCode()
                .equalsIgnoreCase(dto.getSubProductCode()))) {
            results.add(0, new SubProductDTO(null,
                    SubProductEnum.OVERDRAFT.getCode(),
                    SubProductEnum.OVERDRAFT.getDescription()));
        }

        return results;
    }

    @Override
    public List<SubProductDTO> getSubProductsEInvoiceDownload(List<SubProductDTO> input) {
        List<String> allowedCodes = List.of(
                SubProductEnum.TERM_LOAN.getCode()
        );

        List<SubProductDTO> results = allowedCodes.stream()
                .map(code -> input.stream()
                        .filter(dto -> code.equalsIgnoreCase(dto.getSubProductCode()))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));

        return results;
    }
}
