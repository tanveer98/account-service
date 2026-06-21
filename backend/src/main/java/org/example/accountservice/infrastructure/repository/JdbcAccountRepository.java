package org.example.accountservice.infrastructure.repository;

import org.example.accountservice.domain.Account;
import org.example.accountservice.domain.AccountId;
import org.example.accountservice.domain.Currency;
import org.example.accountservice.domain.UserId;
import org.example.accountservice.domain.repository.AccountRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcAccountRepository implements AccountRepository {
    private final JdbcClient jdbcClient;

    public JdbcAccountRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public AccountId save(UserId userId, Currency currency, BigDecimal currentAmount, boolean active) {
        var keyholder = new GeneratedKeyHolder();
        var sql = """
                INSERT INTO account(user_id, currency, current_amount, active)
                VALUES (:userId, :currency, :currentAmount, :active)
                """;

        jdbcClient.sql(sql)
                .param("userId", userId.value())
                .param("currency", currency.name())
                .param("currentAmount", currentAmount)
                .param("active", active)
                .update(keyholder);

        return new AccountId(keyholder.getKeyAs(Long.class));
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        var sql = "SELECT * FROM account where id = :accountId";
        return jdbcClient.sql(sql)
                .param("accountId", accountId.value())
                .query(JdbcAccountRepository::mapRow)
                .optional();

    }

    @Override
    public boolean incrementBalance(AccountId accountId, BigDecimal incrementAmount) {
        var sql = "UPDATE account SET current_amount = current_amount + :incrementAmount WHERE id = :accountId";
        var rowsUpdated = jdbcClient.sql(sql)
                .param("accountId", accountId.value())
                .param("incrementAmount", incrementAmount)
                .update();
        return rowsUpdated > 0;
    }

    @Override
    public boolean decrementBalance(AccountId accountId, BigDecimal decrementAmount) {
        var sql = "UPDATE account SET current_amount = current_amount - :decrementAmount WHERE id = :accountId AND current_amount >= :decrementAmount";
        var rowsUpdated = jdbcClient.sql(sql)
                .param("accountId", accountId.value())
                .param("decrementAmount", decrementAmount)
                .update();
        return rowsUpdated > 0;
    }

    @Override
    public List<Account> findByUserId(UserId userId) {
        var sql = "SELECT * FROM account where user_id = :userId";
        return jdbcClient.sql(sql)
                .param("userId", userId.value())
                .query(JdbcAccountRepository::mapRow)
                .list();
    }


    private static Account mapRow(ResultSet rs, int rowNum)  {
        try {
            return new Account(
                    new AccountId(rs.getLong("id")),
                    new UserId(rs.getLong("user_id")),
                    Currency.valueOf(rs.getString("currency")),
                    rs.getBigDecimal("current_amount"),
                    rs.getBoolean("active")
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
