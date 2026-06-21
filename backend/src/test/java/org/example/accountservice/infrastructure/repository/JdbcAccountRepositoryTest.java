package org.example.accountservice.infrastructure.repository;

import org.example.accountservice.BaseIntegrationTest;
import org.example.accountservice.domain.AccountId;
import org.example.accountservice.domain.Currency;
import org.example.accountservice.domain.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JdbcAccountRepositoryTest extends BaseIntegrationTest {

    @Autowired
    JdbcLedgerRepository underTest;

    @Test
    void testInsertAndFind() {
        var type = TransactionType.CREDIT;
        var accountId = new AccountId(123);
        var amount = new BigDecimal("12.0002");
        var currency = Currency.EUR;
        var uuid = UUID.randomUUID();
        var createdAt = Instant.now();

        var savedId = underTest.save(type, accountId, amount, currency, uuid, createdAt);

        var found = underTest.findById(savedId).orElseThrow();
        assertEquals(savedId, found.id());
        assertEquals(type, found.transactionType());
        assertEquals(0, found.amount().compareTo(amount));
        assertEquals(currency, found.currency());
        assertEquals(uuid, found.transactionUuid());
        assertEquals(createdAt.toEpochMilli(), found.createdAt().toEpochMilli());
    }
}