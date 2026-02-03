package com.m2e.dashboard.dto.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatisticDTO implements Serializable {
    private Integer numberOfAccounts;
    private String lastUpdatedDate;
    private String lastUpdatedDateTime;
}
