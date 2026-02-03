package com.m2e.dashboard.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SourceDTO implements Serializable {

    private String accountNumber;
    private String accountName;
    private String beginBalance;
    private String endBalance;
    private String debit;
    private String credit;
    private String currency;
}
