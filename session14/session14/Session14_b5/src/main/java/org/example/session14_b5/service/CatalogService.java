package org.example.session14_b5.service;

import org.example.session14_b5.model.ProductCatalogItem;
import org.example.session14_b5.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogService {

    private final ProductRepository productRepository;

    public CatalogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductCatalogItem> getCatalog() {
        return productRepository.findCatalog();
    }
}

