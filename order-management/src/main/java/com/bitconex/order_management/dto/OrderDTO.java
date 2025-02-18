package com.bitconex.order_management.dto;

import com.bitconex.order_management.entity.OrderStatus;
import com.bitconex.order_management.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderDTO {
    private Long userId;
    private OrderStatus status;

    private double totalPrice;
    private LocalDateTime createdAt;
}
