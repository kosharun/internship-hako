package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.OrderDTO;
import com.bitconex.order_management.entity.Order;
import com.bitconex.order_management.entity.User;
import com.bitconex.order_management.mapper.DTOMapper;
import com.bitconex.order_management.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        User user = new User();

        order = new Order();
        order.setOrderId(100L);
        order.setUser(user);
        order.setTotalPrice(50.0);
        order.setCreatedAt(LocalDate.now());

        orderDTO = new OrderDTO();
        orderDTO.setUserId(1L);
        orderDTO.setTotalPrice(50.0);
        orderDTO.setCreatedAt(order.getCreatedAt());
    }

    @Test
    void testGetAllOrders_ShouldReturnOrderDTOList() {
        // Mock repository return value
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));

        // Mock DTO mapper
        when(dtoMapper.mapToDTO(order)).thenReturn(orderDTO);

        // Call service method
        List<OrderDTO> result = orderService.getAllOrders();

        // Assertions
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);

        // Verify method calls
        verify(orderRepository, times(1)).findAll();
        verify(dtoMapper, times(1)).mapToDTO(order);
    }

    @Test
    void testGetAllOrders_WhenNoOrders_ShouldReturnEmptyList() {
        // Mock repository to return an empty list
        when(orderRepository.findAll()).thenReturn(List.of());

        // Call service method
        List<OrderDTO> result = orderService.getAllOrders();

        // Assertions
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // Verify method calls
        verify(orderRepository, times(1)).findAll();
        verify(dtoMapper, never()).mapToDTO(any(Order.class));
    }

}
