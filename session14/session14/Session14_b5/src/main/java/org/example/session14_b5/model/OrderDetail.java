package org.example.session14_b5.model;

import java.math.BigDecimal;

public record OrderDetail(Long id, Long orderId, Long productId, int quantity, BigDecimal price) {
}

