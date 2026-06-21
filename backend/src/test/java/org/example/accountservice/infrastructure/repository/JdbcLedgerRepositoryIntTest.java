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

class JdbcLedgerRepositoryIntTest extends BaseIntegrationTest {

    @Autowired
    JdbcLedgerRepository underTest;

    @Test
    void testInsertAndFind() {
        var txUuid = UUID.randomUUID();
        var createdAt = Instant.now();
        var savedId = underTest.save(TransactionType.CREDIT, new AccountId(444L), new BigDecimal("10.1234") , Currency.EUR, txUuid, createdAt);

        var found = underTest.findById(savedId).orElseThrow();

        assertEquals(savedId, found.id());
        assertEquals(TransactionType.CREDIT, found.transactionType());
        assertEquals(new AccountId(444L), found.accountId());
        assertEquals(Currency.EUR, found.currency());
        assertEquals(txUuid, found.transactionUuid());
        // plain assert equals won't work due to precision mismatch between saved timestamp in DB (millis precision) and in memory instant (nanos precision)
        assertEquals(createdAt.toEpochMilli(), found.createdAt().toEpochMilli());
    }

}