package org.example.session14_b5.repository;

import org.example.session14_b5.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findById(long id) {
        return jdbcTemplate.query("select id, name from users where id = ?", rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(new User(rs.getLong("id"), rs.getString("name")));
        }, id);
    }
}

