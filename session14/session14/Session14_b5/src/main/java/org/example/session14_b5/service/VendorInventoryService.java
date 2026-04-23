package org.example.session14_b5.service;

import org.example.session14_b5.exception.InsufficientStockException;
import org.example.session14_b5.model.CheckoutItem;
import org.example.session14_b5.model.Product;
import org.example.session14_b5.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VendorInventoryService {

    private final ProductRepository productRepository;

    public VendorInventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reserveStock(long vendorId, List<CheckoutItem> items) {
        for (CheckoutItem item : items) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new InsufficientStockException("Không tìm thấy sản phẩm ID " + item.productId()));
            if (!product.vendorId().equals(vendorId)) {
                throw new InsufficientStockException("Sản phẩm ID " + item.productId() + " không thuộc vendor hiện tại");
            }
            if (product.stock() < item.quantity()) {
                throw new InsufficientStockException("Sản phẩm " + product.name() + " không đủ tồn kho");
            }
        }

        for (CheckoutItem item : items) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new InsufficientStockException("Không tìm thấy sản phẩm ID " + item.productId()));
            productRepository.updateStock(product.id(), product.stock() - item.quantity());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void releaseStock(List<CheckoutItem> items) {
        for (CheckoutItem item : items) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new InsufficientStockException("Không tìm thấy sản phẩm ID " + item.productId()));
            productRepository.updateStock(product.id(), product.stock() + item.quantity());
        }
    }
}

