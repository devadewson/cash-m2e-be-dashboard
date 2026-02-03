package com.m2e.dashboard.service.einvoice.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EInvoiceStatementStrategyFactory {

    private final List<EInvoiceStatementAcctStrategy> strategies;

    public Optional<EInvoiceStatementAcctStrategy> resolve(String acctType) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(acctType))
                .findFirst();
    }
}
