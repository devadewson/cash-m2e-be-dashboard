package com.m2e.dashboard.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoreActionsEntitlement implements Serializable {
    private MoreActionsRedirection makePayment;
    private MoreActionsRedirection makeBillPayment;
    private MoreActionsRedirection makePlacement;
    private MoreActionsRedirection uplift;
    private MoreActionsRedirection manage;
}
