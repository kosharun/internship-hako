package com.bitconex.order_management.dto;

import com.bitconex.order_management.entity.OrderItem;
import com.bitconex.order_management.entity.OrderStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderDTO {
    private Long userId;
    private OrderStatus status;
    private List<OrderItem> orderItems;

    private double totalPrice;
    private LocalDate createdAt;
}
