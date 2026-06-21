package org.example.accountservice.presentation.dto;

import jakarta.validation.constraints.Positive;
import org.example.accountservice.domain.Currency;

import java.math.BigDecimal;

public record TransactionRequestDto(@Positive BigDecimal amount) {
}
