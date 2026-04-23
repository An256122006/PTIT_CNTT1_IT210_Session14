package org.example.session14_b5.model;

import java.math.BigDecimal;

public record ProductCatalogItem(Long id, String name, BigDecimal price, int stock, Long vendorId, String vendorName) {
}

