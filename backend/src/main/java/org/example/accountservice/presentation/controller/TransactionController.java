package org.example.accountservice.presentation.controller;

import org.example.accountservice.application.TransactionHistoryService;
import org.example.accountservice.application.mapper.TransactionMapper;
import org.example.accountservice.domain.LedgerId;
import org.example.accountservice.presentation.dto.TransactionDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {
    private final TransactionHistoryService transactionHistoryService;

    public TransactionController(TransactionHistoryService transactionHistoryService) {
        this.transactionHistoryService = transactionHistoryService;
    }

    @GetMapping("/{id}")
    public TransactionDto getById(@PathVariable long id) {
        return TransactionMapper.toTransactionDto(transactionHistoryService.getTransactionById(new LedgerId(id)));
    }
}
