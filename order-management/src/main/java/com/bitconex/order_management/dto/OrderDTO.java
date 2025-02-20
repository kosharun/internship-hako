package com.bitconex.order_management.dto;

import com.bitconex.order_management.entity.OrderItem;
import com.bitconex.order_management.entity.OrderStatus;
import com.bitconex.order_management.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;
}
