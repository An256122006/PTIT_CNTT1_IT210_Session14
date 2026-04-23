package org.example.session14_b5.repository;

import org.example.session14_b5.model.Order;
import org.example.session14_b5.model.OrderStatus;
import org.example.session14_b5.model.RevenueStats;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long createPendingOrder(long userId, BigDecimal totalAmount) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into orders(user_id, total_amount, status, created_at) values (?, ?, ?, current_timestamp)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, userId);
            ps.setBigDecimal(2, totalAmount);
            ps.setString(3, OrderStatus.PENDING.name());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Không thể tạo order mới");
        }
        return key.longValue();
    }

    public void updateStatus(long orderId, OrderStatus status) {
        jdbcTemplate.update("update orders set status = ? where id = ?", status.name(), orderId);
    }

    public Optional<Order> findById(long orderId) {
        return jdbcTemplate.query("select id, user_id, total_amount, status, created_at from orders where id = ?", rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(new Order(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getBigDecimal("total_amount"),
                    OrderStatus.valueOf(rs.getString("status")),
                    rs.getTimestamp("created_at").toLocalDateTime()
            ));
        }, orderId);
    }

    public RevenueStats getRevenueStats() {
        Long successOrders = jdbcTemplate.queryForObject("select count(*) from orders where status = 'SUCCESS'", Long.class);
        Long failedOrders = jdbcTemplate.queryForObject("select count(*) from orders where status = 'FAILED'", Long.class);
        BigDecimal revenue = jdbcTemplate.queryForObject(
                "select coalesce(sum(total_amount), 0) from orders where status = 'SUCCESS'",
                BigDecimal.class
        );
        return new RevenueStats(
                successOrders == null ? 0L : successOrders,
                failedOrders == null ? 0L : failedOrders,
                revenue == null ? BigDecimal.ZERO : revenue
        );
    }
}

