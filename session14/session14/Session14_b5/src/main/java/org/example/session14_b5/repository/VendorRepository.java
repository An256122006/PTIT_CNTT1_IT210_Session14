package org.example.session14_b5.repository;

import org.example.session14_b5.model.Vendor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VendorRepository {

    private final JdbcTemplate jdbcTemplate;

    public VendorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Vendor> findAll() {
        return jdbcTemplate.query("select id, name from vendors order by id", (rs, rowNum) ->
                new Vendor(rs.getLong("id"), rs.getString("name")));
    }

    public Optional<Vendor> findById(long id) {
        return jdbcTemplate.query("select id, name from vendors where id = ?", rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(new Vendor(rs.getLong("id"), rs.getString("name")));
        }, id);
    }
}

