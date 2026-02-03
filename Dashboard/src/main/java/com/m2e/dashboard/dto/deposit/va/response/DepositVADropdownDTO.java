package com.m2e.dashboard.dto.deposit.va.response;

import lombok.Data;

import java.util.List;
@Data
public class DepositVADropdownDTO {
    private List<String> virtualAcctNumber;
    private List<String> corpCode;
}
