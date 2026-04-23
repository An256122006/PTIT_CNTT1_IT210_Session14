package org.example.session14_b5.repository;

import org.example.session14_b5.model.Product;
import org.example.session14_b5.model.ProductCatalogItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProductCatalogItem> findCatalog() {
        return jdbcTemplate.query("""
                select p.id, p.name, p.price, p.stock, p.vendor_id, v.name as vendor_name
                from products p
                join vendors v on v.id = p.vendor_id
                order by v.id, p.id
                """, (rs, rowNum) -> new ProductCatalogItem(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getBigDecimal("price"),
                rs.getInt("stock"),
                rs.getLong("vendor_id"),
                rs.getString("vendor_name")
        ));
    }

    public Optional<Product> findById(long id) {
        return jdbcTemplate.query("select id, name, price, stock, vendor_id from products where id = ?", rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(new Product(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getBigDecimal("price"),
                    rs.getInt("stock"),
                    rs.getLong("vendor_id")
            ));
        }, id);
    }

    public int updateStock(long productId, int newStock) {
        return jdbcTemplate.update("update products set stock = ? where id = ?", newStock, productId);
    }
}

