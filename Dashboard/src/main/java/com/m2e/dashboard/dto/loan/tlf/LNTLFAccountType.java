package com.m2e.dashboard.dto.loan.tlf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LNTLFAccountType implements Serializable {
    String provider;
    boolean isForeign;
    boolean isIslamic;
    boolean isFITAS;
}