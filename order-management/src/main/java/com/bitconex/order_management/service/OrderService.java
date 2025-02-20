package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.OrderDTO;
import com.bitconex.order_management.dto.OrderRequestDTO;
import com.bitconex.order_management.entity.Order;
import com.bitconex.order_management.entity.OrderStatus;
import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.entity.User;
import com.bitconex.order_management.mapper.DTOMapper;
import com.bitconex.order_management.repository.OrderRepository;
import com.bitconex.order_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final DTOMapper dtoMapper;
    private final UserRepository userRepository;
    private final OrderStatusService orderStatusService;

    public OrderService(OrderRepository orderRepository, DTOMapper dtoMapper, UserRepository userRepository, OrderStatusService orderStatusService) {
        this.orderRepository = orderRepository;
        this.dtoMapper = dtoMapper;
        this.userRepository = userRepository;
        this.orderStatusService = orderStatusService;
    }


    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> orderDTOS = new ArrayList<>();
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            orderDTOS.add(dtoMapper.mapToDTO(order));
        }

        return orderDTOS;
    }

    public Order getOrderById(Long Id)  {
        return orderRepository.findById(Id).orElseThrow(() -> new RuntimeException("Cannot find order"));
    }

    public OrderDTO createOrder(OrderRequestDTO orderRequestDTO) {

        Long userId = orderRequestDTO.getUserId();
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        LocalDate createdAt = LocalDate.now();

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<OrderStatus> status = orderStatusService.getOrderStatusByName("Pending");

        Order order = Order.builder()
                .user(user)
                .status(status.get())
                .totalPrice(orderRequestDTO.getTotalPrice())
                .createdAt(createdAt)
                .build();
        return dtoMapper.mapToDTO(orderRepository.save(order));
    }

}
