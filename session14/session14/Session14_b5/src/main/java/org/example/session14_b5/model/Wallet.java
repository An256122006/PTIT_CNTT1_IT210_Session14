package org.example.session14_b5.model;

import java.math.BigDecimal;

public record Wallet(Long id, Long userId, BigDecimal balance) {
}

