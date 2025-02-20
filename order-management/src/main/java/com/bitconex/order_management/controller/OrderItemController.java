package com.bitconex.order_management.controller;

import com.bitconex.order_management.dto.OrderDTO;
import com.bitconex.order_management.dto.OrderItemRequestDTO;
import com.bitconex.order_management.dto.OrderRequestDTO;
import com.bitconex.order_management.entity.OrderItem;
import com.bitconex.order_management.service.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderItems")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    ResponseEntity<List<OrderItem>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    @PostMapping
    ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItemRequestDTO orderItemRequestDTO) {
        return ResponseEntity.ok(orderItemService.createOrderItem(orderItemRequestDTO));
    }
}
