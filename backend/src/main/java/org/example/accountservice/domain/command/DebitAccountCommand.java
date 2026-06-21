package org.example.accountservice.domain.command;

import org.example.accountservice.domain.AccountId;
import org.example.accountservice.domain.Currency;

import java.math.BigDecimal;

public record DebitAccountCommand(AccountId accountId, BigDecimal amount) {

    public DebitAccountCommand {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
