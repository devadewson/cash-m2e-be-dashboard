package com.m2e.dashboard.mapper.entitlement;


import com.m2e.dashboard.dto.entitlement.response.AcctProdEntitlementDto;
import com.m2e.dashboard.dto.entitlement.response.CodeDscpDto;
import com.m2e.dashboard.sp.entitlement.response.AcctProductResDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SpResMapper {
    public List<AcctProdEntitlementDto> mapAcctProdList(List<AcctProductResDto> acctProductResDtos) {
        if (acctProductResDtos == null || acctProductResDtos.isEmpty())
            return new ArrayList<>();

        Map<String, AcctProdEntitlementDto> productMap = acctProductResDtos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getProductCode() + "_" + p.getProductDescription(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    AcctProdEntitlementDto acctProdEntitlementResDto = new AcctProdEntitlementDto();
                                    acctProdEntitlementResDto.setCode(list.get(0).getProductCode());
                                    acctProdEntitlementResDto.setDescription(list.get(0).getProductDescription());

                                    List<CodeDscpDto> subProducts = list.stream().map(p -> {
                                        CodeDscpDto subProduct = new CodeDscpDto();
                                        subProduct.setCode(p.getSubProductCode());
                                        subProduct.setDescription(p.getSubProductDescription());
                                        return subProduct;
                                    }).collect(Collectors.toList());

                                    acctProdEntitlementResDto.setSubProducts(subProducts);
                                    return acctProdEntitlementResDto;
                                }
                        )
                ));

        return new ArrayList<>(productMap.values());
    }
}
