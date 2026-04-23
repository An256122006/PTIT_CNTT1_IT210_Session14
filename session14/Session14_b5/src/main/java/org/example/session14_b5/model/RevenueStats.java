package org.example.session14_b5.model;

import java.math.BigDecimal;

public record RevenueStats(long successfulOrders, long failedOrders, BigDecimal totalRevenue) {
}

