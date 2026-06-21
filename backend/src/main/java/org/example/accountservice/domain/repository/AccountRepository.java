package org.example.accountservice.domain.repository;

import org.example.accountservice.domain.Account;
import org.example.accountservice.domain.AccountId;
import org.example.accountservice.domain.Currency;
import org.example.accountservice.domain.UserId;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    AccountId save(UserId userId, Currency currency, BigDecimal currentAmount, boolean active);
    Optional<Account> findById(AccountId accountId);
    boolean incrementBalance(AccountId accountId, BigDecimal incrementAmount);
    boolean decrementBalance(AccountId accountId, BigDecimal decrementAmount);
    List<Account> findByUserId(UserId userId);
}
