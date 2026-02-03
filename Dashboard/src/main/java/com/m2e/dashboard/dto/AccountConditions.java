package com.m2e.dashboard.dto;

import com.m2e.dashboard.enums.AcctStatusEnum;
import lombok.Data;

import java.util.function.Supplier;

import static com.m2e.dashboard.util.UtilityFunctions.convertSpBooleanResult;

@Data
public class AccountConditions {
    private boolean closed;
    private boolean tlast;

    public AccountConditions(Supplier<String> statusSupplier) {
        closed = isEqualsToClosedOrInactive(statusSupplier);
    }

    public AccountConditions(Supplier<String> statusSupplier, Supplier<String> tlastIndicatorSupplier) {
        closed = isEqualsToClosedOrInactive(statusSupplier);
        tlast = convertSpBooleanResult(tlastIndicatorSupplier.get());
    }

    private boolean isEqualsToClosedOrInactive(Supplier<String> statusSupplier) {
        return AcctStatusEnum.CLOSED.name().equalsIgnoreCase(statusSupplier.get())
               || AcctStatusEnum.INACTIVE.name().equalsIgnoreCase(statusSupplier.get());
    }
}
