package com.bitconex.order_management.mapper;

import com.bitconex.order_management.dto.OrderItemDTO;
import com.bitconex.order_management.dto.ProductRequestDTO;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.dto.UserRequestDTO;
import com.bitconex.order_management.entity.OrderItem;
import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.entity.User;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {

    private final ModelMapper modelMapper;

    public DTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureMappings();
    }

    private void configureMappings() {
        modelMapper.addMappings(new PropertyMap<OrderItem, OrderItemDTO>() {
            @Override
            protected void configure() {
                map(source.getOrder().getOrderId(), destination.getOrderId());  // Explicitly mapping the orderId from Order to DTO
            }
        });
    }


    public UserDTO mapToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public ProductRequestDTO mapToDTO(Product product) {
        return modelMapper.map(product, ProductRequestDTO.class);
    }

    public OrderItemDTO mapToDTO(OrderItem orderItem) {
        return modelMapper.map(orderItem, OrderItemDTO.class);
    }

    public User mapToEntity(UserRequestDTO userRequestDTO) {
        return modelMapper.map(userRequestDTO, User.class);
    }
}
