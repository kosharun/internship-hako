package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.OrderItemRequestDTO;
import com.bitconex.order_management.entity.Order;
import com.bitconex.order_management.entity.OrderItem;
import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.mapper.DTOMapper;
import com.bitconex.order_management.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTests {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private DTOMapper dtoMapper;

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderItemService orderItemService;

    private Product product;
    private Order order;
    private OrderItem orderItem;
    private OrderItemRequestDTO orderItemRequestDTO;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .productId(1L)
                .name("Test Product")
                .build();

        order = Order.builder()
                .orderId(1L)
                .build();

        orderItem = OrderItem.builder()
                .product(product)
                .order(order)
                .quantity(2)
                .build();

        orderItemRequestDTO = OrderItemRequestDTO.builder()
                .productId(1L)
                .orderId(1L)
                .quantity(2)
                .build();
    }

    @Test
    @DisplayName("Should return all order items")
    void testGetAllOrderItems_ShouldReturnAllOrderItems() {
        when(orderItemRepository.findAll()).thenReturn(List.of(orderItem));
        List<OrderItem> result = orderItemService.getAllOrderItems();
        assertThat(result).isNotEmpty().contains(orderItem);
        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should create order item")
    void testCreateOrderItem_ShouldCreateOrderItem() {
        when(productService.getProductById(orderItemRequestDTO.getProductId())).thenReturn(product);
        when(orderService.getOrderById(orderItemRequestDTO.getOrderId())).thenReturn(order);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        OrderItem result = orderItemService.createOrderItem(orderItemRequestDTO);
        assertThat(result).isNotNull();
        assertThat(result.getProduct()).isEqualTo(product);
        assertThat(result.getOrder()).isEqualTo(order);
        assertThat(result.getQuantity()).isEqualTo(orderItemRequestDTO.getQuantity());
        verify(productService, times(1)).getProductById(orderItemRequestDTO.getProductId());
        verify(orderService, times(1)).getOrderById(orderItemRequestDTO.getOrderId());
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should throw exception when product is not found")
    void testCreateOrderItem_ShouldThrowException_WhenProductNotFound() {
        when(productService.getProductById(orderItemRequestDTO.getProductId()))
                .thenThrow(new RuntimeException("Product not found"));
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderItemService.createOrderItem(orderItemRequestDTO));
        assertThat(exception.getMessage()).isEqualTo("Product not found");
        verify(productService, times(1)).getProductById(orderItemRequestDTO.getProductId());
        verify(orderService, never()).getOrderById(anyLong());
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should throw exception when order is not found")
    void testCreateOrderItem_ShouldThrowException_WhenOrderNotFound() {
        when(productService.getProductById(orderItemRequestDTO.getProductId())).thenReturn(product);
        when(orderService.getOrderById(orderItemRequestDTO.getOrderId()))
                .thenThrow(new RuntimeException("Order not found"));
        assertThrows(Exception.class, () -> orderItemService.createOrderItem(orderItemRequestDTO));
        verify(productService, times(1)).getProductById(orderItemRequestDTO.getProductId());
        verify(orderService, times(1)).getOrderById(orderItemRequestDTO.getOrderId());
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should throw exception when quantity is invalid")
    void testCreateOrderItem_ShouldThrowException_WhenQuantityIsInvalid() {
        OrderItemRequestDTO invalidRequest = OrderItemRequestDTO.builder()
                .productId(1L)
                .orderId(1L)
                .quantity(-1)
                .build();
        assertThrows(Exception.class, () -> orderItemService.createOrderItem(invalidRequest));
        verify(productService, never()).getProductById(anyLong());
        verify(orderService, never()).getOrderById(anyLong());
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }
}
