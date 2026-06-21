package org.example.accountservice.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Ledger(
        LedgerId id,
        TransactionType transactionType,
        AccountId accountId,
        BigDecimal amount,
        Currency currency,
        UUID transactionUuid,
        Instant createdAt
) {
}
