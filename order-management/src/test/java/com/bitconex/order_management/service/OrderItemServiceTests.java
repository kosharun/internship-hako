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



}
