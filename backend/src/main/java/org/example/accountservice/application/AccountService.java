package org.example.accountservice.application;

import org.example.accountservice.domain.Account;
import org.example.accountservice.domain.AccountId;
import org.example.accountservice.domain.Currency;
import org.example.accountservice.domain.UserId;
import org.example.accountservice.domain.exception.AccountNotFoundError;
import org.example.accountservice.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public Account findById(AccountId accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundError(accountId));
    }

    public List<Account> findByUserId(UserId userId) {
        return accountRepository.findByUserId(userId);
    }

    // We can add additional validations to check user ids existance etc., not adding it here to keep it simple
    public Account save(UserId userId, Currency currency) {
        var id = accountRepository.save(userId, currency, BigDecimal.ZERO, true);
        return findById(id);
    }
}
