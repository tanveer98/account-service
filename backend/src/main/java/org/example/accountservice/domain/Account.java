package org.example.accountservice.domain;

import java.math.BigDecimal;

public record Account(
        AccountId id,
        UserId userId,
        Currency currency,
        BigDecimal currentAmount,
        boolean active
) {
}
