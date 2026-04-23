package org.example.session14_b5.repository;

import org.example.session14_b5.model.OrderDetail;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class OrderDetailRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderDetailRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(OrderDetail detail) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into order_details(order_id, product_id, quantity, price) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, detail.orderId());
            ps.setLong(2, detail.productId());
            ps.setInt(3, detail.quantity());
            ps.setBigDecimal(4, detail.price());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Không thể lưu order detail");
        }
        return key.longValue();
    }

    public void saveAll(List<OrderDetail> details) {
        for (OrderDetail detail : details) {
            save(detail);
        }
    }
}

