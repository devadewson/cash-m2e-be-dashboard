package com.m2e.dashboard.dto.loan.tlf.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class LNTLFTrancheDTO implements Serializable {
    private String trancheNumber;
    private String rate;
    private String paymentFrequency;
    private String drawdownAmount;
    private String currency;
}