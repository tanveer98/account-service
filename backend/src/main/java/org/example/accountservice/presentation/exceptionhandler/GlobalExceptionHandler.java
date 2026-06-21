package org.example.accountservice.presentation.exceptionhandler;

import org.example.accountservice.domain.exception.*;
import org.example.accountservice.presentation.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccountNotFoundError.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundError(AccountNotFoundError e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(404).body(new ErrorResponse("account.not.found"));
    }

    @ExceptionHandler(InsufficientBalanceError.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceError(InsufficientBalanceError e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(422).body(new ErrorResponse("account.balance.insufficient"));
    }

    @ExceptionHandler(InvalidAccountForCurrencyConversionError.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccountForCurrencyConversionError(InvalidAccountForCurrencyConversionError e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(400).body(new ErrorResponse("transaction.conversion.currency.invalid"));
    }

    @ExceptionHandler(LedgerEntryNotFoundError.class)
    public ResponseEntity<ErrorResponse> handleLedgerEntryNotFoundError(LedgerEntryNotFoundError e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(404).body(new ErrorResponse("transaction.not.found"));
    }
}
