package com.m2e.dashboard.dto.deposit.ca.response;

import com.m2e.dashboard.dto.common.ContentDTO;
import lombok.Data;

import java.util.List;

@Data
public class DepositCAGPIBankDTO {
    private String bank;
    private List<ContentDTO> content;
}
