package com.m2e.dashboard.service.overview;

import com.m2e.dashboard.enums.CurrentAccountResult;
import com.m2e.dashboard.enums.Result;
import com.m2e.dashboard.service.overview.strategy.OverviewDepositHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OverviewServiceTest {
    private final OverviewDepositHandler overviewService = new OverviewDepositHandler();
    @Test
    void testRetrieveCurrentAccounts_RealTime() {
        int spResult = 20;
        Result result = overviewService.retrieveCurrentAccounts(spResult);
        assertEquals(CurrentAccountResult.REAL_TIME, result, "Expected REAL_TIME when account count is equal to 20");
    }

    @Test
    void testRetrieveCurrentAccounts_TMinusOne() {
        int spResult = 21;
        Result result = overviewService.retrieveCurrentAccounts(spResult);
        assertEquals(CurrentAccountResult.T_MINUS_ONE, result, "Expected T_MINUS_ONE when account count is greater than 20");
    }


}