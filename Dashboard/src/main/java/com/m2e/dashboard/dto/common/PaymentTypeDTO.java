package com.m2e.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTypeDTO {
    private String code;
    private String desc;
    private String provider;
}
