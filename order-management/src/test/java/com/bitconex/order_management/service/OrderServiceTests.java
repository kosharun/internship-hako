package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.OrderDTO;
import com.bitconex.order_management.dto.OrderRequestDTO;
import com.bitconex.order_management.entity.Order;
import com.bitconex.order_management.entity.OrderStatus;
import com.bitconex.order_management.entity.User;
import com.bitconex.order_management.mapper.DTOMapper;
import com.bitconex.order_management.repository.OrderRepository;
import com.bitconex.order_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderStatusService orderStatusService;

    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private User user;
    private OrderDTO orderDTO;
    private OrderRequestDTO orderRequestDTO;
    private OrderStatus orderStatus;

    @BeforeEach
    void setUp() {
        user = User.builder().userId(1L).build();

        order = new Order();
        order.setOrderId(100L);
        order.setUser(user);
        order.setTotalPrice(50.0);
        order.setCreatedAt(LocalDate.now());

        orderStatus = new OrderStatus();
        orderStatus.setName("Pending");

        orderDTO = new OrderDTO();
        orderDTO.setUserId(1L);
        orderDTO.setTotalPrice(50.0);
        orderDTO.setCreatedAt(order.getCreatedAt());

        orderRequestDTO = OrderRequestDTO.builder()
                .userId(1L)
                .totalPrice(50.0)
                .build();
    }

    @Test
    @DisplayName("Should return OrderDTO when order is successfully created")
    void createOrder_ShouldReturnOrderDTO_WhenValidRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderStatusService.getOrderStatusByNameIgnoreCase("Pending")).thenReturn(Optional.of(orderStatus));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(dtoMapper.mapToDTO(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(orderRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(userRepository, times(1)).findById(1L);
        verify(orderStatusService, times(1)).getOrderStatusByNameIgnoreCase("Pending");
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(dtoMapper, times(1)).mapToDTO(order);
    }

    @Test
    @DisplayName("Should throw exception when userId is null")
    void createOrder_ShouldThrowException_WhenUserIdIsNull() {
        orderRequestDTO.setUserId(null);
        assertThrows(Exception.class, () -> orderService.createOrder(orderRequestDTO));
        verifyNoInteractions(userRepository, orderStatusService, orderRepository, dtoMapper);
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void createOrder_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> orderService.createOrder(orderRequestDTO));
        verify(userRepository, times(1)).findById(1L);
        verifyNoInteractions(orderStatusService, orderRepository, dtoMapper);
    }

    @Test
    @DisplayName("Should throw exception when order status is not found")
    void createOrder_ShouldThrowException_WhenOrderStatusNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderStatusService.getOrderStatusByNameIgnoreCase("Pending")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> orderService.createOrder(orderRequestDTO));
        verify(userRepository, times(1)).findById(1L);
        verify(orderStatusService, times(1)).getOrderStatusByNameIgnoreCase("Pending");
        verifyNoInteractions(orderRepository, dtoMapper);
    }

    @Test
    @DisplayName("Should return list of OrderDTO when orders exist")
    void testGetAllOrders_ShouldReturnOrderDTOList() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));
        when(dtoMapper.mapToDTO(order)).thenReturn(orderDTO);
        List<OrderDTO> result = orderService.getAllOrders();
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        verify(orderRepository, times(1)).findAll();
        verify(dtoMapper, times(1)).mapToDTO(order);
    }

    @Test
    @DisplayName("Should return empty list when no orders exist")
    void testGetAllOrders_WhenNoOrders_ShouldReturnEmptyList() {
        when(orderRepository.findAll()).thenReturn(List.of());
        List<OrderDTO> result = orderService.getAllOrders();
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(orderRepository, times(1)).findAll();
        verify(dtoMapper, never()).mapToDTO(any(Order.class));
    }

    @Test
    @DisplayName("Should return Order when order ID exists")
    void getOrderById_ShouldReturnOrder_WhenIdExists() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        Order result = orderService.getOrderById(100L);
        assertNotNull(result);
        assertEquals(100L, result.getOrderId());
        verify(orderRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("Should throw exception when order ID does not exist")
    void getOrderById_ShouldThrowException_WhenIdDoesNotExist() {
        when(orderRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> orderService.getOrderById(100L));
        verify(orderRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("Should return list of OrderDTO when orders exist for a given user")
    void getAllOrdersByUserId_ShouldReturnOrderDTOList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.findAllByUser(user)).thenReturn(Arrays.asList(order));
        when(dtoMapper.mapToDTO(order)).thenReturn(orderDTO);
        List<OrderDTO> result = orderService.getAllOrdersByUserId(1L);
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        verify(userRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).findAllByUser(user);
        verify(dtoMapper, times(1)).mapToDTO(order);
    }

    @Test
    @DisplayName("Should throw exception when user not found in getAllOrdersByUserId")
    void getAllOrdersByUserId_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> orderService.getAllOrdersByUserId(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should update order status successfully")
    void updateOrderStatus_ShouldUpdateStatus() {
        OrderStatus newStatus = new OrderStatus();
        newStatus.setName("Shipped");
        when(orderStatusService.getOrderStatusByNameIgnoreCase("Shipped")).thenReturn(Optional.of(newStatus));
        orderService.updateOrderStatus(order, "Shipped");
        assertEquals("Shipped", order.getStatus().getName());
        verify(orderStatusService, times(1)).getOrderStatusByNameIgnoreCase("Shipped");
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw exception when new order status not found in updateOrderStatus")
    void updateOrderStatus_ShouldThrowException_WhenStatusNotFound() {
        when(orderStatusService.getOrderStatusByNameIgnoreCase("Shipped")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.updateOrderStatus(order, "Shipped"));
        verify(orderStatusService, times(1)).getOrderStatusByNameIgnoreCase("Shipped");
        verify(orderRepository, never()).save(order);
    }

    @Test
    @DisplayName("Should cancel order successfully")
    void cancelOrder_ShouldCancelOrder() {
        OrderStatus cancelledStatus = new OrderStatus();
        cancelledStatus.setName("Cancelled");
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderStatusService.getOrderStatusByNameIgnoreCase("Cancelled")).thenReturn(Optional.of(cancelledStatus));
        orderService.cancelOrder(100L);
        assertEquals("Cancelled", order.getStatus().getName());
        verify(orderRepository, times(1)).findById(100L);
        verify(orderStatusService, times(1)).getOrderStatusByNameIgnoreCase("Cancelled");
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw exception when order not found in cancelOrder")
    void cancelOrder_ShouldThrowException_WhenOrderNotFound() {
        when(orderRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> orderService.cancelOrder(100L));
        verify(orderRepository, times(1)).findById(100L);
        verify(orderStatusService, never()).getOrderStatusByNameIgnoreCase("Cancelled");
    }

    @Test
    @DisplayName("Should throw exception when cancel status not found in cancelOrder")
    void cancelOrder_ShouldThrowException_WhenCancelStatusNotFound() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderStatusService.getOrderStatusByNameIgnoreCase("Cancelled")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(100L));
        verify(orderRepository, times(1)).findById(100L);
        verify(orderStatusService, times(1)).getOrderStatusByNameIgnoreCase("Cancelled");
    }
}
