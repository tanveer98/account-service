package org.example.accountservice.presentation.dto;

import java.math.BigDecimal;

public record AccountDto(long id, long userId, String currency, BigDecimal currentAmount, boolean active) {
}
