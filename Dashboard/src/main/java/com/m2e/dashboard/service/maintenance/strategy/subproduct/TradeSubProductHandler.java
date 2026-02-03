package com.m2e.dashboard.service.maintenance.strategy.subproduct;

import com.m2e.dashboard.dto.maintenance.response.SubProductDTO;
import com.m2e.dashboard.enums.Product;
import com.m2e.dashboard.enums.SubProductEnum;
import com.m2e.dashboard.service.SubProductStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TradeSubProductHandler implements SubProductStrategy {
    @Override
    public boolean supports(Product product) {
        return Product.TRADE.equals(product);
    }

    @Override
    public List<SubProductDTO> getSubProducts(List<SubProductDTO> ignored) {
        return List.of(
                        SubProductEnum.IMPORT,
                        SubProductEnum.EXPORT,
                        SubProductEnum.TRADE_LOANS,
                        SubProductEnum.GUARANTEE
                ).stream()
                .map(e -> new SubProductDTO(null, e.getCode(), e.getDescription()))
                .toList();
    }

    @Override
    public List<SubProductDTO> getSubProductsEInvoiceDownload(List<SubProductDTO> input) {
        return List.of(
                        SubProductEnum.IMPORT,
                        SubProductEnum.EXPORT,
                        SubProductEnum.TRADE_LOANS,
                        SubProductEnum.GUARANTEE
                ).stream()
                .map(e -> new SubProductDTO(null, e.getCode(), e.getDescription()))
                .toList();
    }
}
