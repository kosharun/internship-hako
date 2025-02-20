package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.OrderItemDTO;
import com.bitconex.order_management.dto.OrderItemRequestDTO;
import com.bitconex.order_management.entity.Order;
import com.bitconex.order_management.entity.OrderItem;
import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.mapper.DTOMapper;
import com.bitconex.order_management.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final DTOMapper dtoMapper;
    private final ProductService productService;
    private final OrderService orderService;


    public OrderItemService(OrderItemRepository orderItemRepository, DTOMapper dtoMapper, ProductService productService, OrderService orderService) {
        this.orderItemRepository = orderItemRepository;
        this.dtoMapper = dtoMapper;
        this.productService = productService;
        this.orderService = orderService;
    }

    public List<OrderItem> getAllOrderItems() {

        return orderItemRepository.findAll();
    }


    public OrderItem createOrderItem(OrderItemRequestDTO orderItemRequestDTO) {
        Product product = productService.getProductById(orderItemRequestDTO.getProductId());
        Order order = orderService.getOrderById(orderItemRequestDTO.getOrderId());

        return orderItemRepository.save(OrderItem.builder()
                .product(product)
                .order(order)
                .quantity(orderItemRequestDTO.getQuantity())
                .build());
    }


}
