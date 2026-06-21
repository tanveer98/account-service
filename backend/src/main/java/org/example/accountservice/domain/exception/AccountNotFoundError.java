package org.example.accountservice.domain.exception;

import org.example.accountservice.domain.AccountId;

public class AccountNotFoundError extends RuntimeException {
    public AccountNotFoundError(AccountId accountId) {
        super("Account with id: " + accountId.value() + " does not exist");
    }
}
