package org.example.accountservice.presentation.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CurrencyExchangeRequestDto(
        long sourceAccountId,
        @Positive
        BigDecimal sourceAmountToConvert,
        long targetAccountId
) {
}
