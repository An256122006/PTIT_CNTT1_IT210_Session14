package org.example.session14_b5.service;

import org.example.session14_b5.model.RevenueStats;
import org.example.session14_b5.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RevenueService {

    private final OrderRepository orderRepository;

    public RevenueService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public RevenueStats getRevenueStats() {
        return orderRepository.getRevenueStats();
    }
}

