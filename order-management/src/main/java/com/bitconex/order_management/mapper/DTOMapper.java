package com.bitconex.order_management.mapper;

import com.bitconex.order_management.dto.AddressDTO;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.entity.Address;
import com.bitconex.order_management.entity.User;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {


    public static UserDTO mapToDTO(User user) {
        UserDTO userDTO = UserDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .dateOfBirth(user.getDateOfBirth())
                .build();


        Address userAddress = user.getAddress();

        userDTO.setAddress(userAddress);
        return userDTO;
    }



}
