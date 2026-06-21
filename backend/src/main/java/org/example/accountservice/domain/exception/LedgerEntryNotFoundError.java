package org.example.accountservice.domain.exception;

import org.example.accountservice.domain.LedgerId;

public class LedgerEntryNotFoundError extends RuntimeException {
    public LedgerEntryNotFoundError(LedgerId ledgerId) {
        super("Ledger entry with id " + ledgerId.value() + " is not found");
    }
}
