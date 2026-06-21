package org.example.accountservice.domain.exception;

import org.example.accountservice.domain.AccountId;

public class InsufficientBalanceError extends RuntimeException {
    public InsufficientBalanceError(AccountId accountId) {
        super("Account with id " + accountId.value() + " does not have sufficient funds");
    }
}
