package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.OrderItemDTO;
import com.bitconex.order_management.entity.OrderItem;
import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.mapper.DTOMapper;
import com.bitconex.order_management.repository.OrderItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTests {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    private OrderItemService orderItemService;

    // GET TESTS

    @Test
    @DisplayName("Should retrieve list of Order Items by product")
    void testFindOrderItemsRelatedToProduct_ShouldReturnListOfDTOS() {

        OrderItem orderItem1 = OrderItem.builder()
                .quantity(2)
                .build();
        OrderItem orderItem2 = OrderItem.builder()
                .quantity(3)
                .build();

        OrderItemDTO orderItemDTO1 = OrderItemDTO.builder()
                .quantity(2)
                .build();
        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        Product product = Product.builder()
                .name("product test")
                .build();

        when(orderItemRepository.findAllByProduct(product)).thenReturn(orderItems);
        when(dtoMapper.mapToDTO(orderItem1)).thenReturn(orderItemDTO1);
        List<OrderItemDTO> testOrderItems = orderItemService.findOrderItemsRelatedToProduct(product);

        assertThat(testOrderItems).isNotEmpty();
    }

    @Test
    @DisplayName("Should return empty list when no Order Items are found for the product")
    void testFindOrderItemsRelatedToProduct_ShouldReturnEmptyList() {

        Product product = Product.builder()
                .name("non-existent product")
                .build();

        when(orderItemRepository.findAllByProduct(product)).thenReturn(List.of());

        List<OrderItemDTO> testOrderItems = orderItemService.findOrderItemsRelatedToProduct(product);

        assertThat(testOrderItems).isEmpty();
        verify(dtoMapper, never()).mapToDTO(any(OrderItem.class));
    }


}
