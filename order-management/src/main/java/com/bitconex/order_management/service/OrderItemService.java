package com.bitconex.order_management.service;

import com.bitconex.order_management.dto.OrderItemDTO;
import com.bitconex.order_management.entity.OrderItem;
import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.mapper.DTOMapper;
import com.bitconex.order_management.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final DTOMapper dtoMapper;


    public OrderItemService(OrderItemRepository orderItemRepository, DTOMapper dtoMapper) {
        this.orderItemRepository = orderItemRepository;
        this.dtoMapper = dtoMapper;
    }


    public List<OrderItemDTO> findOrderItemsRelatedToProduct(Product product) {
        List<OrderItem> orderItems = orderItemRepository.findAllByProduct(product);

        return orderItems.stream()
                .map(dtoMapper::mapToDTO)
                .toList();
    }


    public OrderItemDTO createOrderItem(OrderItemDTO orderItemDTO) {
        OrderItem orderItem = dtoMapper.mapToEntity(orderItemDTO);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return dtoMapper.mapToDTO(savedOrderItem);
    }


}
