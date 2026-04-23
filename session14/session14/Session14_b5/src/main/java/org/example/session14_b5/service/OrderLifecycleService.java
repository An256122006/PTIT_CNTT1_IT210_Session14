package org.example.session14_b5.service;

import org.example.session14_b5.model.OrderStatus;
import org.example.session14_b5.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderLifecycleService {

    private final OrderRepository orderRepository;

    public OrderLifecycleService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long createPendingOrder(long userId, BigDecimal totalAmount) {
        return orderRepository.createPendingOrder(userId, totalAmount);
    }

    @Transactional
    public void markSuccess(long orderId) {
        orderRepository.updateStatus(orderId, OrderStatus.SUCCESS);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(long orderId) {
        orderRepository.updateStatus(orderId, OrderStatus.FAILED);
    }
}

