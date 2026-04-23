package org.example.session14_b5.service;

import org.example.session14_b5.exception.InsufficientBalanceException;
import org.example.session14_b5.exception.ValidationException;
import org.example.session14_b5.model.CheckoutItem;
import org.example.session14_b5.model.CheckoutReceipt;
import org.example.session14_b5.model.OrderDetail;
import org.example.session14_b5.model.Product;
import org.example.session14_b5.model.Wallet;
import org.example.session14_b5.repository.OrderDetailRepository;
import org.example.session14_b5.repository.ProductRepository;
import org.example.session14_b5.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final WalletRepository walletRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderLifecycleService orderLifecycleService;
    private final VendorInventoryService vendorInventoryService;

    public OrderService(WalletRepository walletRepository,
                        ProductRepository productRepository,
                        OrderDetailRepository orderDetailRepository,
                        OrderLifecycleService orderLifecycleService,
                        VendorInventoryService vendorInventoryService) {
        this.walletRepository = walletRepository;
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderLifecycleService = orderLifecycleService;
        this.vendorInventoryService = vendorInventoryService;
    }

    @Transactional
    public CheckoutReceipt checkout(long userId, List<CheckoutItem> items) {
        if (items == null || items.isEmpty()) {
            throw new ValidationException("Phải chọn ít nhất một sản phẩm để thanh toán");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ValidationException("Không tìm thấy ví của user ID " + userId));

        Map<Long, ResolvedItem> resolvedItems = new LinkedHashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CheckoutItem item : items) {
            if (item.quantity() <= 0) {
                throw new ValidationException("Số lượng phải lớn hơn 0 cho product ID " + item.productId());
            }
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ValidationException("Không tìm thấy sản phẩm ID " + item.productId()));
            resolvedItems.merge(product.id(), new ResolvedItem(product, item.quantity()), (oldValue, newValue) ->
                    new ResolvedItem(oldValue.product(), oldValue.quantity() + newValue.quantity()));
        }

        for (ResolvedItem resolvedItem : resolvedItems.values()) {
            totalAmount = totalAmount.add(resolvedItem.product().price().multiply(BigDecimal.valueOf(resolvedItem.quantity())));
        }

        if (wallet.balance().compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException("Số dư ví không đủ để thanh toán tổng tiền " + totalAmount);
        }

        long orderId = orderLifecycleService.createPendingOrder(userId, totalAmount);

        Map<Long, List<CheckoutItem>> vendorGroups = new LinkedHashMap<>();
        for (ResolvedItem resolvedItem : resolvedItems.values()) {
            vendorGroups.computeIfAbsent(resolvedItem.product().vendorId(), key -> new ArrayList<>())
                    .add(new CheckoutItem(resolvedItem.product().id(), resolvedItem.quantity()));
        }

        List<Map.Entry<Long, List<CheckoutItem>>> successfulGroups = new ArrayList<>();
        try {
            for (Map.Entry<Long, List<CheckoutItem>> vendorGroup : vendorGroups.entrySet()) {
                vendorInventoryService.reserveStock(vendorGroup.getKey(), vendorGroup.getValue());
                successfulGroups.add(vendorGroup);
            }

            List<OrderDetail> details = new ArrayList<>();
            for (ResolvedItem resolvedItem : resolvedItems.values()) {
                details.add(new OrderDetail(null, orderId, resolvedItem.product().id(), resolvedItem.quantity(), resolvedItem.product().price()));
            }
            orderDetailRepository.saveAll(details);

            walletRepository.debit(userId, totalAmount);
            orderLifecycleService.markSuccess(orderId);

            Wallet updatedWallet = walletRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy ví sau khi thanh toán"));
            return new CheckoutReceipt(orderId, totalAmount, updatedWallet.balance());
        } catch (RuntimeException ex) {
            for (int i = successfulGroups.size() - 1; i >= 0; i--) {
                vendorInventoryService.releaseStock(successfulGroups.get(i).getValue());
            }
            orderLifecycleService.markFailed(orderId);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    private record ResolvedItem(Product product, int quantity) {
    }
}

