package com.m2e.dashboard.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DemoScreenEnum {
    OVERVIEW("IS_BD_OV_EXPLORED"),
    DEPOSIT("IS_BD_DP_EXPLORED"),
    LOAN("IS_BD_LN_EXPLORED"),
    STATEMENT("IS_BD_STMT_EXPLORED"),
    AUTOFINANCE("IS_BD_AF_EXPLORED"),
    INVESTMENT("IS_BD_IV_EXPLORED"),
    CARD("IS_BD_CCD_EXPLORED"),
    TRADE("IS_BD_TD_EXPLORED"),
    OTHERBANK("IS_BD_OB_EXPLORED");

    private final String columnName;
}