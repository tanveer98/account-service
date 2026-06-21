package org.example.accountservice.domain.exception;

public class InvalidAccountForCurrencyConversionError extends RuntimeException {
    public InvalidAccountForCurrencyConversionError() {
        super("Invalid account ids passed for currency conversion");
    }
}
