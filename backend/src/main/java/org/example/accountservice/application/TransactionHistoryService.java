package org.example.accountservice.application;

import org.example.accountservice.domain.AccountId;
import org.example.accountservice.domain.Ledger;
import org.example.accountservice.domain.LedgerId;
import org.example.accountservice.domain.exception.LedgerEntryNotFoundError;
import org.example.accountservice.domain.repository.LedgerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TransactionHistoryService {
    private final LedgerRepository ledgerRepository;

    public TransactionHistoryService(LedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    public Ledger getTransactionById(LedgerId ledgerId) {
        return ledgerRepository.findById(ledgerId).orElseThrow(() -> new LedgerEntryNotFoundError(ledgerId));
    }

    public Page<Ledger> getTransactionHistoryPage(AccountId accountId, Pageable pageable) {
        return ledgerRepository.pagedTransactionHistoryForAccount(accountId, pageable);
    }
}
