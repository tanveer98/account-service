package org.example.accountservice.application.mapper;

import org.example.accountservice.domain.Ledger;
import org.example.accountservice.presentation.dto.TransactionDto;

public class TransactionMapper {
    private TransactionMapper() {

    }

    public static TransactionDto toTransactionDto(Ledger it) {
        return new TransactionDto(it.id().value(), it.transactionType().name(), it.accountId().value(), it.amount(), it.currency().name(), it.transactionUuid().toString(), it.createdAt());
    }
}
