package org.example.accountservice.application;

import org.example.accountservice.domain.TransactionType;
import org.example.accountservice.domain.client.LoggingClient;
import org.example.accountservice.domain.command.CreditAccountCommand;
import org.example.accountservice.domain.command.CurrencyExchangeCommand;
import org.example.accountservice.domain.command.DebitAccountCommand;
import org.example.accountservice.domain.exception.AccountNotFoundError;
import org.example.accountservice.domain.exception.InsufficientBalanceError;
import org.example.accountservice.domain.exception.InvalidAccountForCurrencyConversionError;
import org.example.accountservice.domain.repository.AccountRepository;
import org.example.accountservice.domain.repository.LedgerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Service
public class TransferService {
    private final AccountRepository accountRepository;
    private final LedgerRepository ledgerRepository;
    private final LoggingClient loggingClient;
    private final RateService rateService;
    private final TransactionTemplate transactionTemplate;

    public TransferService(AccountRepository accountRepository, LedgerRepository ledgerRepository, LoggingClient loggingClient, RateService rateService, TransactionTemplate transactionTemplate) {
        this.accountRepository = accountRepository;
        this.ledgerRepository = ledgerRepository;
        this.loggingClient = loggingClient;
        this.rateService = rateService;
        this.transactionTemplate = transactionTemplate;
    }


    public void credit(CreditAccountCommand command) {
        transactionTemplate.executeWithoutResult(status -> {
            var account = accountRepository.findById(command.accountId())
                    .orElseThrow(() -> new AccountNotFoundError(command.accountId()));
            // normally this should not fail, but still throwing exception in case it does happen
            if (!accountRepository.incrementBalance(command.accountId(), command.amount())) {
                throw new RuntimeException("Error crediting amount");
            }
            ledgerRepository.save(
                    TransactionType.CREDIT,
                    command.accountId(),
                    command.amount(),
                    account.currency(),
                    UUID.randomUUID(),
                    Instant.now()
            );
        });
    }

    public void debit(DebitAccountCommand command) {
        // keeping the external call outside of transaction
        loggingClient.logDebitRequest();

        transactionTemplate.executeWithoutResult(status -> {
            var account = accountRepository.findById(command.accountId())
                    .orElseThrow(() -> new AccountNotFoundError(command.accountId()));
            // if the update failed it's because users balance does not support the debit
            if (!accountRepository.decrementBalance(command.accountId(), command.amount())) {
                throw new InsufficientBalanceError(command.accountId());
            }
            ledgerRepository.save(
                    TransactionType.DEBIT,
                    command.accountId(),
                    command.amount(),
                    account.currency(),
                    UUID.randomUUID(),
                    Instant.now()
            );
        });
    }

    public void performCurrencyExchange(CurrencyExchangeCommand command) {
        var source = accountRepository.findById(command.sourceAccountId()).orElseThrow(() -> new AccountNotFoundError(command.sourceAccountId()));
        var target = accountRepository.findById(command.targetAccountId()).orElseThrow(() -> new AccountNotFoundError(command.targetAccountId()));

        if (source.userId().value() != target.userId().value()) {
            throw new InvalidAccountForCurrencyConversionError();
        }

        UUID transactionID = UUID.randomUUID();
        Instant currentTime = Instant.now();
        loggingClient.logDebitRequest();

        transactionTemplate.executeWithoutResult(status -> {
            // debit
            if (!accountRepository.decrementBalance(command.sourceAccountId(), command.sourceAmountToConvert())) {
                throw new InsufficientBalanceError(command.sourceAccountId());
            }
            ledgerRepository.save(
                    TransactionType.FX_DEBIT,
                    command.sourceAccountId(),
                    command.sourceAmountToConvert(),
                    source.currency(),
                    transactionID,
                    currentTime
            );

            // credit
            var rate = rateService.getRate(source.currency(), target.currency());
            var targetAmountToAdd = command.sourceAmountToConvert().multiply(rate).setScale(4, RoundingMode.HALF_EVEN);
            if (!accountRepository.incrementBalance(command.targetAccountId(), targetAmountToAdd)) {
                throw new RuntimeException("Error crediting amount");
            }
            ledgerRepository.save(
                    TransactionType.FX_CREDIT,
                    command.targetAccountId(),
                    targetAmountToAdd,
                    target.currency(),
                    transactionID,
                    currentTime
            );
        });

    }


}
