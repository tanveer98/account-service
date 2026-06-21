package org.example.accountservice.domain.command;

import org.example.accountservice.domain.AccountId;

import java.math.BigDecimal;

public record CurrencyExchangeCommand(AccountId sourceAccountId, BigDecimal sourceAmountToConvert, AccountId targetAccountId) {
}
