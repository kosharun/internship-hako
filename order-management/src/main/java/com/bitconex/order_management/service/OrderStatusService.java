package com.bitconex.order_management.service;

import com.bitconex.order_management.entity.OrderStatus;
import com.bitconex.order_management.repository.OrderStatusRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderStatusService {
    private final OrderStatusRepository orderStatusRepository;

    public OrderStatusService(OrderStatusRepository orderStatusRepository) {
        this.orderStatusRepository = orderStatusRepository;
    }

    public Optional<OrderStatus> getOrderStatusByNameIgnoreCase(String name) {
        return orderStatusRepository.findByNameIgnoreCase(name);
    }
}
