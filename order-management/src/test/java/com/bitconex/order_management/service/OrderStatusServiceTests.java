package com.bitconex.order_management.service;

import com.bitconex.order_management.entity.OrderStatus;
import com.bitconex.order_management.repository.OrderStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderStatusServiceTests {

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @InjectMocks
    private OrderStatusService orderStatusService;

    private OrderStatus orderStatus;

    @BeforeEach
    void setUp() {
        orderStatus = new OrderStatus();
        orderStatus.setName("Pending");
    }

    @Test
    @DisplayName("Should return OrderStatus when found")
    void testGetOrderStatusByNameIgnoreCase_Found() {
        when(orderStatusRepository.findByNameIgnoreCase("pending")).thenReturn(Optional.of(orderStatus));
        Optional<OrderStatus> result = orderStatusService.getOrderStatusByNameIgnoreCase("pending");
        assertTrue(result.isPresent());
        assertEquals("Pending", result.get().getName());
    }

    @Test
    @DisplayName("Should return empty Optional when not found")
    void testGetOrderStatusByNameIgnoreCase_NotFound() {
        when(orderStatusRepository.findByNameIgnoreCase("nonexistent")).thenReturn(Optional.empty());
        Optional<OrderStatus> result = orderStatusService.getOrderStatusByNameIgnoreCase("nonexistent");
        assertFalse(result.isPresent());
    }
}
