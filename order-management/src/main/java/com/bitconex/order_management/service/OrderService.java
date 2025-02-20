package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.OrderDTO;
import com.bitconex.order_management.entity.Order;
import com.bitconex.order_management.mapper.DTOMapper;
import com.bitconex.order_management.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final DTOMapper dtoMapper;

    public OrderService(OrderRepository orderRepository, DTOMapper dtoMapper) {
        this.orderRepository = orderRepository;
        this.dtoMapper = dtoMapper;
    }


    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> orderDTOS = new ArrayList<>();
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            orderDTOS.add(dtoMapper.mapToDTO(order));
        }

        return orderDTOS;
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

}
