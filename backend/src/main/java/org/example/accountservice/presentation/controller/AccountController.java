package org.example.accountservice.presentation.controller;

import jakarta.validation.Valid;
import org.example.accountservice.application.AccountService;
import org.example.accountservice.application.TransactionHistoryService;
import org.example.accountservice.application.TransferService;
import org.example.accountservice.application.mapper.AccountMapper;
import org.example.accountservice.application.mapper.TransactionMapper;
import org.example.accountservice.domain.AccountId;
import org.example.accountservice.domain.Currency;
import org.example.accountservice.domain.UserId;
import org.example.accountservice.domain.command.CreditAccountCommand;
import org.example.accountservice.domain.command.CurrencyExchangeCommand;
import org.example.accountservice.domain.command.DebitAccountCommand;
import org.example.accountservice.presentation.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private final TransferService transferService;
    private final TransactionHistoryService transactionHistoryService;

    public AccountController(AccountService accountService, TransferService transferService, TransactionHistoryService transactionHistoryService) {
        this.accountService = accountService;
        this.transferService = transferService;
        this.transactionHistoryService = transactionHistoryService;
    }

    @PostMapping
    public AccountDto create(@RequestBody @Valid CreateAccountRequestDto dto) {
        return AccountMapper.toAccountDto(accountService.save(new UserId(dto.userId()), Currency.valueOf(dto.currency())));
    }

    @GetMapping("/{id}")
    public AccountDto getById(@PathVariable long id) {
        return AccountMapper.toAccountDto(accountService.findById(new AccountId(id)));
    }

    @PostMapping("/{id}/credit")
    public void credit(@PathVariable long id, @Valid @RequestBody TransactionRequestDto dto) {
        var command = new CreditAccountCommand(new AccountId(id), dto.amount());
        transferService.credit(command);
    }


    @PostMapping("/{id}/debit")
    public void debit(@PathVariable long id, @Valid @RequestBody TransactionRequestDto dto) {
        var command = new DebitAccountCommand(new AccountId(id), dto.amount());
        transferService.debit(command);
    }

    @PostMapping("/exchange")
    public void currencyExchange(@Valid @RequestBody CurrencyExchangeRequestDto dto) {
        var command = new CurrencyExchangeCommand(new AccountId(dto.sourceAccountId()), dto.sourceAmountToConvert(),new AccountId(dto.targetAccountId()));
        transferService.performCurrencyExchange(command);
    }


    @GetMapping("/{id}/transaction-history")
    public Page<TransactionDto> transactionHistory(@PathVariable long id, Pageable pageable) {
        return transactionHistoryService.getTransactionHistoryPage(new AccountId(id), pageable)
                .map(TransactionMapper::toTransactionDto);
    }

}
