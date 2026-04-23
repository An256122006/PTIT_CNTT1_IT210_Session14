package org.example.session14_b5.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Order(Long id, Long userId, BigDecimal totalAmount, OrderStatus status, LocalDateTime createdAt) {
}

