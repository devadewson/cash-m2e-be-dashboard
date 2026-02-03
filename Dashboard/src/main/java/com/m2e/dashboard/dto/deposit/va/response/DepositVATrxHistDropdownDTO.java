package com.m2e.dashboard.dto.deposit.va.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DepositVATrxHistDropdownDTO {
    private String virtualAcctName;
    private String virtualAcctNumber;
}
