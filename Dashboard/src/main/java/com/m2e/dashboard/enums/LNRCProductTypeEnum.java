package com.m2e.dashboard.enums;

import com.m2e.dashboard.service.loan.overdraft.LNOverdraftSPService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public enum LNRCProductTypeEnum {

    FIXED_RATE("01","Fixed Rate"),
    DISCOUNTED("02","Discounted");

    private static final Logger logger = LoggerFactory.getLogger(LNRCProductTypeEnum.class.getName());
    private final String code;
    private final String description;

    LNRCProductTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getLNRCProductTypeName(String productTypeCode){
        for (LNRCProductTypeEnum productType : values()) {
            if (productType.getCode().equals(productTypeCode)) {
                return productType.getDescription();
            }
        }
        logger.error("IllegalArgument | Invalid product type code: {}", productTypeCode);
        return null;
    }
}
