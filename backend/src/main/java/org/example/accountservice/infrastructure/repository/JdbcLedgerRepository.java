package org.example.accountservice.infrastructure.repository;

import org.example.accountservice.domain.*;
import org.example.accountservice.domain.repository.LedgerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcLedgerRepository implements LedgerRepository {
    private final JdbcClient jdbcClient;

    public JdbcLedgerRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public LedgerId save(TransactionType transactionType, AccountId accountId, BigDecimal amount, Currency currency, UUID transactionUuid, Instant createdAt) {
        var keyHolder = new GeneratedKeyHolder();
        var sql = """
                INSERT INTO ledger(transaction_type, account_id, amount, currency, transaction_uuid, created_at)
                VALUES(:transactionType, :accountId, :amount, :currency, :transactionUuid, :createdAt)
                """;

        jdbcClient.sql(sql)
                .param("transactionType", transactionType.name())
                .param("accountId", accountId.value())
                .param("amount", amount)
                .param("currency", currency.name())
                .param("transactionUuid", transactionUuid)
                .param("createdAt", createdAt)
                .update(keyHolder);

        return new LedgerId(keyHolder.getKeyAs(Long.class));
    }


    public Optional<Ledger> findById(LedgerId ledgerId) {
        var sql = "SELECT * FROM ledger where id = :ledgerId";
        return jdbcClient.sql(sql)
                .param("ledgerId", ledgerId.value())
                .query(JdbcLedgerRepository::mapRow)
                .optional();


    }

    @Override
    public Page<Ledger> pagedTransactionHistoryForAccount(AccountId accountId, Pageable pageable) {
        var total = jdbcClient.sql("SELECT COUNT(*) from ledger")
                .query(Long.class)
                .single();


        var sql = "SELECT * FROM ledger where account_id = :accountId ORDER BY id LIMIT :limit OFFSET :offset";
        var content = jdbcClient.sql(sql)
                .param("accountId", accountId.value())
                .param("limit", pageable.getPageSize())
                .param("offset", pageable.getOffset())
                .query(JdbcLedgerRepository::mapRow)
                .list();

        return new PageImpl<>(content, pageable, total);
    }


    private static Ledger mapRow(ResultSet rs, int rownum) {
       try {
           return new Ledger(
                   new LedgerId(rs.getLong("id")),
                   TransactionType.valueOf(rs.getString("transaction_type")),
                   new AccountId(rs.getLong("account_id")),
                   rs.getBigDecimal("amount"),
                   Currency.valueOf(rs.getString("currency")),
                   rs.getObject("transaction_uuid", UUID.class),
                   rs.getTimestamp("created_at").toInstant()

           );
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }
}
