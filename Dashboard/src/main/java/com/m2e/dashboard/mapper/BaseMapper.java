package com.m2e.dashboard.mapper;

import com.m2e.dashboard.dto.AccountConditions;
import org.mapstruct.Context;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.m2e.dashboard.util.UtilityFunctions.convertSpBooleanResult;

/**
 * This interface is to be extended, therefore not annotated with @Mapper.
 * <p>
 * Mapper methods should be annotated with @BeanMapping(ignoreByDefault = true)
 * to NOT implicitly map values without explicit @Mapping.
 */
public interface BaseMapper {
    String NULL_IF_CLOSED = "nullIfClosed";
    String NULL_IF_TLAST = "nullIfTlast";
    String NULL_IF_CLOSED_OR_TLAST = "nullIfClosedOrTlast";
    String Y_OR_N_TO_BOOLEAN = "yOrNToBoolean";
    String FORMAT_2_DP = "format2Dp";

    @Named(NULL_IF_CLOSED)
    default <T> T nullIfClosed(T value, @Context AccountConditions accountConditions) {
        return accountConditions.isClosed() ? null : value;
    }

    @Named(NULL_IF_TLAST)
    default <T> T nullIfTlast(T value, @Context AccountConditions accountConditions) {
        return accountConditions.isTlast() ? null : value;
    }

    @Named(NULL_IF_CLOSED_OR_TLAST)
    default <T> T nullIfClosedOrTlast(T value, @Context AccountConditions accountConditions) {
        return accountConditions.isClosed() || accountConditions.isTlast() ? null : value;
    }

    @Named(Y_OR_N_TO_BOOLEAN)
    default boolean yOrNToBoolean(String flag) {
        return convertSpBooleanResult(flag);
    }

    @Named(FORMAT_2_DP)
    default BigDecimal format2DecimalPlaces(BigDecimal value) {
        if (value == null) return null;
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
