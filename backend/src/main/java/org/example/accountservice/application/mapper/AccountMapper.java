package org.example.accountservice.application.mapper;

import org.example.accountservice.domain.Account;
import org.example.accountservice.presentation.dto.AccountDto;

public class AccountMapper {
    private AccountMapper() {
    }

    public static AccountDto toAccountDto(Account it) {
        return new AccountDto(it.id().value(), it.userId().value(), it.currency().name(), it.currentAmount(), it.active());
    }
}
