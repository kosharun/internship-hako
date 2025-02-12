package com.bitconex.order_management.mapper;

import com.bitconex.order_management.dto.ProductRequestDTO;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.dto.UserRequestDTO;
import com.bitconex.order_management.entity.Product;
import com.bitconex.order_management.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {

    private final ModelMapper modelMapper;

    public DTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserDTO mapToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public ProductRequestDTO mapToDTO(Product product) {
        return modelMapper.map(product, ProductRequestDTO.class);
    }

    public User mapToEntity(UserRequestDTO userRequestDTO) {
        return modelMapper.map(userRequestDTO, User.class);
    }
}
