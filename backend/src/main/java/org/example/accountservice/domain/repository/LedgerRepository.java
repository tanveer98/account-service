package org.example.accountservice.domain.repository;

import org.example.accountservice.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface LedgerRepository {
    LedgerId save(TransactionType transactionType, AccountId accountId, BigDecimal amount, Currency currency, UUID transactionUuid, Instant createdAt);
    Optional<Ledger> findById(LedgerId ledgerId);
    Page<Ledger> pagedTransactionHistoryForAccount(AccountId accountId, Pageable pageable);
}
