package org.example.session14_b5.repository;

import org.example.session14_b5.model.Wallet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public class WalletRepository {

    private final JdbcTemplate jdbcTemplate;

    public WalletRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Wallet> findByUserId(long userId) {
        return jdbcTemplate.query("select id, user_id, balance from wallets where user_id = ?", rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(new Wallet(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getBigDecimal("balance")
            ));
        }, userId);
    }

    public void deposit(long userId, BigDecimal amount) {
        jdbcTemplate.update("update wallets set balance = balance + ? where user_id = ?", amount, userId);
    }

    public void debit(long userId, BigDecimal amount) {
        jdbcTemplate.update("update wallets set balance = balance - ? where user_id = ?", amount, userId);
    }
}

