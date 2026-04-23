package org.example.session14_b5.model;

import java.math.BigDecimal;

public record CheckoutReceipt(Long orderId, BigDecimal totalAmount, BigDecimal walletBalance) {
}

