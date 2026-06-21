package org.example.accountservice.presentation.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionDto(long id, String transactionType, long accountId, BigDecimal amount, String currency, String transactionUuid, Instant createdAt) {
}
