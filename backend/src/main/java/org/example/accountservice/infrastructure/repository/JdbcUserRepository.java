package org.example.accountservice.infrastructure.repository;

import org.example.accountservice.domain.User;
import org.example.accountservice.domain.UserId;
import org.example.accountservice.domain.UserName;
import org.example.accountservice.domain.repository.UserRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {
    private final JdbcClient jdbcClient;

    public JdbcUserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public UserId save(UserName name) {
        var keyholder = new GeneratedKeyHolder();
        var sql = "INSERT INTO bank_user(name, active) VALUES (:name, true);";
        jdbcClient.sql(sql)
                .param("name", name.value())
                .update(keyholder);

        return new UserId(keyholder.getKeyAs(Long.class));
    }

    @Override
    public Optional<User> findById(UserId userId) {
        var sql = "SELECT * FROM bank_user where id = :userId";
        return jdbcClient.sql(sql)
                .param("userId", userId.value())
                .query(JdbcUserRepository::mapRow)
                .optional();
    }


    private static User mapRow(ResultSet rs, int rownum) {
        try {
            return new User(
                    new UserId(rs.getLong("id")),
                    new UserName(rs.getString("name")),
                    rs.getBoolean("active")
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
